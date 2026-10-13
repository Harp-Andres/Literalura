package com.alura.literalura.infrastructure.persistence;

import com.alura.literalura.domain.model.Autor;
import com.alura.literalura.domain.model.Libro;
import com.alura.literalura.domain.port.RepositorioLibros;
import com.alura.literalura.infrastructure.persistence.entity.AutorEntity;
import com.alura.literalura.infrastructure.persistence.jpa.AutorJpaRepository;
import com.alura.literalura.infrastructure.persistence.jpa.LibroJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class RepositorioLibrosJpa implements RepositorioLibros {

    private final LibroJpaRepository libros;
    private final AutorJpaRepository autores;
    private final PersistenciaMapper mapper;

    public RepositorioLibrosJpa(LibroJpaRepository libros, AutorJpaRepository autores, PersistenciaMapper mapper) {
        this.libros = libros;
        this.autores = autores;
        this.mapper = mapper;
    }

    @Override
    public List<Libro> listar() {
        return libros.findAll().stream().map(mapper::aDominio).toList();
    }

    @Override
    public List<Libro> buscarPorIdioma(String idioma) {
        return libros.findByIdioma(idioma).stream().map(mapper::aDominio).toList();
    }

    @Override
    public Optional<Libro> buscarPorTitulo(String titulo) {
        return libros.findByTituloIgnoreCase(titulo).map(mapper::aDominio);
    }

    @Override
    public boolean existePorTitulo(String titulo) {
        return libros.existsByTituloIgnoreCase(titulo);
    }

    @Override
    @Transactional
    public Libro guardar(Libro libro) {
        AutorEntity autor = resolverAutor(libro.autor());
        return mapper.aDominio(libros.save(mapper.aEntidad(libro, autor)));
    }

    private AutorEntity resolverAutor(Autor autor) {
        if (autor == null) {
            return null;
        }
        if (autor.id() == null) {
            throw new IllegalStateException("El autor debe persistirse antes que el libro.");
        }
        return autores.findById(autor.id())
                .orElseThrow(() -> new IllegalStateException("No existe el autor " + autor.id()));
    }
}
