package com.alura.literalura.adapter.console;

import com.alura.literalura.application.ConsultarCatalogo;
import com.alura.literalura.application.ImportarLibro;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@Profile("cli")
public class ConsoleMenuRunner implements CommandLineRunner {

    private final ConsultarCatalogo consultar;
    private final ImportarLibro importar;

    public ConsoleMenuRunner(ConsultarCatalogo consultar, ImportarLibro importar) {
        this.consultar = consultar;
        this.importar = importar;
    }

    @Override
    public void run(String... args) {
        new MenuConsola(consultar, importar, new Scanner(System.in), System.out).ejecutar();
    }
}
