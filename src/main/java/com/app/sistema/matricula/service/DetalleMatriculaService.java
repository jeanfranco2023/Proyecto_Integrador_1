package com.app.sistema.matricula.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.sistema.matricula.models.DetalleMatricula;
import com.app.sistema.matricula.repository.DetalleMatriculaRepository;

@Service
public class DetalleMatriculaService implements IService<DetalleMatricula> {
    @Autowired
    private DetalleMatriculaRepository detalleMatriculaRepository;

    @Override
    public List<DetalleMatricula> listar() {
        return detalleMatriculaRepository.findAll();
    }

    @Override
    public DetalleMatricula buscarPorId(Integer id) {
        return detalleMatriculaRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(DetalleMatricula objeto) {
        detalleMatriculaRepository.save(objeto);
    }

    @Override
    public void eliminar(DetalleMatricula objeto) {
        detalleMatriculaRepository.delete(objeto);
    }

}
