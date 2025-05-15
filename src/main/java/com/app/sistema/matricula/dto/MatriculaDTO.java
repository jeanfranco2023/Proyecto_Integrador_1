package com.app.sistema.matricula.dto;

import java.time.LocalDate;

import com.app.sistema.matricula.models.Alumnos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaDTO {
    private Alumnos alumno = new Alumnos();
    private LocalDate fechaMatricula;
    private String periodoAcademico;
    private String grado;
}
