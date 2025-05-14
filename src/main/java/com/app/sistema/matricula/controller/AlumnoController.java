package com.app.sistema.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.app.sistema.matricula.dto.AlumnoDTO;
import com.app.sistema.matricula.models.Alumnos;
import com.app.sistema.matricula.service.AlumnoService;

@Controller
@RequestMapping("/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;
  
    @GetMapping("/registroAlumno")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("alumno", new AlumnoDTO());
        model.addAttribute("alumnos", alumnoService.listar());
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
        
        return "redirect:/alumnos/registroAlumno";
    }

    @GetMapping("/editar/alumno/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Integer id, ModelMap model) {
        Alumnos alumno = alumnoService.buscarPorId(id);
        if (alumno != null) {
            AlumnoDTO alumnoDTO = new AlumnoDTO();
            alumnoDTO.setIdAlumno(alumno.getIdAlumno());
            alumnoDTO.setNombreAlumno(alumno.getNombreAlumno());
            alumnoDTO.setApellidoAlumno(alumno.getApellidoAlumno());
            alumnoDTO.setDniAlumno(alumno.getDniAlumno());
            alumnoDTO.setCorreoAlumno(alumno.getCorreoAlumno());
            alumnoDTO.setPasswordAlumno(alumno.getPasswordAlumno());
            alumnoDTO.setNombreApoderado(alumno.getNombreApoderado());
            alumnoDTO.setFechaNacimiento(alumno.getFechaNacimiento());
            model.addAttribute("usuario", alumnoDTO);
            return "editarAlumno";
        }
        return "redirect:/alumnos/registro";
    }

    @PostMapping("/actualizar")
    public String actualizarAlumno(@ModelAttribute("alumno") AlumnoDTO alumnoDTO) {
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