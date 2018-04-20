package br.com.cetip.aplicacao.garantias.negocio;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.custodia.apinegocio.CarteiraParticipanteFactory;
import br.com.cetip.aplicacao.custodia.apinegocio.ICarteiraParticipante;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.operacao.apinegocio.DetalheCaucaoFactory;
import br.com.cetip.aplicacao.operacao.apinegocio.IDetalheCaucao;
import br.com.cetip.aplicacao.operacao.apinegocio.OperacaoVO;
import br.com.cetip.dados.aplicacao.custodia.CarteiraParticipanteDO;
import br.com.cetip.dados.aplicacao.custodia.TipoDebitoDO;
import br.com.cetip.dados.aplicacao.custodia.TipoPosicaoCarteiraDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.operacao.DetalheCaucaoDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;

/**
 * Operacao de RETIRADA
 * 
 * @author marco sergio
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
final class Operacao894 extends MIGOperacaoUnitaria {

   protected Operacao894() {
      setComIdentificaComitente(true);
   }

   public IdStatusMovimentacaoGarantia validar() {
      IdStatusMovimentacaoGarantia status = super.validar();

      if (!status.mesmoConteudo(IdStatusMovimentacaoGarantia.OK)) {
         return status;
      }

      if (Condicional.vazio(getConta60Garantido())) {
         return IdStatusMovimentacaoGarantia.GARANTIDO_INVALIDO;
      }

      if (!validaPosicaoCarteira()) {
         return IdStatusMovimentacaoGarantia.IF_EM_RESERVA_TECNICA;
      }

      if (isOperacaoFinalizada() == false) {
         Quantidade qtdTotal = obterQuantidadeDetalheCaucao();

         if (qtdTotal != null && qtdTotal.subtrair(getMovimentacao().getQtdGarantia()).obterConteudo().intValue() < 0) {
            return IdStatusMovimentacaoGarantia.SEM_SALDO;
         }
      }

      return IdStatusMovimentacaoGarantia.OK;
   }

   private Quantidade obterQuantidadeDetalheCaucao() {
      // validar Quantidades atraves da tabela DETALHE_CAUCAO
      Quantidade qtdTotal = new Quantidade("0");

      Id idNumIF = getIfDO().getId();
      Id idCesta = getCesta().getNumIdCestaGarantias();
      Booleano indDireitoGarantidor = getMovimentacao().getIndDireitosGarantidor();
      IdTipoGarantia idTipoGarantia = getMovimentacao().getCestaGarantias().getTipoGarantia().getNumIdTipoGarantia();

      IDetalheCaucao iDetalheCaucao;
      try {
         iDetalheCaucao = DetalheCaucaoFactory.getInstance();
      } catch (Exception e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, e.getMessage());
      }

      List listaDetalheCaucaoCesta;
      listaDetalheCaucaoCesta = iDetalheCaucao.obterDetalheCaucaoCestaDeGarantias(idCesta, idNumIF, idTipoGarantia,
            indDireitoGarantidor);
      Iterator itlistaDetalheCaucaoCesta = listaDetalheCaucaoCesta.iterator();

      while (itlistaDetalheCaucaoCesta.hasNext()) {
         DetalheCaucaoDO detalheCaucaoDO = (DetalheCaucaoDO) itlistaDetalheCaucaoCesta.next();
         QuantidadeInteiraPositiva qtdDetCaucao = detalheCaucaoDO.getQtdDetalheCaucao();
         qtdTotal = qtdTotal.somar(new Quantidade(qtdDetCaucao.obterConteudo().toString()));
      }
      return qtdTotal;
   }

   public void completarDadosOperacao() {
      OperacaoVO operacaoVO = getOperacaoUnitariaVO();
      operacaoVO.setContaParticipanteP1(getGarantidor());

      ContaParticipanteDO garantido = getGarantido();
      CodigoContaCetip contaGarantido = garantido.getCodContaParticipante();

      operacaoVO.setContaParticipanteP2(getConta60Garantido());

      // Verifica se o IF vinculado a cesta sofrendo este aporte esta dentro
      // de alguma outra cesta e
      // esta por sua vez, esteja vinculada.
      // Se sim, este aporte sera feito para carteiras de segundo nivel.
      boolean cestaSegundoNivel = ehCestaSegundoNivel();

      Id tipoDebitoP1 = TipoDebitoDO.PROPRIA_LIVRE;
      IdTipoGarantia idTipoGarantia = getCesta().getTipoGarantia().getNumIdTipoGarantia();
      Id tipoDebitoP2 = getTipoDebito(contaGarantido, idTipoGarantia, cestaSegundoNivel);

      operacaoVO.setTipoDebitoP1((TipoDebitoDO) getGp().load(TipoDebitoDO.class, tipoDebitoP1));
      operacaoVO.setTipoDebitoP2((TipoDebitoDO) getGp().load(TipoDebitoDO.class, tipoDebitoP2));
   }

   /**
    * Valida a posicao da Carteira
    * 
    * @param idNumIF
    * @param conta
    * @param codigoSistema
    * @return
    */
   private boolean validaPosicaoCarteira() {
      Id conta = getConta60Garantido().getId();
      Id idNumIF = getIfDO().getId();
      Id codigoSistema = getIfDO().getSistema().getNumero();

      ICarteiraParticipante cp;
      try {
         cp = CarteiraParticipanteFactory.getInstance();
         List l = cp.obterPosicoesCarteira(null, idNumIF, conta, codigoSistema);
         for (Iterator i = l.iterator(); i.hasNext();) {
            CarteiraParticipanteDO cpd = (CarteiraParticipanteDO) i.next();
            if (cpd.getTipoPosicaoCarteira().getCodigo().mesmoConteudo(TipoPosicaoCarteiraDO.RESERVA_TECNICA)
                  && cpd.getQuantidade().obterConteudo().compareTo(new BigDecimal("0")) > 0) {
               return false;
            }
         }

      } catch (Exception e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, e.getMessage());
      }

      return true;
   }

   public static class Operacao894Total extends MIGOperacaoLote {

      protected Operacao894Total() {
         setComIdentificaComitente(true);
      }

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

      protected void executaComandosExtras() {
         // soh movimenta para segundo nivel (cesta de cesta) se estah em primeiro nivel
         if (!ehCestaSegundoNivel()) {
            // DESVINCULACAO foi executada
            // Se ha ativos garantidores STA vinculados a cestas, disparar Operacao990
            // para as cestas vinculadas a estes ativos
            ICestaDeGarantias icg = getGarantias().getInstanceCestaDeGarantias();

            // Aciona Operacao990 para as cestas de todos os ativos garantidores que possuem cesta
            CestaGarantiasDO cesta = getCesta();
            List cestasARetirarLastro = icg.listarCestasSegundoNivel(cesta);
            Iterator it = cestasARetirarLastro.iterator();

            while (it.hasNext()) {
               CestaGarantiasDO innerCesta = (CestaGarantiasDO) it.next();

               boolean ehCestaSegundoNivel = innerCesta.getIndSegundoNivel().ehVerdadeiro();
               if (!ehCestaSegundoNivel) {
                  icg.retirarCestaDeCesta(innerCesta);
               }
            }
         }
      }

      protected void completarDadosOperacao() {
         // Verifica se o IF a ser desvinculado esta dentro de alguma outra cesta e
         // esta por sua vez, esteja vinculada.
         // Se sim, os ativos desta cesta sofrendo operacao 894, serao movidos
         // para carteiras de segundo nivel.
         Object[] params = new Object[] { getCesta().getNumIdCestaGarantias() };

         String nome = "CETIP.F_VINCULA_IF_CESTA_GARANTIAS(?, ?, ?, ?, ?, ?, ?)";
         addSqlWrapperObject(new FunctionSqlWrapper(nome, params));

         nome = "CETIP.F_VINC_IF_ALTP_CESTA_GARANTIAS(?, ?, ?, ?, ?, ?, ?)";
         addSqlWrapperObject(new FunctionSqlWrapper(nome, params));
      }

   }

   protected CodigoTipoOperacao getCodigoTipoOperacao() {
      return CodigoTipoOperacao.COD_RETIRADA_GARANTIA;
   }

}
