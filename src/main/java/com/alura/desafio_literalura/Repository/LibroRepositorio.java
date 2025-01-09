package com.alura.desafio_literalura.Repository;

import com.alura.desafio_literalura.Model.Libros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepositorio extends JpaRepository<Libros, Long> {
    Libros findByTitulo(String titulo);

    boolean existsByTitulo(String titulo);

    @Query("SELECT l FROM Libros l WHERE l.idiomas LIKE %:idiomas%")
    List<Libros> findByIdiomas(@Param("idiomas") String idiomas);

    @Query("SELECT COUNT(l) FROM Libros l WHERE l.idiomas LIKE %:idioma%")
    long obtenerCantidadLibrosPorIdioma(@Param("idioma") String idioma);
}