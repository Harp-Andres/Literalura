package com.alura.literalura.model;

import com.google.gson.annotations.SerializedName;


public record AutorDTO(
        @SerializedName("name") String nombre,
        @SerializedName("birth_year") Integer anioNacimiento,
        @SerializedName("death_year") Integer anioMuerte
) {
}
