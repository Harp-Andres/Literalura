import { Libro } from "../../domain/libro.js";
import { mismoTexto, siguienteId } from "./almacen-local.js";
import { aAutor } from "./repositorio-autores-local.js";

export class RepositorioLibrosLocal {
    #almacen;

    constructor(almacen) {
        this.#almacen = almacen;
    }

    async listar() {
        return this.#buscar(() => true);
    }

    async buscarPorIdioma(idioma) {
        return this.#buscar((libro) => libro.idioma === idioma);
    }

    async buscarPorTitulo(titulo) {
        const [libro] = this.#buscar((registro) => mismoTexto(registro.titulo, titulo));
        return libro ?? null;
    }

    async existePorTitulo(titulo) {
        return this.#almacen.leer().libros.some((libro) => mismoTexto(libro.titulo, titulo));
    }

    async guardar(libro) {
        const autorId = libro.autor?.id ?? null;
        if (libro.autor !== null && autorId === null) {
            throw new Error("El autor debe guardarse antes que el libro.");
        }
        return this.#almacen.modificar((estado) => {
            if (autorId !== null && !estado.autores.some((autor) => autor.id === autorId)) {
                throw new Error(`No existe el autor ${autorId}.`);
            }
            const id = siguienteId(estado);
            estado.libros.push({
                id,
                titulo: libro.titulo,
                idioma: libro.idioma,
                numeroDescargas: libro.numeroDescargas,
                autorId
            });
            return libro.conId(id);
        });
    }

    #buscar(criterio) {
        const { libros, autores } = this.#almacen.leer();
        const autoresPorId = new Map(autores.map((autor) => [autor.id, autor]));
        return libros
            .filter(criterio)
            .map((registro) => aLibro(registro, autoresPorId.get(registro.autorId)));
    }
}

function aLibro({ id, titulo, idioma, numeroDescargas }, registroAutor) {
    return new Libro({
        id,
        titulo,
        idioma,
        numeroDescargas,
        autor: registroAutor ? aAutor(registroAutor) : null
    });
}
