package com.alura.desafio_literalura.Service;

import com.alura.desafio_literalura.Model.Autor;
import com.alura.desafio_literalura.Model.DatosLibros;
import com.alura.desafio_literalura.Model.Libros;
import com.alura.desafio_literalura.Repository.AutorRepositorio;
import com.alura.desafio_literalura.Repository.LibroRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {
    @Autowired
    private AutorRepositorio autorRepositorio;
    @Autowired
    private LibroRepositorio libroRepositorio;

    @Autowired
    public LibroService(AutorRepositorio autorRepositorio, LibroRepositorio libroRepositorio){
        this.autorRepositorio = autorRepositorio;
        this.libroRepositorio = libroRepositorio;
    }

    public void guardarLibro(DatosLibros datos) {
        Autor autor = autorRepositorio.findByNombre(datos.autor().get(0).nombre());
        if (autor == null) {
            autor = new Autor(datos.autor().get(0));
            autor = autorRepositorio.save(autor);
        }

        Libros libro = new Libros(datos, autor);
        libro = libroRepositorio.save(libro);
        autor.getLibros().add(libro);
        autorRepositorio.save(autor);
    }

    public long obtenerCantidadLibrosPorIdioma(String idiomaSeleccionado) {
        return libroRepositorio.obtenerCantidadLibrosPorIdioma(idiomaSeleccionado);
    }

    public List<Libros> findByIdiomas(String idiomaSeleccionado) {
        return libroRepositorio.findByIdiomas(idiomaSeleccionado);
    }
}
