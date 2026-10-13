package com.alura.literalura.infrastructure.persistence;

import com.alura.literalura.domain.model.Autor;
import com.alura.literalura.domain.model.Libro;
import com.alura.literalura.domain.port.RepositorioAutores;
import com.alura.literalura.domain.port.RepositorioLibros;
import com.alura.literalura.support.PostgresIntegrationSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("database")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RepositorioCatalogoDatabaseTest extends PostgresIntegrationSupport {

    @Autowired
    private RepositorioLibros libros;

    @Autowired
    private RepositorioAutores autores;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbc;

    @BeforeEach
    void limpiar() {
        jdbc.update("DELETE FROM libros");
        jdbc.update("DELETE FROM autores");
    }

    @Test
    void persisteLibroConAutorYLoRecuperaSinImportarMayusculas() {
        Autor autor = autores.guardar(new Autor(null, "Cervantes", 1547, 1616));
        libros.guardar(new Libro(null, "Don Quijote", "es", 100, autor));

        assertThat(libros.existePorTitulo("don quijote")).isTrue();
        assertThat(libros.buscarPorTitulo("DON QUIJOTE"))
                .get()
                .extracting(Libro::idioma, libro -> libro.autor().nombre())
                .containsExactly("es", "Cervantes");
        assertThat(libros.buscarPorIdioma("es")).hasSize(1);
        assertThat(autores.buscarPorNombre("cervantes")).isPresent();
    }

    @Test
    void elTituloEsUnicoEnLaBase() {
        Autor autor = autores.guardar(new Autor(null, "Shelley", 1797, 1851));
        libros.guardar(new Libro(null, "Frankenstein", "en", 10, autor));

        assertThatThrownBy(() -> libros.guardar(new Libro(null, "Frankenstein", "en", 11, autor)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void elTituloEsUnicoSinImportarMayusculas() {
        Autor autor = autores.guardar(new Autor(null, "Shelley", 1797, 1851));
        libros.guardar(new Libro(null, "Frankenstein", "en", 10, autor));

        assertThatThrownBy(() -> libros.guardar(new Libro(null, "frankenstein", "en", 11, autor)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void elNombreDelAutorEsUnicoSinImportarMayusculas() {
        autores.guardar(new Autor(null, "Shelley", 1797, 1851));

        assertThatThrownBy(() -> autores.guardar(new Autor(null, "SHELLEY", 1797, 1851)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
