import { TituloInvalidoError } from "../domain/errores.js";
import { tieneContenido } from "../domain/texto.js";

export class ImportarLibro {
    #remoto;
    #libros;
    #autores;

    constructor({ remoto, libros, autores }) {
        this.#remoto = remoto;
        this.#libros = libros;
        this.#autores = autores;
    }

    async importar(titulo) {
        if (!tieneContenido(titulo)) {
            throw new TituloInvalidoError("El título no puede estar vacío.");
        }

        const encontrados = await this.#remoto.buscarPorTitulo(titulo.trim());
        const persistidos = [];
        let omitidos = 0;

        for (const candidato of encontrados) {
            if (await this.#libros.existePorTitulo(candidato.titulo)) {
                omitidos++;
                continue;
            }
            const autor = await this.#persistirAutor(candidato.autor);
            persistidos.push(await this.#libros.guardar(candidato.conAutor(autor)));
        }

        return Object.freeze({
            guardados: persistidos.length,
            omitidos,
            libros: Object.freeze(persistidos)
        });
    }

    async #persistirAutor(autor) {
        if (autor === null) {
            return null;
        }
        return (await this.#autores.buscarPorNombre(autor.nombre)) ?? this.#autores.guardar(autor);
    }
}
