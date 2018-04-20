package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.ITipoGarantiaCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.ITransferirCesta;
import br.com.cetip.aplicacao.garantias.negocio.transferencia.TransferenciaCessaoFiduciaria;
import br.com.cetip.aplicacao.garantias.negocio.transferencia.TransferenciaPenhorEmissor;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.log.Logger;

class TransferirCesta extends BaseGarantias implements ITransferirCesta {

   private Map implementacoes = null;

   public TransferirCesta() {
      implementacoes = new HashMap(2);
      implementacoes.put(IdTipoGarantia.PENHOR_NO_EMISSOR, TransferenciaPenhorEmissor.class);
      implementacoes.put(IdTipoGarantia.CESSAO_FIDUCIARIA, TransferenciaCessaoFiduciaria.class);
   }

   public ITransferirCesta getImplementacao(CestaGarantiasDO cesta) {
      ITipoGarantiaCesta itgc = getFactory().getInstanceTipoGarantiaCesta();
      IdTipoGarantia tipo = itgc.obterTipoGarantia(cesta);
      Class clazz = (Class) implementacoes.get(tipo);
      ITransferirCesta instancia = null;

      try {
         instancia = (ITransferirCesta) clazz.newInstance();
      } catch (InstantiationException e) {
         Logger.error(e);
      } catch (IllegalAccessException e) {
         Logger.error(e);
      }

      return instancia;
   }

   public void acionarTransferencia(CestaGarantiasDO cesta, ContaParticipanteDO conta, Data dataOperacao,
         InstrumentoFinanceiroDO ifDO) {
      ITransferirCesta implementacao = getImplementacao(cesta);
      implementacao.acionarTransferencia(cesta, conta, dataOperacao, ifDO);
   }

   public void finalizarTransferencia(MovimentacaoGarantiaDO movimentacao) {
      CestaGarantiasDO cestaGarantias = movimentacao.getCestaGarantias();
      ITransferirCesta implementacao = getImplementacao(cestaGarantias);
      implementacao.finalizarTransferencia(movimentacao);
   }

}
