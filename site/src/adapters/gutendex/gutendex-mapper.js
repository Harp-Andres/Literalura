import { Autor } from "../../domain/autor.js";
import { DatoInvalidoError } from "../../domain/errores.js";
import { Libro } from "../../domain/libro.js";
import { tieneContenido } from "../../domain/texto.js";

const IDIOMA_DESCONOCIDO = "und";

export function interpretar(pagina) {
    if (!pagina || !Array.isArray(pagina.results)) {
        return [];
    }
    return pagina.results.map(aLibro).filter((libro) => libro !== null);
}

function aLibro(book) {
    if (!book || !tieneContenido(book.title)) {
        return null;
    }
    const idioma = Array.isArray(book.languages) && book.languages.length > 0
        ? book.languages[0]
        : IDIOMA_DESCONOCIDO;
    const descargas = Number.isInteger(book.download_count) && book.download_count > 0
        ? book.download_count
        : 0;
    try {
        return new Libro({
            titulo: book.title,
            idioma,
            numeroDescargas: descargas,
            autor: autorPrincipal(book.authors)
        });
    } catch (error) {
        if (error instanceof DatoInvalidoError) {
            return null;
        }
        throw error;
    }
}

function autorPrincipal(authors) {
    const autor = Array.isArray(authors) ? authors[0] : null;
    if (!autor || !tieneContenido(autor.name)) {
        return null;
    }
    return new Autor({
        nombre: autor.name,
        anioNacimiento: autor.birth_year,
        anioMuerte: autor.death_year
    });
}
