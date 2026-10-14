export class DatoInvalidoError extends Error {
    constructor(mensaje) {
        super(mensaje);
        this.name = new.target.name;
    }
}

export class ReglaDeNegocioError extends Error {
    constructor(mensaje) {
        super(mensaje);
        this.name = new.target.name;
    }
}

export class TituloInvalidoError extends ReglaDeNegocioError {
}

export class IdiomaInvalidoError extends ReglaDeNegocioError {
}

export class AnioInvalidoError extends ReglaDeNegocioError {
}

export class CatalogoRemotoError extends Error {
    constructor(mensaje, opciones) {
        super(mensaje, opciones);
        this.name = new.target.name;
    }
}
