package com.app.sistema.matricula.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocenteDTO {
    private Integer idDocente;
    private String nombreDocente;
    private String apellidoDocente;
    private String dniDocente;
    private String correoDocente;
    private String passwordDocente;
    private String rol;
    private String especialidadDocente;
    private String telefonoDocente;
}
