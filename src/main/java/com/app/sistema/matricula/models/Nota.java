package com.app.sistema.matricula.models;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNota;

    private double valorNota;
    private String bimestre;
    private LocalDate fechaRegistro;

    @ManyToOne
    private Cursos curso;

    @ManyToOne
    private Matricula matricula;

    @ManyToOne
    private Alumnos alumno;
}
