package com.alura.literalura.repository;



import com.alura.literalura.model.AutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AutorRepository extends JpaRepository<AutorEntity, Long> {

    // Buscar un autor por nombre
    Optional<AutorEntity> findByNombre(String nombre);


}
