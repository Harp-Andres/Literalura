CREATE TABLE autores (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    anio_nacimiento INTEGER,
    anio_muerte INTEGER,
    CONSTRAINT uk_autores_nombre UNIQUE (nombre)
);

CREATE TABLE libros (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(500) NOT NULL,
    idioma VARCHAR(8) NOT NULL,
    numero_descargas INTEGER NOT NULL,
    autor_id BIGINT,
    CONSTRAINT uk_libros_titulo UNIQUE (titulo),
    CONSTRAINT fk_libros_autor FOREIGN KEY (autor_id) REFERENCES autores (id)
);

CREATE INDEX idx_libros_idioma ON libros (idioma);
