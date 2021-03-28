package com.sicredi.receita.service;

import com.google.common.collect.Lists;
import com.opencsv.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.sicredi.receita.dto.RespostaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class ReceitaIntegracaoService {

    private static final int COLUNA_AGENCIA = 0;
    private static final int COLUNA_CONTA = 1;
    private static final int COLUNA_SALDO = 2;
    private static final int COLUNA_STATUS = 3;

    public static final String ATUALIZADO = "Atualizado";
    public static final String NAO_ATUALIZADO_ERRO_INESPERADO = "Não atualizado: Erro inesperado no serviço da receita";
    public static final String NAO_ATUALIZADO_ERRO_FORMATACAO_LINHA = "Não atualizado: Erro na formatação da linha";


    private static final NumberFormat format = NumberFormat.getInstance(Locale.getDefault());

    private final ReceitaService receitaService = new ReceitaService();

    public RespostaDTO<ByteArrayOutputStream> processaEnviaCsvReceita(MultipartFile csvFile) throws IOException, ParseException, InterruptedException {
        //PREPARA O LEITOR CSV
        InputStream targetStream = csvFile.getInputStream();
        CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build(); // separador

        //PREPARA O ARQUIVO A SER ENVIADO
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(bufferedOutputStream));

        //TENTA CRIAR O LEITOR
        try (CSVReader csvReader = new CSVReaderBuilder(
                new InputStreamReader(targetStream))
                .withCSVParser(csvParser)   // adiciona o parser
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS) //campo null = vazio no arquivo
                .build()) {

            //ADICIONA COLUNA NO CABECALHO
            this.adicionaCabecalho(csvReader, csvWriter);

            String[] colunasPorLinha = null;
            //ITERAÇÃO LINHA POR LINHA DO CSV
            while ((colunasPorLinha = csvReader.readNext()) != null) {
                String resposta = this.enviaLinhaInformacaoParaReceita(colunasPorLinha);
                List<String> list = Lists.newArrayList(colunasPorLinha);
                list.add(resposta);
                csvWriter.writeNext(list.toArray(new String[0]));
            }
            csvWriter.close();
        }

        return RespostaDTO.<ByteArrayOutputStream>builder()
                .data(out)
                .build();
    }

    public void adicionaCabecalho(CSVReader csvReader, CSVWriter csvWriter) throws IOException {
        List<String> cabecalho = Lists.newArrayList(csvReader.readNext());
        cabecalho.add("resultado");
        csvWriter.writeNext(cabecalho.toArray(new String[0]));
    }

    public String enviaLinhaInformacaoParaReceita(String[] linha) throws ParseException, InterruptedException {
        boolean sucesso;
        try{
            sucesso = receitaService.atualizarConta(linha[COLUNA_AGENCIA], linha[COLUNA_CONTA].replaceAll("-", ""), format.parse(linha[COLUNA_SALDO]).doubleValue(), linha[COLUNA_STATUS]);
        }catch (RuntimeException e){
            return NAO_ATUALIZADO_ERRO_INESPERADO;
        }
        if(!sucesso)
            return NAO_ATUALIZADO_ERRO_FORMATACAO_LINHA;
        else
            return ATUALIZADO;
    }
}
