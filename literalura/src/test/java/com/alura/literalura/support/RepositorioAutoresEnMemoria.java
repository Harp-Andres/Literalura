package com.alura.literalura.support;

import com.alura.literalura.domain.model.Autor;
import com.alura.literalura.domain.port.RepositorioAutores;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class RepositorioAutoresEnMemoria implements RepositorioAutores {

    private final List<Autor> autores = new ArrayList<>();
    private long secuencia = 1;

    @Override
    public List<Autor> listar() {
        return List.copyOf(autores);
    }

    @Override
    public Optional<Autor> buscarPorNombre(String nombre) {
        return autores.stream().filter(autor -> autor.nombre().equalsIgnoreCase(nombre)).findFirst();
    }

    @Override
    public Autor guardar(Autor autor) {
        Autor guardado = autor.conId(secuencia++);
        autores.add(guardado);
        return guardado;
    }
}
