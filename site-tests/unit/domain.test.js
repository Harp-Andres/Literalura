import assert from "node:assert/strict";
import { describe, test } from "node:test";
import { Autor } from "../../site/src/domain/autor.js";
import { DatoInvalidoError } from "../../site/src/domain/errores.js";
import { Libro } from "../../site/src/domain/libro.js";

describe("Autor", () => {
    test("recorta el nombre", () => {
        assert.equal(new Autor({ nombre: "  Mary Shelley  " }).nombre, "Mary Shelley");
    });

    test("exige un nombre", () => {
        for (const nombre of [undefined, null, "", "   "]) {
            assert.throws(() => new Autor({ nombre }), DatoInvalidoError);
        }
    });

    test("no estaba vivo sin año de nacimiento o antes de nacer", () => {
        assert.equal(new Autor({ nombre: "Anónimo", anioMuerte: 1900 }).estabaVivoEn(1850), false);
        assert.equal(new Autor({ nombre: "Shelley", anioNacimiento: 1797, anioMuerte: 1851 }).estabaVivoEn(1796), false);
    });

    test("estaba vivo entre su nacimiento y su muerte, ambos incluidos", () => {
        const shelley = new Autor({ nombre: "Shelley", anioNacimiento: 1797, anioMuerte: 1851 });
        assert.equal(shelley.estabaVivoEn(1797), true);
        assert.equal(shelley.estabaVivoEn(1818), true);
        assert.equal(shelley.estabaVivoEn(1851), true);
        assert.equal(shelley.estabaVivoEn(1852), false);
    });

    test("sigue vivo si no tiene año de muerte", () => {
        assert.equal(new Autor({ nombre: "Autora", anioNacimiento: 1950 }).estabaVivoEn(2026), true);
    });

    test("es inmutable y conId devuelve una copia", () => {
        const autor = new Autor({ nombre: "Shelley", anioNacimiento: 1797 });
        const conId = autor.conId(7);
        assert.equal(autor.id, null);
        assert.equal(conId.id, 7);
        assert.equal(conId.anioNacimiento, 1797);
        assert.throws(() => { autor.nombre = "Otro"; }, TypeError);
    });
});

describe("Libro", () => {
    test("recorta el título y normaliza el idioma", () => {
        const libro = new Libro({ titulo: "  Frankenstein ", idioma: " EN ", numeroDescargas: 3 });
        assert.equal(libro.titulo, "Frankenstein");
        assert.equal(libro.idioma, "en");
        assert.equal(libro.numeroDescargas, 3);
        assert.equal(libro.autor, null);
    });

    test("exige título e idioma", () => {
        assert.throws(() => new Libro({ titulo: " ", idioma: "en" }), DatoInvalidoError);
        assert.throws(() => new Libro({ titulo: "Frankenstein", idioma: "" }), DatoInvalidoError);
        assert.throws(() => new Libro({ titulo: "Frankenstein", idioma: null }), DatoInvalidoError);
    });

    test("rechaza descargas negativas o no enteras", () => {
        assert.throws(() => new Libro({ titulo: "Frankenstein", idioma: "en", numeroDescargas: -1 }),
            /no pueden ser negativas/);
        assert.throws(() => new Libro({ titulo: "Frankenstein", idioma: "en", numeroDescargas: 1.5 }),
            DatoInvalidoError);
    });

    test("solo acepta un Autor como autor", () => {
        assert.throws(() => new Libro({ titulo: "Frankenstein", idioma: "en", autor: { nombre: "Shelley" } }),
            DatoInvalidoError);
    });

    test("conAutor y conId devuelven copias", () => {
        const libro = new Libro({ titulo: "Frankenstein", idioma: "en" });
        const autor = new Autor({ nombre: "Shelley" });
        const completo = libro.conAutor(autor).conId(4);
        assert.equal(libro.autor, null);
        assert.equal(completo.autor, autor);
        assert.equal(completo.id, 4);
        assert.equal(completo.titulo, "Frankenstein");
    });
});
