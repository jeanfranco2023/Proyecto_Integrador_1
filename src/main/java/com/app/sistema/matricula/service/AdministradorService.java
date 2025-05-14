package com.app.sistema.matricula.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.sistema.matricula.models.Administrador;
import com.app.sistema.matricula.repository.AdministradorRepository;
@Service
public class AdministradorService implements IService<Administrador> {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public List<Administrador> listar() {
        return administradorRepository.findAll();
    }

    @Override
    public Administrador buscarPorId(Integer id) {
       return administradorRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Administrador administrador) {
        administradorRepository.save(administrador);
    }

    @Override
    public void eliminar(Administrador administrador) {
        administradorRepository.delete(administrador);
    }

    public List<Administrador> buscarPorCorreo(String correo) {
        return administradorRepository.findByCorreoAdministrador(correo);
    }
}
