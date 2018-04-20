package br.com.cetip.aplicacao.garantias.servico.colateral;

import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.texto.NomeRegra;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * @requisicao.class
 * 
 * @requisicao.method
 *     atributo="CodigoSistema"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="GARANTIAS_SISTEMA"
 *     
 *  @requisicao.method 
 *    atributo="CodigoTipoIF"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="GARANTIAS_TIPO_IF"
 *     
 * @requisicao.method 
 *    atributo="CodigoIF"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="GARANTIAS_CODIGO_IF"
 *    
 *   @requisicao.method 
 *    atributo="Booleano"
 *    pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *    contexto="GARANTIAS_SEL_TODOS_IF"
 *    
 * @resultado.class
 */
public class ServicoValidaParametroIFGarantidor implements Servico {

   /* (non-Javadoc)
    * @see br.com.cetip.infra.servico.interfaces.Servico#executar(br.com.cetip.infra.servico.interfaces.Requisicao)
    */
   public Resultado executar(Requisicao arg0) throws Exception {
      return null;
   }

   /* (non-Javadoc)
    * @see br.com.cetip.infra.servico.interfaces.Servico#executarConsulta(br.com.cetip.infra.servico.interfaces.Requisicao)
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoValidaParametroIFGarantidor req = (RequisicaoServicoValidaParametroIFGarantidor) requisicao;
      ResultadoServicoValidaParametroIFGarantidor res = new ResultadoServicoValidaParametroIFGarantidor();

      CodigoSistema codSistema = req.obterGARANTIAS_SISTEMA_CodigoSistema();
      CodigoTipoIF codTipoIF = req.obterGARANTIAS_TIPO_IF_CodigoTipoIF();
      CodigoIF codigoIF = req.obterGARANTIAS_CODIGO_IF_CodigoIF();
      Booleano selTodosIF = req.obterGARANTIAS_SEL_TODOS_IF_Booleano();

      //Acrescentar o parametro para fazer valiações
      NomeRegra predicado = ConstantesDeNomeDeRegras.validaParametroIFGarantidor;

      AtributosColunados termos = new AtributosColunados();
      termos.atributo(codSistema);
      termos.atributo(codTipoIF);
      termos.atributo(codigoIF);
      termos.atributo(selTodosIF);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "### ServicoValidaParametroIFGarantidor : AtributosColunados (termos) : ");
         Logger.debug(this, termos.toString());
      }

      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(predicado, termos);

      return res;
   }

}
