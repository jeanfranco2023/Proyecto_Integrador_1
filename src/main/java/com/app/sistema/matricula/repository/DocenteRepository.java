package com.app.sistema.matricula.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.sistema.matricula.models.Docentes;

public interface DocenteRepository extends JpaRepository<Docentes, Integer> {
    List<Docentes> findByNombreDocente(String nombreDocente);
    List<Docentes> findByApellidoDocente(String apellidoDocente);
    List<Docentes> findByCorreoDocente(String correoDocente);
}
