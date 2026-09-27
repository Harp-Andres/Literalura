package com.alura.literalura.service;

import com.alura.literalura.constants.Constantes;
import com.alura.literalura.model.DTO.DatosLibroDTO;
import com.alura.literalura.services.DataDeserializer;
import com.alura.literalura.services.HttpClientExecutor;
import com.alura.literalura.services.HttpRequestBuilder;
import com.alura.literalura.services.ServiceGuardarEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GutendexImportService {

    private static final Logger LOG = LoggerFactory.getLogger(GutendexImportService.class);

    private final HttpRequestBuilder requestBuilder;
    private final HttpClientExecutor clientExecutor;
    private final DataDeserializer dataDeserializer;
    private final ServiceGuardarEntity serviceGuardarEntity;

    public GutendexImportService(
            HttpRequestBuilder requestBuilder,
            HttpClientExecutor clientExecutor,
            DataDeserializer dataDeserializer,
            ServiceGuardarEntity serviceGuardarEntity) {
        this.requestBuilder = requestBuilder;
        this.clientExecutor = clientExecutor;
        this.dataDeserializer = dataDeserializer;
        this.serviceGuardarEntity = serviceGuardarEntity;
    }

    public void importarPorTitulo(String tituloLibro) {
        String query = tituloLibro.replace(" ", "%20");
        LOG.info("Consultando Gutendex para titulo='{}'", tituloLibro);

        HttpRequest getRequest = requestBuilder.construirGetRequest(
                Constantes.URL_GUTENDEX, "?search=", query);
        HttpResponse<String> response = clientExecutor.enviarPeticion(getRequest);
        LOG.info("Gutendex respondio con HTTP {}", response.statusCode());

        DatosLibroDTO datosLibroDTO = dataDeserializer.obtenerDatos(response.body(), DatosLibroDTO.class);
        serviceGuardarEntity.guardarAutoresYLibros(datosLibroDTO);
    }
}
