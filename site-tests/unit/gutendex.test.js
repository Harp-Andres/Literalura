import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import { describe, test } from "node:test";
import { ClienteGutendex } from "../../site/src/adapters/gutendex/cliente-gutendex.js";
import { interpretar } from "../../site/src/adapters/gutendex/gutendex-mapper.js";
import { CatalogoRemotoError } from "../../site/src/domain/errores.js";

const frankenstein = JSON.parse(await readFile(new URL("../fixtures/frankenstein.json", import.meta.url), "utf8"));

function pagina(...results) {
    return { count: results.length, results };
}

describe("interpretar", () => {
    test("convierte la respuesta de Gutendex en libros con su autor", () => {
        const [libro] = interpretar(frankenstein);

        assert.equal(libro.titulo, "Frankenstein; Or, The Modern Prometheus");
        assert.equal(libro.idioma, "en");
        assert.equal(libro.numeroDescargas, 15000);
        assert.equal(libro.autor.nombre, "Shelley, Mary Wollstonecraft");
        assert.equal(libro.autor.anioNacimiento, 1797);
        assert.equal(libro.autor.anioMuerte, 1851);
    });

    test("devuelve una lista vacía si no hay resultados", () => {
        assert.deepEqual(interpretar(null), []);
        assert.deepEqual(interpretar({}), []);
        assert.deepEqual(interpretar({ results: null }), []);
    });

    test("omite los resultados sin título", () => {
        const libros = interpretar(pagina(null, { title: "  ", languages: ["en"] }, { languages: ["en"] },
            { title: "Válido", languages: ["en"] }));
        assert.deepEqual(libros.map((libro) => libro.titulo), ["Válido"]);
    });

    test("usa el primer idioma o und si no hay ninguno", () => {
        const libros = interpretar(pagina(
            { title: "Bilingüe", languages: ["FR", "en"] },
            { title: "Sin idioma", languages: [] },
            { title: "Idioma nulo" }));
        assert.deepEqual(libros.map((libro) => libro.idioma), ["fr", "und", "und"]);
    });

    test("omite el resultado si el primer idioma no es válido", () => {
        assert.deepEqual(interpretar(pagina({ title: "Raro", languages: [""] })), []);
    });

    test("trata las descargas nulas o negativas como cero", () => {
        const libros = interpretar(pagina(
            { title: "Nulo", languages: ["en"], download_count: null },
            { title: "Negativo", languages: ["en"], download_count: -5 }));
        assert.deepEqual(libros.map((libro) => libro.numeroDescargas), [0, 0]);
    });

    test("usa solo el primer autor y lo ignora si no tiene nombre", () => {
        const libros = interpretar(pagina(
            { title: "Dos autores", languages: ["en"], authors: [{ name: "Primero" }, { name: "Segundo" }] },
            { title: "Primero sin nombre", languages: ["en"], authors: [{ name: " " }, { name: "Segundo" }] },
            { title: "Sin autores", languages: ["en"], authors: [] }));
        assert.deepEqual(libros.map((libro) => libro.autor?.nombre ?? null), ["Primero", null, null]);
    });
});

describe("ClienteGutendex", () => {
    function respuesta(cuerpo, status = 200) {
        return new Response(typeof cuerpo === "string" ? cuerpo : JSON.stringify(cuerpo), { status });
    }

    test("consulta la búsqueda de Gutendex con el título codificado", async () => {
        const urls = [];
        const cliente = new ClienteGutendex({
            fetch: async (url) => {
                urls.push(String(url));
                return respuesta(frankenstein);
            }
        });

        const libros = await cliente.buscarPorTitulo("Don Quijote & Sancho");

        assert.deepEqual(urls, ["https://gutendex.com/books/?search=Don+Quijote+%26+Sancho"]);
        assert.equal(libros.length, 1);
    });

    test("informa un error si Gutendex no responde 200", async () => {
        const cliente = new ClienteGutendex({ fetch: async () => respuesta({}, 503) });
        await assert.rejects(cliente.buscarPorTitulo("x"), (error) =>
            error instanceof CatalogoRemotoError && error.message.includes("HTTP 503"));
    });

    test("informa un error si no hay conexión", async () => {
        const cliente = new ClienteGutendex({ fetch: async () => { throw new TypeError("Failed to fetch"); } });
        await assert.rejects(cliente.buscarPorTitulo("x"), /No se pudo conectar con Gutendex/);
    });

    test("informa un error si la respuesta no es JSON", async () => {
        const cliente = new ClienteGutendex({ fetch: async () => respuesta("<html>") });
        await assert.rejects(cliente.buscarPorTitulo("x"), /no se pudo leer/);
    });

    test("cancela la consulta si Gutendex tarda demasiado", async () => {
        const cliente = new ClienteGutendex({
            tiempoLimiteMs: 20,
            fetch: (url, { signal }) => new Promise((resolver, rechazar) => {
                signal.addEventListener("abort", () => rechazar(signal.reason));
            })
        });
        await assert.rejects(cliente.buscarPorTitulo("x"), (error) =>
            error instanceof CatalogoRemotoError && error.message.includes("tardó demasiado"));
    });
});
