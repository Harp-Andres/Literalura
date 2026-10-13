package com.alura.literalura.infrastructure.gutendex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GutendexPage(int count, List<GutendexBook> results) {
}
