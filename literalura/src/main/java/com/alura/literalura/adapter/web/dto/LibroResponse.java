package com.alura.literalura.adapter.web.dto;

import com.alura.literalura.domain.model.Libro;

public record LibroResponse(Long id, String titulo, String idioma, int numeroDescargas, String autor) {

    public static LibroResponse desde(Libro libro) {
        String nombreAutor = libro.autor() == null ? null : libro.autor().nombre();
        return new LibroResponse(libro.id(), libro.titulo(), libro.idioma(), libro.numeroDescargas(), nombreAutor);
    }
}
