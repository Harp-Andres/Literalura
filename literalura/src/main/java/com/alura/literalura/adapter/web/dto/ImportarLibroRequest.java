package com.alura.literalura.adapter.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ImportarLibroRequest(
        @NotBlank(message = "El titulo no puede estar vacio.")
        @Size(max = 500, message = "El titulo supera el maximo de 500 caracteres.")
        String titulo
) {
}
