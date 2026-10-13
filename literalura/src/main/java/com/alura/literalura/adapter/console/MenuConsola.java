package com.alura.literalura.adapter.console;

import com.alura.literalura.application.ConsultarCatalogo;
import com.alura.literalura.application.ImportarLibro;
import com.alura.literalura.domain.model.Autor;
import com.alura.literalura.domain.model.Libro;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public final class MenuConsola {

    private final ConsultarCatalogo consultar;
    private final ImportarLibro importar;
    private final Scanner entrada;
    private final java.io.PrintStream salida;

    public MenuConsola(ConsultarCatalogo consultar, ImportarLibro importar, Scanner entrada, java.io.PrintStream salida) {
        this.consultar = consultar;
        this.importar = importar;
        this.entrada = entrada;
        this.salida = salida;
    }

    public void ejecutar() {
        salida.println("-------------------------------------------------------------------------------");
        salida.println("Bienvenido a Literalura");
        int opcion = -1;
        do {
            imprimirOpciones();
            try {
                opcion = entrada.nextInt();
                switch (opcion) {
                    case 1 -> importarTitulo();
                    case 2 -> imprimirLibros(consultar.listarLibros());
                    case 3 -> imprimirAutores(consultar.listarAutores());
                    case 4 -> autoresVivos();
                    case 5 -> librosPorIdioma();
                    case 6 -> libroPorTitulo();
                    case 0 -> salida.println("Saliendo del programa.");
                    default -> salida.println("Seleccione un numero valido del menu.");
                }
            } catch (InputMismatchException ex) {
                salida.println("Ingrese un numero valido del menu.");
                entrada.nextLine();
                opcion = -1;
            } catch (RuntimeException ex) {
                salida.println(ex.getMessage());
            }
        } while (opcion != 0);
    }

    private void imprimirOpciones() {
        salida.println("======= Menu principal =======");
        salida.println("1 -> Buscar y guardar un titulo");
        salida.println("2 -> Listar libros registrados");
        salida.println("3 -> Listar autores registrados");
        salida.println("4 -> Listar autores vivos en un año");
        salida.println("5 -> Listar libros por idioma");
        salida.println("6 -> Buscar libro por titulo");
        salida.println("0 -> Salir");
    }

    private void importarTitulo() {
        salida.println("Ingrese el titulo:");
        entrada.nextLine();
        String titulo = entrada.nextLine();
        var resultado = importar.importar(titulo);
        salida.println("Guardados: " + resultado.guardados() + ". Omitidos: " + resultado.omitidos() + ".");
        imprimirLibros(resultado.libros());
    }

    private void autoresVivos() {
        salida.println("Ingrese el año:");
        int anio = entrada.nextInt();
        imprimirAutores(consultar.autoresVivosEn(anio));
    }

    private void librosPorIdioma() {
        salida.println("Ingrese el codigo de idioma (2 letras):");
        entrada.nextLine();
        imprimirLibros(consultar.librosPorIdioma(entrada.nextLine()));
    }

    private void libroPorTitulo() {
        salida.println("Ingrese el titulo:");
        entrada.nextLine();
        consultar.libroPorTitulo(entrada.nextLine())
                .ifPresentOrElse(
                        libro -> imprimirLibros(List.of(libro)),
                        () -> salida.println("No se encontro el libro en la base de datos."));
    }

    private void imprimirLibros(List<Libro> libros) {
        if (libros.isEmpty()) {
            salida.println("No hay libros para mostrar.");
            return;
        }
        libros.forEach(libro -> salida.println("Titulo: " + libro.titulo()
                + " | Idioma: " + libro.idioma()
                + " | Descargas: " + libro.numeroDescargas()
                + " | Autor: " + (libro.autor() == null ? "sin autor" : libro.autor().nombre())));
    }

    private void imprimirAutores(List<Autor> autores) {
        if (autores.isEmpty()) {
            salida.println("No hay autores para mostrar.");
            return;
        }
        autores.forEach(autor -> salida.println("Nombre: " + autor.nombre()
                + " | Nacimiento: " + autor.anioNacimiento()
                + " | Muerte: " + autor.anioMuerte()));
    }
}
