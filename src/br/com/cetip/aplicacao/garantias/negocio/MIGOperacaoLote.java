package br.com.cetip.aplicacao.garantias.negocio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.IConsultaGarantias;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSituacaoOS;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.ListasDeAtributos;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;

/**
 * Classe abstrata para criação de operações vindas do MIG (modulo de garantias) atraves de PLSQL
 */
abstract class MIGOperacaoLote extends MIGOperacao {

   private Set wrappers = new LinkedHashSet();

   /*
    * 
    * @author brunob
    */
   protected interface ISqlWrapper {

      public String getName();

      public Object[] getParams();

      public Object getReturn();

      public void executar();

   }

   /*
    * 
    * @author brunob
    */
   protected class FunctionSqlWrapper extends SqlWrapper {

      private final Class FUNCTION_RETURN_TYPE = Texto.class;

      public FunctionSqlWrapper(String name, Object[] params) {
         super(name, params);
      }

      public void executar() {
         if (Logger.estaHabilitadoDebug(MIGOperacaoLote.this)) {
            Logger.debug(MIGOperacaoLote.this, "SqlWrapper eh uma FUNCTION");
         }

         Texto o = (Texto) getGp().executarFunctionPorCall(getName(), getParams(), FUNCTION_RETURN_TYPE);
         setReturn(o);
      }

   }

   /*
    * 
    * @author brunob
    */
   protected class ProcedureSqlWrapper extends SqlWrapper {

      public ProcedureSqlWrapper(String name, Object[] params) {
         super(name, params);
      }

      public void executar() {
         if (Logger.estaHabilitadoDebug(MIGOperacaoLote.this)) {
            Logger.debug(MIGOperacaoLote.this, "SqlWrapper eh uma PROCEDURE");
         }

         getGp().executarProcedure(getName(), getParams());
      }

   }

   /*
    * 
    * @author brunob
    */
   private abstract class SqlWrapper implements ISqlWrapper {

      private String nome = null;

      private Object[] params = null;

      private Object retorno = null;

      /*
       * Utilizar este construtor para executar uma Procedure
       * 
       * @param nome
       * 
       * @param params
       */
      public SqlWrapper(String name, Object[] params) {
         this.nome = name;
         this.params = (Object[]) params.clone();

         if (Logger.estaHabilitadoDebug(MIGOperacaoLote.this)) {
            Logger.debug(MIGOperacaoLote.this, "Parametros para o objeto " + (getClass().getName()) + ": " + nome);
            for (int i = 0; i < params.length; i++) {
               Logger.debug(MIGOperacaoLote.this, "params[" + i + "]=" + params[i].toString());
            }
         }
      }

      public final String getName() {
         return nome;
      }

      public final Object[] getParams() {
         return (Object[]) params.clone();
      }

      protected final void setReturn(Object o) {
         if (Logger.estaHabilitadoDebug(MIGOperacaoLote.this)) {
            Logger.debug(MIGOperacaoLote.this, "Retorno: " + o);
         }

         this.retorno = o;
      }

      public final Object getReturn() {
         return retorno;
      }

   }

   protected void addSqlWrapperObject(ISqlWrapper sw) {
      wrappers.add(sw);
   }

   public IdStatusMovimentacaoGarantia validar() {
      IdStatusMovimentacaoGarantia status = super.validar();

      if (!status.mesmoConteudo(IdStatusMovimentacaoGarantia.OK)) {
         return status;
      }

      // valida se os TipoIF validos para o Cetip21 contidos na cesta nao
      // estao bloqueados
      String hql = "select count(*) from "
            + DetalheGarantiaDO.class.getName()
            + " dg inner join dg.instrumentoFinanceiro ativo where dg.cestaGarantias.numIdCestaGarantias = :idCesta and dg.quantidadeGarantia > 0 and ativo.tipoIF.objetoServico.codSituacaoOS <> :s";
      IConsulta cons = getGp().criarConsulta(hql);
      cons.setAtributo("idCesta", getCesta().getNumIdCestaGarantias());
      cons.setAtributo("s", CodigoSituacaoOS.LIVRE);
      Integer count = (Integer) cons.list().get(0);

      if (count.intValue() > 0) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Existe um tipo de Instrumento Financeiro na cesta que nao esta livre");
         }

         return IdStatusMovimentacaoGarantia.TIPO_IF_BLOQUEADO;
      }

      return IdStatusMovimentacaoGarantia.OK;
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.aplicacao.operacao.apinegocio.IMIGOperacao#criarOperacao()
    */
   public IdStatusMovimentacaoGarantia criarOperacao() {
      completarDadosOperacao();

      if (wrappers.isEmpty() == false) {
         // Execucao das Procedures e Functions das Operacoes do MMG
         for (Iterator i = wrappers.iterator(); i.hasNext();) {
            ISqlWrapper pw = (ISqlWrapper) i.next();
            i.remove();

            if (Logger.estaHabilitadoDebug(this)) {
               Logger.debug(this, "Nome da procedure/function: " + pw.getName());
               Logger.debug(this, "Parametros: " + ListasDeAtributos.toString(pw.getParams()));
            }

            pw.executar();
         }

         // Post Process sql wrappers para outras operacoes
         processaIdentificacaoComitente();
      }

      executaComandosExtras();

      IConsultaGarantias ics = getGarantias().getConsultaGarantias();

      IdStatusMovimentacaoGarantia migResultado = null;
      Id idTipoMovimentacao = getMovimentacao().getTipoMovimentacaoGarantia().getNumIdTipoMovGarantia();
      if (idTipoMovimentacao.mesmoConteudo(IdTipoMovimentacaoGarantia.VINCULACAO)) {
         migResultado = IdStatusMovimentacaoGarantia.OK;
      } else if (idTipoMovimentacao.mesmoConteudo(IdTipoMovimentacaoGarantia.LIBERACAO_PARCIAL)
            || idTipoMovimentacao.mesmoConteudo(IdTipoMovimentacaoGarantia.LIBERACAO_PENHOR_EMISSOR)) {
         if (isIndPlataformaBaixa()) {
            migResultado = IdStatusMovimentacaoGarantia.OK;
         } else {
            migResultado = IdStatusMovimentacaoGarantia.PENDENTE_ATUALIZA;
         }
      } else if (ics.existeGarantiasAltaPlataforma(getCesta())) {
         migResultado = IdStatusMovimentacaoGarantia.PENDENTE_ATUALIZA;
      } else {
         migResultado = IdStatusMovimentacaoGarantia.OK;
      }

      return migResultado;
   }

   protected void executaComandosExtras() {
   }

   /*
    * Executa a identificacao de comitente das operacoes criadas pelas Functions Sql
    * 
    * @param it
    */
   private final void processaIdentificacaoComitente() {
      if (!deveIdentificarComitente()) {
         return;
      }

      String hql = "select o.id, o.qtdOperacaoDecimal from OperacaoDO o where o.numControleLancamentoOriginalP1 = ?";
      List l = getGp().find(hql, getMovimentacao().getNumIdMovimentacaoGarantia());

      List lsOps = new ArrayList(l.size());
      List lsQtd = new ArrayList(l.size());

      Iterator i = l.iterator();
      while (i.hasNext()) {
         Object[] row = (Object[]) i.next();
         Id idOp = (Id) row[0];
         Quantidade qtdOp = (Quantidade) row[1];

         lsOps.add(Integer.valueOf(idOp.obterConteudo()));
         lsQtd.add(qtdOp.obterBigDecimal());
      }

      Integer[] ids = (Integer[]) lsOps.toArray(new Integer[lsOps.size()]);
      BigDecimal[] qtds = (BigDecimal[]) lsQtd.toArray(new BigDecimal[lsQtd.size()]);

      identificaOperacoes(ids, qtds);
   }

}