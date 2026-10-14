import { ClienteGutendex } from "./adapters/gutendex/cliente-gutendex.js";
import { AlmacenLocal } from "./adapters/persistencia/almacen-local.js";
import { AlmacenamientoEnMemoria } from "./adapters/persistencia/almacenamiento-en-memoria.js";
import { RepositorioAutoresLocal } from "./adapters/persistencia/repositorio-autores-local.js";
import { RepositorioLibrosLocal } from "./adapters/persistencia/repositorio-libros-local.js";
import { ControladorCatalogo } from "./adapters/ui/controlador-catalogo.js";
import { VistaCatalogo } from "./adapters/ui/vista-catalogo.js";
import { ConsultarCatalogo } from "./application/consultar-catalogo.js";
import { ImportarLibro } from "./application/importar-libro.js";

const almacenamiento = almacenamientoDelNavegador();
const almacen = new AlmacenLocal(almacenamiento ?? new AlmacenamientoEnMemoria());
const libros = new RepositorioLibrosLocal(almacen);
const autores = new RepositorioAutoresLocal(almacen);
const vista = new VistaCatalogo(document);

const controlador = new ControladorCatalogo({
    documento: document,
    vista,
    importar: new ImportarLibro({ remoto: new ClienteGutendex(), libros, autores }),
    consultar: new ConsultarCatalogo({ libros, autores }),
    borrarDatos: () => almacen.borrar(),
    confirmar: (mensaje) => window.confirm(mensaje)
});

await controlador.iniciar();

if (almacenamiento === null) {
    vista.mostrarMensaje("datos", "Este navegador no permite guardar datos: el catálogo se perderá al recargar la página.");
}

function almacenamientoDelNavegador() {
    try {
        const clave = "literalura.prueba";
        window.localStorage.setItem(clave, clave);
        window.localStorage.removeItem(clave);
        return window.localStorage;
    } catch {
        return null;
    }
}
