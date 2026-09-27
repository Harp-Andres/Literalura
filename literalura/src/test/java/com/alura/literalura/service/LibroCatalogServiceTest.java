package com.alura.literalura.service;

import com.alura.literalura.model.Entity.LibroEntity;
import com.alura.literalura.repository.LibroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibroCatalogServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroCatalogService libroCatalogService;

    @Test
    void listarTodosDelegatesToRepository() {
        LibroEntity libro = new LibroEntity();
        libro.setTituloLibro("Test");
        when(libroRepository.findAll()).thenReturn(List.of(libro));

        assertEquals(1, libroCatalogService.listarTodos().size());
        verify(libroRepository).findAll();
    }

    @Test
    void buscarPorTituloReturnsOptional() {
        LibroEntity libro = new LibroEntity();
        when(libroRepository.findByTituloLibroIgnoreCase("x")).thenReturn(Optional.of(libro));

        assertTrue(libroCatalogService.buscarPorTitulo("x").isPresent());
    }
}
