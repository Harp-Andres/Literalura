import { readFile } from "node:fs/promises";
import { createServer } from "node:http";
import { extname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const RAIZ = fileURLToPath(new URL("../site/", import.meta.url));
const PREFIJO = "/Literalura/";
const PUERTO = Number(process.env.PORT ?? 4173);
const TIPOS = {
    ".html": "text/html; charset=utf-8",
    ".css": "text/css; charset=utf-8",
    ".js": "text/javascript; charset=utf-8"
};

const servidor = createServer(async (peticion, respuesta) => {
    const { pathname } = new URL(peticion.url, "http://localhost");
    if (!pathname.startsWith(PREFIJO)) {
        respuesta.writeHead(302, { Location: PREFIJO }).end();
        return;
    }

    let ruta;
    try {
        const relativa = decodeURIComponent(pathname.slice(PREFIJO.length));
        ruta = resolve(RAIZ, relativa === "" || relativa.endsWith("/") ? `${relativa}index.html` : relativa);
    } catch {
        respuesta.writeHead(400).end();
        return;
    }
    if (!ruta.startsWith(RAIZ)) {
        respuesta.writeHead(403).end();
        return;
    }

    try {
        const contenido = await readFile(ruta);
        respuesta.writeHead(200, { "Content-Type": TIPOS[extname(ruta)] ?? "application/octet-stream" });
        respuesta.end(contenido);
    } catch {
        respuesta.writeHead(404, { "Content-Type": "text/plain; charset=utf-8" }).end("No encontrado");
    }
});

servidor.listen(PUERTO, "127.0.0.1", () => {
    console.log(`Sitio disponible en http://127.0.0.1:${PUERTO}${PREFIJO}`);
});
