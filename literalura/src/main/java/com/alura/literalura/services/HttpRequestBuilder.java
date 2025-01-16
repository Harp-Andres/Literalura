package com.alura.literalura.services;

import java.net.URI;
import java.net.http.HttpRequest;

public class HttpRequestBuilder {

    public HttpRequest construirGetRequest(String uriAPI, String opcionesBusquedaAPI, String textoBusqueda) {
        if (uriAPI == null || uriAPI.isBlank()) {
            throw new IllegalArgumentException("La URI no puede ser nula o vacía.");
        }

        URI direccion = URI.create(uriAPI + opcionesBusquedaAPI + textoBusqueda);
        System.out.println("Imprimiendo direccion: " + direccion);
        return HttpRequest.newBuilder()
                .uri(direccion)
                .GET() // Por defecto realiza solicitudes GET
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
        return HttpRequest.newBuilder()
                .uri(direccion)
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                .header("Content-Type", "application/json")
                .build();
    }

}
