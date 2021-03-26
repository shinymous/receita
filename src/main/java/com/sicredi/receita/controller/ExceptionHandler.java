package com.sicredi.receita.controller;

import com.sicredi.receita.dto.RespostaDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {


    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public final ResponseEntity<RespostaDTO<?>> handleAllExceptions(Exception ex) {
        return new ResponseEntity<RespostaDTO<?>>(buildErrorDetails(ex, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private RespostaDTO<?> buildErrorDetails(Exception ex, HttpStatus httpStatus) {
        log.error(ex.getMessage(), ex);
        return RespostaDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(ex.getMessage())
                .build();
    }

}
