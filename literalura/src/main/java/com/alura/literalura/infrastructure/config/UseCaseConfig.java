package com.alura.literalura.infrastructure.config;

import com.alura.literalura.application.ConsultarCatalogo;
import com.alura.literalura.application.ImportarLibro;
import com.alura.literalura.application.ImportarLibroService;
import com.alura.literalura.domain.model.ResultadoImportacion;
import com.alura.literalura.domain.port.CatalogoRemoto;
import com.alura.literalura.domain.port.RepositorioAutores;
import com.alura.literalura.domain.port.RepositorioLibros;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class UseCaseConfig {

    @Bean
    ConsultarCatalogo consultarCatalogo(RepositorioLibros libros, RepositorioAutores autores) {
        return new ConsultarCatalogo(libros, autores);
    }

    @Bean
    ImportarLibro importarLibro(
            CatalogoRemoto remoto,
            RepositorioLibros libros,
            RepositorioAutores autores,
            PlatformTransactionManager transacciones) {
        ImportarLibroService servicio = new ImportarLibroService(remoto, libros, autores);
        TransactionTemplate plantilla = new TransactionTemplate(transacciones);
        return titulo -> {
            ResultadoImportacion resultado = plantilla.execute(estado -> servicio.importar(titulo));
            if (resultado == null) {
                throw new IllegalStateException("La importacion no produjo resultado.");
            }
            return resultado;
        };
    }
}
