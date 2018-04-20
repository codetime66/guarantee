package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.IConsulta;

/**
 * 
 * Operacao de LIBERACAO de Cesta de Garantias
 * <p>
 * Classe para criar operacoes 895 (Liberacao de cesta de garantias) em lote, ou seja, eh chamado uma procedure para
 * criar varias operacoes de uma so vez
 * <p>
 * <b>IMPORTANTE</b> <br>
 * A identificacao de Comitente ocorre de fato, na operacao 888, registrada nas funcoes executadas diretamente no Banco
 * de Dados pelas functions nos SqlWrappers
 * </p>
 * </p>
 * 
 * @author marco sergio
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * @since 2006
 */
class Operacao895 extends MIGOperacaoLote {

   private List cestasARetirarLastro;

   protected void executaComandosExtras() {
      // LIBERACAO foi executada
      // Se ha ativos garantidores STA que foram liberados, disparar Operacao991
      // para as cestas vinculadas a estes ativos
      ICestaDeGarantias icg = getGarantias().getInstanceCestaDeGarantias();

      // Aciona Operacao991 para as cestas de todos os ativos STA
      Iterator it = cestasARetirarLastro.iterator();

      while (it.hasNext()) {
         CestaGarantiasDO cesta = (CestaGarantiasDO) it.next();
         icg.retirarCestaDeCesta(cesta);
      }
   }

   private void listarCestasSegundoNivel() {
      ICestaDeGarantias icg = getGarantias().getInstanceCestaDeGarantias();
      cestasARetirarLastro = icg.listarCestasSegundoNivel(getCesta());
   }

   protected Operacao895() {
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

   public void completarDadosOperacao() {
      // Verifica se o IF vinculado esta dentro de alguma outra cesta e
      // esta por sua vez, esteja vinculada.
      Texto p_ind_segundo_nvl_grtia = ehCestaSegundoNivel() ? new Texto("S") : new Texto("N");
      Texto p_cod_tipo_conta_garantido = tipoContaGarantido();

      // Obtem a lista de Cestas de segundo nivel antes de efetuar a liberacao
      listarCestasSegundoNivel();

      // Se cesta possui algum ativo garantidor STA e este for liberado, deve acionar Operacao991
      String nome;

      Id idCesta = getCesta().getNumIdCestaGarantias();
      Id idMovimentacao = getMovimentacao().getNumIdMovimentacaoGarantia();
      Id contaGarantidor = getGarantidor().getId();
      Id conta60Garantido = getConta60Garantido().getId();

      Object[] params1 = new Object[] { idCesta, idMovimentacao, contaGarantidor, conta60Garantido,
            p_ind_segundo_nvl_grtia };

      nome = "CETIP.F_EXECUTA_IF_CESTA_GARANTIAS(?, ?, ?, ?, ?)";
      addSqlWrapperObject(new FunctionSqlWrapper(nome, params1));

      Object[] params2 = new Object[] { idCesta, idMovimentacao, contaGarantidor, conta60Garantido,
            p_ind_segundo_nvl_grtia, p_cod_tipo_conta_garantido };
      nome = "CETIP.F_EXEC_IF_ALTP_CESTA_GARANTIAS(?, ?, ?, ?, ?, ?)";
      addSqlWrapperObject(new FunctionSqlWrapper(nome, params2));
   }

   /**
    * Operacao 895 LIBERACAO Parcial
    */
   public static class Operacao895Parcial extends Operacao895 {

      protected Id idNumIFLiberado;

      protected void executaComandosExtras() {
         // LIBERACAO foi executada
         // Se ha ativos garantidores STA que foram liberados, disparar Operacao991
         // para as cestas vinculadas a estes ativos
         // Só vai ao 2º nível se todas as garantias liberadas
         ICestaDeGarantias icg = getGarantias().getInstanceCestaDeGarantias();

         // obtem a cesta do ativo liberado, caso exista
         NumeroCestaGarantia numCesta = icg.obterCestaGarantindoIF(idNumIFLiberado);

         if (numCesta != null) {
            CestaGarantiasDO innerCesta = icg.obterCestaDeGarantias(numCesta);
            icg.retirarCestaDeCesta(innerCesta);
         }
      }

      public IdStatusMovimentacaoGarantia validar() {
         String hql = "select count(*) from DetalheGarantiaDO dg where dg.instrumentoFinanceiro.id = :id and dg.quantidadeGarantia >= :qtd";

         IConsulta consulta = getGp().criarConsulta(hql);
         consulta.setAtributo("id", getIfDO().getId());
         consulta.setAtributo("qtd", getMovimentacao().getQtdGarantia());
         Integer i = (Integer) consulta.list().get(0);

         if (i.intValue() == 0) {
            return IdStatusMovimentacaoGarantia.SEM_SALDO;
         }

         IdStatusMovimentacaoGarantia status = super.validar();

         if (!status.mesmoConteudo(IdStatusMovimentacaoGarantia.OK)) {
            return status;
         }

         return IdStatusMovimentacaoGarantia.OK;
      }

      public void completarDadosOperacao() {
         // Verifica se o IF vinculado esta dentro de alguma outra cesta e
         // esta por sua vez, esteja vinculada.
         Texto p_ind_segundo_nvl_grtia = ehCestaSegundoNivel() ? new Texto("S") : new Texto("N");
         Texto p_cod_tipo_conta_garantido = tipoContaGarantido();

         Id idCesta = getCesta().getNumIdCestaGarantias();
         Id idMovimentacao = getMovimentacao().getNumIdMovimentacaoGarantia();
         Id contaGarantidor = getGarantidor().getId();
         Id conta60Garantido = getConta60Garantido().getId();
         Id idNumIF = getIfDO().getId();
         Id idTipoGarantia = getCesta().getTipoGarantia().getNumIdTipoGarantia();
         Booleano indDireitoGarantidor = getMovimentacao().getIndDireitosGarantidor();
         QuantidadeInteiraPositiva quantidade = new QuantidadeInteiraPositiva(getMovimentacao().getQtdGarantia()
               .obterParteInteira().obterConteudo().toString());

         Object[] params1 = new Object[] { idCesta, idMovimentacao, contaGarantidor, conta60Garantido, idNumIF,
               idTipoGarantia, indDireitoGarantidor, quantidade, p_ind_segundo_nvl_grtia };

         String funcExecutaBaixa = "CETIP.F_EX_P_IF_CESTA_GARANTIAS(?, ?, ?, ?, ?, ?, ?, ?, ?)";
         addSqlWrapperObject(new FunctionSqlWrapper(funcExecutaBaixa, params1));

         Object[] params2 = new Object[] { idCesta, idMovimentacao, contaGarantidor, conta60Garantido, idNumIF,
               idTipoGarantia, indDireitoGarantidor, quantidade, p_ind_segundo_nvl_grtia, p_cod_tipo_conta_garantido };

         String funcExecutaAlta = "CETIP.F_EX_P_IF_ALTP_CESTA_GARANTIAS(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
         addSqlWrapperObject(new FunctionSqlWrapper(funcExecutaAlta, params2));

         this.idNumIFLiberado = idNumIF;
      }
   }

   /**
    * Operacao 895 LIBERACAO Penhor Emissor
    */
   public static class Operacao895PenhorEmissor extends Operacao895Parcial {

      public void completarDadosOperacao() {
         // Verifica se o IF vinculado esta dentro de alguma outra cesta e
         // esta por sua vez, esteja vinculada.
         Texto p_ind_segundo_nvl_grtia = ehCestaSegundoNivel() ? new Texto("S") : new Texto("N");

         String nome = null;

         Id idCesta = getCesta().getNumIdCestaGarantias();
         Id idMovimentacao = getMovimentacao().getNumIdMovimentacaoGarantia();
         Id contaGarantidor = getGarantidor().getId();
         Id conta60Garantido = getConta60Garantido().getId();
         Id idNumIF = getIfDO().getId();
         Id idTipoGarantia = getCesta().getTipoGarantia().getNumIdTipoGarantia();
         Id codigoSistema = getIfDO().getSistema().getNumero();
         Booleano indDireitoGarantidor = getMovimentacao().getIndDireitosGarantidor();
         QuantidadeInteiraPositiva quantidade = new QuantidadeInteiraPositiva(getMovimentacao().getQtdGarantia()
               .obterParteInteira().obterConteudo().toString());

         Object[] params = null;
         if (codigoSistema.mesmoConteudo(SistemaDO.CETIP21)) {
            nome = "CETIP.F_LIB_CESTA_PENHOR_EMISSOR(?, ?, ?, ?, ?)";
            params = new Object[] { idCesta, idMovimentacao, contaGarantidor, conta60Garantido, p_ind_segundo_nvl_grtia };
         } else {
            // tipos da alta
            Texto p_cod_tipo_conta_garantido = tipoContaGarantido();

            nome = "CETIP.F_EX_P_IF_ALTP_CESTA_GARANTIAS(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            params = new Object[] { idCesta, idMovimentacao, contaGarantidor, conta60Garantido, idNumIF,
                  idTipoGarantia, indDireitoGarantidor, quantidade, p_ind_segundo_nvl_grtia, p_cod_tipo_conta_garantido };
         }

         addSqlWrapperObject(new FunctionSqlWrapper(nome, params));

         this.idNumIFLiberado = idNumIF;
      }

      public IdStatusMovimentacaoGarantia validar() {
         IdStatusMovimentacaoGarantia status = super.validar();

         if (status.mesmoConteudo(IdStatusMovimentacaoGarantia.GARANTIDO_NAO_E_CONTA_60)) {
            return IdStatusMovimentacaoGarantia.OK;
         }

         return status;
      }

   }

}