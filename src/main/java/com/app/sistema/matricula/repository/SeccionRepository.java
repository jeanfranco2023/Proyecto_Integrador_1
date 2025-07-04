package com.app.sistema.matricula.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.sistema.matricula.models.Seccion;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Integer> {
    List<Seccion> findByNombreSeccion(String nombre);
    List<Seccion> findByGradoSeccion(String grado);
    List<Seccion> findByTurnoSeccion(String turno);
    
}
