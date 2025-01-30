package com.alura.literalura.repository;


import com.alura.literalura.model.Entity.LibroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<LibroEntity, Long> {


    // Buscar un autor por nombre
    Optional<LibroEntity> findByTituloLibroIgnoreCase(String nombreLibro);

    //Buscar libros por lenguaje
    List<LibroEntity>findByLenguaje(String codigoLenguaje);

    //Listar todos los libros de la base de datos
    List<LibroEntity>findAll();


}


