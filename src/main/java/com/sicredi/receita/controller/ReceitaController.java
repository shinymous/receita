package com.sicredi.receita.controller;


import com.sicredi.receita.service.ReceitaIntegracaoService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/receita")
@Api(tags = "Receita")
public class ReceitaController {

    private final ReceitaIntegracaoService receitaIntegracaoService;

    @PostMapping("/csv-file")
    public ResponseEntity<?> teste(@RequestParam("file") MultipartFile csvFile) throws IOException, InterruptedException, ParseException {
        return ResponseEntity.ok(receitaIntegracaoService.processaEnviaCsvReceita(csvFile));
    }
}
