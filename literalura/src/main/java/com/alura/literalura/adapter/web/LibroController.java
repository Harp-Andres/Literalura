package com.alura.literalura.adapter.web;

import com.alura.literalura.adapter.web.dto.ImportarLibroRequest;
import com.alura.literalura.adapter.web.dto.LibroResponse;
import com.alura.literalura.adapter.web.dto.ResultadoImportacionResponse;
import com.alura.literalura.application.ConsultarCatalogo;
import com.alura.literalura.application.ImportarLibro;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final ConsultarCatalogo consultar;
    private final ImportarLibro importar;

    public LibroController(ConsultarCatalogo consultar, ImportarLibro importar) {
        this.consultar = consultar;
        this.importar = importar;
    }

    @GetMapping
    public List<LibroResponse> listar(
            @RequestParam(required = false) String idioma,
            @RequestParam(required = false) String titulo) {
        if (idioma != null) {
            return consultar.librosPorIdioma(idioma).stream().map(LibroResponse::desde).toList();
        }
        if (titulo != null) {
            return consultar.libroPorTitulo(titulo).stream().map(LibroResponse::desde).toList();
        }
        return consultar.listarLibros().stream().map(LibroResponse::desde).toList();
    }

    @PostMapping("/importaciones")
    public ResultadoImportacionResponse importar(@Valid @RequestBody ImportarLibroRequest request) {
        return ResultadoImportacionResponse.desde(importar.importar(request.titulo()));
    }
}
