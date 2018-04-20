package br.com.cetip.aplicacao.garantias.negocio.transferencia;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.ITransferirCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.log.Logger;

public class TransferenciaCessaoFiduciaria extends BaseGarantias implements ITransferirCesta {

   public void acionarTransferencia(CestaGarantiasDO cesta, ContaParticipanteDO conta, Data dataOperacao,
         InstrumentoFinanceiroDO ativo) {
      IMovimentacoesGarantias imovs = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO movimentacao = imovs.incluirMovimentacaoTransferencia(cesta, conta, ativo);

      boolean temSelic = temSelic(cesta);
      if (temSelic) {
         transferePorSelic(cesta);
      } else {
         finalizarTransferencia(movimentacao);
      }
   }

   private void transferePorSelic(CestaGarantiasDO cesta) {
      IGarantiasSelic igs = getFactory().getInstanceGarantiasSelic();
      try {
         igs.transferirGarantias(cesta);
      } catch (Exception e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, "SELIC: " + e.getMessage());
      }
   }

   private boolean temSelic(CestaGarantiasDO cesta) {
      IGarantiasSelic ig = getFactory().getInstanceGarantiasSelic();
      try {
         return ig.temSelicEmDetalhes(cesta.getNumIdCestaGarantias());
      } catch (Exception e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, "SELIC: " + e.getMessage());
      }
   }

   public void finalizarTransferencia(MovimentacaoGarantiaDO movimentacao) {
      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();

      //Aciona a Transferencia
      icg.acionaMIG(movimentacao, Booleano.FALSO, null);

      ContaParticipanteDO conta = movimentacao.getContaParticipante();
      CestaGarantiasDO cesta = movimentacao.getCestaGarantias();

      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
      // remove acesso do antigo garantido
      igc.removerAcesso(cesta, cesta.getGarantido());
      // cadastra o novo garantido e associa a conta 60 na cesta
      igc.associarGarantidoNaCesta(cesta, conta);
   }

}
