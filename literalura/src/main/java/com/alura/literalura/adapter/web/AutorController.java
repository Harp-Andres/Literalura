package com.alura.literalura.adapter.web;

import com.alura.literalura.adapter.web.dto.AutorResponse;
import com.alura.literalura.application.ConsultarCatalogo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
public class AutorController {

    private final ConsultarCatalogo consultar;

    public AutorController(ConsultarCatalogo consultar) {
        this.consultar = consultar;
    }

    @GetMapping
    public List<AutorResponse> listar(@RequestParam(required = false) Integer vivosEn) {
        if (vivosEn != null) {
            return consultar.autoresVivosEn(vivosEn).stream().map(AutorResponse::desde).toList();
        }
        return consultar.listarAutores().stream().map(AutorResponse::desde).toList();
    }
}
