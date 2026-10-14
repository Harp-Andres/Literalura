import { CatalogoRemotoError, ReglaDeNegocioError } from "../../domain/errores.js";

const SIN_LIBROS = "Todavía no hay libros guardados. Importa un título para empezar.";
const SIN_AUTORES = "Todavía no hay autores guardados.";

export class ControladorCatalogo {
    #documento;
    #vista;
    #importar;
    #consultar;
    #borrarDatos;
    #confirmar;

    constructor({ documento, vista, importar, consultar, borrarDatos, confirmar }) {
        this.#documento = documento;
        this.#vista = vista;
        this.#importar = importar;
        this.#consultar = consultar;
        this.#borrarDatos = borrarDatos;
        this.#confirmar = confirmar;
    }

    async iniciar() {
        this.#alEnviar("#form-importar", "importacion", (formulario) => this.#importarLibro(formulario));
        this.#alEnviar("#form-idioma", "libros", (formulario) => this.#filtrarPorIdioma(formulario));
        this.#alEnviar("#form-titulo", "libros", (formulario) => this.#buscarPorTitulo(formulario));
        this.#alEnviar("#form-anio", "autores", (formulario) => this.#filtrarPorAnio(formulario));
        this.#alPulsar("#ver-libros", "libros", () => this.#verTodosLosLibros());
        this.#alPulsar("#ver-autores", "autores", () => this.#verTodosLosAutores());
        this.#alPulsar("#borrar-datos", "datos", () => this.#borrarCatalogo());

        await this.#ejecutar("libros", () => this.#mostrarLibros());
        await this.#ejecutar("autores", () => this.#mostrarAutores());
    }

    async #importarLibro(formulario) {
        this.#vista.marcarOcupado(formulario, true);
        this.#vista.mostrarMensaje("importacion",
            "Consultando Gutendex. La primera búsqueda de un título puede tardar hasta un minuto.");
        try {
            const resultado = await this.#importar.importar(formulario.elements.titulo.value);
            this.#vista.mostrarMensaje("importacion", resumenImportacion(resultado));
            await this.#mostrarLibros();
            await this.#mostrarAutores();
        } finally {
            this.#vista.marcarOcupado(formulario, false);
        }
    }

    async #filtrarPorIdioma(formulario) {
        const idioma = formulario.elements.idioma.value;
        if (idioma.trim() === "") {
            await this.#mostrarLibros();
            return;
        }
        const libros = await this.#consultar.librosPorIdioma(idioma);
        this.#vista.mostrarLibros(libros, `No hay libros guardados en el idioma «${idioma.trim().toLowerCase()}».`);
    }

    async #buscarPorTitulo(formulario) {
        const titulo = formulario.elements.titulo.value;
        const libro = await this.#consultar.libroPorTitulo(titulo);
        this.#vista.mostrarLibros(libro ? [libro] : [], `No hay un libro guardado con el título «${titulo.trim()}».`);
    }

    async #filtrarPorAnio(formulario) {
        const valor = formulario.elements.anio.value.trim();
        if (valor === "") {
            await this.#mostrarAutores();
            return;
        }
        const anio = Number(valor);
        const autores = await this.#consultar.autoresVivosEn(anio);
        this.#vista.mostrarAutores(autores, `Ningún autor guardado estaba vivo en ${anio}.`);
    }

    async #verTodosLosLibros() {
        this.#documento.querySelector("#form-idioma").reset();
        this.#documento.querySelector("#form-titulo").reset();
        await this.#mostrarLibros();
    }

    async #verTodosLosAutores() {
        this.#documento.querySelector("#form-anio").reset();
        await this.#mostrarAutores();
    }

    async #borrarCatalogo() {
        if (!this.#confirmar("¿Borrar todos los libros y autores guardados en este navegador?")) {
            return;
        }
        this.#borrarDatos();
        this.#vista.limpiarMensajes();
        await this.#mostrarLibros();
        await this.#mostrarAutores();
        this.#vista.mostrarMensaje("datos", "Se borraron los libros y autores guardados en este navegador.");
    }

    async #mostrarLibros() {
        this.#vista.mostrarLibros(await this.#consultar.listarLibros(), SIN_LIBROS);
    }

    async #mostrarAutores() {
        this.#vista.mostrarAutores(await this.#consultar.listarAutores(), SIN_AUTORES);
    }

    #alEnviar(selector, zona, accion) {
        this.#documento.querySelector(selector).addEventListener("submit", (evento) => {
            evento.preventDefault();
            this.#ejecutar(zona, () => accion(evento.currentTarget));
        });
    }

    #alPulsar(selector, zona, accion) {
        this.#documento.querySelector(selector).addEventListener("click", () => this.#ejecutar(zona, accion));
    }

    async #ejecutar(zona, accion) {
        this.#vista.mostrarMensaje(zona, "");
        try {
            await accion();
        } catch (error) {
            this.#vista.mostrarError(zona, mensajeDeError(error));
        }
    }
}

function resumenImportacion({ guardados, omitidos }) {
    if (guardados === 0 && omitidos === 0) {
        return "Gutendex no encontró libros con ese título.";
    }
    return `Libros guardados: ${guardados}. Omitidos por estar repetidos: ${omitidos}.`;
}

function mensajeDeError(error) {
    if (error instanceof ReglaDeNegocioError || error instanceof CatalogoRemotoError) {
        return error.message;
    }
    if (error?.name === "QuotaExceededError") {
        return "El navegador no tiene espacio para guardar más datos. Borra los datos locales e inténtalo de nuevo.";
    }
    console.error(error);
    return "Ocurrió un error inesperado. Si se repite, usa «Borrar datos locales».";
}
