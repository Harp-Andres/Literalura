package com.alura.literalura.console;

import com.alura.literalura.model.Entity.AutorEntity;
import com.alura.literalura.model.Entity.LibroEntity;
import com.alura.literalura.service.AutorCatalogService;
import com.alura.literalura.service.GutendexImportService;
import com.alura.literalura.service.LibroCatalogService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

@Component
@Profile("!test")
public class ConsoleMenuRunner implements CommandLineRunner {

    private final ConsolePresenter presenter;
    private final GutendexImportService gutendexImportService;
    private final LibroCatalogService libroCatalogService;
    private final AutorCatalogService autorCatalogService;

    public ConsoleMenuRunner(
            ConsolePresenter presenter,
            GutendexImportService gutendexImportService,
            LibroCatalogService libroCatalogService,
            AutorCatalogService autorCatalogService) {
        this.presenter = presenter;
        this.gutendexImportService = gutendexImportService;
        this.libroCatalogService = libroCatalogService;
        this.autorCatalogService = autorCatalogService;
    }

    @Override
    public void run(String... args) {
        Map<Integer, String> opciones = buildMenu();
        Scanner teclado = new Scanner(System.in);
        int entrada = -1;

        presenter.line("-------------------------------------------------------------------------------");
        presenter.line("Bienvenido a Literalura — demo de consola (API Gutendex + JPA)");

        do {
            presenter.line("======= Menú Principal =======");
            opciones.forEach((num, texto) -> presenter.line("{} -> {}", num, texto));
            presenter.line("-------------------------------------------------------------------------------");
            presenter.line("Ingrese la opción por su número:");

            try {
                entrada = teclado.nextInt();
                switch (entrada) {
                    case 1 -> handleImport(teclado);
                    case 2 -> listarLibros();
                    case 3 -> listarAutores();
                    case 4 -> autoresPorAnio(teclado);
                    case 5 -> librosPorIdioma(teclado);
                    case 6 -> libroPorTitulo(teclado);
                    case 0 -> presenter.line("Saliendo del programa...");
                    default -> presenter.line("Seleccione un número válido del menú.");
                }
            } catch (InputMismatchException e) {
                presenter.line("Ingrese un número válido del menú.");
                teclado.nextLine();
                entrada = -1;
            }
        } while (entrada != 0);
    }

    private static Map<Integer, String> buildMenu() {
        Map<Integer, String> menu = new LinkedHashMap<>();
        menu.put(1, "Buscar y guardar título por libro en base de datos");
        menu.put(2, "Listar libros registrados");
        menu.put(3, "Listar autores registrados");
        menu.put(4, "Listar autores vivos hasta un año determinado");
        menu.put(5, "Listar libros por idioma");
        menu.put(6, "Listar libro por título");
        menu.put(0, "Salir");
        return menu;
    }

    private void handleImport(Scanner teclado) {
        presenter.line("Ingrese el título del libro que desea buscar y guardar:");
        teclado.nextLine();
        String titulo = teclado.nextLine();
        try {
            gutendexImportService.importarPorTitulo(titulo);
            presenter.line("Operación terminada.");
        } catch (RuntimeException e) {
            presenter.error("Error durante la importación: " + e.getMessage(), e);
        }
    }

    private void listarLibros() {
        presenter.line("Libros registrados:");
        libroCatalogService.listarTodos().forEach(this::printLibro);
    }

    private void listarAutores() {
        presenter.line("Autores registrados:");
        autorCatalogService.listarTodos().forEach(this::printAutor);
    }

    private void autoresPorAnio(Scanner teclado) {
        presenter.line("Ingrese el año límite:");
        int anio = teclado.nextInt();
        autorCatalogService.autoresConMuerteAntesDe(anio).forEach(this::printAutor);
    }

    private void librosPorIdioma(Scanner teclado) {
        presenter.line("Ingrese la abreviatura del idioma (2 letras):");
        teclado.nextLine();
        String idioma = teclado.nextLine();
        libroCatalogService.buscarPorIdioma(idioma).forEach(this::printLibro);
    }

    private void libroPorTitulo(Scanner teclado) {
        presenter.line("Ingrese el nombre del libro:");
        teclado.nextLine();
        String titulo = teclado.nextLine();
        libroCatalogService.buscarPorTitulo(titulo)
                .ifPresentOrElse(
                        this::printLibro,
                        () -> presenter.line("No se encontró el libro en la base de datos.")
                );
    }

    private void printLibro(LibroEntity libro) {
        presenter.line("Título: {}, Lenguaje: {}, Descargas: {}",
                libro.getTituloLibro(), libro.getLenguaje(), libro.getNumeroDescargas());
    }

    private void printAutor(AutorEntity autor) {
        presenter.line("Nombre: {}, Nacimiento: {}, Muerte: {}",
                autor.getNombre(), autor.getAnioNacimiento(), autor.getAnioMuerte());
    }
}
