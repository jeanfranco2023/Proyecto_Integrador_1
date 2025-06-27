package com.app.sistema.matricula.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.sistema.matricula.models.Padres;
import com.app.sistema.matricula.repository.PadresRepository;

@Service
public class PadresService implements IService<Padres>{

    @Autowired
    private PadresRepository padresRepository;

    @Override
    public List<Padres> listar() {
        return padresRepository.findAll();
    }

    @Override
    public Padres buscarPorId(Integer id) {
        return padresRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Padres objeto) {
        if (objeto != null) {
            padresRepository.save(objeto);
        } else {
            throw new IllegalArgumentException("El objeto Padres no puede ser nulo");
        }
    }

    @Override
    public void eliminar(Padres objeto) {
        if (objeto != null && objeto.getIdPadre() != null) {
            padresRepository.delete(objeto);
        } else {
            throw new IllegalArgumentException("El objeto Padres o su ID no puede ser nulo");
        }
    }

    
}
