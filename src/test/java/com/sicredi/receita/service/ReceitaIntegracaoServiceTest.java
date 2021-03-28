package com.sicredi.receita.service;

import com.sicredi.receita.dto.RespostaDTO;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ReceitaIntegracaoServiceTest {


    @Autowired
    private ReceitaIntegracaoService receitaIntegracaoService;


    @Test
    public void shouldProcessarCsvReceita() throws ParseException, InterruptedException, IOException {
        ReceitaIntegracaoService mock = Mockito.mock(ReceitaIntegracaoService.class);
        BDDMockito.when(mock.enviaLinhaInformacaoParaReceita(any(String[].class)))
                .thenReturn(ReceitaIntegracaoService.ATUALIZADO);
        BDDMockito.when(mock.processaEnviaCsvReceita(any(MultipartFile.class)))
                .thenCallRealMethod();
        File file = new File("src/test/java/com/sicredi/receita/teste.csv");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("teste.csv", fileInputStream);
        RespostaDTO<ByteArrayOutputStream> resposta = mock.processaEnviaCsvReceita(multipartFile);
        Assertions.assertThat((long) resposta.getData().size()).isGreaterThanOrEqualTo(multipartFile.getSize());
    }

    @Test
    public void shouldEnviarCsvReceita() throws ParseException, InterruptedException {
        String[] linha1 = new String[]{"0101", "12225-6", "100,00", "A"};
        String resposta = receitaIntegracaoService.enviaLinhaInformacaoParaReceita(linha1);
        Assertions.assertThat(resposta).isEqualTo(ReceitaIntegracaoService.ATUALIZADO);
    }
}
