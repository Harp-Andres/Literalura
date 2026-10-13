package com.alura.literalura.domain.model;

import java.util.List;

public record ResultadoImportacion(int guardados, int omitidos, List<Libro> libros) {
}
