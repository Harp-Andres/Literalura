package com.alura.literalura.e2e;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@Tag("e2e")
class CatalogoE2ETest {

    @BeforeAll
    static void configurar() {
        RestAssured.baseURI = System.getenv().getOrDefault("E2E_BASE_URL", "http://localhost:8080");
    }

    @Test
    void elSistemaCompletoImportaYPublicaElCatalogo() {
        RestAssured.given()
                .when().get("/actuator/health")
                .then().statusCode(200)
                .body("status", equalTo("UP"));

        RestAssured.given()
                .when().get("/")
                .then().statusCode(200)
                .body(containsString("id=\"titulo\""));

        var importacion = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("titulo", "Frankenstein"))
                .when().post("/api/libros/importaciones")
                .then().statusCode(200)
                .extract().jsonPath();

        assertThat(importacion.getInt("guardados") + importacion.getInt("omitidos")).isEqualTo(1);

        RestAssured.given()
                .when().get("/api/libros")
                .then().statusCode(200)
                .body(containsString("Frankenstein; Or, The Modern Prometheus"));

        RestAssured.given()
                .when().get("/api/autores?vivosEn=1818")
                .then().statusCode(200)
                .body(containsString("Shelley, Mary Wollstonecraft"));
    }
}
