package br.com.cetip.aplicacao.garantias.negocio.mainframe;

import java.util.Date;

import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;

/**
 * Mensagem de desvinculacao para mainframe, de ativos garantidos.
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class WTCRegistraDesvinculacao extends WTCAbstrato {

   /*
    * AVSEQUENCIA
    */
   private static final int NUM_AVISO = 3;

   /*
    * AVSISUNICO
    */
   private int numSistemaNX = 16;

   /*
    * AVATIVO
    */
   private String codigoIF;

   /*
    * AVCESTA
    */
   private String numeroCesta;

   /*
    * AVCPDATOP
    */
   private String dataOperacao;

   public WTCRegistraDesvinculacao(Id idCesta, CodigoIF codIF, Id idSistema) {
      if (Condicional.vazio(idCesta) || Condicional.vazio(codIF) || Condicional.vazio(idSistema)) {
         throw new IllegalArgumentException("Parametros nao podem ser vazios!");
      }

      dataOperacao = new Data(new Date()).obterDataFormatadaParaMF();
      numeroCesta = idCesta.obterConteudo();
      codigoIF = codIF.obterConteudo();
      numSistemaNX = Integer.parseInt(idSistema.obterConteudo());
   }

   protected String obterNomeServico() {
      return "SEAVISOSOBE";
   }

   protected String obterMensagem() {
      StringBuffer sb = new StringBuffer(2000);

      sb.append("<WTC>");

      sb.append("<indAcao>DSVC001</indAcao>");

      sb.append("<numAviso>");
      sb.append(NUM_AVISO);
      sb.append("</numAviso>");

      sb.append("<numSistemaNX>");
      sb.append(numSistemaNX);
      sb.append("</numSistemaNX>");

      sb.append("<codIF>");
      sb.append(codigoIF);
      sb.append("</codIF>");

      sb.append("<numCesta>");
      sb.append(numeroCesta);
      sb.append("</numCesta>");

      sb.append("<datOperacao>");
      sb.append(dataOperacao);
      sb.append("</datOperacao>");

      sb.append("</WTC>");

      return sb.toString();
   }

}
