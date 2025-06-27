package com.app.sistema.matricula.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.sistema.matricula.models.Ubigeo;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class UbigeoService {

    private Map<String, Map<String, Map<String, Ubigeo>>> ubigeos = new HashMap<>();

    public UbigeoService() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream input = getClass().getClassLoader().getResourceAsStream("data/departamentos.json");
            if (input != null) {
                ubigeos = mapper.readValue(input,
                        new TypeReference<Map<String, Map<String, Map<String, Ubigeo>>>>() {
                        });
            } else {
                System.out.println("No se encontró el archivo departamentos.json");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getDepartamentos() {
        return new ArrayList<>(ubigeos.keySet());
    }

    public List<String> getProvincias(String departamento) {
        return new ArrayList<>(ubigeos.getOrDefault(departamento, Collections.emptyMap()).keySet());
    }

    public List<String> getDistritos(String departamento, String provincia) {
        return new ArrayList<>(
                ubigeos.getOrDefault(departamento, Collections.emptyMap())
                        .getOrDefault(provincia, Collections.emptyMap())
                        .keySet());
    }
}
