package br.com.cetip.aplicacao.garantias.negocio.transferencia;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.ITransferirCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

public class TransferenciaPenhorEmissor extends BaseGarantias implements ITransferirCesta {

   public void acionarTransferencia(CestaGarantiasDO cesta, ContaParticipanteDO conta, Data dataOperacao,
         InstrumentoFinanceiroDO ifDO) {

      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
      igc.cadastrarAcessoGarantido(cesta, conta);
      igc.excluirGarantidosSemCustodia(cesta);
   }

   public void finalizarTransferencia(MovimentacaoGarantiaDO mov) {
   }

}
