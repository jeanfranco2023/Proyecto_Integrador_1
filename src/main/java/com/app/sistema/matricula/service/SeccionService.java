package com.app.sistema.matricula.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.sistema.matricula.models.Seccion;
import com.app.sistema.matricula.repository.SeccionRepository;

@Service
public class SeccionService implements IService<Seccion> {

    @Autowired
    private SeccionRepository seccionRepository;

    @Override
    public List<Seccion> listar() {
        return seccionRepository.findAll();
    }

    @Override
    public Seccion buscarPorId(Integer id) {
        return seccionRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Seccion seccion) {
        seccionRepository.save(seccion);
    }

    @Override
    public void eliminar(Seccion seccion) {
        seccionRepository.delete(seccion);
    }

}
