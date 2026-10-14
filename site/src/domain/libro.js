import { Autor } from "./autor.js";
import { DatoInvalidoError } from "./errores.js";
import { tieneContenido } from "./texto.js";

export class Libro {
    constructor({ id = null, titulo, idioma, numeroDescargas = 0, autor = null }) {
        if (!tieneContenido(titulo)) {
            throw new DatoInvalidoError("El título es obligatorio.");
        }
        if (!tieneContenido(idioma)) {
            throw new DatoInvalidoError("El idioma es obligatorio.");
        }
        if (!Number.isInteger(numeroDescargas)) {
            throw new DatoInvalidoError("Las descargas deben ser un número entero.");
        }
        if (numeroDescargas < 0) {
            throw new DatoInvalidoError("Las descargas no pueden ser negativas.");
        }
        if (autor !== null && !(autor instanceof Autor)) {
            throw new DatoInvalidoError("El autor del libro no es válido.");
        }
        this.id = id;
        this.titulo = titulo.trim();
        this.idioma = idioma.trim().toLowerCase();
        this.numeroDescargas = numeroDescargas;
        this.autor = autor;
        Object.freeze(this);
    }

    conId(nuevoId) {
        return new Libro({ ...this, id: nuevoId });
    }

    conAutor(nuevoAutor) {
        return new Libro({ ...this, autor: nuevoAutor });
    }
}
