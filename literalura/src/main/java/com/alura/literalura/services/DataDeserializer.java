package com.alura.literalura.services;

import com.alura.literalura.exceptions.DeserializationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class DataDeserializer implements IConvierteDatos {

    private final Gson gson = new GsonBuilder().create();

    @Override
    public <T> T obtenerDatos(String json, Class<T> clase) {
        try {
            // Deserialización
            return gson.fromJson(json, clase);
        } catch (JsonSyntaxException | IllegalStateException e) {
            throw new DeserializationException("Error al deserializar el JSON: " + e.getMessage(), e);
        }
    }


}
