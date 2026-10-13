package com.alura.literalura.application;

import com.alura.literalura.domain.exception.TituloInvalidoException;
import com.alura.literalura.domain.model.Autor;
import com.alura.literalura.domain.model.Libro;
import com.alura.literalura.domain.model.ResultadoImportacion;
import com.alura.literalura.domain.port.CatalogoRemoto;
import com.alura.literalura.domain.port.RepositorioAutores;
import com.alura.literalura.domain.port.RepositorioLibros;

import java.util.ArrayList;
import java.util.List;

public final class ImportarLibroService implements ImportarLibro {

    private final CatalogoRemoto remoto;
    private final RepositorioLibros libros;
    private final RepositorioAutores autores;

    public ImportarLibroService(CatalogoRemoto remoto, RepositorioLibros libros, RepositorioAutores autores) {
        this.remoto = remoto;
        this.libros = libros;
        this.autores = autores;
    }

    @Override
    public ResultadoImportacion importar(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new TituloInvalidoException("El titulo no puede estar vacio.");
        }

        List<Libro> encontrados = remoto.buscarPorTitulo(titulo.trim());
        List<Libro> persistidos = new ArrayList<>();
        int omitidos = 0;

        for (Libro candidato : encontrados) {
            if (libros.existePorTitulo(candidato.titulo())) {
                omitidos++;
                continue;
            }
            Autor autor = persistirAutor(candidato.autor());
            persistidos.add(libros.guardar(candidato.conAutor(autor)));
        }

        return new ResultadoImportacion(persistidos.size(), omitidos, List.copyOf(persistidos));
    }

    private Autor persistirAutor(Autor autor) {
        if (autor == null) {
            return null;
        }
        return autores.buscarPorNombre(autor.nombre())
                .orElseGet(() -> autores.guardar(autor));
    }
}
