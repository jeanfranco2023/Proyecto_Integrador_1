package com.app.sistema.matricula.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.sistema.matricula.models.Docentes;
import com.app.sistema.matricula.repository.DocenteRepository;

@Service
public class DocenteService implements IService<Docentes> {

    @Autowired
    private DocenteRepository docenteRepository;

    @Override
    public List<Docentes> listar() {
        return docenteRepository.findAll();
    }

    @Override
    public Docentes buscarPorId(Integer id) {
        return docenteRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Docentes docente) {
        docenteRepository.save(docente);
    }

    @Override
    public void eliminar(Docentes docente) {
        docenteRepository.delete(docente);
    }

    public List<Docentes> buscarPorNombre(String nombre) {
        return docenteRepository.findByNombreDocente(nombre);
    }
    public List<Docentes> buscarPorApellido(String apellido) {
        return docenteRepository.findByApellidoDocente(apellido);
    }
    public List<Docentes> buscarPorCorreo(String correo) {
        return docenteRepository.findByCorreoDocente(correo);
    }

}
