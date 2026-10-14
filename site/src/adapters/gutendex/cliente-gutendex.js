import { CatalogoRemotoError } from "../../domain/errores.js";
import { interpretar } from "./gutendex-mapper.js";

export const GUTENDEX_URL = "https://gutendex.com/books/";
export const TIEMPO_LIMITE_MS = 45_000;

export class ClienteGutendex {
    #baseUrl;
    #fetch;
    #tiempoLimiteMs;

    constructor({
        baseUrl = GUTENDEX_URL,
        fetch = (...argumentos) => globalThis.fetch(...argumentos),
        tiempoLimiteMs = TIEMPO_LIMITE_MS
    } = {}) {
        this.#baseUrl = baseUrl;
        this.#fetch = fetch;
        this.#tiempoLimiteMs = tiempoLimiteMs;
    }

    async buscarPorTitulo(titulo) {
        const url = new URL(this.#baseUrl);
        url.searchParams.set("search", titulo);
        const senal = AbortSignal.timeout(this.#tiempoLimiteMs);

        let respuesta;
        try {
            respuesta = await this.#fetch(url, { headers: { Accept: "application/json" }, signal: senal });
        } catch (error) {
            throw errorDeConexion(error);
        }
        if (!respuesta.ok) {
            throw new CatalogoRemotoError(
                `Gutendex no está disponible en este momento (HTTP ${respuesta.status}). Inténtalo más tarde.`);
        }

        let cuerpo;
        try {
            cuerpo = await respuesta.json();
        } catch (error) {
            if (esTiempoAgotado(error)) {
                throw errorDeConexion(error);
            }
            throw new CatalogoRemotoError("Gutendex devolvió una respuesta que no se pudo leer.", { cause: error });
        }
        return interpretar(cuerpo);
    }
}

function errorDeConexion(error) {
    if (esTiempoAgotado(error)) {
        return new CatalogoRemotoError(
            "Gutendex tardó demasiado en responder. Inténtalo de nuevo en unos minutos.", { cause: error });
    }
    return new CatalogoRemotoError(
        "No se pudo conectar con Gutendex. Revisa tu conexión e inténtalo de nuevo.", { cause: error });
}

function esTiempoAgotado(error) {
    return error?.name === "TimeoutError" || error?.name === "AbortError";
}
