package com.app.sistema.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.sistema.matricula.dto.DocenteDTO;
import com.app.sistema.matricula.models.Docentes;
import com.app.sistema.matricula.service.DocenteService;

@Controller
@RequestMapping("/docentes")
public class DocenteController {
    
    @Autowired
    private DocenteService docenteService;

    @PostMapping("/guardarDocente")
    public String guardarDocente(DocenteDTO docenteDTO) {
        Docentes docente = new Docentes();
        docente.setIdDocente(docenteDTO.getIdDocente());
        docente.setNombreDocente(docenteDTO.getNombreDocente());
        docente.setApellidoDocente(docenteDTO.getApellidoDocente());
        docente.setDniDocente(docenteDTO.getDniDocente());
        docente.setCorreoDocente(docenteDTO.getCorreoDocente());
        docente.setTelefonoDocente(docenteDTO.getTelefonoDocente());
        docente.setEspecialidadDocente(docenteDTO.getEspecialidadDocente());
        docente.setPasswordDocente(docenteDTO.getPasswordDocente());
        docente.setRol("Docente");
        docenteService.insertar(docente);
        
        return "redirect:/usuarios/dashboard?seccion=registro-docentes";
    }
    
}
