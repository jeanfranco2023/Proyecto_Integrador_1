package com.app.sistema.matricula.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoDTO {
    private Integer idAlumno;
    private String nombreAlumno;
    private String apellidoAlumno;
    private String dniAlumno;
    private String correoAlumno;
    private String passwordAlumno;
    private String nombreApoderado;
    private LocalDate fechaNacimiento;
    private String rol;
}
