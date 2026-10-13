package com.alura.literalura.domain.model;

public record Libro(Long id, String titulo, String idioma, int numeroDescargas, Autor autor) {

    public Libro {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El titulo es obligatorio.");
        }
        if (idioma == null || idioma.isBlank()) {
            throw new IllegalArgumentException("El idioma es obligatorio.");
        }
        if (numeroDescargas < 0) {
            throw new IllegalArgumentException("Las descargas no pueden ser negativas.");
        }
        titulo = titulo.trim();
        idioma = idioma.trim().toLowerCase();
    }

    public Libro conId(Long nuevoId) {
        return new Libro(nuevoId, titulo, idioma, numeroDescargas, autor);
    }

    public Libro conAutor(Autor nuevoAutor) {
        return new Libro(id, titulo, idioma, numeroDescargas, nuevoAutor);
    }
}
