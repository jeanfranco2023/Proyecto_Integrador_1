package com.app.sistema.matricula.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.sistema.matricula.models.Alumnos;
import com.app.sistema.matricula.models.Matricula;

public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {
    List<Matricula> findByAlumno(Alumnos alumno);
    List<Matricula> findByPeriodoAcademico(String periodoAcademico);
    
}
