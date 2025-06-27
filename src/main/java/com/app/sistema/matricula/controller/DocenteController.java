package com.app.sistema.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.sistema.matricula.dto.DocenteDTO;
import com.app.sistema.matricula.models.Cursos;
import com.app.sistema.matricula.models.DetalleCursoSeccion;
import com.app.sistema.matricula.models.Docentes;
import com.app.sistema.matricula.models.Seccion;
import com.app.sistema.matricula.service.CursoService;
import com.app.sistema.matricula.service.DetalleCursoSeccionService;
import com.app.sistema.matricula.service.DocenteService;
import com.app.sistema.matricula.service.SeccionService;

@Controller
@RequestMapping("/docentes")
public class DocenteController {

    @Autowired
    private DocenteService docenteService;
    @Autowired
    private CursoService cursoService;
    @Autowired
    private SeccionService seccionService;

    @Autowired
    private DetalleCursoSeccionService detalleCursoSeccionService;

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

        return "redirect:/usuarios/dashboard?seccion=lista-docentes";
    }

    @PostMapping("/eliminarDocente/{idDocente}")
    public String eliminarDocente(@PathVariable Integer idDocente) {
        Docentes docente = docenteService.buscarPorId(idDocente);
        if (docente != null) {
            docenteService.eliminar(docente);
        }
        return "redirect:/usuarios/dashboard?seccion=lista-docentes";
    }

    @PostMapping("/actualizarDocente")
    public String actualizarDocente(DocenteDTO docenteDTO) {
        Docentes docente = docenteService.buscarPorId(docenteDTO.getIdDocente());
        if (docente != null) {
            docente.setNombreDocente(docenteDTO.getNombreDocente());
            docente.setApellidoDocente(docenteDTO.getApellidoDocente());
            docente.setDniDocente(docenteDTO.getDniDocente());
            docente.setCorreoDocente(docenteDTO.getCorreoDocente());
            docente.setTelefonoDocente(docenteDTO.getTelefonoDocente());
            docente.setEspecialidadDocente(docenteDTO.getEspecialidadDocente());
            docente.setPasswordDocente(docenteDTO.getPasswordDocente());
            docenteService.insertar(docente);
        }
        return "redirect:/usuarios/dashboard?seccion=registro-docentes";
    }

    @PostMapping("/buscarDocentePorId")
    public String buscarDocentePorId(Integer idDocente, Model model) {
        Docentes docente = docenteService.buscarPorId(idDocente);
        if (docente != null) {
            DocenteDTO docenteDTO = new DocenteDTO();
            docenteDTO.setIdDocente(docente.getIdDocente());
            docenteDTO.setNombreDocente(docente.getNombreDocente());
            docenteDTO.setApellidoDocente(docente.getApellidoDocente());
            docenteDTO.setDniDocente(docente.getDniDocente());
            docenteDTO.setCorreoDocente(docente.getCorreoDocente());
            docenteDTO.setTelefonoDocente(docente.getTelefonoDocente());
            docenteDTO.setEspecialidadDocente(docente.getEspecialidadDocente());
            docenteDTO.setPasswordDocente(docente.getPasswordDocente());
            model.addAttribute("docente", docenteDTO);
        }
        return "redirect:/usuarios/dashboard?seccion=registro-docentes";
    }

    @PostMapping("/asignarCursoSeccion")
    public String guardar(@RequestParam("cursoId") Integer cursoId,
            @RequestParam("docenteId") Integer docenteId,
            @RequestParam("seccionId") Integer seccionId) {

        Cursos curso = cursoService.buscarPorId(cursoId);
        Docentes docente = docenteService.buscarPorId(docenteId);
        Seccion seccion = seccionService.buscarPorId(seccionId);

        DetalleCursoSeccion detalle = new DetalleCursoSeccion();
        detalle.setCurso(curso);
        detalle.setDocente(docente);
        detalle.setSeccion(seccion);

        detalleCursoSeccionService.insertar(detalle);

        return "redirect:/usuarios/dashboard?seccion=asignar-seccion";
    }

}
