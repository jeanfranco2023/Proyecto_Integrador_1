package com.app.sistema.matricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.sistema.matricula.models.DetalleMatricula;

@Repository
public interface DetalleMatriculaRepository extends JpaRepository<DetalleMatricula, Integer> {

    @Query("DELETE FROM DetalleMatricula d WHERE d.matricula.idMatricula = :idMatricula")
    void deleteByMatriculaId(@Param("idMatricula") Integer idMatricula);

}
