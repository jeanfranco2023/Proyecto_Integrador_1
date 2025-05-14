package com.app.sistema.matricula.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.sistema.matricula.models.Administrador;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    List<Administrador> findByCorreoAdministrador(String correo);
    Optional<AdministradorRepository> findByDniAdministrador(String dni);
    
}
