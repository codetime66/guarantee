package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

public interface ITransferirCesta {

   public void acionarTransferencia(CestaGarantiasDO cesta, ContaParticipanteDO conta, Data dataOperacao,
         InstrumentoFinanceiroDO ifDO);

   public void finalizarTransferencia(MovimentacaoGarantiaDO movimentacao);

}
