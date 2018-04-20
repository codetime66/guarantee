package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

/**
 * Operacoes de Cesta de Cesta
 * 
 * Classe para criar operacoes 990 e 991 (Bloqueio/Retirada em Cesta de Cesta) em lote, ou seja, eh chamado uma procedure para
 * criar varias operacoes de uma so vez
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
abstract class OperacaoCestaDeCesta extends MIGOperacaoLote {

   public void completarDadosOperacao() {
      Texto tipoVendedorP2 = tipoContaGarantido();

      Id idCesta = getCesta().getNumIdCestaGarantias();
      Id idMovimentacao = getMovimentacao().getNumIdMovimentacaoGarantia();
      Id conta60Garantido = getConta60Garantido().getId();

      Object[] params = new Object[] { idCesta, idMovimentacao, conta60Garantido, tipoVendedorP2, getDataOperacao() };

      StringBuffer nomeFuncaoBuffer = new StringBuffer();
      nomeFuncaoBuffer.append("CETIP.");
      nomeFuncaoBuffer.append(getFuncaoOperacao());
      nomeFuncaoBuffer.append("(?, ?, ?, ?, ?)");

      String nomeFuncao = nomeFuncaoBuffer.toString();

      ISqlWrapper pw1 = new ProcedureSqlWrapper(nomeFuncao, params);
      addSqlWrapperObject(pw1);
   }

   public abstract String getFuncaoOperacao();

   public static class Operacao990 extends OperacaoCestaDeCesta {

      public String getFuncaoOperacao() {
         return "P_BLOQUEIA_CESTA_DE_CESTA";
      }

   }

   public static class Operacao991 extends OperacaoCestaDeCesta {

      public String getFuncaoOperacao() {
         return "P_RETIRADA_CESTA_DE_CESTA";
      }

   }
}
