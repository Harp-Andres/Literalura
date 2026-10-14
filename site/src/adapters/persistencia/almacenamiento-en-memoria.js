export class AlmacenamientoEnMemoria {
    #valores = new Map();

    getItem(clave) {
        return this.#valores.has(clave) ? this.#valores.get(clave) : null;
    }

    setItem(clave, valor) {
        this.#valores.set(clave, String(valor));
    }

    removeItem(clave) {
        this.#valores.delete(clave);
    }
}
