package com.app.sistema.matricula.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaDTO {
    private String bimestre;
    private double valorNota;
}
