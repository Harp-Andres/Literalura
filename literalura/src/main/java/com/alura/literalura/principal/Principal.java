package com.alura.literalura.principal;

import com.alura.literalura.constants.Constantes;
import com.alura.literalura.model.DTO.DatosLibroDTO;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.services.DataDeserializer;
import com.alura.literalura.services.HttpClientExecutor;
import com.alura.literalura.services.HttpRequestBuilder;
import com.alura.literalura.services.ServiceGuardarEntity;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Principal {

    private final DataDeserializer dataDeserializer = new DataDeserializer();
    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;
    Scanner teclado = new Scanner(System.in);
    int entradaTecladoInt;
    String entradaTecladoString;
    // Crear una lista de mapas para almacenar las opciones
    List<Map<Integer, String>> menuOpciones = new ArrayList<>();
    HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    HttpClientExecutor clientExecutor = new HttpClientExecutor();
    String opcionesBusquedaAPI;

    public Principal(AutorRepository autorRepository, LibroRepository libroRepository) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
    }

    /**
     * Método para agregar una opción al menú.
     *
     * @param lista  Lista de opciones del menú.
     * @param numero Número de la opción.
     * @param texto  Texto descriptivo de la opción.
     */
    private static void agregarOpcion(List<Map<Integer, String>> lista, int numero, String texto) {
        Map<Integer, String> opcion = new HashMap<>();
        opcion.put(numero, texto); // Usar 'numero' como clave (tipo Integer)
        lista.add(opcion);
    }

    public void muestraElMenu() {

        // Añadir opciones al menú
        agregarOpcion(menuOpciones, 1, "Buscar y guardar titulo por libro en base de datos");
        agregarOpcion(menuOpciones, 2, "Listar libros registrados en base de datos");
        agregarOpcion(menuOpciones, 3, "Listar autores registrados en base de datos");
        agregarOpcion(menuOpciones, 4, "Listar autores vivos hasta un determinado año registrados en base de datos");
        agregarOpcion(menuOpciones, 5, "Listar libros por idioma registrados en base de datos");
        agregarOpcion(menuOpciones, 6, "Listar libros por titulo de libro registrado en base de datos");
        agregarOpcion(menuOpciones, 0, "Salir del programa");
        System.out.println("-------------------------------------------------------------------------------\n");
        System.out.println("Bienvenido a Literalura una aplicacion de consola de literatura!!!");
        do {
            System.out.println("======= Menú Principal =======");
            System.out.println("Ingrese porfavor la opcion que desee a través de su numero: ");
            for (Map<Integer, String> opcion : menuOpciones) {
                for (Map.Entry<Integer, String> entry : opcion.entrySet())
                    System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
            System.out.println("-------------------------------------------------------------------------------\n");
            System.out.println("Ingrese porfavor la opcion que desee a través de su numero: ");
            try {
                entradaTecladoInt = teclado.nextInt();
                switch (entradaTecladoInt) {
                    case 1:
                        opcionesBusquedaAPI = "?search=";
                        System.out.print("Ingrese el titulo del libro que desea buscar y guardar: ");
                        teclado.nextLine(); // Limpiar el buffer de entrada
                        entradaTecladoString = teclado.nextLine();
                        var busquedaEnAPI = entradaTecladoString.replace(" ", "%20");
                        System.out.println("Imprimir busqueda en API:" + busquedaEnAPI);
                        try {
                            // Construir solicitud GET
                            HttpRequest getRequest = requestBuilder.construirGetRequest(Constantes.URL_GUTENDEX, opcionesBusquedaAPI,
                                    busquedaEnAPI);
                            // Enviar solicitud y manejar respuesta
                            HttpResponse<String> response = clientExecutor.enviarPeticion(getRequest);
                            System.out.println("Código de estado: " + response.statusCode());
                            System.out.println("Cuerpo de la respuesta: " + response.body());
                            DatosLibroDTO datosLibroDTO = dataDeserializer.obtenerDatos(response.body(), DatosLibroDTO.class);
                            System.out.println("Imprimir datos base: " + datosLibroDTO);
                            ServiceGuardarEntity serviceGuardar = new ServiceGuardarEntity(
                                    autorRepository, libroRepository);
                            serviceGuardar.guardarAutoresYLibros(datosLibroDTO);

                        } catch (RuntimeException e) {
                            System.err.println("Error durante la solicitud: " + e.getMessage());
                        }
                        System.out.println("Operacion terminada.");
                        break;
                    case 2:
                        System.out.println("Lista de todos los libros registrados en base de datos");
                        libroRepository.findAll()
                                .forEach(
                                        libro -> System.out.printf("Título: %s, Lenguaje: %s, Descargas: %d%n",
                                                libro.getTituloLibro(),
                                                libro.getLenguaje(),
                                                libro.getNumeroDescargas()));
                        break;
                    case 3:
                        System.out.println("Lista de todos los autores registrados en base de datos");
                        autorRepository.findAll()
                                .forEach(
                                        autor -> System.out.printf("Nombre: %s, Año de nacimiento: %s, Año de su muerte: %d%n",
                                                autor.getNombre(),
                                                autor.getAnioNacimiento(),
                                                autor.getAnioMuerte()));
                        break;
                    case 4:
                        System.out.println("Ingrese el año especifico para informa sobre los autores vivos hasta ese año");
                        entradaTecladoInt = teclado.nextInt();
                        autorRepository.findByAnioMuerteLessThan(entradaTecladoInt).
                                forEach(autor -> System.out.printf("Nombre: %s, Año de nacimiento: %s, Año de su muerte: %d%n",
                                        autor.getNombre(),
                                        autor.getAnioNacimiento(),
                                        autor.getAnioMuerte()));
                        break;
                    case 5:
                        System.out.println("Ingrese la abreviatura del idioma, tenga en cuenta que solo son 2 letras");
                        teclado.nextLine(); // Limpiar el buffer de entrada
                        entradaTecladoString = teclado.nextLine();
                        libroRepository.findByLenguaje(entradaTecladoString)
                                .forEach(libro -> System.out.printf("Título: %s, Lenguaje: %s, Descargas: %d%n",
                                        libro.getTituloLibro(),
                                        libro.getLenguaje(),
                                        libro.getNumeroDescargas()));
                        break;
                    case 6:
                        System.out.println("Ingrese el nombre del libro a buscar en la base de datos");
                        teclado.nextLine(); // Limpiar el buffer de entrada
                        entradaTecladoString = teclado.nextLine();
                        libroRepository.findByTituloLibroIgnoreCase(entradaTecladoString)
                                .ifPresentOrElse(
                                        libro -> System.out.printf("Título: %s, Lenguaje: %s, Descargas: %d%n",
                                                libro.getTituloLibro(),
                                                libro.getLenguaje(),
                                                libro.getNumeroDescargas()),
                                        () -> System.out.println("❌ No se encontró el libro en la base de datos.")
                                );
                        break;
                    case 0:
                        System.out.println("Saliendo del programa...");
                        break;
                    default:
                        System.out.println("Por favor, seleccione un número válido del menú.");
                        break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido del Menú.");
                teclado.nextInt(); // Limpiar el buffer de entrada
                entradaTecladoInt = -1;
            }
        } while (entradaTecladoInt != 0);
        teclado.close();
    }
}
