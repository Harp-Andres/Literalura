package com.alura.literalura.domain.port;

import com.alura.literalura.domain.model.Libro;

import java.util.List;
import java.util.Optional;

public interface RepositorioLibros {

    List<Libro> listar();

    List<Libro> buscarPorIdioma(String idioma);

    Optional<Libro> buscarPorTitulo(String titulo);

    boolean existePorTitulo(String titulo);

    Libro guardar(Libro libro);
}
