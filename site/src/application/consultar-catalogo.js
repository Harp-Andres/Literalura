import { AnioInvalidoError, IdiomaInvalidoError, TituloInvalidoError } from "../domain/errores.js";
import { tieneContenido } from "../domain/texto.js";

const CODIGO_IDIOMA = /^[a-z]{2}$/i;

export class ConsultarCatalogo {
    #libros;
    #autores;

    constructor({ libros, autores }) {
        this.#libros = libros;
        this.#autores = autores;
    }

    async listarLibros() {
        return this.#libros.listar();
    }

    async librosPorIdioma(idioma) {
        return this.#libros.buscarPorIdioma(normalizarIdioma(idioma));
    }

    async libroPorTitulo(titulo) {
        if (!tieneContenido(titulo)) {
            throw new TituloInvalidoError("El título no puede estar vacío.");
        }
        return this.#libros.buscarPorTitulo(titulo.trim());
    }

    async listarAutores() {
        return this.#autores.listar();
    }

    async autoresVivosEn(anio) {
        if (!Number.isInteger(anio)) {
            throw new AnioInvalidoError("El año debe ser un número entero.");
        }
        const autores = await this.#autores.listar();
        return autores.filter((autor) => autor.estabaVivoEn(anio));
    }
}

function normalizarIdioma(idioma) {
    if (typeof idioma !== "string" || !CODIGO_IDIOMA.test(idioma.trim())) {
        throw new IdiomaInvalidoError("El idioma debe ser un código de dos letras.");
    }
    return idioma.trim().toLowerCase();
}
