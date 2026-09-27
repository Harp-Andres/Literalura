package com.alura.literalura.services;

import com.alura.literalura.model.DTO.AutorDTO;
import com.alura.literalura.model.DTO.DatosLibroDTO;
import com.alura.literalura.model.DTO.ResultadosLibroDTO;
import com.alura.literalura.model.Entity.AutorEntity;
import com.alura.literalura.model.Entity.LibroEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataToEntityMapperTest {

    private final DataToEntityMapper mapper = new DataToEntityMapper();

    @Test
    void mapsAuthorsAndBooks() {
        DatosLibroDTO dto = new DatosLibroDTO(1, List.of(
                new ResultadosLibroDTO(
                        "Frankenstein",
                        List.of(new AutorDTO("Shelley", 1797, 1851)),
                        List.of("en"),
                        42
                )
        ));

        List<AutorEntity> autores = mapper.convertirAAutorEntity(dto);
        List<LibroEntity> libros = mapper.convertirALibroEntity(dto);

        assertEquals(1, autores.size());
        assertEquals("Shelley", autores.getFirst().getNombre());
        assertEquals(1, libros.size());
        assertEquals("Frankenstein", libros.getFirst().getTituloLibro());
        assertEquals("en", libros.getFirst().getLenguaje());
        assertEquals(42, libros.getFirst().getNumeroDescargas());
    }
}
