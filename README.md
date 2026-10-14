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

## Integracion y despliegue continuos

Hay dos workflows: CI prueba y CD publica. CD nunca vuelve a correr las pruebas; solo despliega un commit de `main` que ya paso CI.

### CI

`.github/workflows/ci.yml` corre en cada pull request, en cada push a `main` (es decir, al mezclar) y a mano desde la pestaña Actions. Un push nuevo a la misma rama o al mismo pull request cancela la ejecucion anterior.

| Job | Que hace |
| --- | --- |
| `unit` | Pruebas unitarias de Java |
| `database` | Pruebas contra PostgreSQL con Testcontainers |
| `integration` | API con PostgreSQL y WireMock |
| `e2e` | Empaqueta la aplicacion, levanta PostgreSQL, WireMock y la aplicacion con Compose y la prueba en caja negra |
| `site` | Pruebas unitarias y de Playwright de la demo; verifica con `.github/scripts/verificar-site.sh` que `site/` solo tenga `.html`, `.css` y `.js`, y sube el reporte de Playwright si algo falla |

### CD

`.github/workflows/cd.yml` se dispara con `workflow_run` cuando termina CI. Tambien se puede lanzar a mano sobre `main` para volver a publicar.

| Job | Que hace |
| --- | --- |
| `verificar` | Decide que commit se despliega. Si viene de CI, toma el commit que CI probo; si se lanzo a mano, toma la punta de `main` y falla si ese commit no tiene una ejecucion exitosa de CI |
| `artefacto-pages` | Hace checkout de ese commit, repite la verificacion de `site/` y lo sube como artefacto de Pages |
| `pages` | Publica ese artefacto en GitHub Pages (entorno `github-pages`) |

`verificar` solo deja pasar una ejecucion de CI que termino en exito, que fue disparada por un push a `main` y que viene de este repositorio. Una ejecucion fallida o cancelada, o una de otra rama o de un pull request, no despliega nada. `pages` tiene su propia cola de concurrencia: un despliegue nuevo espera al que esta en curso sin interrumpirlo, y una ejecucion que `verificar` descarto no entra a la cola.

Subir y publicar van en jobs separados porque `deploy-pages` a veces no encuentra un artefacto subido una fraccion de segundo antes en el mismo job. Si `pages` falla, se puede usar "Re-run failed jobs": reutiliza el artefacto ya subido. "Re-run all jobs" sube un segundo artefacto con el mismo nombre y `deploy-pages` falla; en ese caso conviene lanzar CD a mano sobre `main`.

### Del pull request al sitio

1. Se abre un pull request: corre CI una vez y aparecen los cinco checks. CD no corre.
2. Con los cinco checks en verde y los hilos de revision resueltos, se mezcla en `main`.
3. El push a `main` vuelve a correr CI sobre el commit mezclado.
4. Si CI pasa, CD publica ese mismo commit en [harp-andres.github.io/Literalura](https://harp-andres.github.io/Literalura/). Si falla, el sitio queda como estaba.

GitHub solo ejecuta los workflows con `workflow_run` desde la rama por defecto, asi que un cambio en `cd.yml` solo se puede probar despues de mezclarlo.

La rama `main` esta protegida:

- Todo cambio entra por pull request.
- `unit`, `database`, `integration`, `e2e` y `site` son checks obligatorios.
- Todos los hilos de revision deben quedar resueltos antes de mezclar.
- No se puede borrar la rama ni hacer force push.

Un pull request con algun check en rojo no se puede mezclar, y como CD solo publica commits de `main` que pasaron CI, ese cambio tampoco llega al sitio.
