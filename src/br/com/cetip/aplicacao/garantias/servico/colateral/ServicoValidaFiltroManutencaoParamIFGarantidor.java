package br.com.cetip.aplicacao.garantias.servico.colateral;

import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.texto.NomeRegra;
import br.com.cetip.infra.atributo.utilitario.Condicional;
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
 * @resultado.class
 */
public class ServicoValidaFiltroManutencaoParamIFGarantidor implements Servico {

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
      RequisicaoServicoValidaFiltroManutencaoParamIFGarantidor req = (RequisicaoServicoValidaFiltroManutencaoParamIFGarantidor) requisicao;
      ResultadoServicoValidaFiltroManutencaoParamIFGarantidor res = new ResultadoServicoValidaFiltroManutencaoParamIFGarantidor();

      CodigoSistema codSistema = req.obterGARANTIAS_SISTEMA_CodigoSistema();
      if (Condicional.vazio(codSistema)) {
         codSistema = new CodigoSistema(Contexto.GARANTIAS_SISTEMA);
      }
      CodigoTipoIF codTipoIF = req.obterGARANTIAS_TIPO_IF_CodigoTipoIF();
      if (Condicional.vazio(codTipoIF)) {
         codTipoIF = new CodigoTipoIF(Contexto.GARANTIAS_TIPO_IF);
      }

      //Acrescentar o parametro para fazer valiações
      NomeRegra predicado = ConstantesDeNomeDeRegras.validaFiltroManutencaoParamIFGarantidor;

      AtributosColunados termos = new AtributosColunados();
      termos.atributo(codSistema);
      termos.atributo(codTipoIF);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "### ServicoValidaParametroIFGarantidor : AtributosColunados (termos) : ");
         Logger.debug(this, termos.toString());
      }

      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(predicado, termos);

      return res;
   }

}
