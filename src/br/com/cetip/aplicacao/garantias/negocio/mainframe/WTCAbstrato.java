package br.com.cetip.aplicacao.garantias.negocio.mainframe;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import weblogic.wtc.gwt.TuxedoConnection;
import weblogic.wtc.gwt.TuxedoConnectionFactory;
import weblogic.wtc.jatmi.Reply;
import weblogic.wtc.jatmi.TPException;
import weblogic.wtc.jatmi.TPReplyException;
import weblogic.wtc.jatmi.TypedString;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.log.Logger;

/**
 * <p>
 * Super classe para execucao de servicos no tuxedo
 * </p>
 */
abstract class WTCAbstrato {

   /*
    * Metodo que valida os parametros e o nome do servico a ser chamado do WTC
    */
   public void execute() {
      String controle = System.getProperty("desativawtc");
      if (controle != null && controle.equals("true")) {
         return;
      }

      String nomeServico = obterNomeServico();
      String mensagem = obterMensagem();

      validaExecucao(nomeServico, mensagem);
      executaTuxedo(nomeServico, mensagem);
   }

   private void validaExecucao(String nomeServico, String mensagem) {
      if (nomeServico == null || nomeServico.trim().length() == 0) {
         Erro erro = new Erro(CodigoErro.NOME_SERVICO_WTC_INVALIDO);
         Logger.error(this, erro);
         throw erro;
      }

      if (mensagem == null || mensagem.trim().length() == 0) {
         Erro erro = new Erro(CodigoErro.MENSAGEM_WTC_INVALIDA);
         Logger.error(this, erro);
         throw erro;
      }
   }

   protected abstract String obterNomeServico();

   protected abstract String obterMensagem();

   /*
    * Metodo que recebe os parametros e o nome do servico a ser chamado e efetua a chamada do WTC
    */
   private void executaTuxedo(String nomeServico, String mensagem) {
      TuxedoConnectionFactory tcf = instanciaFactory();
      TuxedoConnection myTux = null;
      TypedString myData;
      Reply myRtn;

      try {
         myTux = tcf.getTuxedoConnection();

         myData = new TypedString(mensagem);

         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "About to call tpcall");
         }

         myRtn = myTux.tpcall(nomeServico, myData, 0);
      } catch (TPReplyException tre) {
         Erro erro = new Erro(CodigoErro.ERRO_WTC_TPREPLYEXCEPTION);
         erro.parametroMensagem(tre.toString(), 0);
         Logger.error(this, tre);
         throw erro;
      } catch (TPException te) {
         Logger.error(this, te);
         Erro erro = new Erro(CodigoErro.ERRO_WTC_TPEXCEPTION);
         erro.parametroMensagem(te.toString(), 0);
         throw erro;
      } catch (Exception ee) {
         Erro erro = new Erro(CodigoErro.ERRO_WTC_EXCEPTION);
         erro.parametroMensagem(ee.toString(), 0);
         Logger.error(this, ee);
         throw erro;
      } finally {
         if (myTux != null) {
            myTux.tpterm();
         }
      }

      trataRetorno(myRtn);
   }

   private void trataRetorno(Reply myRtn) {
      TypedString myDataReply = (TypedString) myRtn.getReplyBuffer();

      if (myDataReply == null) {
         Erro erro = new Erro(CodigoErro.RETORNO_NULO);
         throw erro;
      }

      // Vamos validar a execucao do servico do WTC.
      String retornoExecWTC = myDataReply.getStringBuffer().toString();
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, retornoExecWTC);
      }

      String codStatus = obtemValorNode("codStatus", retornoExecWTC);

      if (!"000000".equals(codStatus)) {
         String msg = obtemValorNode("msgErro", retornoExecWTC);

         Erro erro = new Erro(CodigoErro.ERRO_WTC_EXCEPTION);
         erro.parametroMensagem(new Texto(msg), 0);
         throw erro;
      }
   }

   private TuxedoConnectionFactory instanciaFactory() {
      try {
         Context ctx = new InitialContext();
         return (TuxedoConnectionFactory) ctx.lookup(TuxedoConnectionFactory.JNDI_NAME);
      } catch (NamingException ne) {
         Erro erro = new Erro(CodigoErro.ERRO_WTC_FAZENDO_LOOKUP_NOME_JNDI);
         throw erro;
      }
   }

   private String obtemValorNode(String node, String xml) {
      int indexOfNode = xml.lastIndexOf("<" + node + ">");
      int indexOfNodeEnd = xml.indexOf("</" + node + ">");
      int lengthNode = node.length() + 2; // soma 2 para o < e o >

      String nodeValue = xml.substring(indexOfNode + lengthNode, indexOfNodeEnd);

      return nodeValue;
   }

}
