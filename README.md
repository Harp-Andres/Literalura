# Literalura

**Literalura** es una aplicación desarrollada en Java con Spring Boot que consume una API de literatura, mapeando los datos obtenidos y almacenándolos en una base de datos PostgreSQL utilizando JPA. Esta aplicacion es una aplicaicon de consola.

---

## Tabla de Contenidos
1. [Descripción General](#descripción-general)
2. [Requisitos](#requisitos)
3. [Configuración del Proyecto](#configuración-del-proyecto)
4. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
5. [Detalles de Implementación](#detalles-de-implementación)
6. [Ejemplo de Ejecución](#ejemplo-de-ejecución)
7. [Pruebas](#pruebas)
8. [Contribuciones](#contribuciones)
9. [Licencia](#licencia)

---

## Descripción General
Literalura es una aplicación diseñada para:

- Consumir datos de una API de literatura que incluye detalles de libros y autores.
- Mapear la respuesta JSON de la API a estructuras Java mediante **records**.
- Persistir los datos obtenidos en una base de datos PostgreSQL utilizando JPA y Hibernate.

El objetivo principal de Literalura es procesar información literaria de manera eficiente y estructurada.

---

## Requisitos

- **Java 11 o superior**
- **Maven 3.8.0 o superior**
- **PostgreSQL**
- Dependencias:
  - Spring Boot Starter Data JPA
  - Gson
  - PostgreSQL Driver

---

## Configuración del Proyecto

1. **Clonar el repositorio:**
   ```bash
   https://github.com/Harp-Andres/Literalura.git
   cd literalura
   ```

2. **Configurar la base de datos:**
   Crear una base de datos en PostgreSQL llamada `literalura`.
   ```sql
   CREATE DATABASE literalura;
   ```

3. **Configurar `application.properties`:**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/literalura
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   ```

4. **Construir y ejecutar el proyecto:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

---

## Arquitectura del Proyecto

### Paquetes Principales:

- **service:** Lógica de negocio.
- **repository:** Acceso y operaciones en la base de datos.
- **model:** Clases `@Entity` para mapeo con JPA.
- **dto:** Clases DTO para el mapeo de datos desde la API.
- **record:** Estructuras para deserializar datos JSON.

### Flujo de Trabajo:
1. El usuario solicita datos de la API.
2. La aplicación consume la API, deserializando la respuesta JSON en records.
3. Los datos se transforman y se guardan en la base de datos PostgreSQL.

---

## Detalles de Implementación

### Records Principales:

1. **DatosLibro:** Mapea la respuesta general de la API.
   ```java
   public record DatosLibro(int numeroRegistros, List<ResultadosLibro> listaResultados) {}
   ```

2. **ResultadosLibro:** Representa cada libro.
   ```java
   public record ResultadosLibro(String tituloLibro, List<Autor> autores, List<String> lenguajes, int numeroDescargas) {}
   ```

3. **Autor:** Representa la información de un autor.
   ```java
   public record Autor(String nombre, int anioNacimiento, int anioMuerte) {}
   ```

### Entidades JPA:

1. **LibroEntity:** Mapeo para libros.
   ```java
   @Entity
   public class LibroEntity {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       private String tituloLibro;
       private String lenguaje;
       private int numeroDescargas;
       @ManyToOne
       private AutorEntity autor;
   }
   ```

2. **AutorEntity:** Mapeo para autores.
   ```java
   @Entity
   public class AutorEntity {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       private String nombre;
       private int anioNacimiento;
       private int anioMuerte;
       @OneToMany(mappedBy = "autor")
       private List<LibroEntity> libros;
   }
   ```

### Manejo de Errores:
Los errores de deserialización con Gson y operaciones de JPA están envueltos en excepciones personalizadas para facilitar el rastreo.

---

## Ejemplo de Ejecución

1. **Realizar una consulta a la API:**
   ```java
   DatosLibro datos = consumirAPI();
   service.procesarDatos(datos);
   ```

2. **Verificar los datos en la base de datos:**
   ```sql
   SELECT * FROM libros;
   SELECT * FROM autores;
   ```

---

## Pruebas

Las pruebas incluyen:
- Pruebas de integración con la base de datos.
- Validación del mapeo de JSON a records.

Ejecución:
```bash
mvn test
```

---

## Contribuciones

Las contribuciones son bienvenidas. Por favor, abre un `pull request` o crea un `issue` para discutir los cambios.

---

## Licencia

Este proyecto está bajo la Licencia MIT. Para más detalles, revisa el archivo `LICENSE`.

