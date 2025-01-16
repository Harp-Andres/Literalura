package com.alura.literalura.services;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
