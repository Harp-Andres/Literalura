export const CLAVE_CATALOGO = "literalura.catalogo";

export class AlmacenLocal {
    #storage;
    #clave;

    constructor(storage, clave = CLAVE_CATALOGO) {
        this.#storage = storage;
        this.#clave = clave;
    }

    leer() {
        const crudo = this.#storage.getItem(this.#clave);
        if (crudo === null) {
            return estadoVacio();
        }
        try {
            const estado = JSON.parse(crudo);
            return esEstadoValido(estado) ? estado : estadoVacio();
        } catch {
            return estadoVacio();
        }
    }

    modificar(cambio) {
        const estado = this.leer();
        const resultado = cambio(estado);
        this.#storage.setItem(this.#clave, JSON.stringify(estado));
        return resultado;
    }

    borrar() {
        this.#storage.removeItem(this.#clave);
    }
}

export function siguienteId(estado) {
    estado.secuencia += 1;
    return estado.secuencia;
}

export function mismoTexto(a, b) {
    return a.toLowerCase() === b.toLowerCase();
}

function estadoVacio() {
    return { secuencia: 0, autores: [], libros: [] };
}

function esEstadoValido(estado) {
    return estado !== null
        && typeof estado === "object"
        && Number.isInteger(estado.secuencia)
        && Array.isArray(estado.autores)
        && Array.isArray(estado.libros);
}
