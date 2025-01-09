package com.alura.desafio_literalura.Principal;

import com.alura.desafio_literalura.Model.Autor;
import com.alura.desafio_literalura.Model.Datos;
import com.alura.desafio_literalura.Model.DatosLibros;
import com.alura.desafio_literalura.Model.Libros;
import com.alura.desafio_literalura.Repository.AutorRepositorio;
import com.alura.desafio_literalura.Repository.LibroRepositorio;
import com.alura.desafio_literalura.Service.ConsumoAPI;
import com.alura.desafio_literalura.Service.ConvierteDatos;
import com.alura.desafio_literalura.Service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.*;

@Component
public class Principal {
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private static final String URL_BASE = "https://gutendex.com/books/";
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final Scanner teclado = new Scanner(System.in);
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private AutorRepositorio autorRepositorio;
    @Autowired
    private LibroService libroService;

    public void muestraElMenu() {
        var json = consumoAPI.obtenerDatos(URL_BASE);

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    =======================================================================
                    Bienvenido a la biblioteca de Literalura!!!
                    Selecciona una de opciones mostradas abajo para buscar al libro o autor que desees.
                    =======================================================================
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    
                    0 - Salir de la aplicación
                    
                    Ingresar opción:
                    """;
            System.out.println(menu);
            try {
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        buscarLibro();
                        break;
                    case 2:
                        mostrarLibrosBuscados();
                        break;
                    case 3:
                        mostrarAutoresBuscados();
                        break;
                    case 4:
                        mostrarAutorVivoPorAno();
                        break;
                    case 5:
                        mostrarLibrosPorIdioma();
                        break;
                    case 0:
                        System.out.println("Cerrando la aplicación...");
                        break;
                    default:
                        System.out.println("Opción inválida, por favor elija un número de entre las opciones.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Por favor, introduce un número.");
                teclado.nextLine();
            }
        }
    }

    private DatosLibros getDatosLibro() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()){
            return libroBuscado.get();
        } else {
            return null;
        }
    }
    private void buscarLibro() {
        DatosLibros datos = getDatosLibro();
        if (datos != null) {
            if (!libroRepositorio.existsByTitulo(datos.titulo())) {
                try {
                    Autor autor = new Autor(datos.autor().get(0));
                    Autor autorExistente = autorRepositorio.findByNombre(autor.getNombre());

                    if (autorExistente == null) {
                        autor = autorRepositorio.save(autor);
                    } else {
                        autor = autorExistente;
                    }

                    Libros libro = new Libros(datos, autor);
                    libroRepositorio.save(libro);

                    System.out.println("\nLibro guardado correctamente:");
                    System.out.println(libro);
                } catch (Exception e) {
                    System.err.println("\nError al guardar el libro:");
                    System.err.println(datos);
                }
            } else {
                System.out.println("\nEl libro ya existe en la base de datos:");
                Libros libroExistente = libroRepositorio.findByTitulo(datos.titulo());
                System.out.println(libroExistente);
            }
        } else {
            System.out.println("\nLibro no encontrado.");
        }
    }


    private void mostrarLibrosBuscados() {
        List<Libros> libros = libroRepositorio.findAll();

        libros.stream()
                .sorted(Comparator.comparing(Libros::getTitulo))
                .forEach(System.out::println);
    }

    private void mostrarAutoresBuscados() {
        List<Autor> autoresBuscados = autorRepositorio.findAll();

        if (autoresBuscados.isEmpty()) {
            System.out.println("No se han registrado autores.");
            return;
        }

        autoresBuscados.forEach(autor -> {
            String librosDelAutor = autor.getLibros().isEmpty() ?
                    "Este autor no tiene libros registrados." :
                    autor.getLibros().stream()
                            .map(Libros::getTitulo)
                            .reduce((titulo1, titulo2) -> titulo1 + ", " + titulo2)
                            .orElse("");

            // Imprime el formato del autor con todos sus libros
            System.out.println("\n===============Autor===============");
            System.out.println("Nombre: " + autor.getNombre());
            System.out.println("Fecha de nacimiento: " + autor.getFechaDeNacimiento());
            System.out.println("Fecha de fallecimiento: " + autor.getFechaDeFallecimiento());
            System.out.println("Libros: " + librosDelAutor);
            System.out.println("===================================");
        });
    }

    private void mostrarLibrosPorIdioma() {
        System.out.println("""
            Escriba el código del idioma para buscar un libro:
            es - Español
            en - Inglés
            fr - Francés
            pt - Portugués
            """);

        var idiomaSeleccionado = teclado.nextLine().toLowerCase();
        if (idiomaSeleccionado.equals("es") || idiomaSeleccionado.equals("en") || idiomaSeleccionado.equals("fr") || idiomaSeleccionado.equals("pt")) {
            long cantidad = libroService.obtenerCantidadLibrosPorIdioma(idiomaSeleccionado);
            System.out.println("\nCantidad de libros en ese idioma: " + cantidad);
        } else {
            System.out.println("\nEscribe una opción correcta: es, en, fr, o pt.");
            return;
        }

        List<Libros> librosPorIdioma = libroService.findByIdiomas(idiomaSeleccionado);

        if (librosPorIdioma.isEmpty()) {
            System.out.println("\nNo se encontraron libros en el idioma seleccionado.");
        } else {
            librosPorIdioma.forEach(l -> System.out.println(l)); // Llama al metodo to string de Libros
        }
    }


    private void mostrarAutorVivoPorAno() {
        System.out.println("Ingresa el año para buscar autores vivos en ese momento:");
        int anoSeleccionado;

        try {
            anoSeleccionado = Integer.parseInt(teclado.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingresa un año válido.");
            return;
        }

        List<Autor> autoresVivos = autorRepositorio.buscarAutorVivoPorAno(anoSeleccionado);

        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + anoSeleccionado + ".");
            return;
        }

        autoresVivos.forEach(autor -> {
            String librosDelAutor = autor.getLibros().isEmpty() ?
                    "Este autor no tiene libros registrados." :
                    autor.getLibros().stream()
                            .map(Libros::getTitulo)
                            .reduce((titulo1, titulo2) -> titulo1 + ", " + titulo2)
                            .orElse("");

            System.out.println("\n===============Autor===============");
            System.out.println("Nombre: " + autor.getNombre());
            System.out.println("Fecha de nacimiento: " + autor.getFechaDeNacimiento());
            System.out.println("Fecha de fallecimiento: " + autor.getFechaDeFallecimiento());
            System.out.println("Libros: " + librosDelAutor);
            System.out.println("===================================");
        });
    }
}