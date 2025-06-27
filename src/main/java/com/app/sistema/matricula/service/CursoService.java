package com.app.sistema.matricula.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.sistema.matricula.models.Cursos;
import com.app.sistema.matricula.repository.CursoRepository;

@Service
public class CursoService implements IService<Cursos> {

    @Autowired
    private CursoRepository cursoRepository;

    @Override
    public List<Cursos> listar() {
        return cursoRepository.findAll();
    }

    @Override
    public Cursos buscarPorId(Integer id) {
        return cursoRepository.findById(id).orElse(null);
    }

    @Override
    public void insertar(Cursos curso) {
        cursoRepository.save(curso);
    }

    @Override
    public void eliminar(Cursos curso) {
        cursoRepository.delete(curso);
    }

    public Object contarCursos() {
        return cursoRepository.count();
    }

    public List<String> obtenerGradosUnico() {
        return cursoRepository.findDistinctGradosDesdeSeccion();
    }

    public List<Cursos> obtenerCursosPorGrado(String grado) {
        return cursoRepository.findByGradoCurso(grado);
    }

    public List<Cursos> obtenerCursosPorAlumno(Integer idAlumno) {
        return cursoRepository.findCursosByAlumnoId(idAlumno);
    }

}
