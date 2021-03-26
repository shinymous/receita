package com.sicredi.receita.service;

import com.sicredi.receita.dto.IntegracaoReceitaRespostaDTO;
import com.sicredi.receita.dto.RespostaDTO;
import com.sicredi.receita.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class ReceitaIntegracaoService {

    private static final int COLUNA_AGENCIA = 0;
    private static final int COLUNA_CONTA = 1;
    private static final int COLUNA_SALDO = 2;
    private static final int COLUNA_STATUS = 3;

    private static final NumberFormat format = NumberFormat.getInstance(Locale.getDefault());

    private final ReceitaService receitaService = new ReceitaService();

    public RespostaDTO<List<IntegracaoReceitaRespostaDTO>> processaEnviaCsvReceita(MultipartFile csvFile) throws IOException, ParseException, InterruptedException {
        List<String[]> linhas = CsvUtil.leEConverteCsv(csvFile);
        List<IntegracaoReceitaRespostaDTO> linhasNaoAtualizadas = this.enviaListaDeInformacaoParaReceita(linhas);
        return RespostaDTO.<List<IntegracaoReceitaRespostaDTO>>builder()
                .data(linhasNaoAtualizadas)
                .build();
    }

    public List<IntegracaoReceitaRespostaDTO> enviaListaDeInformacaoParaReceita(List<String[]> linhas) throws ParseException, InterruptedException {
        List<IntegracaoReceitaRespostaDTO> linhasNaoAtualizadas = new ArrayList<>();
        for(int i = 0; i < linhas.size(); i++){
            String[] linha = linhas.get(i);
            boolean sucesso;
            try{
                sucesso = receitaService.atualizarConta(linha[COLUNA_AGENCIA], linha[COLUNA_CONTA].replaceAll("-", ""), format.parse(linha[COLUNA_SALDO]).doubleValue(), linha[COLUNA_STATUS]);
            }catch (RuntimeException e){
                linhasNaoAtualizadas.add(IntegracaoReceitaRespostaDTO.builder()
                        .linhaNaoAtualizada(i+1)
                        .motivo("Erro inesperado no serviço da receita")
                        .build());
                continue;
            }
            if(!sucesso)
                linhasNaoAtualizadas.add(IntegracaoReceitaRespostaDTO.builder()
                        .linhaNaoAtualizada(i+1)
                        .motivo("Linha com informação incorreta")
                        .build());
        }
        return linhasNaoAtualizadas;
    }
}
