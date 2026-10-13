# Literalura

Catalogo de libros con una arquitectura hexagonal: el dominio y los casos de uso no conocen Spring, JPA ni HTTP. PostgreSQL, Gutendex y la interfaz (web o consola) son adaptadores.

## Arquitectura

```
domain/            modelo, reglas y puertos
application/       casos de uso
infrastructure/    JPA, Flyway, cliente Gutendex
adapter/web        API y pagina
adapter/console    menu, solo con el perfil cli
```

La regla de "autor vivo en un año" vive en `Autor`. La persistencia solo guarda y recupera.

## Stack

- Java 21
- Spring Boot 3.5
- PostgreSQL 16 y Flyway
- Jackson
- Docker Compose

## Pruebas

Cada tipo corre aparte. `./mvnw test` ejecuta solo las unitarias y no necesita Docker.

| Tipo | Comando | Que cubre |
| --- | --- | --- |
| Unitarias | `./mvnw test` | Dominio, casos de uso, mapeo y arquitectura |
| Base de datos | `./mvnw test -Pdatabase` | Repositorios contra PostgreSQL real con Testcontainers |
| Integracion | `./mvnw test -Pintegration` | API, caso de uso, PostgreSQL y Gutendex simulado con WireMock |
| Extremo a extremo | ver abajo | El mismo Compose que en local, en caja negra |

Para base de datos e integracion se usa Testcontainers, que es la opcion habitual en Java frente a Robot Framework: levanta PostgreSQL de verdad y no agrega otro lenguaje al pipeline.

## Ejecutar en local

Requiere Docker.

```bash
cd literalura
./mvnw -DskipTests package
cd ..
docker compose up --build
```

La pagina queda en `http://localhost:8080`.

La consola interactiva usa el perfil `cli` y una base ya levantada:

```bash
cd literalura
./mvnw spring-boot:run -Dspring-boot.run.profiles=cli
```

Variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `GUTENDEX_BASE_URL`.

## Extremo a extremo

Sustituye Gutendex por WireMock para que el resultado no dependa de la red.

```bash
cd literalura
./mvnw -DskipTests package
cd ..
docker compose -f docker-compose.yml -f docker-compose.ci.yml up --build -d --wait
cd literalura
./mvnw test -Pe2e
docker compose -f docker-compose.yml -f docker-compose.ci.yml down -v
```

## Integracion continua

En cada push hay cuatro checks: `unit`, `database`, `integration` y `e2e`. El job `e2e` levanta la aplicacion, PostgreSQL y WireMock. GitHub Pages se publica solo en `main`, y solo si esos cuatro checks pasaron.

Para que un pull request hacia `main` no se pueda mezclar con checks en rojo, el repositorio debe exigir `unit`, `database`, `integration` y `e2e` como status checks obligatorios.
