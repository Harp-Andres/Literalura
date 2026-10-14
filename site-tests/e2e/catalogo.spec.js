import { readFile } from "node:fs/promises";
import { expect, test } from "@playwright/test";

const fixtures = {
    frankenstein: await leerFixture("frankenstein.json"),
    quijote: await leerFixture("quijote.json")
};

async function leerFixture(nombre) {
    return readFile(new URL(`../fixtures/${nombre}`, import.meta.url), "utf8");
}

function esGutendex(url) {
    return url.hostname === "gutendex.com";
}

async function simularGutendex(page) {
    const consultas = [];
    await page.route(esGutendex, (route) => {
        const busqueda = new URL(route.request().url()).searchParams.get("search") ?? "";
        consultas.push(busqueda);
        return route.fulfill({
            status: 200,
            contentType: "application/json",
            headers: { "access-control-allow-origin": "*" },
            body: fixtures[busqueda.toLowerCase()] ?? JSON.stringify({ count: 0, results: [] })
        });
    });
    return consultas;
}

async function importar(page, titulo) {
    await page.getByLabel("Título", { exact: true }).fill(titulo);
    await page.getByRole("button", { name: "Buscar y guardar" }).click();
}

const libros = (page) => page.getByRole("list", { name: "Libros" }).getByRole("listitem");
const autores = (page) => page.getByRole("list", { name: "Autores" }).getByRole("listitem");
const estadoImportacion = (page) => page.locator("#estado-importacion");

test.beforeEach(async ({ page }) => {
    await page.goto("./");
});

test("muestra estados vacíos y el aviso de demo local", async ({ page }) => {
    await expect(libros(page)).toHaveText(["Todavía no hay libros guardados. Importa un título para empezar."]);
    await expect(autores(page)).toHaveText(["Todavía no hay autores guardados."]);
    await expect(page.getByRole("link", { name: "repositorio en GitHub" }))
        .toHaveAttribute("href", "https://github.com/Harp-Andres/Literalura");
});

test("importa un título y lista el libro y su autor", async ({ page }) => {
    const consultas = await simularGutendex(page);

    await importar(page, "Frankenstein");

    await expect(estadoImportacion(page)).toHaveText("Libros guardados: 1. Omitidos por estar repetidos: 0.");
    expect(consultas).toEqual(["Frankenstein"]);
    await expect(libros(page)).toHaveCount(1);
    await expect(libros(page).first()).toContainText("Frankenstein; Or, The Modern Prometheus");
    await expect(libros(page).first()).toContainText("Shelley, Mary Wollstonecraft · Idioma: en · 15.000 descargas");
    await expect(autores(page)).toHaveCount(1);
    await expect(autores(page).first()).toContainText("Shelley, Mary Wollstonecraft");
    await expect(autores(page).first()).toContainText("1797 – 1851");
});

test("cuenta como omitido un título que ya estaba guardado", async ({ page }) => {
    await simularGutendex(page);

    await importar(page, "Frankenstein");
    await expect(estadoImportacion(page)).toHaveText("Libros guardados: 1. Omitidos por estar repetidos: 0.");
    await importar(page, "Frankenstein");

    await expect(estadoImportacion(page)).toHaveText("Libros guardados: 0. Omitidos por estar repetidos: 1.");
    await expect(libros(page)).toHaveCount(1);
    await expect(autores(page)).toHaveCount(1);
});

test("avisa cuando Gutendex no encuentra el título", async ({ page }) => {
    await simularGutendex(page);

    await importar(page, "Título inexistente");

    await expect(estadoImportacion(page)).toHaveText("Gutendex no encontró libros con ese título.");
});

test("muestra un error claro si Gutendex falla", async ({ page }) => {
    await page.route(esGutendex, (route) => route.fulfill({
        status: 503,
        headers: { "access-control-allow-origin": "*" },
        body: "Service Unavailable"
    }));

    await importar(page, "Frankenstein");

    await expect(estadoImportacion(page))
        .toHaveText("Gutendex no está disponible en este momento (HTTP 503). Inténtalo más tarde.");
    await expect(estadoImportacion(page)).toHaveClass(/error/);
});

test("filtra los libros por idioma", async ({ page }) => {
    await simularGutendex(page);
    await importar(page, "Frankenstein");
    await expect(libros(page)).toHaveCount(1);
    await importar(page, "Quijote");
    await expect(libros(page)).toHaveCount(2);

    await page.getByLabel("Idioma").fill("ES");
    await page.getByRole("button", { name: "Filtrar" }).first().click();
    await expect(libros(page)).toHaveCount(1);
    await expect(libros(page).first()).toContainText("Don Quijote");

    await page.getByLabel("Idioma").fill("fr");
    await page.getByRole("button", { name: "Filtrar" }).first().click();
    await expect(libros(page)).toHaveText(["No hay libros guardados en el idioma «fr»."]);

    await page.getByRole("button", { name: "Ver todos" }).first().click();
    await expect(libros(page)).toHaveCount(2);
    await expect(page.getByLabel("Idioma")).toHaveValue("");
});

test("rechaza un idioma que no es un código de dos letras", async ({ page }) => {
    await page.getByLabel("Idioma").fill("e1");
    await page.getByRole("button", { name: "Filtrar" }).first().click();

    await expect(page.locator("#estado-libros")).toHaveText("El idioma debe ser un código de dos letras.");
    await expect(page.locator("#estado-libros")).toHaveClass(/error/);
});

test("busca un libro por su título exacto", async ({ page }) => {
    await simularGutendex(page);
    await importar(page, "Frankenstein");
    await expect(libros(page)).toHaveCount(1);

    await page.getByLabel("Título exacto").fill("frankenstein; or, the modern prometheus");
    await page.getByRole("button", { name: "Buscar", exact: true }).click();
    await expect(libros(page).first()).toContainText("Frankenstein; Or, The Modern Prometheus");

    await page.getByLabel("Título exacto").fill("Drácula");
    await page.getByRole("button", { name: "Buscar", exact: true }).click();
    await expect(libros(page)).toHaveText(["No hay un libro guardado con el título «Drácula»."]);
});

test("filtra los autores vivos en un año", async ({ page }) => {
    await simularGutendex(page);
    await importar(page, "Frankenstein");
    await expect(autores(page)).toHaveCount(1);

    await page.getByLabel("Vivos en").fill("1818");
    await page.getByRole("button", { name: "Filtrar" }).last().click();
    await expect(autores(page)).toHaveCount(1);
    await expect(autores(page).first()).toContainText("Shelley, Mary Wollstonecraft");

    await page.getByLabel("Vivos en").fill("1700");
    await page.getByRole("button", { name: "Filtrar" }).last().click();
    await expect(autores(page)).toHaveText(["Ningún autor guardado estaba vivo en 1700."]);
});

test("conserva los datos al recargar la página", async ({ page }) => {
    await simularGutendex(page);
    await importar(page, "Frankenstein");
    await expect(libros(page)).toHaveCount(1);
    await expect(libros(page).first()).toContainText("Frankenstein");

    await page.reload();

    await expect(libros(page).first()).toContainText("Frankenstein; Or, The Modern Prometheus");
    await expect(autores(page).first()).toContainText("Shelley, Mary Wollstonecraft");
});

test("borra los datos locales tras confirmar", async ({ page }) => {
    await simularGutendex(page);
    await importar(page, "Frankenstein");
    await expect(libros(page).first()).toContainText("Frankenstein");

    page.once("dialog", (dialogo) => dialogo.dismiss());
    await page.getByRole("button", { name: "Borrar datos locales" }).click();
    await expect(libros(page).first()).toContainText("Frankenstein");

    page.once("dialog", (dialogo) => dialogo.accept());
    await page.getByRole("button", { name: "Borrar datos locales" }).click();
    await expect(page.locator("#estado-datos")).toHaveText("Se borraron los libros y autores guardados en este navegador.");
    await expect(libros(page)).toHaveText(["Todavía no hay libros guardados. Importa un título para empezar."]);
    await expect(autores(page)).toHaveText(["Todavía no hay autores guardados."]);

    await page.reload();
    await expect(libros(page)).toHaveText(["Todavía no hay libros guardados. Importa un título para empezar."]);
});

test("muestra los títulos de Gutendex como texto y no como HTML", async ({ page }) => {
    await page.route(esGutendex, (route) => route.fulfill({
        status: 200,
        contentType: "application/json",
        headers: { "access-control-allow-origin": "*" },
        body: JSON.stringify({
            count: 1,
            results: [{ title: "<img src=x onerror=alert(1)>", authors: [], languages: ["en"], download_count: 1 }]
        })
    }));

    await importar(page, "malicioso");

    await expect(libros(page).first()).toContainText("<img src=x onerror=alert(1)>");
    await expect(page.locator("#libros img")).toHaveCount(0);
});
