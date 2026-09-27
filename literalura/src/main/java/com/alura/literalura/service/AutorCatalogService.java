package com.alura.literalura.service;

import com.alura.literalura.model.Entity.AutorEntity;
import com.alura.literalura.repository.AutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorCatalogService {

    private final AutorRepository autorRepository;

    public AutorCatalogService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public List<AutorEntity> listarTodos() {
        return autorRepository.findAll();
    }

    public List<AutorEntity> autoresConMuerteAntesDe(int anio) {
        return autorRepository.findByAnioMuerteLessThan(anio);
    }
}
