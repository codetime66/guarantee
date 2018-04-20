package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IContaGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.ITransferirCesta;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

/**
 * Operacao de TRANSFERENCIA de Cesta de Garantias
 * 
 * Classe para criar operacoes 892 (Transferencia de cesta de garantias) em lote, ou seja, eh chamado uma procedure para
 * criar varias operacoes de uma so vez
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
final class Operacao892 extends MIGOperacaoLote {

   protected void executaComandosExtras() {
      // transfere as cestas dentro da cesta que foi transferida... claro, somente se essa cesta NAO eh de segundo nivel
      CestaGarantiasDO cesta = getCesta();

      // soh movimenta para segundo nivel (cesta de cesta) se estah em primeiro nivel
      if (!ehCestaSegundoNivel()) {
         // TRANSFEREENCIA foi executada
         // Se ha ativos garantidores STA vinculados a cestas, disparar Operacao 892
         // para as cestas vinculadas a estes ativos
         ICestaDeGarantias icg = getGarantias().getInstanceCestaDeGarantias();
         ITransferirCesta itc = getGarantias().getInstanceTransferirCesta();

         ContaParticipanteDO contaGarantido = getMovimentacao().getContaParticipante();

         // Aciona operacao 892 para as cestas de todos os ativos garantidores que possuem cesta
         List cestasATransferir = icg.listarCestasSegundoNivel(cesta);
         Iterator it = cestasATransferir.iterator();

         while (it.hasNext()) {
            CestaGarantiasDO innerCesta = (CestaGarantiasDO) it.next();

            Iterator i = innerCesta.getAtivosVinculados().iterator();
            while (i.hasNext()) {
               InstrumentoFinanceiroDO ifDO = ((CestaGarantiasIFDO) i.next()).getInstrumentoFinanceiro();
               itc.acionarTransferencia(innerCesta, contaGarantido, getDataOperacao(), ifDO);
            }
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.aplicacao.operacao.negocio.MIGOperacao#completarDadosOperacao()
    */
   public void completarDadosOperacao() {
      IContaGarantia icg = getGarantias().getInstanceContaGarantia();

      Texto tipoCompradorP1 = tipoDebito(getMovimentacao().getContaParticipante().getCodContaParticipante());
      Texto tipoVendedorP2 = tipoContaGarantido();
      Texto cestaSegundoNivel = ehCestaSegundoNivel() ? new Texto("S") : new Texto("N");

      Id idCesta = getCesta().getNumIdCestaGarantias();
      Id idMovimentacao = getMovimentacao().getNumIdMovimentacaoGarantia();
      Id idGarantidor = getGarantidor().getId();
      Id idConta60 = icg.obterConta60(getMovimentacao().getContaParticipante()).getId();

      Object[] params = new Object[] { idCesta, idMovimentacao, idGarantidor, idConta60, cestaSegundoNivel,
            tipoVendedorP2, tipoCompradorP1 };

      ISqlWrapper pw1 = new ProcedureSqlWrapper("CETIP.P_TRANSFERE_IF_CESTA_GARANTIAS(?, ?, ?, ?, ?, ?, ?)", params);
      addSqlWrapperObject(pw1);

      ISqlWrapper pw2 = new ProcedureSqlWrapper("CETIP.P_TRNS_IF_ALTP_CESTA_GARANTIAS(?, ?, ?, ?, ?, ?, ?)", params);
      addSqlWrapperObject(pw2);
   }

}
