package br.com.cetip.aplicacao.garantias.servico.colateral;

import br.com.cetip.aplicacao.garantias.apinegocio.colateral.ParametroIFGarantidorFactory;
import br.com.cetip.dados.aplicacao.garantias.HabilitaIFGarantidorDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

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
 * 
 */

public class ServicoRegistraParametroIFGarantidor implements Servico {

   /* (non-Javadoc)
    * @see br.com.cetip.infra.servico.interfaces.Servico#executar(br.com.cetip.infra.servico.interfaces.Requisicao)
    */
   public Resultado executar(Requisicao requisicao) throws Exception {

      RequisicaoServicoRegistraParametroIFGarantidor req = (RequisicaoServicoRegistraParametroIFGarantidor) requisicao;
      ResultadoServicoRegistraParametroIFGarantidor res = new ResultadoServicoRegistraParametroIFGarantidor();

      CodigoSistema codSistema = req.obterGARANTIAS_SISTEMA_CodigoSistema();
      CodigoTipoIF codTipoIF = req.obterGARANTIAS_TIPO_IF_CodigoTipoIF();
      CodigoIF codigoIF = req.obterGARANTIAS_CODIGO_IF_CodigoIF();

      Booleano habilitaTodosIF = req.obterGARANTIAS_SEL_TODOS_IF_Booleano();

      if (habilitaTodosIF.ehVerdadeiro()) {

         HabilitaIFGarantidorDO paramDO = ParametroIFGarantidorFactory.getInstance()
               .obtemParamIFGarantidorPorModuloSistema(codSistema, codTipoIF);
         if (!Condicional.vazio(paramDO)) {
            if (!Condicional.vazio(paramDO.getInstrumentoFinanceiro().getId())) {
               //excluir a linha com a habilitação do if especifico para incluir a habilitação para todos os ifs
               ParametroIFGarantidorFactory.getInstance().excluiParamIFGarantidor(codSistema, codTipoIF);
            }
         }
      }
      ParametroIFGarantidorFactory.getInstance().incluirParamIFGarantidor(codSistema, codTipoIF, codigoIF);

      return res;
   }

   /* (non-Javadoc)
    * @see br.com.cetip.infra.servico.interfaces.Servico#executarConsulta(br.com.cetip.infra.servico.interfaces.Requisicao)
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      return null;
   }
}
