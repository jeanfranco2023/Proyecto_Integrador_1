package com.app.sistema.matricula.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.sistema.matricula.models.Cursos;

public interface CursoRepository extends JpaRepository<Cursos, Integer> {
    List<Cursos> findByNombreCurso(String nombreCurso);
    
}
