package com.alura.literalura.infrastructure.persistence;

import com.alura.literalura.domain.model.Autor;
import com.alura.literalura.domain.model.Libro;
import com.alura.literalura.infrastructure.persistence.entity.AutorEntity;
import com.alura.literalura.infrastructure.persistence.entity.LibroEntity;
import org.springframework.stereotype.Component;

@Component
public class PersistenciaMapper {

    public Autor aDominio(AutorEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Autor(entity.getId(), entity.getNombre(), entity.getAnioNacimiento(), entity.getAnioMuerte());
    }

    public Libro aDominio(LibroEntity entity) {
        return new Libro(
                entity.getId(),
                entity.getTitulo(),
                entity.getIdioma(),
                entity.getNumeroDescargas(),
                aDominio(entity.getAutor()));
    }

    public AutorEntity aEntidad(Autor autor) {
        return new AutorEntity(autor.nombre(), autor.anioNacimiento(), autor.anioMuerte());
    }

    public LibroEntity aEntidad(Libro libro, AutorEntity autor) {
        return new LibroEntity(libro.titulo(), libro.idioma(), libro.numeroDescargas(), autor);
    }
}
