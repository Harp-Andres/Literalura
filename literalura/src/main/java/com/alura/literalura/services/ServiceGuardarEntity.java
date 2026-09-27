package com.alura.literalura.services;

import com.alura.literalura.model.DTO.DatosLibroDTO;
import com.alura.literalura.model.Entity.AutorEntity;
import com.alura.literalura.model.Entity.LibroEntity;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceGuardarEntity {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceGuardarEntity.class);

    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;
    private final DataToEntityMapper dataToEntityMapper;
    private final AsignarRelaciones asignarRelaciones;

    public ServiceGuardarEntity(
            AutorRepository autorRepository,
            LibroRepository libroRepository,
            DataToEntityMapper dataToEntityMapper,
            AsignarRelaciones asignarRelaciones) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
        this.dataToEntityMapper = dataToEntityMapper;
        this.asignarRelaciones = asignarRelaciones;
    }

    @Transactional
    public void guardarAutoresYLibros(DatosLibroDTO datosLibroDTO) {
        List<AutorEntity> autores = dataToEntityMapper.convertirAAutorEntity(datosLibroDTO);
        List<LibroEntity> libros = dataToEntityMapper.convertirALibroEntity(datosLibroDTO);
        LOG.info("Persistiendo {} autores y {} libros", autores.size(), libros.size());

        asignarRelaciones.asignarRelacionesAutoresYLibros(datosLibroDTO);

        autorRepository.saveAll(autores);
        libroRepository.saveAll(libros);
    }
}
