import { DatoInvalidoError } from "./errores.js";
import { tieneContenido } from "./texto.js";

export class Autor {
    constructor({ id = null, nombre, anioNacimiento = null, anioMuerte = null }) {
        if (!tieneContenido(nombre)) {
            throw new DatoInvalidoError("El nombre del autor es obligatorio.");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.anioNacimiento = anioOpcional(anioNacimiento);
        this.anioMuerte = anioOpcional(anioMuerte);
        Object.freeze(this);
    }

    estabaVivoEn(anio) {
        if (this.anioNacimiento === null || this.anioNacimiento > anio) {
            return false;
        }
        return this.anioMuerte === null || this.anioMuerte >= anio;
    }

    conId(nuevoId) {
        return new Autor({ ...this, id: nuevoId });
    }
}

function anioOpcional(anio) {
    return Number.isInteger(anio) ? anio : null;
}
