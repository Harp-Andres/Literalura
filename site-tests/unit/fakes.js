export class RepositorioLibrosEnMemoria {
    libros = [];
    #secuencia = 0;

    async listar() {
        return [...this.libros];
    }

    async buscarPorIdioma(idioma) {
        return this.libros.filter((libro) => libro.idioma === idioma);
    }

    async buscarPorTitulo(titulo) {
        return this.libros.find((libro) => libro.titulo.toLowerCase() === titulo.toLowerCase()) ?? null;
    }

    async existePorTitulo(titulo) {
        return (await this.buscarPorTitulo(titulo)) !== null;
    }

    async guardar(libro) {
        const guardado = libro.conId(++this.#secuencia);
        this.libros.push(guardado);
        return guardado;
    }
}

export class RepositorioAutoresEnMemoria {
    autores = [];
    #secuencia = 0;

    async listar() {
        return [...this.autores];
    }

    async buscarPorNombre(nombre) {
        return this.autores.find((autor) => autor.nombre.toLowerCase() === nombre.toLowerCase()) ?? null;
    }

    async guardar(autor) {
        const guardado = autor.conId(++this.#secuencia);
        this.autores.push(guardado);
        return guardado;
    }
}

export class CatalogoRemotoFijo {
    consultas = [];

    constructor(libros) {
        this.libros = libros;
    }

    async buscarPorTitulo(titulo) {
        this.consultas.push(titulo);
        return this.libros;
    }
}
