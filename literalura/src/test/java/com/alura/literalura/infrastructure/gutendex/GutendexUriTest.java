package com.alura.literalura.infrastructure.gutendex;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class GutendexUriTest {

    @Test
    void codificaElTituloEnLaConsulta() {
        var uri = GutendexUri.buscar("https://gutendex.com/books", "Don Quijote");

        assertThat(uri.toString()).isEqualTo("https://gutendex.com/books?search=Don%20Quijote");
    }
}
