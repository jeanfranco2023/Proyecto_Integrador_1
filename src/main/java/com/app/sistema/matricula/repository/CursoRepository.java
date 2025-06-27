package com.app.sistema.matricula.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.sistema.matricula.models.Cursos;

@Repository
public interface CursoRepository extends JpaRepository<Cursos, Integer> {
    List<Cursos> findByNombreCurso(String nombreCurso);

    @Query("SELECT c FROM Cursos c WHERE c.idCurso IN (" +
            "SELECT MIN(c2.idCurso) FROM Cursos c2 GROUP BY c2.nombreCurso)")
    List<Cursos> findCursosUnicos();

    @Query("SELECT DISTINCT s.gradoSeccion FROM Seccion s")
    List<String> findDistinctGradosDesdeSeccion();

    List<Cursos> findByGradoCurso(String grado);

    @Query("SELECT DISTINCT n.curso FROM Nota n WHERE n.alumno.idAlumno = :idAlumno")
    List<Cursos> findCursosByAlumnoId(@Param("idAlumno") Integer idAlumno);
}
