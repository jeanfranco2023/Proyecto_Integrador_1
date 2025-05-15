package com.app.sistema.matricula.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.sistema.matricula.models.Cursos;
import com.app.sistema.matricula.models.DetalleMatricula;
import com.app.sistema.matricula.models.Matricula;
import com.app.sistema.matricula.repository.CursoRepository;
import com.app.sistema.matricula.repository.MatriculaRepository;

import jakarta.transaction.Transactional;

@Service
public class MatriculaService implements IService<Matricula> {

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private CursoRepository cursoRepository;

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

    @Transactional
    public void guardarMatriculaConCursosDeGrado(Matricula matricula, String grado) {
        Matricula matriculaGuardada = matriculaRepository.save(matricula);
        List<Cursos> cursos = cursoRepository.findByGradoCurso(grado);

        List<DetalleMatricula> detalles = cursos.stream()
                .map(curso -> {
                    DetalleMatricula detalle = new DetalleMatricula();
                    detalle.setMatricula(matriculaGuardada);
                    detalle.setCurso(curso);
                    return detalle;
                })
                .collect(Collectors.toList());

        matriculaGuardada.setDetalles(detalles);

        matriculaRepository.save(matriculaGuardada);
    }

}
