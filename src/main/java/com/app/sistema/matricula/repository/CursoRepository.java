package com.app.sistema.matricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.sistema.matricula.models.Cursos;

public interface CursoRepository extends JpaRepository<Cursos, Integer> {
    
}
