package com.app.sistema.matricula.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.sistema.matricula.service.UbigeoService;

@RestController
@RequestMapping("/api/ubigeo")
public class RestControllerPrueba {
    @Autowired
    private UbigeoService ubigeoService;

    @GetMapping("/departamentos")
    public List<String> listarDepartamentos() {
        return ubigeoService.getDepartamentos();
    }

    @GetMapping("/provincias")
    public List<String> listarProvincias(@RequestParam String departamento) {
        return ubigeoService.getProvincias(departamento);
    }

    @GetMapping("/distritos")
    public List<String> listarDistritos(@RequestParam String departamento, @RequestParam String provincia) {
        return ubigeoService.getDistritos(departamento, provincia);
    }
}
