package com.app.sistema.matricula.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.sistema.matricula.models.Alumnos;
import com.app.sistema.matricula.models.Cursos;
import com.app.sistema.matricula.models.Nota;

public interface NotaRepository extends JpaRepository<Nota, Integer> {
    List<Nota> findByAlumno(Alumnos alumno);
    List<Nota> findByCurso(Cursos curso);
    
}
