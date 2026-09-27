package com.alura.literalura.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;

@Component
public class HttpRequestBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestBuilder.class);

    public HttpRequest construirGetRequest(String uriAPI, String opcionesBusquedaAPI, String textoBusqueda) {
        if (uriAPI == null || uriAPI.isBlank()) {
            throw new IllegalArgumentException("La URI no puede ser nula o vacía.");
        }

        URI direccion = URI.create(uriAPI + opcionesBusquedaAPI + textoBusqueda);
        LOG.debug("GET {}", direccion);
        return HttpRequest.newBuilder()
                .uri(direccion)
                .GET()
                .build();
    }

    public HttpRequest construirPostRequest(String uriAPI, String cuerpo) {
        if (uriAPI == null || uriAPI.isBlank()) {
            throw new IllegalArgumentException("La URI no puede ser nula o vacía.");
        }
        if (cuerpo == null) {
            throw new IllegalArgumentException("El cuerpo de la solicitud POST no puede ser nulo.");
        }

        URI direccion = URI.create(uriAPI);
        LOG.debug("POST {}", direccion);
        return HttpRequest.newBuilder()
                .uri(direccion)
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                .header("Content-Type", "application/json")
                .build();
    }
}
