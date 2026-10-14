import assert from "node:assert/strict";
import { beforeEach, describe, test } from "node:test";
import { AlmacenLocal, CLAVE_CATALOGO } from "../../site/src/adapters/persistencia/almacen-local.js";
import { AlmacenamientoEnMemoria } from "../../site/src/adapters/persistencia/almacenamiento-en-memoria.js";
import { RepositorioAutoresLocal } from "../../site/src/adapters/persistencia/repositorio-autores-local.js";
import { RepositorioLibrosLocal } from "../../site/src/adapters/persistencia/repositorio-libros-local.js";
import { Autor } from "../../site/src/domain/autor.js";
import { Libro } from "../../site/src/domain/libro.js";

describe("Repositorios en el almacenamiento del navegador", () => {
    let storage;
    let almacen;
    let libros;
    let autores;

    beforeEach(() => {
        storage = new AlmacenamientoEnMemoria();
        almacen = new AlmacenLocal(storage);
        libros = new RepositorioLibrosLocal(almacen);
        autores = new RepositorioAutoresLocal(almacen);
    });

    async function guardarFrankenstein() {
        const shelley = await autores.guardar(new Autor({ nombre: "Shelley", anioNacimiento: 1797, anioMuerte: 1851 }));
        return libros.guardar(new Libro({ titulo: "Frankenstein", idioma: "en", numeroDescargas: 10, autor: shelley }));
    }

    test("empieza vacío", async () => {
        assert.deepEqual(await libros.listar(), []);
        assert.deepEqual(await autores.listar(), []);
    });

    test("asigna ids y recupera el libro con su autor", async () => {
        const guardado = await guardarFrankenstein();

        const [libro] = await libros.listar();
        assert.equal(libro.id, guardado.id);
        assert.ok(libro instanceof Libro);
        assert.ok(libro.autor instanceof Autor);
        assert.equal(libro.autor.nombre, "Shelley");
        assert.equal(libro.autor.estabaVivoEn(1818), true);
        assert.notEqual(libro.id, libro.autor.id);
    });

    test("busca por título y por nombre sin importar mayúsculas", async () => {
        await guardarFrankenstein();

        assert.equal(await libros.existePorTitulo("FRANKENSTEIN"), true);
        assert.equal(await libros.existePorTitulo("Drácula"), false);
        assert.equal((await libros.buscarPorTitulo("frankenstein")).titulo, "Frankenstein");
        assert.equal(await libros.buscarPorTitulo("Drácula"), null);
        assert.equal((await autores.buscarPorNombre("shelley")).nombre, "Shelley");
        assert.equal(await autores.buscarPorNombre("Cervantes"), null);
    });

    test("filtra por idioma", async () => {
        await guardarFrankenstein();
        await libros.guardar(new Libro({ titulo: "Don Quijote", idioma: "es" }));

        assert.deepEqual((await libros.buscarPorIdioma("es")).map((libro) => libro.titulo), ["Don Quijote"]);
        assert.deepEqual(await libros.buscarPorIdioma("fr"), []);
    });

    test("exige que el autor esté guardado antes que el libro", async () => {
        const autorSinGuardar = new Autor({ nombre: "Shelley" });
        await assert.rejects(libros.guardar(new Libro({ titulo: "Frankenstein", idioma: "en", autor: autorSinGuardar })),
            /El autor debe guardarse antes/);
        await assert.rejects(libros.guardar(new Libro({ titulo: "Frankenstein", idioma: "en", autor: autorSinGuardar.conId(99) })),
            /No existe el autor 99/);
        assert.deepEqual(await libros.listar(), []);
    });

    test("conserva los datos entre instancias que comparten el almacenamiento", async () => {
        await guardarFrankenstein();

        const otroAlmacen = new AlmacenLocal(storage);
        assert.equal((await new RepositorioLibrosLocal(otroAlmacen).listar()).length, 1);
        assert.equal((await new RepositorioAutoresLocal(otroAlmacen).listar()).length, 1);
    });

    test("borra todos los datos", async () => {
        await guardarFrankenstein();

        almacen.borrar();

        assert.equal(storage.getItem(CLAVE_CATALOGO), null);
        assert.deepEqual(await libros.listar(), []);
        assert.deepEqual(await autores.listar(), []);
    });

    test("ignora datos guardados que no se pueden leer", async () => {
        storage.setItem(CLAVE_CATALOGO, "{no es json");
        assert.deepEqual(await libros.listar(), []);

        storage.setItem(CLAVE_CATALOGO, JSON.stringify({ libros: "x" }));
        assert.deepEqual(await autores.listar(), []);
    });
});
