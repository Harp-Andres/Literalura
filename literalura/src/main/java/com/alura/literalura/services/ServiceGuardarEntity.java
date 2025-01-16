package com.alura.literalura.services;

import com.alura.literalura.model.Entity.AutorEntity;
import com.alura.literalura.model.DTO.DatosLibroDTO;
import com.alura.literalura.model.Entity.LibroEntity;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ServiceGuardarEntity {

    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;
    private final DataToEntityMapper dataToEntityMapper = new DataToEntityMapper();// Mapper para convertir DTO a entidades

    public ServiceGuardarEntity(AutorRepository autorRepository, LibroRepository libroRepository) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
    }


    @Transactional
    public void guardarAutoresYLibros(DatosLibroDTO datosLibroDTO) {
        AsignarRelaciones asignarRelaciones = new AsignarRelaciones(autorRepository,libroRepository);  // Asignar relaciones entre entidades

        // Convertir los datos a entidades
        List<AutorEntity> autores = dataToEntityMapper.convertirAAutorEntity(datosLibroDTO);
        List<LibroEntity> libros = dataToEntityMapper.convertirALibroEntity(datosLibroDTO);
        System.out.println("Imprimir lista de autores: " + autores);
        System.out.println("Imprimir lista de libros: " + libros);
        // Asignar relaciones entre autores y libros
        asignarRelaciones.asignarRelacionesAutoresYLibros(datosLibroDTO);

        //Guardar los autores y libros
       autorRepository.saveAll(autores);
        libroRepository.saveAll(libros);
    }
}

