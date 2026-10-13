package com.alura.literalura.support;

import com.alura.literalura.domain.model.Libro;
import com.alura.literalura.domain.port.RepositorioLibros;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class RepositorioLibrosEnMemoria implements RepositorioLibros {

    private final List<Libro> libros = new ArrayList<>();
    private long secuencia = 1;

    @Override
    public List<Libro> listar() {
        return List.copyOf(libros);
    }

    @Override
    public List<Libro> buscarPorIdioma(String idioma) {
        return libros.stream().filter(libro -> libro.idioma().equals(idioma)).toList();
    }

    @Override
    public Optional<Libro> buscarPorTitulo(String titulo) {
        return libros.stream().filter(libro -> libro.titulo().equalsIgnoreCase(titulo)).findFirst();
    }

    @Override
    public boolean existePorTitulo(String titulo) {
        return buscarPorTitulo(titulo).isPresent();
    }

    @Override
    public Libro guardar(Libro libro) {
        Libro guardado = libro.conId(secuencia++);
        libros.add(guardado);
        return guardado;
    }
}
