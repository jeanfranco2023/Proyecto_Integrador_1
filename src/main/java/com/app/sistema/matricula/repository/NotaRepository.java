package com.app.sistema.matricula.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.sistema.matricula.models.Alumnos;
import com.app.sistema.matricula.models.Cursos;
import com.app.sistema.matricula.models.Nota;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Integer> {
    List<Nota> findByAlumno(Alumnos alumno);

    List<Nota> findByCurso(Cursos curso);

    @Query("SELECT n FROM Nota n WHERE n.alumno.idAlumno = :idAlumno")
    List<Nota> findByAlumnoId(@Param("idAlumno") Integer idAlumno);

}
