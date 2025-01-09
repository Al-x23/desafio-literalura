package com.alura.desafio_literalura.Repository;

import com.alura.desafio_literalura.Model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepositorio extends JpaRepository<Autor, Long> {
    Autor findByNombre(String nombre);

    @Query("SELECT a FROM Autor a WHERE a.fechaDeNacimiento <= :anoSeleccionado AND a.fechaDeFallecimiento >= :anoSeleccionado")
    List<Autor> buscarAutorVivoPorAno(int anoSeleccionado);

    List<Autor> findByNombreContainingIgnoreCase(String nombreAutor);
}
