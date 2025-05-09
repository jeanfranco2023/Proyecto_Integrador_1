package com.app.sistema.matricula.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Cursos curso;
    @ManyToOne
    @JoinColumn(name = "matricula_id")
    private Matricula matricula;
    @ManyToOne
    @JoinColumn(name = "detalle_nota_id")
    private DetalleNota detalleNota;
    @ManyToOne
    @JoinColumn(name = "alumno_id")
    private Alumnos alumno;
    private String bimestre;
}
