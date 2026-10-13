package com.alura.literalura.application;

import com.alura.literalura.domain.model.ResultadoImportacion;

@FunctionalInterface
public interface ImportarLibro {

    ResultadoImportacion importar(String titulo);
}
