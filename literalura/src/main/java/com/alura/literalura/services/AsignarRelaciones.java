package com.alura.literalura.services;

import com.alura.literalura.model.Entity.AutorEntity;
import com.alura.literalura.model.DTO.DatosLibroDTO;
import com.alura.literalura.model.Entity.LibroEntity;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;

public class AsignarRelaciones {

    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;


    public AsignarRelaciones(AutorRepository autorRepository, LibroRepository libroRepository) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
    }

    public void asignarRelacionesAutoresYLibros(DatosLibroDTO datosLibroDTO) {

        datosLibroDTO.listaResultados().forEach(resultadosLibro -> {
            LibroEntity libro = new LibroEntity(resultadosLibro);  // Crear entidad de libro
            System.out.println("Imprimiendo entidad libro: " + libro);
            // Buscar o crear autores y establecer relaciones
            resultadosLibro.autores().forEach(autor -> {
                AutorEntity autorEntity = autorRepository.findByNombreIgnoreCase(autor.nombre())
                        .orElseGet(() -> {
                            AutorEntity nuevoAutor = new AutorEntity(autor.nombre(), autor.anioNacimiento(), autor.anioMuerte());
                            autorRepository.save(nuevoAutor); // Persistir nuevo autor
                            return nuevoAutor;
                        });
                autorEntity.agregarLibro(libro); // Relación bidireccional
            });

            libroRepository.save(libro); // Persistir libro
        });
    }
}

