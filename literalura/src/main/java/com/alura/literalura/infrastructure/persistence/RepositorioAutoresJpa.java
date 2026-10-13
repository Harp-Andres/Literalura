package com.alura.literalura.infrastructure.persistence;

import com.alura.literalura.domain.model.Autor;
import com.alura.literalura.domain.port.RepositorioAutores;
import com.alura.literalura.infrastructure.persistence.entity.AutorEntity;
import com.alura.literalura.infrastructure.persistence.jpa.AutorJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class RepositorioAutoresJpa implements RepositorioAutores {

    private final AutorJpaRepository autores;
    private final PersistenciaMapper mapper;

    public RepositorioAutoresJpa(AutorJpaRepository autores, PersistenciaMapper mapper) {
        this.autores = autores;
        this.mapper = mapper;
    }

    @Override
    public List<Autor> listar() {
        return autores.findAll().stream().map(mapper::aDominio).toList();
    }

    @Override
    public Optional<Autor> buscarPorNombre(String nombre) {
        return autores.findByNombreIgnoreCase(nombre).map(mapper::aDominio);
    }

    @Override
    @Transactional
    public Autor guardar(Autor autor) {
        AutorEntity guardado = autores.save(mapper.aEntidad(autor));
        return mapper.aDominio(guardado);
    }
}
