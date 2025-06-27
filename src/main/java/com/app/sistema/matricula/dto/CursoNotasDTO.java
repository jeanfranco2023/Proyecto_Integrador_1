package com.app.sistema.matricula.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoNotasDTO {
    private String nombreCurso;
    private List<NotaDTO> notas;
}
