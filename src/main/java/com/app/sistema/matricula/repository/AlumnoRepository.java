package com.app.sistema.matricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.sistema.matricula.models.Alumnos;
import java.util.List;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumnos, Integer> {

    List<Alumnos> findByNombreAlumno(String nombreAlumno);
    List<Alumnos> findByCorreoAlumno(String correoAlumno);
    boolean existsByDniAlumno(String dni);

    
}
