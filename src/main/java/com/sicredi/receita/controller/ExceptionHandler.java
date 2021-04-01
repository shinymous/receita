package com.sicredi.receita.controller;

import com.sicredi.receita.dto.RespostaDTO;
import com.sicredi.receita.exception.CsvParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {


    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public final ResponseEntity<RespostaDTO<?>> handleAllExceptions(Exception ex) {
        return new ResponseEntity<RespostaDTO<?>>(buildErrorDetails(ex, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IOException.class)
    public final ResponseEntity<RespostaDTO<?>> handleIOException(IOException ex){
        return new ResponseEntity<RespostaDTO<?>>(buildErrorDetails(ex, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ParseException.class)
    public final ResponseEntity<RespostaDTO<?>> handleParseException(ParseException ex){
        return new ResponseEntity<RespostaDTO<?>>(buildErrorDetails(ex, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(CsvParseException.class)
    public final ResponseEntity<RespostaDTO<?>> handleCsvParseException(CsvParseException ex){
        return new ResponseEntity<RespostaDTO<?>>(buildErrorDetails(ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InterruptedException.class)
    public final ResponseEntity<RespostaDTO<?>> handleInterruptedException(InterruptedException ex){
        return new ResponseEntity<RespostaDTO<?>>(buildErrorDetails(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno: thread interrompida"), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<RespostaDTO<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        StringBuilder sb = new StringBuilder();
        for(ObjectError objectError : ex.getBindingResult().getAllErrors()){
            sb.append("[");
            sb.append(objectError.getDefaultMessage());
            sb.append("] ");
        }
        return new ResponseEntity<RespostaDTO<?>>(buildErrorDetails(ex, HttpStatus.BAD_REQUEST, sb.toString()), HttpStatus.BAD_REQUEST);
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

    private RespostaDTO<?> buildErrorDetails(Exception ex, HttpStatus httpStatus, String mensagemErro) {
        log.error(ex.getMessage(), ex);
        return RespostaDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(mensagemErro)
                .build();
    }

}
