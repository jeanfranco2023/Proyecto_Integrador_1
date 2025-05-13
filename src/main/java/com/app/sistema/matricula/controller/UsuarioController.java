package com.app.sistema.matricula.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.sistema.matricula.dto.Login;
import com.app.sistema.matricula.models.Alumnos;
import com.app.sistema.matricula.models.Docentes;
import com.app.sistema.matricula.service.AlumnoService;
import com.app.sistema.matricula.service.DocenteService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private AlumnoService alumnoService;

    @Autowired
    private DocenteService docenteService;

    private Alumnos alumnoIniciado;

    private Docentes docenteIniciado;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("login", new Login());
        return "login";
    }

    @PostMapping("/logeo")
    public String logeo(Login login, Model model, HttpSession session) {
        List<Alumnos> alumnos = alumnoService.buscarPorCorreo(login.getUsername());
        if (!alumnos.isEmpty()) {
            Alumnos alumno = alumnos.get(0);
            if (alumno.getPasswordAlumno().equals(login.getPassword())) {
                session.setAttribute("alumnoIniciado", alumno);
                return "redirect:/usuarios/principal";
            }
        }
        List<Docentes> docentes = docenteService.buscarPorCorreo(login.getUsername());
        if (!docentes.isEmpty()) {
            Docentes docente = docentes.get(0);
            if (docente.getPasswordDocente().equals(login.getPassword())) {
                session.setAttribute("docenteIniciado", docente);
                return "redirect:/usuarios/principal";
            }
        }
        model.addAttribute("error", "Usuario o contraseña incorrectos");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usuarios/principal";
    }

    @GetMapping("/principal")
    public String principal(Model model, HttpSession session) {
        if (session.getAttribute("alumnoIniciado") != null) {
            alumnoIniciado = (Alumnos) session.getAttribute("alumnoIniciado");
            model.addAttribute("usuario", alumnoIniciado);
        } else if (session.getAttribute("docenteIniciado") != null) {
            docenteIniciado = (Docentes) session.getAttribute("docenteIniciado");
            model.addAttribute("usuario", docenteIniciado);
        }
        return "principal";
    }

    @ModelAttribute("rol")
    public String rol(HttpSession session) {
        if (session.getAttribute("alumnoIniciado") != null) {
            return "Alumno";
        } else if (session.getAttribute("docenteIniciado") != null) {
            return "Docente";
        }
        return null;
    }

    @ModelAttribute("usuarioIniciado")
    public Object usuarioIniciado(HttpSession session) {
        if (session.getAttribute("alumnoIniciado") != null) {
            return (Alumnos) session.getAttribute("alumnoIniciado");
        } else if (session.getAttribute("docenteIniciado") != null) {
            return (Docentes) session.getAttribute("docenteIniciado");
        }
        return "";
    }
}
