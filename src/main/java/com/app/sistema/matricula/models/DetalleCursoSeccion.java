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
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"docente", "seccion"})
@Table(name = "detalle_curso_seccion")
public class DetalleCursoSeccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Cursos curso;

    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Docentes docente;

    @ManyToOne
    @JoinColumn(name = "seccion_id")
    private Seccion seccion;
}