const formatoNumero = new Intl.NumberFormat("es");

export class VistaCatalogo {
    #documento;
    #libros;
    #autores;
    #estados;

    constructor(documento) {
        this.#documento = documento;
        this.#libros = documento.querySelector("#libros");
        this.#autores = documento.querySelector("#autores");
        this.#estados = {
            importacion: documento.querySelector("#estado-importacion"),
            libros: documento.querySelector("#estado-libros"),
            autores: documento.querySelector("#estado-autores"),
            datos: documento.querySelector("#estado-datos")
        };
    }

    mostrarLibros(libros, mensajeVacio) {
        this.#pintar(this.#libros, libros.map((libro) => this.#itemLibro(libro)), mensajeVacio);
    }

    mostrarAutores(autores, mensajeVacio) {
        this.#pintar(this.#autores, autores.map((autor) => this.#itemAutor(autor)), mensajeVacio);
    }

    mostrarMensaje(zona, texto) {
        this.#escribirEstado(zona, texto, false);
    }

    mostrarError(zona, texto) {
        this.#escribirEstado(zona, texto, true);
    }

    limpiarMensajes() {
        for (const zona of Object.keys(this.#estados)) {
            this.mostrarMensaje(zona, "");
        }
    }

    marcarOcupado(formulario, ocupado) {
        formulario.setAttribute("aria-busy", String(ocupado));
        for (const boton of formulario.querySelectorAll("button")) {
            boton.disabled = ocupado;
        }
    }

    #escribirEstado(zona, texto, esError) {
        const estado = this.#estados[zona];
        estado.textContent = texto;
        estado.classList.toggle("error", esError);
    }

    #pintar(lista, items, mensajeVacio) {
        if (items.length === 0) {
            lista.replaceChildren(this.#elemento("li", "vacio", mensajeVacio));
            return;
        }
        lista.replaceChildren(...items);
    }

    #itemLibro(libro) {
        const autor = libro.autor ? libro.autor.nombre : "Autor desconocido";
        const descargas = `${formatoNumero.format(libro.numeroDescargas)} descargas`;
        return this.#item(libro.titulo, `${autor} · Idioma: ${libro.idioma} · ${descargas}`);
    }

    #itemAutor(autor) {
        return this.#item(autor.nombre, `${autor.anioNacimiento ?? "?"} – ${autor.anioMuerte ?? "?"}`);
    }

    #item(titulo, detalle) {
        const item = this.#documento.createElement("li");
        item.append(this.#elemento("span", "titulo", titulo), this.#elemento("span", "detalle", detalle));
        return item;
    }

    #elemento(etiqueta, clase, texto) {
        const elemento = this.#documento.createElement(etiqueta);
        elemento.className = clase;
        elemento.textContent = texto;
        return elemento;
    }
}
