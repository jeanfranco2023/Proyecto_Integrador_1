package com.app.sistema.matricula.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.app.sistema.matricula.models.DetalleMatricula;

public interface DetalleMatriculaRepository extends JpaRepository<DetalleMatricula, Integer> {
    
    
}
