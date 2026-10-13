package com.alura.literalura.infrastructure.gutendex;

import com.alura.literalura.domain.exception.CatalogoRemotoException;
import com.alura.literalura.domain.model.Libro;
import com.alura.literalura.domain.port.CatalogoRemoto;
import com.alura.literalura.infrastructure.config.GutendexProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Component
public class ClienteGutendex implements CatalogoRemoto {

    private static final Logger LOG = LoggerFactory.getLogger(ClienteGutendex.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final GutendexProperties properties;

    public ClienteGutendex(HttpClient httpClient, ObjectMapper objectMapper, GutendexProperties properties) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public List<Libro> buscarPorTitulo(String titulo) {
        HttpRequest request = HttpRequest.newBuilder(GutendexUri.buscar(properties.baseUrl(), titulo))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET()
                .build();
        LOG.info("Consultando Gutendex para '{}'", titulo);
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new CatalogoRemotoException("Gutendex respondio HTTP " + response.statusCode());
            }
            return GutendexMapper.interpretar(response.body(), objectMapper);
        } catch (CatalogoRemotoException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new CatalogoRemotoException("No se pudo consultar Gutendex.", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new CatalogoRemotoException("La consulta a Gutendex fue interrumpida.", ex);
        }
    }
}
