package com.alura.literalura.infrastructure.persistence.jpa;

import com.alura.literalura.infrastructure.persistence.entity.LibroEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibroJpaRepository extends JpaRepository<LibroEntity, Long> {

    @Override
    @EntityGraph(attributePaths = "autor")
    List<LibroEntity> findAll();

    @EntityGraph(attributePaths = "autor")
    List<LibroEntity> findByIdioma(String idioma);

    @EntityGraph(attributePaths = "autor")
    Optional<LibroEntity> findByTituloIgnoreCase(String titulo);

    boolean existsByTituloIgnoreCase(String titulo);
}
