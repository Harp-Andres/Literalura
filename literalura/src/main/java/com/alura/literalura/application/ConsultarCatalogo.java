package com.alura.literalura.application;

import com.alura.literalura.domain.exception.IdiomaInvalidoException;
import com.alura.literalura.domain.exception.TituloInvalidoException;
import com.alura.literalura.domain.model.Autor;
import com.alura.literalura.domain.model.Libro;
import com.alura.literalura.domain.port.RepositorioAutores;
import com.alura.literalura.domain.port.RepositorioLibros;

import java.util.List;
import java.util.Optional;

public final class ConsultarCatalogo {

    private final RepositorioLibros libros;
    private final RepositorioAutores autores;

    public ConsultarCatalogo(RepositorioLibros libros, RepositorioAutores autores) {
        this.libros = libros;
        this.autores = autores;
    }

    public List<Libro> listarLibros() {
        return libros.listar();
    }

    public List<Libro> librosPorIdioma(String idioma) {
        return libros.buscarPorIdioma(normalizarIdioma(idioma));
    }

    public Optional<Libro> libroPorTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new TituloInvalidoException("El titulo no puede estar vacio.");
        }
        return libros.buscarPorTitulo(titulo.trim());
    }

    public List<Autor> listarAutores() {
        return autores.listar();
    }

    public List<Autor> autoresVivosEn(int anio) {
        return autores.listar().stream()
                .filter(autor -> autor.estabaVivoEn(anio))
                .toList();
    }

    private static String normalizarIdioma(String idioma) {
        if (idioma == null || !idioma.trim().matches("(?i)[a-z]{2}")) {
            throw new IdiomaInvalidoException("El idioma debe ser un codigo de dos letras.");
        }
        return idioma.trim().toLowerCase();
    }
}
