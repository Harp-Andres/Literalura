package com.alura.literalura.services;

import com.alura.literalura.model.Entity.AutorEntity;
import com.alura.literalura.model.DTO.DatosLibroDTO;
import com.alura.literalura.model.Entity.LibroEntity;

import java.util.List;
import java.util.stream.Collectors;


public class DataToEntityMapper {

    // Convierte datos de autores en entidades de AutorEntity
    public List<AutorEntity> convertirAAutorEntity(DatosLibroDTO datosLibroDTO) {
        return datosLibroDTO.listaResultados().stream()
                .flatMap(resultadosLibro -> resultadosLibro.autores().stream()) // Obtener lista de autores
                .distinct() // Eliminar duplicados
                .map(autor -> new AutorEntity(autor.nombre(), autor.anioNacimiento(), autor.anioMuerte())) // Mapear a AutorEntity
                .collect(Collectors.toList());
    }

    // Convierte datos de libros en entidades de LibroEntity
    public List<LibroEntity> convertirALibroEntity(DatosLibroDTO datosLibro) {
        return datosLibro.listaResultados().stream()
                .map(LibroEntity::new) // Crear LibroEntity
                .collect(Collectors.toList());
    }
}

