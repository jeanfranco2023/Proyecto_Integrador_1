package com.app.sistema.matricula.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.sistema.matricula.models.Nota;
import com.app.sistema.matricula.repository.NotaRepository;

@Service
public class NotaService implements IService<Nota> {

    @Autowired
    private NotaRepository notaRepository;

    @Override
    public List<Nota> listar() {
        return notaRepository.findAll();
    }

    @Override
    public Nota buscarPorId(Integer id) {
        return notaRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Nota nota) {
        notaRepository.save(nota);
    }

    @Override
    public void eliminar(Nota nota) {
        notaRepository.delete(nota);
    }

}
