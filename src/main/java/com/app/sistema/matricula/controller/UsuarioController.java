package com.app.sistema.matricula.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.sistema.matricula.dto.CursoNotasDTO;
import com.app.sistema.matricula.dto.DocenteDTO;
import com.app.sistema.matricula.dto.Login;
import com.app.sistema.matricula.dto.MatriculaDTO;
import com.app.sistema.matricula.dto.NotaDTO;
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
import com.app.sistema.matricula.service.SeccionService;
import com.app.sistema.matricula.service.UbigeoService;

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
    private UbigeoService ubigeoService;

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

        model.addAttribute("grados", cursoRepository.findDistinctGradosDesdeSeccion());
        model.addAttribute("periodos", List.of("2025-I", "2025-II"));
        model.addAttribute("seccion", seccion);

        // --- Sección matrícula ---
        if ("matricula".equals(seccion)) {
            MatriculaDTO dto = new MatriculaDTO();
            dto.setAlumno(new Alumnos()); // Previene null en modo nuevo

            if ("editar".equalsIgnoreCase(modo) && id != null) {
                Alumnos alumno = alumnoService.buscarPorId(id);
                if (alumno != null && alumno.getMatriculas() != null && !alumno.getMatriculas().isEmpty()) {
                    Optional<Matricula> matriculaOpt = alumno.getMatriculas().stream().findFirst();
                    if (matriculaOpt.isPresent()) {
                        Matricula matricula = matriculaOpt.get();
                        dto.setIdMatricula(matricula.getIdMatricula());
                        dto.setAlumno(matricula.getAlumno());
                        dto.setFechaMatricula(matricula.getFechaMatricula());
                        dto.setPeriodoAcademico(matricula.getPeriodoAcademico());
                    }
                }
            }

            String depSeleccionado = dto.getAlumno().getDepartamento();
            String provSeleccionado = dto.getAlumno().getProvincia();

            System.out.println("Departamento seleccionado: " + depSeleccionado);
            System.out.println("Provincia seleccionada: " + provSeleccionado);

            model.addAttribute("departamentos", ubigeoService.getDepartamentos());

            model.addAttribute("provincias",
                    depSeleccionado != null && !depSeleccionado.isEmpty()
                            ? ubigeoService.getProvincias(depSeleccionado)
                            : List.of());

            model.addAttribute("distritos",
                    depSeleccionado != null && !depSeleccionado.isEmpty()
                            && provSeleccionado != null && !provSeleccionado.isEmpty()
                                    ? ubigeoService.getDistritos(depSeleccionado, provSeleccionado)
                                    : List.of());

            model.addAttribute("matriculaDTO", dto);
            return "dashboard";
        }

        // --- Otras secciones ---
        model.addAttribute("matriculaDTO", new MatriculaDTO());
        model.addAttribute("curso", new Cursos());
        model.addAttribute("docentes", new DocenteDTO());
        model.addAttribute("docente", docenteService.listar());
        model.addAttribute("alumnos", alumnoService.listar());
        model.addAttribute("detalleCursoSeccion", new DetalleCursoSeccion());
        model.addAttribute("listaCursos", cursoRepository.findCursosUnicos());
        model.addAttribute("secciones", seccionService.listar());

        // --- Rol del usuario ---
        switch (rol) {
            case "Alumno" -> {
                Object alumnoAttr = session.getAttribute("alumnoId");
                if (alumnoAttr instanceof Integer alumnoId) {
                    List<Cursos> cursos = alumnoService.obtenerCursosPorId(alumnoId);
                    List<CursoNotasDTO> listaDTO = cursos.stream().map(curso -> {
                        CursoNotasDTO dto = new CursoNotasDTO();
                        dto.setNombreCurso(curso.getNombreCurso());

                        List<NotaDTO> notasDTO = curso.getNotas().stream().map(nota -> {
                            NotaDTO n = new NotaDTO();
                            n.setBimestre(nota.getBimestre());
                            n.setValorNota(nota.getValorNota());
                            return n;
                        }).collect(Collectors.toList());

                        dto.setNotas(notasDTO);
                        return dto;
                    }).collect(Collectors.toList());

                    model.addAttribute("cursosNotas", listaDTO);
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

    @GetMapping("/ubigeo/provincias")
    @ResponseBody
    public List<String> obtenerProvincias(@RequestParam String departamento) {
        return ubigeoService.getProvincias(departamento);
    }

    @GetMapping("/ubigeo/distritos")
    @ResponseBody
    public List<String> obtenerDistritos(@RequestParam String departamento, @RequestParam String provincia) {
        return ubigeoService.getDistritos(departamento, provincia);
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