import { Autor } from "../../domain/autor.js";
import { mismoTexto, siguienteId } from "./almacen-local.js";

export class RepositorioAutoresLocal {
    #almacen;

    constructor(almacen) {
        this.#almacen = almacen;
    }

    async listar() {
        return this.#almacen.leer().autores.map(aAutor);
    }

    async buscarPorNombre(nombre) {
        const registro = this.#almacen.leer().autores.find((autor) => mismoTexto(autor.nombre, nombre));
        return registro ? aAutor(registro) : null;
    }

    async guardar(autor) {
        return this.#almacen.modificar((estado) => {
            const id = siguienteId(estado);
            estado.autores.push({
                id,
                nombre: autor.nombre,
                anioNacimiento: autor.anioNacimiento,
                anioMuerte: autor.anioMuerte
            });
            return autor.conId(id);
        });
    }
}

export function aAutor(registro) {
    return new Autor(registro);
}
