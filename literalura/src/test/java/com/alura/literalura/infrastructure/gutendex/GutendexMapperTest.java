package com.alura.literalura.infrastructure.gutendex;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class GutendexMapperTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void mapeaElPrimerAutorYElPrimerIdioma() throws Exception {
        String json = """
                {
                  "count": 1,
                  "results": [{
                    "title": " Frankenstein ",
                    "authors": [
                      {"name": "Shelley, Mary", "birth_year": 1797, "death_year": 1851},
                      {"name": "Otro", "birth_year": 1800, "death_year": 1900}
                    ],
                    "languages": ["EN", "fr"],
                    "download_count": 42
                  }]
                }
                """;

        var libros = GutendexMapper.interpretar(json, mapper);

        assertThat(libros).hasSize(1);
        assertThat(libros.getFirst().titulo()).isEqualTo("Frankenstein");
        assertThat(libros.getFirst().idioma()).isEqualTo("en");
        assertThat(libros.getFirst().numeroDescargas()).isEqualTo(42);
        assertThat(libros.getFirst().autor().nombre()).isEqualTo("Shelley, Mary");
    }

    @Test
    void omiteResultadosSinTituloYToleraAniosNulos() throws Exception {
        String json = """
                {
                  "count": 2,
                  "results": [
                    {"title": " ", "authors": [], "languages": ["es"], "download_count": 1},
                    {"title": "Sin autor", "authors": [{"name": "  "}], "languages": [], "download_count": null}
                  ]
                }
                """;

        var libros = GutendexMapper.interpretar(json, mapper);

        assertThat(libros).hasSize(1);
        assertThat(libros.getFirst().autor()).isNull();
        assertThat(libros.getFirst().idioma()).isEqualTo("und");
        assertThat(libros.getFirst().numeroDescargas()).isZero();
    }
}
