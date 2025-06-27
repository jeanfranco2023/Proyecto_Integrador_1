package com.app.sistema.matricula.models;

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
public class Padres {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPadre;
    private String nombrePadre;
    private String apellidoPadre;
    private String dniPadre;
    private Integer telefonoPadre;
    private String parentesco;

    @ManyToOne
    private Alumnos alumno;

    @Override
    public String toString() {
        return nombrePadre + " " + apellidoPadre;
    }
}
