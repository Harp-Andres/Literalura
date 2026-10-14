export function tieneContenido(valor) {
    return typeof valor === "string" && valor.trim() !== "";
}
