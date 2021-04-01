package com.sicredi.receita.service;

import com.google.common.collect.Lists;
import com.opencsv.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.sicredi.receita.dto.RespostaDTO;
import com.sicredi.receita.exception.CsvParseException;
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

    private final ReceitaService receitaService;

    public RespostaDTO<ByteArrayOutputStream> processaEnviaCsvReceita(MultipartFile csvFile) throws IOException, ParseException, InterruptedException {
        InputStream targetStream = csvFile.getInputStream();
        CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(bufferedOutputStream));

        try (CSVReader csvReader = this.buildCsvReader(targetStream, csvParser)) {
            this.adicionaCabecalho(csvReader, csvWriter);

            String[] colunasPorLinha = null;
            int linhaAtual = 2;
            while ((colunasPorLinha = csvReader.readNext()) != null) {
                String resposta = this.processaLinhaParaReceita(colunasPorLinha, linhaAtual);
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

    private CSVReader buildCsvReader(InputStream targetStream, CSVParser csvParser){
        return new CSVReaderBuilder(
                new InputStreamReader(targetStream))
                .withCSVParser(csvParser)
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                .build();
    }

    private void adicionaCabecalho(CSVReader csvReader, CSVWriter csvWriter) throws IOException {
        List<String> cabecalho = Lists.newArrayList(csvReader.readNext());
        cabecalho.add("resultado");
        csvWriter.writeNext(cabecalho.toArray(new String[0]));
    }

    protected String processaLinhaParaReceita(String[] linha, int linhaPosicao) throws ParseException, InterruptedException {
        boolean sucesso;
        try{
            sucesso = this.enviaInformacaoParaReceita(linha);
        }catch (IndexOutOfBoundsException ex){
            throw new CsvParseException("Linha "+linhaPosicao+ " inválida");
        }catch (RuntimeException e){
            return NAO_ATUALIZADO_ERRO_INESPERADO;
        }
        if(!sucesso)
            return NAO_ATUALIZADO_ERRO_FORMATACAO_LINHA;
        else
            return ATUALIZADO;
    }

    protected boolean enviaInformacaoParaReceita(String[] informacoes) throws ParseException, InterruptedException {
        return receitaService.atualizarConta(informacoes[COLUNA_AGENCIA], informacoes[COLUNA_CONTA].replaceAll("-", ""), format.parse(informacoes[COLUNA_SALDO]).doubleValue(), informacoes[COLUNA_STATUS]);
    }
}
