import assert from "node:assert/strict";
import { beforeEach, describe, test } from "node:test";
import { ConsultarCatalogo } from "../../site/src/application/consultar-catalogo.js";
import { Autor } from "../../site/src/domain/autor.js";
import { AnioInvalidoError, IdiomaInvalidoError, TituloInvalidoError } from "../../site/src/domain/errores.js";
import { Libro } from "../../site/src/domain/libro.js";
import { RepositorioAutoresEnMemoria, RepositorioLibrosEnMemoria } from "./fakes.js";

describe("ConsultarCatalogo", () => {
    let consultar;

    beforeEach(async () => {
        const libros = new RepositorioLibrosEnMemoria();
        const autores = new RepositorioAutoresEnMemoria();
        await libros.guardar(new Libro({ titulo: "Frankenstein", idioma: "en" }));
        await libros.guardar(new Libro({ titulo: "Don Quijote", idioma: "es" }));
        await autores.guardar(new Autor({ nombre: "Shelley", anioNacimiento: 1797, anioMuerte: 1851 }));
        await autores.guardar(new Autor({ nombre: "Cervantes", anioNacimiento: 1547, anioMuerte: 1616 }));
        consultar = new ConsultarCatalogo({ libros, autores });
    });

    test("lista libros y autores", async () => {
        assert.equal((await consultar.listarLibros()).length, 2);
        assert.equal((await consultar.listarAutores()).length, 2);
    });

    test("filtra por idioma normalizando el código", async () => {
        const libros = await consultar.librosPorIdioma(" ES ");
        assert.deepEqual(libros.map((libro) => libro.titulo), ["Don Quijote"]);
    });

    test("rechaza idiomas que no son un código de dos letras", async () => {
        for (const idioma of [undefined, null, "", "e", "esp", "e1", "ñu"]) {
            await assert.rejects(consultar.librosPorIdioma(idioma), IdiomaInvalidoError, `idioma ${idioma}`);
        }
    });

    test("busca un libro por título exacto sin importar mayúsculas", async () => {
        assert.equal((await consultar.libroPorTitulo("  frankenstein ")).titulo, "Frankenstein");
        assert.equal(await consultar.libroPorTitulo("Drácula"), null);
    });

    test("rechaza buscar un título vacío", async () => {
        await assert.rejects(consultar.libroPorTitulo("  "), TituloInvalidoError);
    });

    test("devuelve los autores vivos en un año", async () => {
        assert.deepEqual((await consultar.autoresVivosEn(1818)).map((autor) => autor.nombre), ["Shelley"]);
        assert.deepEqual((await consultar.autoresVivosEn(1600)).map((autor) => autor.nombre), ["Cervantes"]);
        assert.deepEqual(await consultar.autoresVivosEn(1700), []);
    });

    test("rechaza un año que no es entero", async () => {
        for (const anio of [Number.NaN, 1818.5, "1818"]) {
            await assert.rejects(consultar.autoresVivosEn(anio), AnioInvalidoError);
        }
    });
});
