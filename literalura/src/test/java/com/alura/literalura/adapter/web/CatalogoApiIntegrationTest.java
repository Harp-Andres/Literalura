package com.alura.literalura.adapter.web;

import com.alura.literalura.adapter.web.dto.ImportarLibroRequest;
import com.alura.literalura.support.PostgresIntegrationSupport;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogoApiIntegrationTest extends PostgresIntegrationSupport {

    private static WireMockServer wireMock;

    @DynamicPropertySource
    static void gutendex(DynamicPropertyRegistry registry) {
        if (wireMock == null) {
            wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
            wireMock.start();
        }
        registry.add("literalura.gutendex.base-url", () -> wireMock.baseUrl() + "/books");
    }

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void preparar() {
        jdbc.update("DELETE FROM libros");
        jdbc.update("DELETE FROM autores");
        wireMock.resetAll();
        wireMock.stubFor(get(urlPathEqualTo("/books"))
                .withQueryParam("search", equalTo("Frankenstein"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(fixture())));
    }

    @Test
    void importaDesdeGutendexYConsultaElCatalogo() {
        ResponseEntity<String> importacion = rest.postForEntity(
                "/api/libros/importaciones",
                new ImportarLibroRequest("Frankenstein"),
                String.class);

        assertThat(importacion.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(importacion.getBody()).contains("\"guardados\":1");

        ResponseEntity<String> segunda = rest.postForEntity(
                "/api/libros/importaciones",
                new ImportarLibroRequest("Frankenstein"),
                String.class);
        assertThat(segunda.getBody()).contains("\"guardados\":0").contains("\"omitidos\":1");

        assertThat(rest.getForObject("/api/libros?idioma=en", String.class))
                .contains("Frankenstein; Or, The Modern Prometheus");
        assertThat(rest.getForObject("/api/autores?vivosEn=1818", String.class))
                .contains("Shelley, Mary Wollstonecraft");
        assertThat(rest.getForObject("/api/autores?vivosEn=1700", String.class)).isEqualTo("[]");

        ResponseEntity<String> idiomaInvalido = rest.getForEntity("/api/libros?idioma=esp", String.class);
        assertThat(idiomaInvalido.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private static String fixture() {
        try (var input = CatalogoApiIntegrationTest.class.getResourceAsStream("/fixtures/frankenstein.json")) {
            if (input == null) {
                throw new IllegalStateException("Falta el fixture frankenstein.json");
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (java.io.IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
