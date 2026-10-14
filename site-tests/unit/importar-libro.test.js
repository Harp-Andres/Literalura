import assert from "node:assert/strict";
import { beforeEach, describe, test } from "node:test";
import { ImportarLibro } from "../../site/src/application/importar-libro.js";
import { Autor } from "../../site/src/domain/autor.js";
import { TituloInvalidoError } from "../../site/src/domain/errores.js";
import { Libro } from "../../site/src/domain/libro.js";
import { CatalogoRemotoFijo, RepositorioAutoresEnMemoria, RepositorioLibrosEnMemoria } from "./fakes.js";

const shelley = new Autor({ nombre: "Shelley, Mary Wollstonecraft", anioNacimiento: 1797, anioMuerte: 1851 });
const frankenstein = new Libro({
    titulo: "Frankenstein; Or, The Modern Prometheus",
    idioma: "en",
    numeroDescargas: 15000,
    autor: shelley
});

describe("ImportarLibro", () => {
    let libros;
    let autores;

    beforeEach(() => {
        libros = new RepositorioLibrosEnMemoria();
        autores = new RepositorioAutoresEnMemoria();
    });

    function importador(encontrados) {
        const remoto = new CatalogoRemotoFijo(encontrados);
        return { remoto, caso: new ImportarLibro({ remoto, libros, autores }) };
    }

    test("rechaza un título vacío sin consultar el catálogo remoto", async () => {
        const { remoto, caso } = importador([frankenstein]);
        for (const titulo of [undefined, null, "", "   "]) {
            await assert.rejects(caso.importar(titulo), TituloInvalidoError);
        }
        assert.deepEqual(remoto.consultas, []);
    });

    test("busca con el título recortado y guarda el libro con su autor", async () => {
        const { remoto, caso } = importador([frankenstein]);

        const resultado = await caso.importar("  Frankenstein ");

        assert.deepEqual(remoto.consultas, ["Frankenstein"]);
        assert.equal(resultado.guardados, 1);
        assert.equal(resultado.omitidos, 0);
        assert.equal(resultado.libros[0].titulo, frankenstein.titulo);
        assert.notEqual(resultado.libros[0].id, null);
        assert.notEqual(resultado.libros[0].autor.id, null);
        assert.equal(autores.autores.length, 1);
    });

    test("omite los títulos que ya existen sin importar mayúsculas", async () => {
        await libros.guardar(new Libro({ titulo: frankenstein.titulo.toUpperCase(), idioma: "en" }));
        const { caso } = importador([frankenstein]);

        const resultado = await caso.importar("Frankenstein");

        assert.equal(resultado.guardados, 0);
        assert.equal(resultado.omitidos, 1);
        assert.deepEqual(resultado.libros, []);
        assert.equal(libros.libros.length, 1);
    });

    test("cuenta como omitido un título repetido dentro de la misma respuesta", async () => {
        const { caso } = importador([frankenstein, frankenstein]);

        const resultado = await caso.importar("Frankenstein");

        assert.equal(resultado.guardados, 1);
        assert.equal(resultado.omitidos, 1);
    });

    test("reutiliza el autor existente por nombre sin importar mayúsculas", async () => {
        const existente = await autores.guardar(new Autor({ nombre: "SHELLEY, MARY WOLLSTONECRAFT" }));
        const { caso } = importador([frankenstein]);

        const resultado = await caso.importar("Frankenstein");

        assert.equal(resultado.libros[0].autor, existente);
        assert.equal(autores.autores.length, 1);
    });

    test("guarda libros sin autor", async () => {
        const anonimo = new Libro({ titulo: "Beowulf", idioma: "en" });
        const { caso } = importador([anonimo]);

        const resultado = await caso.importar("Beowulf");

        assert.equal(resultado.guardados, 1);
        assert.equal(resultado.libros[0].autor, null);
        assert.equal(autores.autores.length, 0);
    });

    test("devuelve cero guardados cuando el catálogo remoto no encuentra nada", async () => {
        const { caso } = importador([]);

        assert.deepEqual({ ...(await caso.importar("Inexistente")) }, { guardados: 0, omitidos: 0, libros: [] });
    });
});
