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

## Demo en GitHub Pages

[harp-andres.github.io/Literalura](https://harp-andres.github.io/Literalura/) publica una version del catalogo que corre entera en el navegador, sin Spring Boot ni PostgreSQL. Consulta Gutendex directamente y guarda libros y autores en el `localStorage` del visitante: los datos solo existen en ese navegador y se eliminan con el boton "Borrar datos locales".

Sigue la misma arquitectura en modulos ES sin dependencias ni paso de build:

```
site/src/domain/        Autor, Libro y errores
site/src/application/   ImportarLibro y ConsultarCatalogo
site/src/adapters/      cliente de Gutendex, repositorios en localStorage e interfaz
```

Las pruebas viven aparte, en `site-tests/`, para que `site/` solo contenga lo que se publica. Requiere Node 22.12 o superior.

```bash
cd site-tests
npm ci
npm run test:unit                      # dominio, casos de uso, Gutendex y persistencia con node --test
npx playwright install chromium
npm run test:e2e                       # Playwright con Gutendex simulado
npm run servir                         # http://127.0.0.1:4173/Literalura/
```

## Integracion continua

En cada push hay cinco checks: `unit`, `database`, `integration`, `e2e` y `site`. El job `e2e` levanta la aplicacion, PostgreSQL y WireMock; `site` ejecuta las pruebas de la demo del navegador. GitHub Pages se publica solo en `main`, y solo si esos cinco checks pasaron.

Para que un pull request hacia `main` no se pueda mezclar con checks en rojo, el repositorio debe exigir `unit`, `database`, `integration`, `e2e` y `site` como status checks obligatorios.
