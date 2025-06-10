package com.app.sistema.matricula.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "detalles")
@Table(name = "docentes")
public class Docentes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDocente;

    private String nombreDocente;
    private String apellidoDocente;
    private String dniDocente;
    private String correoDocente;
    private String passwordDocente;
    private String telefonoDocente;
    private String especialidadDocente;
    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCursoSeccion> detalles;
    private String rol;
}
