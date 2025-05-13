package com.app.sistema.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.app.sistema.matricula.dto.AlumnoDTO;
import com.app.sistema.matricula.models.Alumnos;
import com.app.sistema.matricula.service.AlumnoService;



@Controller
@RequestMapping("/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;
  
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("alumno", new AlumnoDTO());
        return "registroAlumno";
    }

    @PostMapping("/guardarAlumno")
    public String guardarAlumno(@ModelAttribute("alumno") AlumnoDTO alumnoDTO) {
        Alumnos alumno = new Alumnos();
        alumno.setIdAlumno(alumnoDTO.getIdAlumno());
        alumno.setNombreAlumno(alumnoDTO.getNombreAlumno());
        alumno.setApellidoAlumno(alumnoDTO.getApellidoAlumno());
        alumno.setDniAlumno(alumnoDTO.getDniAlumno());
        alumno.setCorreoAlumno(alumnoDTO.getCorreoAlumno());
        alumno.setPasswordAlumno(alumnoDTO.getPasswordAlumno());
        alumno.setRol("Alumno");
        alumno.setNombreApoderado(alumnoDTO.getNombreApoderado());
        alumno.setFechaNacimiento(alumnoDTO.getFechaNacimiento());
        alumnoService.insertar(alumno);
        
        return "redirect:/alumnos/registro";
    }
}