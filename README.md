# Literalura

Catalogo de libros que importa titulos desde [Gutendex](https://gutendex.com/) y permite consultar los libros y autores guardados. Hay dos formas de usarlo:

- **Sistema completo** en `literalura/`: Spring Boot, PostgreSQL y Docker Compose, con API REST, pagina web y menu de consola.
- **Demo en el navegador** en `site/`, publicada en [harp-andres.github.io/Literalura](https://harp-andres.github.io/Literalura/): corre entera en el navegador, sin servidor ni base de datos.

Las dos siguen una arquitectura hexagonal: el dominio y los casos de uso no conocen frameworks, bases de datos ni HTTP. La persistencia, Gutendex y la interfaz son adaptadores.

## Que hace

- **Importar**: busca un titulo en Gutendex y guarda los libros encontrados con su autor. Los que ya estaban guardados se cuentan como omitidos.
- **Libros**: listarlos todos, filtrarlos por idioma (codigo de dos letras) o buscar uno por su titulo exacto.
- **Autores**: listarlos todos o solo los que estaban vivos en un año.

## Arquitectura

### Sistema completo

```
domain/            modelo, reglas y puertos
application/       casos de uso
infrastructure/    JPA, Flyway, cliente Gutendex
adapter/web        API y pagina
adapter/console    menu, solo con el perfil cli
```

La regla de "autor vivo en un año" vive en `Autor`. La persistencia solo guarda y recupera. Una prueba con ArchUnit falla si el dominio o los casos de uso dependen de Spring, Jakarta o de los adaptadores.

API REST:

| Metodo | Ruta | Uso |
| --- | --- | --- |
| `POST` | `/api/libros/importaciones` | Importa un titulo: `{"titulo": "Frankenstein"}` |
| `GET` | `/api/libros` | Todos, `?idioma=es` o `?titulo=...` |
| `GET` | `/api/autores` | Todos o `?vivosEn=1818` |
| `GET` | `/actuator/health` | Estado de la aplicacion |

### Demo en el navegador

La misma arquitectura en modulos ES, sin dependencias ni paso de build:

```
site/src/domain/        Autor, Libro y errores
site/src/application/   ImportarLibro y ConsultarCatalogo
site/src/adapters/      cliente de Gutendex, repositorios en localStorage e interfaz
site/src/main.js        conecta los adaptadores con los casos de uso
```

## Stack

- Java 21
- Spring Boot 3.5
- PostgreSQL 16 y Flyway
- Jackson
- Docker Compose
- JavaScript (modulos ES) para la demo; Node 22.12 o superior y Playwright solo para sus pruebas

## Pruebas

Cada tipo corre aparte y es un check distinto en CI. `./mvnw test` ejecuta solo las unitarias y no necesita Docker.

| Tipo | Comando | Check | Que cubre |
| --- | --- | --- | --- |
| Unitarias | `./mvnw test` | `unit` | Dominio, casos de uso, mapeo y arquitectura |
| Base de datos | `./mvnw test -Pdatabase` | `database` | Repositorios contra PostgreSQL real con Testcontainers |
| Integracion | `./mvnw test -Pintegration` | `integration` | API, caso de uso, PostgreSQL y Gutendex simulado con WireMock |
| Extremo a extremo | ver abajo | `e2e` | El mismo Compose que en local, en caja negra |
| Unitarias del sitio | `npm run test:unit` | `site` | Dominio, casos de uso, cliente de Gutendex y persistencia de la demo, con `node --test` |
| Extremo a extremo del sitio | `npm run test:e2e` | `site` | La demo en Chromium con Playwright, servida bajo `/Literalura/` como en Pages y con Gutendex simulado |

Los comandos de Maven se ejecutan en `literalura/` y los de npm en `site-tests/`. Base de datos e integracion necesitan Docker.

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

[harp-andres.github.io/Literalura](https://harp-andres.github.io/Literalura/) importa y consulta el catalogo con los mismos casos de uso que el sistema completo, pero no usa la API ni PostgreSQL:

- Consulta Gutendex directamente desde el navegador (Gutendex permite CORS).
- Guarda libros y autores en el `localStorage` del visitante: los datos solo existen en ese navegador, nadie mas los ve y se eliminan con el boton "Borrar datos locales". Si el navegador no permite `localStorage`, el catalogo vive en memoria y se pierde al recargar.
- Una busqueda que Gutendex no tiene en cache puede tardar unos 40 segundos. La demo espera hasta 45 segundos antes de mostrar un error.

Las pruebas viven aparte, en `site-tests/`, para que `site/` solo contenga lo que se publica.

```bash
cd site-tests
npm ci
npm run test:unit                      # node --test, sin navegador
npx playwright install chromium        # una sola vez
npm run test:e2e                       # Playwright; levanta solo el servidor estatico
npm test                               # unitarias y Playwright
npm run servir                         # http://127.0.0.1:4173/Literalura/
```

## Integracion continua

El workflow `.github/workflows/ci.yml` corre en cada push a cualquier rama y en cada pull request. Un push nuevo a la misma rama cancela la ejecucion anterior.

| Job | Que hace |
| --- | --- |
| `unit` | Pruebas unitarias de Java |
| `database` | Pruebas contra PostgreSQL con Testcontainers |
| `integration` | API con PostgreSQL y WireMock |
| `e2e` | Empaqueta la aplicacion, levanta PostgreSQL, WireMock y la aplicacion con Compose y la prueba en caja negra |
| `site` | Pruebas unitarias y de Playwright de la demo; verifica que `site/` solo tenga `.html`, `.css` y `.js`, y sube el reporte de Playwright si algo falla |
| `pages` | Publica `site/` en GitHub Pages |

`pages` solo corre en un push a `main` y solo si los otros cinco jobs pasaron. En las demas ramas y en los pull requests corren los cinco checks, pero no se publica nada.

La rama `main` esta protegida:

- Todo cambio entra por pull request.
- `unit`, `database`, `integration`, `e2e` y `site` son checks obligatorios.
- Todos los hilos de revision deben quedar resueltos antes de mezclar.
- No se puede borrar la rama ni hacer force push.

Un pull request con algun check en rojo no se puede mezclar, y como Pages solo se publica desde `main` despues de que pasen los cinco checks, ese cambio tampoco llega al sitio.
