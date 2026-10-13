package com.alura.literalura.infrastructure.gutendex;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public final class GutendexUri {

    private GutendexUri() {
    }

    public static URI buscar(String baseUrl, String titulo) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("search", titulo)
                .build()
                .encode()
                .toUri();
    }
}
