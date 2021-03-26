package com.sicredi.receita.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IntegracaoReceitaRespostaDTO {
    private Integer linhaNaoAtualizada;
    private String motivo;
}
