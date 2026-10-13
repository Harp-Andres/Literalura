package com.alura.literalura.domain.port;

import com.alura.literalura.domain.model.Libro;

import java.util.List;

public interface CatalogoRemoto {

    List<Libro> buscarPorTitulo(String titulo);
}
