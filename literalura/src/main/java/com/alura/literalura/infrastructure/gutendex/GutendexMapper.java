package com.alura.literalura.infrastructure.gutendex;

import com.alura.literalura.domain.model.Autor;
import com.alura.literalura.domain.model.Libro;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

public final class GutendexMapper {

    private GutendexMapper() {
    }

    public static List<Libro> interpretar(String json, ObjectMapper mapper) throws JsonProcessingException {
        GutendexPage page = mapper.readValue(json, GutendexPage.class);
        if (page == null || page.results() == null) {
            return List.of();
        }
        return page.results().stream()
                .map(GutendexMapper::aLibro)
                .flatMap(Optional::stream)
                .toList();
    }

    private static Optional<Libro> aLibro(GutendexBook book) {
        if (book == null || book.title() == null || book.title().isBlank()) {
            return Optional.empty();
        }
        String idioma = book.languages() == null || book.languages().isEmpty()
                ? "und"
                : book.languages().getFirst();
        int descargas = book.downloadCount() == null || book.downloadCount() < 0 ? 0 : book.downloadCount();
        try {
            return Optional.of(new Libro(null, book.title(), idioma, descargas, autorPrincipal(book.authors())));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private static Autor autorPrincipal(List<GutendexAuthor> authors) {
        if (authors == null || authors.isEmpty() || authors.getFirst() == null) {
            return null;
        }
        GutendexAuthor autor = authors.getFirst();
        if (autor.name() == null || autor.name().isBlank()) {
            return null;
        }
        return new Autor(null, autor.name(), autor.birthYear(), autor.deathYear());
    }
}
