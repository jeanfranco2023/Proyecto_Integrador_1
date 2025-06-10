package com.app.sistema.matricula.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.sistema.matricula.models.DetalleCursoSeccion;
import com.app.sistema.matricula.repository.DetalleCursoSeccionRepository;

@Service
public class DetalleCursoSeccionService implements IService<DetalleCursoSeccion> {

    @Autowired
    private DetalleCursoSeccionRepository repository;

    @Override
    public List<DetalleCursoSeccion> listar() {
        return repository.findAll();
    }

    @Override
    public DetalleCursoSeccion buscarPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void insertar(DetalleCursoSeccion objeto) {
        repository.save(objeto);
    }

    @Override
    public void eliminar(DetalleCursoSeccion objeto) {
        repository.delete(objeto);
    }

}
