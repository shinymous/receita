package com.sicredi.receita.service;

import com.sicredi.receita.dto.IntegracaoReceitaRespostaDTO;
import com.sicredi.receita.util.CsvUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ReceitaIntegracaoServiceTest {


    @Autowired
    private ReceitaIntegracaoService receitaIntegracaoService;


    @Test
    public void shouldConverterCsv() throws IOException {
        File file = new File("teste.csv");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("teste.csv", input);
        List<String[]> strings = CsvUtil.leEConverteCsv(multipartFile);
        Assertions.assertThat(strings.size()).isGreaterThanOrEqualTo(1);
        Assertions.assertThat(strings.get(0)[0]).isEqualTo("0101");
    }

    @Test
    public void shouldEnviarCsvReceita() throws ParseException, InterruptedException {
        String[] linha1 = new String[]{"0101", "12225-6", "100,00", "A"};
        String[] linha2 = new String[]{"0101", "12226-8", "3200,50", "A"};
        String[] linha3 = new String[]{"3202", "40011-1", "35,12", "I"};
        List<String[]> listaDeInformacao = new ArrayList<>();
        listaDeInformacao.add(linha1);
        listaDeInformacao.add(linha2);
        listaDeInformacao.add(linha3);
        List<IntegracaoReceitaRespostaDTO> integracaoReceitaRespostaDTOS = receitaIntegracaoService.enviaListaDeInformacaoParaReceita(listaDeInformacao);
        Assertions.assertThat(integracaoReceitaRespostaDTOS).isEmpty();
    }
}
