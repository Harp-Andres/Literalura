package com.alura.literalura.infrastructure.persistence.jpa;

import com.alura.literalura.infrastructure.persistence.entity.AutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutorJpaRepository extends JpaRepository<AutorEntity, Long> {

    Optional<AutorEntity> findByNombreIgnoreCase(String nombre);
}
