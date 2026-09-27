package com.alura.literalura.service;

import com.alura.literalura.model.Entity.LibroEntity;
import com.alura.literalura.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroCatalogService {

    private final LibroRepository libroRepository;

    public LibroCatalogService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public List<LibroEntity> listarTodos() {
        return libroRepository.findAll();
    }

    public List<LibroEntity> buscarPorIdioma(String codigoLenguaje) {
        return libroRepository.findByLenguaje(codigoLenguaje);
    }

    public Optional<LibroEntity> buscarPorTitulo(String titulo) {
        return libroRepository.findByTituloLibroIgnoreCase(titulo);
    }
}
