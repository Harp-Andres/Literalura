package com.alura.literalura.application;

import com.alura.literalura.domain.exception.CatalogoRemotoException;
import com.alura.literalura.domain.exception.TituloInvalidoException;
import com.alura.literalura.domain.model.Autor;
import com.alura.literalura.domain.model.Libro;
import com.alura.literalura.domain.port.CatalogoRemoto;
import com.alura.literalura.support.RepositorioAutoresEnMemoria;
import com.alura.literalura.support.RepositorioLibrosEnMemoria;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
class ImportarLibroServiceTest {

    private final RepositorioLibrosEnMemoria libros = new RepositorioLibrosEnMemoria();
    private final RepositorioAutoresEnMemoria autores = new RepositorioAutoresEnMemoria();

    @Test
    void guardaLibroYReutilizaAutorExistente() {
        autores.guardar(new Autor(null, "Shelley", 1797, 1851));
        ImportarLibroService servicio = servicio(titulo -> List.of(
                new Libro(null, "Frankenstein", "en", 42, new Autor(null, "Shelley", 1797, 1851))
        ));

        var resultado = servicio.importar("Frankenstein");

        assertThat(resultado.guardados()).isEqualTo(1);
        assertThat(resultado.omitidos()).isZero();
        assertThat(autores.listar()).hasSize(1);
        assertThat(resultado.libros().getFirst().autor().id()).isNotNull();
    }

    @Test
    void omiteTituloYaRegistrado() {
        libros.guardar(new Libro(null, "Frankenstein", "en", 1, null));
        ImportarLibroService servicio = servicio(titulo -> List.of(
                new Libro(null, "Frankenstein", "en", 42, null)
        ));

        var resultado = servicio.importar("Frankenstein");

        assertThat(resultado.guardados()).isZero();
        assertThat(resultado.omitidos()).isEqualTo(1);
        assertThat(libros.listar()).hasSize(1);
    }

    @Test
    void rechazaTituloVacioSinConsultarLaFuente() {
        ImportarLibroService servicio = servicio(titulo -> {
            throw new AssertionError("No debio consultar la fuente");
        });

        assertThatThrownBy(() -> servicio.importar(" "))
                .isInstanceOf(TituloInvalidoException.class);
    }

    @Test
    void propagaElFalloDeLaFuente() {
        ImportarLibroService servicio = servicio(titulo -> {
            throw new CatalogoRemotoException("caido");
        });

        assertThatThrownBy(() -> servicio.importar("Quijote"))
                .isInstanceOf(CatalogoRemotoException.class)
                .hasMessage("caido");
    }

    private ImportarLibroService servicio(CatalogoRemoto remoto) {
        return new ImportarLibroService(remoto, libros, autores);
    }
}
