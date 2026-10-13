const estado = document.querySelector("#estado");
const listaLibros = document.querySelector("#libros");
const listaAutores = document.querySelector("#autores");

async function leer(url, opciones) {
    const respuesta = await fetch(url, opciones);
    const cuerpo = await respuesta.json().catch(() => ({}));
    if (!respuesta.ok) {
        throw new Error(cuerpo.mensaje || "No se pudo completar la operacion.");
    }
    return cuerpo;
}

function pintarLibros(libros) {
    listaLibros.replaceChildren();
    if (!libros.length) {
        listaLibros.append(item("No hay libros para mostrar."));
        return;
    }
    for (const libro of libros) {
        listaLibros.append(item(`${libro.titulo} · ${libro.idioma} · ${libro.numeroDescargas} descargas · ${libro.autor || "sin autor"}`));
    }
}

function pintarAutores(autores) {
    listaAutores.replaceChildren();
    if (!autores.length) {
        listaAutores.append(item("No hay autores para mostrar."));
        return;
    }
    for (const autor of autores) {
        listaAutores.append(item(`${autor.nombre} · ${autor.anioNacimiento ?? "?"} – ${autor.anioMuerte ?? "?"}`));
    }
}

function item(texto) {
    const li = document.createElement("li");
    li.textContent = texto;
    return li;
}

async function cargarLibros(query) {
    pintarLibros(await leer("/api/libros" + (query || "")));
}

async function cargarAutores(query) {
    pintarAutores(await leer("/api/autores" + (query || "")));
}

document.querySelector("#form-importar").addEventListener("submit", async (event) => {
    event.preventDefault();
    estado.textContent = "Consultando...";
    try {
        const resultado = await leer("/api/libros/importaciones", {
            method: "POST",
            headers: { "Content-Type": "application/json", "Accept": "application/json" },
            body: JSON.stringify({ titulo: document.querySelector("#titulo").value })
        });
        estado.textContent = `Guardados: ${resultado.guardados}. Omitidos: ${resultado.omitidos}.`;
        await cargarLibros();
        await cargarAutores();
    } catch (error) {
        estado.textContent = error.message;
    }
});

document.querySelector("#form-idioma").addEventListener("submit", async (event) => {
    event.preventDefault();
    const idioma = document.querySelector("#idioma").value.trim();
    try {
        await cargarLibros(idioma ? `?idioma=${encodeURIComponent(idioma)}` : "");
        estado.textContent = "";
    } catch (error) {
        estado.textContent = error.message;
    }
});

document.querySelector("#ver-libros").addEventListener("click", () => cargarLibros().catch(mostrarError));
document.querySelector("#ver-autores").addEventListener("click", () => cargarAutores().catch(mostrarError));

document.querySelector("#form-anio").addEventListener("submit", async (event) => {
    event.preventDefault();
    const anio = document.querySelector("#anio").value;
    try {
        await cargarAutores(anio ? `?vivosEn=${encodeURIComponent(anio)}` : "");
        estado.textContent = "";
    } catch (error) {
        estado.textContent = error.message;
    }
});

function mostrarError(error) {
    estado.textContent = error.message;
}

cargarLibros().catch(mostrarError);
cargarAutores().catch(mostrarError);
