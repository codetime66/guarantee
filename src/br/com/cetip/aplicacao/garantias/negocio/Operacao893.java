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
 * Operacao de APORTE de um ativo a uma Cesta de Garantias
 * 
 * @author marco sergio
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * @since 2006
 */
final class Operacao893 extends MIGOperacaoUnitaria {

   protected Operacao893() {
      setComIdentificaComitente(true);
   }

   /**
    * Validacao
    */
   public IdStatusMovimentacaoGarantia validar() {
      IdStatusMovimentacaoGarantia status = super.validar();

      if (!status.mesmoConteudo(IdStatusMovimentacaoGarantia.OK)) {
         return status;
      }

      if (Condicional.vazio(getConta60Garantido())) {
         return IdStatusMovimentacaoGarantia.GARANTIDO_INVALIDO;
      }

      return IdStatusMovimentacaoGarantia.OK;
   }

   /**
    * Completa os dados da operacao
    */
   public void completarDadosOperacao() {
      ContaParticipanteDO garantidor = getGarantidor();

      OperacaoVO operacaoVO = getOperacaoUnitariaVO();
      operacaoVO.setContaParticipanteP1(getConta60Garantido());
      operacaoVO.setContaParticipanteP2(garantidor);

      CodigoContaCetip contaGarantido = getGarantido().getCodContaParticipante();

      // Verifica se o IF vinculado a cesta sofrendo este aporte esta dentro
      // de alguma outra cesta e
      // esta por sua vez, esteja vinculada.
      // Se sim, este aporte sera feito para carteiras de segundo nivel.
      boolean cestaSegundoNivel = ehCestaSegundoNivel();

      IdTipoGarantia idTipoGarantia = getCesta().getTipoGarantia().getNumIdTipoGarantia();
      Id tipoDebitoP1 = getTipoDebito(contaGarantido, idTipoGarantia, cestaSegundoNivel);
      Id tipoDebitoP2 = TipoDebitoDO.PROPRIA_LIVRE;

      operacaoVO.setTipoDebitoP1((TipoDebitoDO) getGp().load(TipoDebitoDO.class, tipoDebitoP1));
      operacaoVO.setTipoDebitoP2((TipoDebitoDO) getGp().load(TipoDebitoDO.class, tipoDebitoP2));
   }

   protected CodigoTipoOperacao getCodigoTipoOperacao() {
      return CodigoTipoOperacao.COD_APORTE_GARANTIA;
   }

}
