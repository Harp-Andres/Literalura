package com.alura.literalura.service;

import com.alura.literalura.services.DataDeserializer;
import com.alura.literalura.services.HttpClientExecutor;
import com.alura.literalura.services.HttpRequestBuilder;
import com.alura.literalura.services.ServiceGuardarEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GutendexImportServiceTest {

    @Mock
    private HttpRequestBuilder requestBuilder;
    @Mock
    private HttpClientExecutor clientExecutor;
    @Mock
    private DataDeserializer dataDeserializer;
    @Mock
    private ServiceGuardarEntity serviceGuardarEntity;

    @InjectMocks
    private GutendexImportService gutendexImportService;

    @Test
    void importarPorTituloOrchestratesHttpAndPersistence() {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse<String> response = mock(HttpResponse.class);

        when(requestBuilder.construirGetRequest(any(), eq("?search="), eq("Don%20Quijote"))).thenReturn(request);
        when(clientExecutor.enviarPeticion(request)).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"count\":0,\"results\":[]}");
        when(dataDeserializer.obtenerDatos(any(), any())).thenReturn(
                new com.alura.literalura.model.DTO.DatosLibroDTO(0, java.util.List.of()));

        gutendexImportService.importarPorTitulo("Don Quijote");

        verify(serviceGuardarEntity).guardarAutoresYLibros(any());
    }
}
