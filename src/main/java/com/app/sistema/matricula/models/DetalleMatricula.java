package com.app.sistema.matricula.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "detalle_matricula")
public class DetalleMatricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "seccion_id")
    private Seccion seccion;

    @ManyToOne
    @JoinColumn(name = "matricula_id")
    private Matricula matricula;
    
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Cursos curso;

    private int anio;
}