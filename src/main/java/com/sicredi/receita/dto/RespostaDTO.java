package com.sicredi.receita.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RespostaDTO <T> {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private T data;
}
