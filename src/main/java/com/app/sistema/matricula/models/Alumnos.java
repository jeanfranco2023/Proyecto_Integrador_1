package com.app.sistema.matricula.models;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
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
    private String rol;
    private LocalDate fechaNacimiento;
    private String departamento;
    private String provincia;
    private String distrito;
    private String sexo;
    private int edad;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.REMOVE)
    private List<Padres> padres;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.REMOVE)
    private List<Matricula> matriculas;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.REMOVE)
    private List<Nota> notas;
}