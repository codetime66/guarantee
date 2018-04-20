package br.com.cetip.aplicacao.garantias.negocio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidarTipoIF;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.operacao.SituacaoOperacaoDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.SituacaoOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;

/**
 * Operacao de VINCULACAO
 * 
 * Classe para criar operacoes 891 (Vinculacao em cesta de garantias) em lote, ou seja, eh chamado uma function para
 * criar varias operacoes de uma so vez.
 * 
 * @author marco sergio
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
final class Operacao891 extends MIGOperacaoLote {

   protected Operacao891() {
      setComIdentificaComitente(true);
   }

   public IdStatusMovimentacaoGarantia criarOperacao() {
      if (deveIdentificarComitente()) {
         identificaOperacoesBloqueio();
      }

      return super.criarOperacao();
   }

   private void identificaOperacoesBloqueio() {
      List l = listaOperacoesBloqueio();

      List lsOps = new ArrayList(l.size());
      List lsQtd = new ArrayList(l.size());

      preencheListasAIdentificar(l, lsOps, lsQtd);

      Integer[] ids = (Integer[]) lsOps.toArray(new Integer[lsOps.size()]);
      BigDecimal[] qtds = (BigDecimal[]) lsQtd.toArray(new BigDecimal[lsQtd.size()]);

      identificaOperacoes(ids, qtds);
   }

   private void preencheListasAIdentificar(List l, List lsOps, List lsQtd) {
      Iterator i = l.iterator();
      while (i.hasNext()) {
         Object[] row = (Object[]) i.next();
         Id idOp = (Id) row[0];
         Quantidade qtdOp = (Quantidade) row[1];

         lsOps.add(Integer.valueOf(idOp.obterConteudo()));
         lsQtd.add(qtdOp.obterBigDecimal());
      }
   }

   private List listaOperacoesBloqueio() {
      String hql = "select o.id, o.qtdOperacaoDecimal from OperacaoDO o where o.idCestaGarantias = ? and o.tipoOperObjetoServ.tipoOperacao.codTipoOperacao = ? and o.situacaoOperacao.codSituacaoOperacao = ?";
      Id idCesta = getCesta().getNumIdCestaGarantias();
      CodigoTipoOperacao bloqueio = CodigoTipoOperacao.COD_BLOQUEIO_GARANTIA;
      SituacaoOperacao finalizada = SituacaoOperacaoDO.FINALIZADA;
      List l = getGp().find(hql, new Object[] { idCesta, bloqueio, finalizada });
      return l;
   }

   protected void executaComandosExtras() {
      // soh movimenta para segundo nivel (cesta de cesta) se estah em primeiro nivel
      if (!ehCestaSegundoNivel()) {
         // VINCULACAO foi executada
         // Se ha ativos garantidores STA vinculados a cestas, disparar Operacao990
         // para as cestas vinculadas a estes ativos
         ICestaDeGarantias icg = getGarantias().getInstanceCestaDeGarantias();

         // Aciona Operacao991 para as cestas de todos os ativos garantidores que possuem cesta
         CestaGarantiasDO cesta = getCesta();
         List cestasABloquearLastro = icg.listarCestasSegundoNivel(cesta);
         Iterator it = cestasABloquearLastro.iterator();

         while (it.hasNext()) {
            CestaGarantiasDO innerCesta = (CestaGarantiasDO) it.next();

            if (innerCesta.getIndSegundoNivel().ehFalso()) {
               icg.bloquearCestaDeCesta(innerCesta);
            }
         }
      }
   }

   /**
    * Validacao
    */
   public IdStatusMovimentacaoGarantia validar() {
      IdStatusMovimentacaoGarantia status = super.validar();

      IValidarTipoIF validaTipoIF = getGarantias().getInstanceValidarTipoIF();
      Id sistemaId = getIfDO().getSistema().getNumero();
      Id tipoId = getIfDO().getTipoIF().getNumTipoIF();
      if (!validaTipoIF.validarGarantido(tipoId, sistemaId)) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this,
                  "Tipo de Instrumento Financeiro incompativel com sistemas e tipos validos para o Garantias");
         }

         return IdStatusMovimentacaoGarantia.IF_INCOMPATIVEL;
      }

      if (!status.mesmoConteudo(IdStatusMovimentacaoGarantia.OK)) {
         return status;
      }

      if (Condicional.vazio(getConta60Garantido())) {
         return IdStatusMovimentacaoGarantia.GARANTIDO_INVALIDO;
      }

      return IdStatusMovimentacaoGarantia.OK;
   }

   /**
    * Complemento dos dados
    */
   public void completarDadosOperacao() {
      // Verifica se o IF a ser vinculado esta dentro de alguma outra cesta e
      // esta por sua vez, esteja vinculada.
      // Se sim, os ativos desta cesta sofrendo operacao 891, serao movidos
      // para carteiras de segundo nivel.

      // S - segundo nivel; N - primeiro nivel
      Texto p_ind_segundo_nvl_grtia = ehCestaSegundoNivel() ? new Texto("S") : new Texto("N"); // S, N

      // 'P' conta propria
      // 'C1' conta cliente 1
      // 'C2' conta cliente 2
      Texto p_cod_tipo_conta_garantidor = tipoContaGarantidor();
      Texto p_cod_tipo_conta_garantido = tipoContaGarantido();

      Id idCesta = getCesta().getNumIdCestaGarantias();
      Id idMovimentacao = getMovimentacao().getNumIdMovimentacaoGarantia();
      Id contaGarantidor = getGarantidor().getId();
      Id conta60Garantido = getConta60Garantido().getId();

      Object[] params = new Object[] { idCesta, idMovimentacao, contaGarantidor, conta60Garantido,
            p_ind_segundo_nvl_grtia, p_cod_tipo_conta_garantidor, p_cod_tipo_conta_garantido };

      String nome = "CETIP.F_VINCULA_IF_CESTA_GARANTIAS(?, ?, ?, ?, ?, ?, ?)";
      addSqlWrapperObject(new FunctionSqlWrapper(nome, params));

      nome = "CETIP.F_VINC_IF_ALTP_CESTA_GARANTIAS(?, ?, ?, ?, ?, ?, ?)";
      addSqlWrapperObject(new FunctionSqlWrapper(nome, params));
   }

}
