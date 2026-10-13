package com.alura.literalura.domain.model;

public record Autor(Long id, String nombre, Integer anioNacimiento, Integer anioMuerte) {

    public Autor {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del autor es obligatorio.");
        }
        nombre = nombre.trim();
    }

    public boolean estabaVivoEn(int anio) {
        if (anioNacimiento == null || anioNacimiento > anio) {
            return false;
        }
        return anioMuerte == null || anioMuerte >= anio;
    }

    public Autor conId(Long nuevoId) {
        return new Autor(nuevoId, nombre, anioNacimiento, anioMuerte);
    }
}
