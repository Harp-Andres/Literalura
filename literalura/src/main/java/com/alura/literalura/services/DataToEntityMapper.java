package com.alura.literalura.services;

import com.alura.literalura.model.DTO.DatosLibroDTO;
import com.alura.literalura.model.Entity.AutorEntity;
import com.alura.literalura.model.Entity.LibroEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataToEntityMapper {

    public List<AutorEntity> convertirAAutorEntity(DatosLibroDTO datosLibroDTO) {
        return datosLibroDTO.listaResultados().stream()
                .flatMap(resultadosLibro -> resultadosLibro.autores().stream())
                .distinct()
                .map(autor -> new AutorEntity(autor.nombre(), autor.anioNacimiento(), autor.anioMuerte()))
                .collect(Collectors.toList());
    }

    public List<LibroEntity> convertirALibroEntity(DatosLibroDTO datosLibro) {
        return datosLibro.listaResultados().stream()
                .map(LibroEntity::new)
                .collect(Collectors.toList());
    }
}
