package com.alura.literalura.services;

import com.alura.literalura.model.DTO.DatosLibroDTO;
import com.alura.literalura.model.Entity.AutorEntity;
import com.alura.literalura.model.Entity.LibroEntity;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AsignarRelaciones {

    private static final Logger LOG = LoggerFactory.getLogger(AsignarRelaciones.class);

    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;

    public AsignarRelaciones(AutorRepository autorRepository, LibroRepository libroRepository) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
    }

    public void asignarRelacionesAutoresYLibros(DatosLibroDTO datosLibroDTO) {
        datosLibroDTO.listaResultados().forEach(resultadosLibro -> {
            LibroEntity libro = new LibroEntity(resultadosLibro);
            LOG.debug("Procesando libro: {}", libro.getTituloLibro());

            resultadosLibro.autores().forEach(autor -> {
                AutorEntity autorEntity = autorRepository.findByNombreIgnoreCase(autor.nombre())
                        .orElseGet(() -> {
                            AutorEntity nuevoAutor = new AutorEntity(
                                    autor.nombre(), autor.anioNacimiento(), autor.anioMuerte());
                            return autorRepository.save(nuevoAutor);
                        });
                autorEntity.agregarLibro(libro);
            });

            libroRepository.save(libro);
        });
    }
}
