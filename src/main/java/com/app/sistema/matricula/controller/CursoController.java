package com.app.sistema.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.sistema.matricula.models.Cursos;
import com.app.sistema.matricula.service.CursoService;

@Controller
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @PostMapping("/guardar")
    public String guardarCurso(@ModelAttribute Cursos curso,
            RedirectAttributes redirectAttributes) {
        try {
            cursoService.insertar(curso);
            redirectAttributes.addFlashAttribute("success", "Curso registrado exitosamente");
            return "redirect:/usuarios/dashboard?seccion=cursos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el curso: " + e.getMessage());
            return "redirect:/usuarios/dashboard?seccion=cursos";
        }
    }
}
