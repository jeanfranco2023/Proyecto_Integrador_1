package com.app.sistema.matricula.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.sistema.matricula.dto.DocenteDTO;
import com.app.sistema.matricula.dto.Login;
import com.app.sistema.matricula.dto.MatriculaDTO;
import com.app.sistema.matricula.models.Administrador;
import com.app.sistema.matricula.models.Alumnos;
import com.app.sistema.matricula.models.Cursos;
import com.app.sistema.matricula.models.DetalleCursoSeccion;
import com.app.sistema.matricula.models.Docentes;
import com.app.sistema.matricula.models.Matricula;
import com.app.sistema.matricula.repository.CursoRepository;
import com.app.sistema.matricula.service.AdministradorService;
import com.app.sistema.matricula.service.AlumnoService;
import com.app.sistema.matricula.service.CursoService;
import com.app.sistema.matricula.service.DocenteService;
import com.app.sistema.matricula.service.MatriculaService;
import com.app.sistema.matricula.service.SeccionService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private AlumnoService alumnoService;
    @Autowired
    private DocenteService docenteService;
    @Autowired
    private AdministradorService administradorService;
    @Autowired
    private CursoService cursoService;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private SeccionService seccionService;
    @Autowired
    private MatriculaService matriculaService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("login", new Login());
        return "login";
    }

    @PostMapping("/logeo")
    public String logeo(Login login, Model model, HttpSession session) {
        try {
            List<Alumnos> alumnos = alumnoService.buscarPorCorreo(login.getUsername());
            if (!alumnos.isEmpty()) {
                Alumnos alumno = alumnos.get(0);
                if (alumno.getPasswordAlumno().equals(login.getPassword())) {
                    session.setAttribute("alumnoId", alumno.getIdAlumno());
                    session.setAttribute("nombreAlumno", alumno.getNombreAlumno());
                    return "redirect:/usuarios/dashboard?seccion=principal";
                }
            }
            List<Docentes> docentes = docenteService.buscarPorCorreo(login.getUsername());
            if (!docentes.isEmpty()) {
                Docentes docente = docentes.get(0);
                if (docente.getPasswordDocente().equals(login.getPassword())) {
                    session.setAttribute("docenteId", docente.getIdDocente());
                    session.setAttribute("nombreDocente", docente.getNombreDocente());
                    return "redirect:/usuarios/dashboard?seccion=principal";
                }
            }
            List<Administrador> admin = administradorService.buscarPorCorreo(login.getUsername());
            if (!admin.isEmpty()) {
                Administrador administrador = admin.get(0);
                if (administrador.getPasswordAdministrador().equals(login.getPassword())) {
                    session.setAttribute("adminIniciado", administrador.getNombreAdministrador());
                    return "redirect:/usuarios/dashboard?seccion=principal";
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al intentar iniciar sesión: " + e.getMessage());
            return "login";
        } finally {
        }
        model.addAttribute("error", "Usuario o contraseña incorrectos");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usuarios/login";
    }

    @GetMapping("/principal")
    public String principal() {
        return "principal";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session,
            @RequestParam String seccion,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String modo,
            @ModelAttribute("rol") String rol) {

        model.addAttribute("grados", cursoRepository.findDistinctGradoCurso());
        model.addAttribute("periodos", List.of("2025-I", "2025-II"));
        model.addAttribute("seccion", seccion);

        if ("matricula".equals(seccion)) {
            MatriculaDTO dto = new MatriculaDTO();

            if ("editar".equalsIgnoreCase(modo)) {
                if (id != null) {
                    Alumnos alumno = alumnoService.buscarPorId(id);
                    Optional<Integer> idMatricula = alumno.getMatriculas().stream()
                            .map(Matricula::getIdMatricula)
                            .findFirst();
                    if (idMatricula.isPresent()) {
                        Matricula matricula = matriculaService.buscarPorId(idMatricula.get());
                        if (matricula != null) {
                            dto.setIdMatricula(matricula.getIdMatricula());
                            dto.setAlumno(matricula.getAlumno());
                            dto.setFechaMatricula(matricula.getFechaMatricula());
                            dto.setPeriodoAcademico(matricula.getPeriodoAcademico());
                        }
                    }
                } else {
                    model.addAttribute("error", "No se proporcionó un ID válido para editar la matrícula.");
                }
            }

            model.addAttribute("matriculaDTO", dto);
            return "dashboard";
        }

        model.addAttribute("matriculaDTO", new MatriculaDTO());
        model.addAttribute("curso", new Cursos());
        model.addAttribute("docentes", new DocenteDTO());
        model.addAttribute("docente", docenteService.listar());
        model.addAttribute("alumnos", alumnoService.listar());
        model.addAttribute("detalleCursoSeccion", new DetalleCursoSeccion());
        model.addAttribute("listaCursos", cursoRepository.findDistinctNombreCurso());
        model.addAttribute("secciones", seccionService.listar());

        switch (rol) {
            case "Alumno" -> {
                Object alumnoAttr = session.getAttribute("alumnoId");
                if (alumnoAttr instanceof Integer alumnoId) {
                    List<Cursos> cursos = alumnoService.obtenerCursosPorId(alumnoId);
                    model.addAttribute("cursos", cursos);
                }
            }
            case "Docente" -> {
                Object docenteAttr = session.getAttribute("docenteId");
                if (docenteAttr instanceof Integer docenteId) {
                    List<Cursos> cursos = docenteService.obtenerCursosPorId(docenteId);
                    model.addAttribute("cursos", cursos);
                }
            }
            case "Administrador" -> {
                model.addAttribute("totalAlumnos", alumnoService.contarAlumnos());
                model.addAttribute("totalDocentes", docenteService.contarDocentes());
                model.addAttribute("totalCursos", cursoService.contarCursos());
            }
        }

        return "dashboard";
    }

    @ModelAttribute("rol")
    public String rol(HttpSession session) {
        if (session.getAttribute("alumnoId") != null) {
            return "Alumno";
        } else if (session.getAttribute("docenteId") != null) {
            return "Docente";
        } else if (session.getAttribute("adminIniciado") != null) {
            return "Administrador";
        }
        return null;
    }

    @ModelAttribute("usuarioIniciado")
    public Object usuarioIniciado(HttpSession session) {
        if (session.getAttribute("alumnoId") != null) {
            return session.getAttribute("nombreAlumno");

        } else if (session.getAttribute("docenteId") != null) {
            return session.getAttribute("nombreDocente");

        } else if (session.getAttribute("adminIniciado") != null) {
            return session.getAttribute("adminIniciado");
        }
        return "";
    }
}
