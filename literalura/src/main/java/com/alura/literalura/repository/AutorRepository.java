package com.alura.literalura.repository;



import com.alura.literalura.model.Entity.AutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AutorRepository extends JpaRepository<AutorEntity, Long> {

    // Buscar un autor por nombre
    Optional<AutorEntity> findByNombreIgnoreCase(String nombre);

    // Obtiene todos los autores
    List<AutorEntity> findAll();

    // Buscar autores que murieron antes de un año específico
    List<AutorEntity> findByAnioMuerteLessThan(int anioMuerte);

}
