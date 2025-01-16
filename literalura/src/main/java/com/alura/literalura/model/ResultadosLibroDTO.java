package com.alura.literalura.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record ResultadosLibroDTO(
        @SerializedName("title") String tituloLibro,
        @SerializedName("authors") List<AutorDTO> autores,
        @SerializedName("languages") List<String> lenguajes,
        @SerializedName("download_count") int numeroDescargas
) {
    @Override
    public String toString() {
        return "ResultadosLibroDTO{" +
                "tituloLibro='" + tituloLibro + '\'' +
                ", autores=" + autores +
                ", lenguajes=" + lenguajes +
                ", numeroDescargas=" + numeroDescargas +
                '}';
    }

}
