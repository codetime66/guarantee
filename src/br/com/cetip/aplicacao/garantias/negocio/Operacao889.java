package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.operacao.apinegocio.OperacaoVO;
import br.com.cetip.dados.aplicacao.custodia.TipoDebitoDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.utilitario.Condicional;

/**
 * 
 * Operacao de BLOQUEIO de ativos a uma determinada Cesta de Garantias
 * 
 */
final class Operacao889 extends MIGOperacaoUnitaria {

   public IdStatusMovimentacaoGarantia validar() {
      IdStatusMovimentacaoGarantia status = super.validar();

      if (!status.mesmoConteudo(IdStatusMovimentacaoGarantia.OK)) {
         return status;
      }

      if (!Condicional.vazio(getIfDO().getIndInadimplencia()) && getIfDO().getIndInadimplencia().ehVerdadeiro()) {
         return IdStatusMovimentacaoGarantia.IF_INADIMPLENTE;
      }

      return IdStatusMovimentacaoGarantia.OK;
   }

   public void completarDadosOperacao() {
      ContaParticipanteDO garantidor = getGarantidor();

      OperacaoVO operacaoVO = getOperacaoUnitariaVO();
      operacaoVO.setContaParticipanteP1(garantidor);
      operacaoVO.setContaParticipanteP2(garantidor);

      CodigoContaCetip conta = garantidor.getCodContaParticipante();

      Id tipoDebitoP2 = TipoDebitoDO.PROPRIA_LIVRE;

      IdTipoGarantia idTipoGarantia = getCesta().getTipoGarantia().getNumIdTipoGarantia();

      /*
       * Operacao de Bloqueio sempre joga os ativos garantidores para a carteira de nivel 1
       * 
       * Se o ativo garantidor for STA, no momento da Vinculacao desta cesta sera verificado se este ativo eh garantido
       * por uma outra cesta X. Neste caso, as garantias da cesta X serao movidas para carteiras de segundo nivel (Operacao 990)
       */
      Id tipoDebitoP1 = getTipoDebito(conta, idTipoGarantia, false);

      operacaoVO.setTipoDebitoP1((TipoDebitoDO) getGp().load(TipoDebitoDO.class, tipoDebitoP1));
      operacaoVO.setTipoDebitoP2((TipoDebitoDO) getGp().load(TipoDebitoDO.class, tipoDebitoP2));
   }

   protected CodigoTipoOperacao getCodigoTipoOperacao() {
      return CodigoTipoOperacao.COD_BLOQUEIO_GARANTIA;
   }

}
