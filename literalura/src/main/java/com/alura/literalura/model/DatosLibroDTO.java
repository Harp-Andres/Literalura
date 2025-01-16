package com.alura.literalura.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record DatosLibroDTO(
        @SerializedName("count") int numeroRegistros,
        @SerializedName("results") List<ResultadosLibroDTO> listaResultados
) {

}
