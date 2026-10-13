package com.alura.literalura.application;

import com.alura.literalura.domain.exception.IdiomaInvalidoException;
import com.alura.literalura.domain.exception.TituloInvalidoException;
import com.alura.literalura.domain.model.Autor;
import com.alura.literalura.domain.model.Libro;
import com.alura.literalura.support.RepositorioAutoresEnMemoria;
import com.alura.literalura.support.RepositorioLibrosEnMemoria;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
class ConsultarCatalogoTest {

    private final RepositorioLibrosEnMemoria libros = new RepositorioLibrosEnMemoria();
    private final RepositorioAutoresEnMemoria autores = new RepositorioAutoresEnMemoria();
    private final ConsultarCatalogo catalogo = new ConsultarCatalogo(libros, autores);

    @Test
    void filtraIdiomaEnMinusculas() {
        libros.guardar(new Libro(null, "Don Quijote", "ES", 10, null));

        assertThat(catalogo.librosPorIdioma(" es ")).hasSize(1);
    }

    @Test
    void rechazaIdiomaQueNoTieneDosLetras() {
        assertThatThrownBy(() -> catalogo.librosPorIdioma("esp"))
                .isInstanceOf(IdiomaInvalidoException.class);
    }

    @Test
    void rechazaTituloVacio() {
        assertThatThrownBy(() -> catalogo.libroPorTitulo("  "))
                .isInstanceOf(TituloInvalidoException.class);
    }

    @Test
    void listaSoloAutoresVivosEnElAnio() {
        autores.guardar(new Autor(null, "Shelley", 1797, 1851));
        autores.guardar(new Autor(null, "Cervantes", 1547, 1616));

        assertThat(catalogo.autoresVivosEn(1818))
                .extracting(Autor::nombre)
                .containsExactly("Shelley");
    }
}
