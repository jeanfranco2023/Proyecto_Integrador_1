package com.app.sistema.matricula.models;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "detalle_nota")
public class DetalleNota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalleNota;
    @ManyToOne
    private Matricula matricula;
    @ManyToOne
    private Nota nota;
    @ManyToOne
    private Cursos curso;
    private String bimestre;
    private LocalDate fechaRegistro;
    
}
