package com.app.sistema.matricula.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.sistema.matricula.models.Cursos;
@Repository
public interface CursoRepository extends JpaRepository<Cursos, Integer> {
    List<Cursos> findByNombreCurso(String nombreCurso);

    @Query("SELECT DISTINCT c.nombreCurso FROM Cursos c")
    List<String> findDistinctNombreCurso();

    @Query("SELECT DISTINCT c.gradoCurso FROM Cursos c")
    List<String> findDistinctGradoCurso();

    List<Cursos> findByGradoCurso(String grado);

}
