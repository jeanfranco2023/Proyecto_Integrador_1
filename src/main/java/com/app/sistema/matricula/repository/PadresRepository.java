package com.app.sistema.matricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.sistema.matricula.models.Padres;

@Repository
public interface PadresRepository extends JpaRepository<Padres, Integer> {

}
