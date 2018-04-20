package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.AcessoCestaDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ServicoConsultaVisualizadoresCesta extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao r) throws Exception {
      RequisicaoServicoConsultaVisualizadoresCesta req;
      req = (RequisicaoServicoConsultaVisualizadoresCesta) r;

      ResultadoServicoConsultaVisualizadoresCesta res;
      res = new ResultadoServicoConsultaVisualizadoresCesta();

      IGarantias factory = getFactory();

      CestaGarantiasDO cesta = factory.getInstanceCestaDeGarantias().obterCestaDeGarantias(
            req.obterGARANTIAS_CODIGO_NumeroCestaGarantia());

      Iterator i = cesta.getVisualizadores().iterator();
      while (i.hasNext()) {
         CodigoContaCetip conta = ((AcessoCestaDO) i.next()).getContaParticipante().getCodContaParticipante();
         res.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(conta);
      }

      return res;
   }

}