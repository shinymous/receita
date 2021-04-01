package com.sicredi.receita.service;

import com.sicredi.receita.dto.RespostaDTO;
import com.sicredi.receita.exception.CsvParseException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;

import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ReceitaIntegracaoServiceTest {


    @MockBean
    private ReceitaIntegracaoService receitaIntegracaoService;

    @Test
    public void shouldProcessarCsvReceita() throws ParseException, InterruptedException, IOException {
        BDDMockito.when(receitaIntegracaoService.processaLinhaParaReceita(any(String[].class), anyInt()))
                .thenReturn(ReceitaIntegracaoService.ATUALIZADO);
        BDDMockito.when(receitaIntegracaoService.processaEnviaCsvReceita(any(MultipartFile.class)))
                .thenCallRealMethod();
        File file = new File("src/test/java/com/sicredi/receita/teste.csv");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("teste.csv", fileInputStream);
        RespostaDTO<ByteArrayOutputStream> resposta = receitaIntegracaoService.processaEnviaCsvReceita(multipartFile);
        Assertions.assertThat((long) resposta.getData().size()).isGreaterThanOrEqualTo(multipartFile.getSize());
    }

    @Test(expected = CsvParseException.class)
    public void shouldProcessaLinhaParaReceitaThrowParseException() throws ParseException, InterruptedException {
        BDDMockito.when(receitaIntegracaoService.enviaInformacaoParaReceita(any(String[].class)))
                .thenThrow(new IndexOutOfBoundsException());
        BDDMockito.when(receitaIntegracaoService.processaLinhaParaReceita(any(String[].class), anyInt()))
                .thenCallRealMethod();
        String[] linhaInvalida = new String[]{"linhaInvalida; coluna1; coluna2"};
        receitaIntegracaoService.processaLinhaParaReceita(linhaInvalida, 1);
    }

    @Test
    public void shouldEnviarCsvReceita() throws ParseException, InterruptedException {
        BDDMockito.when(receitaIntegracaoService.processaLinhaParaReceita(any(String[].class), anyInt()))
                .thenCallRealMethod();
        BDDMockito.when(receitaIntegracaoService.enviaInformacaoParaReceita(any(String[].class)))
                .thenReturn(true);
        String[] linha1 = new String[]{"0101", "12225-6", "100,00", "A"};
        String resposta = receitaIntegracaoService.processaLinhaParaReceita(linha1, 0);
        Assertions.assertThat(resposta).isEqualTo(ReceitaIntegracaoService.ATUALIZADO);
    }

    @Test
    public void shouldEnviarComErroNaLinha() throws ParseException, InterruptedException {
        BDDMockito.when(receitaIntegracaoService.processaLinhaParaReceita(any(String[].class), anyInt()))
                .thenCallRealMethod();
        BDDMockito.when(receitaIntegracaoService.enviaInformacaoParaReceita(any(String[].class)))
                .thenReturn(false);
        String[] linha1 = new String[]{"0101", "12225-6", "100,00", "A"};
        String resposta = receitaIntegracaoService.processaLinhaParaReceita(linha1, 0);
        Assertions.assertThat(resposta).isEqualTo(ReceitaIntegracaoService.NAO_ATUALIZADO_ERRO_FORMATACAO_LINHA);
    }

    @Test
    public void shouldEnviarComErroNaIntegracao() throws ParseException, InterruptedException {
        BDDMockito.when(receitaIntegracaoService.processaLinhaParaReceita(any(String[].class), anyInt()))
                .thenCallRealMethod();
        BDDMockito.when(receitaIntegracaoService.enviaInformacaoParaReceita(any(String[].class)))
                .thenThrow(new RuntimeException());
        String[] linha1 = new String[]{"0101", "12225-6", "100,00", "A"};
        String resposta = receitaIntegracaoService.processaLinhaParaReceita(linha1, 0);
        Assertions.assertThat(resposta).isEqualTo(ReceitaIntegracaoService.NAO_ATUALIZADO_ERRO_INESPERADO);
    }
}
