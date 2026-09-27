# Literalura — Spring Boot API client + JPA demo

Console application that consumes the public [Gutendex](https://gutendex.com/) books API, maps JSON into DTOs, and persists authors and books with **Spring Data JPA** and **PostgreSQL** (H2 in tests).

## Stack

- Java 21
- Spring Boot 3.4
- `java.net.http` client + Gson
- PostgreSQL (runtime) / H2 (tests)
- SLF4J logging (no `System.out` in domain or services)

## Layout

```
literalura/
  src/main/java/com/alura/literalura/
    console/     # Console UI adapter (CommandLineRunner, presenter)
    service/     # Application services (catalog, Gutendex import)
    services/    # HTTP, mapping, persistence orchestration
    model/       # JPA entities + Gson DTOs
    repository/  # Spring Data repositories
```

The interactive menu lives in `ConsoleMenuRunner` (`@Profile("!test")`). Business rules sit in `*CatalogService` and `GutendexImportService`.

## Configuration

Set database credentials via environment variables (do not commit secrets):

```properties
spring.datasource.username=${LOGIN_USERNAME}
spring.datasource.password=${LOGIN_PASSWORD}
```

Default JDBC URL targets local PostgreSQL database `literalura`.

## Run

```bash
cd literalura
./mvnw test          # unit + context tests (H2)
./mvnw spring-boot:run
```

## Tests

- `DataDeserializerTest`, `DataToEntityMapperTest` — pure mapping
- `LibroCatalogServiceTest`, `GutendexImportServiceTest` — Mockito
- `LiteraluraApplicationTests` — Spring context (`test` profile)

## Suggested GitHub repository name

`demo-spring-boot-gutendex-jpa`

## License

MIT
