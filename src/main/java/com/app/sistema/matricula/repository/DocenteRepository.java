package com.app.sistema.matricula.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.sistema.matricula.models.Docentes;

@Repository
public interface DocenteRepository extends JpaRepository<Docentes, Integer> {
    List<Docentes> findByNombreDocente(String nombreDocente);
    List<Docentes> findByApellidoDocente(String apellidoDocente);
    List<Docentes> findByCorreoDocente(String correoDocente);
}
