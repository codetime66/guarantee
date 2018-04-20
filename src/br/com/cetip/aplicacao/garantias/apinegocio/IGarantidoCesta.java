package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.AcessoCestaDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;

public interface IGarantidoCesta {

   public ContaParticipanteDO obterGarantidoCesta(CestaGarantiasDO cesta);

   public AcessoCestaDO cadastrarAcessoVisualizador(CestaGarantiasDO cesta, ContaParticipanteDO conta);

   public AcessoCestaDO cadastrarAcessoGarantido(CestaGarantiasDO cesta, ContaParticipanteDO conta);

   public void associarGarantidoNaCesta(CestaGarantiasDO cesta, ContaParticipanteDO conta);

   public void excluirGarantidosSemCustodia(CestaGarantiasDO cesta);

   public void removerAcesso(CestaGarantiasDO cesta, ContaParticipanteDO garantido);

}
