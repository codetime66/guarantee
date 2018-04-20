package br.com.cetip.aplicacao.garantias.servico.agro;

import br.com.cetip.aplicacao.garantias.apinegocio.agro.IFGarantidorFactory;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.garantias.negocio.agro.Parametros;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico que habilita IF Garantidor
 * 
 * @author <a href="mailto:reinaldosantana@cetip.com">Reinaldo Santana</a>
 * @since Maio/2010
 * 
 * @requisicao.class 
 * 
 * @requisicao.method atributo="TipoHabilitacao" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="GARANTIAS_CESTA"
 * 
 * @requisicao.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CESTA"
 *                   
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CESTA"
 *                   
 * @requisicao.method atributo="NumeroInteiro" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="ACAO"                                       
 * 
 * @resultado.class
 */
public class ServicoHabilitaIFGarantidor extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {

      RequisicaoServicoHabilitaIFGarantidor req = (RequisicaoServicoHabilitaIFGarantidor) requisicao;
      ResultadoServicoHabilitaIFGarantidor res = new ResultadoServicoHabilitaIFGarantidor();

      CodigoTipoIF codigoTipoIF = req.obterGARANTIAS_CESTA_CodigoTipoIF();
      CodigoIF codigoIF = req.obterGARANTIAS_CESTA_CodigoIF();
      NumeroInteiro acao = req.obterACAO_NumeroInteiro();
      
      if (acao.mesmoConteudo(Parametros.INCLUIR)){
    	  IFGarantidorFactory.getInstance().incluirIFGarantidor(codigoTipoIF, codigoIF);  
      }else if (acao.mesmoConteudo(Parametros.ATIVAR)){
    	  IFGarantidorFactory.getInstance().habilitarIFGarantidor(codigoTipoIF, codigoIF);
      }else if (acao.mesmoConteudo(Parametros.DESATIVAR)){
    	  IFGarantidorFactory.getInstance().desabilitarIFGarantidor(codigoTipoIF, codigoIF);
      }else if (acao.mesmoConteudo(Parametros.EXCLUIR)){
    	  IFGarantidorFactory.getInstance().excluirIFGarantidor(codigoTipoIF, codigoIF);
      }	  

      return res;
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

}
