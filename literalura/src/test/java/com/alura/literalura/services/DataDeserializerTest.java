package com.alura.literalura.services;

import com.alura.literalura.exceptions.DeserializationException;
import com.alura.literalura.model.DTO.DatosLibroDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataDeserializerTest {

    private final DataDeserializer deserializer = new DataDeserializer();

    @Test
    void deserializesGutendexPayload() {
        String json = """
                {
                  "count": 1,
                  "results": [{
                    "title": "Don Quijote",
                    "authors": [{"name": "Cervantes", "birth_year": 1547, "death_year": 1616}],
                    "languages": ["es"],
                    "download_count": 100
                  }]
                }
                """;

        DatosLibroDTO dto = deserializer.obtenerDatos(json, DatosLibroDTO.class);
        assertEquals(1, dto.numeroRegistros());
        assertEquals("Don Quijote", dto.listaResultados().getFirst().tituloLibro());
    }

    @Test
    void rejectsInvalidJson() {
        assertThrows(DeserializationException.class,
                () -> deserializer.obtenerDatos("{not-json", DatosLibroDTO.class));
    }
}
