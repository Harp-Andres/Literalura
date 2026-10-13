package com.alura.literalura.adapter.console;

import com.alura.literalura.application.ConsultarCatalogo;
import com.alura.literalura.domain.model.Autor;
import com.alura.literalura.domain.model.Libro;
import com.alura.literalura.support.RepositorioAutoresEnMemoria;
import com.alura.literalura.support.RepositorioLibrosEnMemoria;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class MenuConsolaTest {

    @Test
    void listaLibrosYSale() {
        var libros = new RepositorioLibrosEnMemoria();
        libros.guardar(new Libro(null, "Frankenstein", "en", 3, new Autor(null, "Shelley", 1797, 1851)));
        var salida = new ByteArrayOutputStream();
        var menu = new MenuConsola(
                new ConsultarCatalogo(libros, new RepositorioAutoresEnMemoria()),
                titulo -> {
                    throw new AssertionError("No debio importar");
                },
                new Scanner("2\n0\n"),
                new PrintStream(salida, true, StandardCharsets.UTF_8));

        menu.ejecutar();

        assertThat(salida.toString(StandardCharsets.UTF_8)).contains("Frankenstein").contains("Saliendo");
    }
}
