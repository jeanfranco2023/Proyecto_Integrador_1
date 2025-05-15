package com.app.sistema.matricula.models;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "alumnos")
public class Alumnos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAlumno;

    private String nombreAlumno;
    private String apellidoAlumno;
    private String dniAlumno;
    private String correoAlumno;
    private String passwordAlumno;
    private String nombreApoderado;

    @OneToMany(mappedBy = "alumno")
    private List<Matricula> matriculas;

    @OneToMany(mappedBy = "alumno")
    private List<Nota> notas;

    private String rol;
    private LocalDate fechaNacimiento;
}