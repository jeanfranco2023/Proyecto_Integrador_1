package com.app.sistema.matricula.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.sistema.matricula.dto.MatriculaDTO;
import com.app.sistema.matricula.models.Alumnos;
import com.app.sistema.matricula.models.Cursos;
import com.app.sistema.matricula.models.DetalleMatricula;
import com.app.sistema.matricula.models.Matricula;
import com.app.sistema.matricula.repository.CursoRepository;
import com.app.sistema.matricula.repository.MatriculaRepository;
import com.app.sistema.matricula.service.AlumnoService;
import com.app.sistema.matricula.service.DetalleMatriculaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/matriculas")
public class MatriculaController {

    @Autowired
    private AlumnoService alumnoService;
    @Autowired
    private MatriculaRepository matriculaRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private DetalleMatriculaService detalleMatriculaService;

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("matriculaDTO", new MatriculaDTO());
        model.addAttribute("grados", cursoRepository.findDistinctGradoCurso());
        model.addAttribute("periodos", List.of("2025-I", "2025-II"));
        model.addAttribute("fechaHoy", LocalDate.now()); 
        return "matricula";
    }

    @GetMapping("/cursos")
    @ResponseBody
    public List<Cursos> obtenerCursosPorGrado(@RequestParam String grado) {
        return cursoRepository.findByGradoCurso(grado);
    }

    @PostMapping("/guardar")
    public String guardarMatricula(@ModelAttribute MatriculaDTO dto,
            RedirectAttributes redirectAttributes, HttpSession session) {

        if (dto.getAlumno().getDniAlumno() == null || dto.getAlumno().getDniAlumno().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El DNI es requerido");
            return "redirect:/matriculas/nueva";
        }

        if (!dto.getAlumno().getDniAlumno().matches("\\d{8}")) {
            redirectAttributes.addFlashAttribute("error", "DNI debe tener 8 dígitos");
            return "redirect:/matriculas/nueva";
        }

        if (alumnoService.existePorDni(dto.getAlumno().getDniAlumno())) {
            redirectAttributes.addFlashAttribute("error",
                    "El DNI " + dto.getAlumno().getDniAlumno() + " ya está registrado");
            return "redirect:/matriculas/nueva";
        }

        try {
            dto.getAlumno().setRol("Alumno");

            alumnoService.insertar(dto.getAlumno());

            Alumnos alumnoGuardado = alumnoService.buscarPorId(dto.getAlumno().getIdAlumno());
            if (alumnoGuardado == null) {
                redirectAttributes.addFlashAttribute("error", "Error al recuperar datos del alumno");
                return "redirect:/matriculas/nueva";
            }

            Matricula matricula = new Matricula();
            matricula.setAlumno(alumnoGuardado);
            matricula.setFechaMatricula(dto.getFechaMatricula());
            matricula.setPeriodoAcademico(dto.getPeriodoAcademico());
            Matricula matriculaGuardada = matriculaRepository.save(matricula);

            List<Cursos> cursos = cursoRepository.findByGradoCurso(dto.getGrado());
            if (cursos.isEmpty()) {
                redirectAttributes.addFlashAttribute("warning", "Se registró la matrícula pero no se asignaron cursos");
            } else {
                cursos.forEach(curso -> {
                    DetalleMatricula detalle = new DetalleMatricula();
                    detalle.setMatricula(matriculaGuardada);
                    detalle.setCurso(curso);
                    detalleMatriculaService.insertar(detalle);
                });
            }

            redirectAttributes.addFlashAttribute("success",
                    "Matrícula registrada exitosamente para " + alumnoGuardado.getNombreAlumno() + " "
                            + alumnoGuardado.getApellidoAlumno());
            if (session.getAttribute("alumnoId") != null || session.getAttribute("docenteId") != null 
                    || session.getAttribute("adminIniciado") != null) {
                return "redirect:/usuarios/dashboard?seccion=matricula";
            }
            return "redirect:/usuarios/matricula";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error al registrar: "
                            + (e.getMessage() != null ? e.getMessage() : "consulte con el administrador"));
            return "redirect:/matriculas/nueva";
        }
    }
}