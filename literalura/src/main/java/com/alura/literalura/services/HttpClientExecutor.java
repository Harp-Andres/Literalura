package com.alura.literalura.services;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class HttpClientExecutor {

    private final HttpClient client;

    public HttpClientExecutor() {
        this.client = HttpClient.newHttpClient();
    }

    public HttpResponse<String> enviarPeticion(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException("Error de I/O al enviar la solicitud: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("La solicitud fue interrumpida: " + e.getMessage(), e);
        }
    }
}
