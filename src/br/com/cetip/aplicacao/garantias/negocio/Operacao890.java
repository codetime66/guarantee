package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.operacao.apinegocio.OperacaoVO;
import br.com.cetip.dados.aplicacao.custodia.TipoDebitoDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;

/**
 * 
 * Operacao de DESBLOQUEIO de ativos de uma determinada Cesta de Garantias
 * 
 * @author marco sergio
 * @author <a href="bruno.borges@summa-tech.com">Bruno Borges</a>
 * @since 2006
 */
final class Operacao890 extends MIGOperacaoUnitaria {

   /*
    * Complemento dos dados da operacao, quando chamado pelo MMG
    */
   public void completarDadosOperacao() {
      ContaParticipanteDO garantidor = getGarantidor();

      OperacaoVO operacaoVO = getOperacaoUnitariaVO();
      operacaoVO.setContaParticipanteP1(garantidor);
      operacaoVO.setContaParticipanteP2(garantidor);

      CodigoContaCetip conta = garantidor.getCodContaParticipante();
      IdTipoGarantia idTipoGarantia = getCesta().getTipoGarantia().getNumIdTipoGarantia();

      /*
       * Desbloqueio de Garantia ocorre quando a cesta ainda nao foi vinculada. Por esta razao, o nivel de carteira em
       * que a garantia foi depositada eh sempre 1.
       */
      Id tipoDebitoP1 = TipoDebitoDO.PROPRIA_LIVRE;
      Id tipoDebitoP2 = getTipoDebito(conta, idTipoGarantia, false);

      operacaoVO.setTipoDebitoP1((TipoDebitoDO) getGp().load(TipoDebitoDO.class, tipoDebitoP1));
      operacaoVO.setTipoDebitoP2((TipoDebitoDO) getGp().load(TipoDebitoDO.class, tipoDebitoP2));
   }

   protected CodigoTipoOperacao getCodigoTipoOperacao() {
      return CodigoTipoOperacao.COD_DESBLOQUEIO_GARANTIA;
   }

}
