package com.sicredi.receita.controller;


import com.sicredi.receita.dto.RespostaDTO;
import com.sicredi.receita.service.ReceitaIntegracaoService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/receita")
@Api(tags = "Receita")
public class ReceitaController {

    private final ReceitaIntegracaoService receitaIntegracaoService;

    @PostMapping(value = "/csv-file", produces = "application/csv")
    public ResponseEntity<Resource> enviaCsv(@RequestParam("file") MultipartFile csv) throws IOException, InterruptedException, ParseException {
        RespostaDTO<ByteArrayOutputStream> resposta = receitaIntegracaoService.processaEnviaCsvReceita(csv);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + csv.getName());
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        ByteArrayResource resource = new ByteArrayResource(resposta.getData().toByteArray());
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
