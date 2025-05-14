package com.app.sistema.matricula.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "administradores")
public class Administrador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAdministrador;
    private String nombreAdministrador;
    private String apellidoAdministrador;
    private String dniAdministrador;
    private String correoAdministrador;
    private String passwordAdministrador;
    private String telefonoAdministrador;
    private String rol;
    @OneToMany(mappedBy = "administrador")
    private List<Matricula> matriculas;
}
