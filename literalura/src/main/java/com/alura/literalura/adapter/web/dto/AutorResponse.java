package com.alura.literalura.adapter.web.dto;

import com.alura.literalura.domain.model.Autor;

public record AutorResponse(Long id, String nombre, Integer anioNacimiento, Integer anioMuerte) {

    public static AutorResponse desde(Autor autor) {
        return new AutorResponse(autor.id(), autor.nombre(), autor.anioNacimiento(), autor.anioMuerte());
    }
}
