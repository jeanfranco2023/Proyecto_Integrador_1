package com.app.sistema.matricula.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.sistema.matricula.models.DetalleMatricula;

@Repository
public interface DetalleMatriculaRepository extends JpaRepository<DetalleMatricula, Integer> {
    
    
}
