package com.alura.desafio_literalura.Model;

import jakarta.persistence.*;

import java.util.OptionalDouble;

@Entity
@Table(name = "libros")
public class Libros {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_autor")
    private Autor autor;
    private String idiomas;
    private Double numeroDeDescargas;

    public Libros(DatosLibros datosLibros, Autor autor){
        this.titulo = datosLibros.titulo();
        this.autor = autor;
        this.idiomas = datosLibros.idiomas().get(0);
        this.numeroDeDescargas = OptionalDouble.of(datosLibros.numeroDeDescargas()).orElse(0);
    }

    public Libros(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(String idiomas) {
        this.idiomas = idiomas;
    }

    public Double getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(Double numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }
    @Override
    public String toString() {
        return  "===============Libro=============" +
                "\nTítulo: " + titulo +
                "\nAutor(es): " + autor.getNombre() +
                "\nIdioma: " + idiomas +
                "\nNúmero de descargas: " + numeroDeDescargas +
                "\n==============================\n";
    }
}

