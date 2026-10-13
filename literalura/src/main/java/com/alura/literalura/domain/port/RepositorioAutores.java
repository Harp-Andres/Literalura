package com.alura.literalura.domain.port;

import com.alura.literalura.domain.model.Autor;

import java.util.List;
import java.util.Optional;

public interface RepositorioAutores {

    List<Autor> listar();

    Optional<Autor> buscarPorNombre(String nombre);

    Autor guardar(Autor autor);
}
