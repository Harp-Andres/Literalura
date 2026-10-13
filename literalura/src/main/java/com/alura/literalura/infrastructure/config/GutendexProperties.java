package com.alura.literalura.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "literalura.gutendex")
public record GutendexProperties(String baseUrl) {

    public GutendexProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("literalura.gutendex.base-url es obligatorio.");
        }
        while (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
    }
}
