package com.app.sistema.matricula.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.sistema.matricula.models.Alumnos;
import com.app.sistema.matricula.repository.AlumnoRepository;

@Service
public class AlumnoService implements IService<Alumnos> {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Override
    public List<Alumnos> listar() {
        return alumnoRepository.findAll();
    }

    @Override
    public Alumnos buscarPorId(Integer id) {
        return alumnoRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Alumnos alumno) {
        alumnoRepository.save(alumno);
    }

    @Override
    public void eliminar(Alumnos alumno) {
        alumnoRepository.delete(alumno);
    }

    public List<Alumnos> buscarPorNombre(String nombre) {
        return alumnoRepository.findByNombreAlumno(nombre);
    }
    
    public List<Alumnos> buscarPorCorreo(String correo) {
        return alumnoRepository.findByCorreoAlumno(correo);
    }

}
