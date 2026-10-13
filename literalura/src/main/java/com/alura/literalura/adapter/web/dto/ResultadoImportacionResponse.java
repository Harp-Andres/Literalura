package com.alura.literalura.adapter.web.dto;

import com.alura.literalura.domain.model.ResultadoImportacion;

import java.util.List;

public record ResultadoImportacionResponse(int guardados, int omitidos, List<LibroResponse> libros) {

    public static ResultadoImportacionResponse desde(ResultadoImportacion resultado) {
        return new ResultadoImportacionResponse(
                resultado.guardados(),
                resultado.omitidos(),
                resultado.libros().stream().map(LibroResponse::desde).toList());
    }
}
