package com.app.sistema.matricula.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.sistema.matricula.models.Matricula;
import com.app.sistema.matricula.repository.MatriculaRepository;

@Service
public class MatriculaService implements IService<Matricula> {

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Override
    public List<Matricula> listar() {
        return matriculaRepository.findAll();
    }

    @Override
    public Matricula buscarPorId(Integer id) {
        return matriculaRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Matricula matricula) {
        matriculaRepository.save(matricula);
    }

    @Override
    public void eliminar(Matricula matricula) {
        matriculaRepository.delete(matricula);
    }

}
