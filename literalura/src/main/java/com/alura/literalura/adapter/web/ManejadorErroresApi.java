package com.alura.literalura.adapter.web;

import com.alura.literalura.adapter.web.dto.ErrorResponse;
import com.alura.literalura.domain.exception.CatalogoRemotoException;
import com.alura.literalura.domain.exception.ReglaDeNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorErroresApi {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorErroresApi.class);

    @ExceptionHandler(ReglaDeNegocioException.class)
    ResponseEntity<ErrorResponse> negocio(ReglaDeNegocioException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Solicitud invalida.");
        return ResponseEntity.badRequest().body(new ErrorResponse(mensaje));
    }

    @ExceptionHandler(CatalogoRemotoException.class)
    ResponseEntity<ErrorResponse> remoto(CatalogoRemotoException ex) {
        LOG.warn("Fallo el catalogo remoto: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorResponse(ex.getMessage()));
    }
}
