package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico que Cadastra Visualizador de Cesta (penhor no emissor)
 * 
 * @author <a href="mailto:fernandenrique@summa-tech.com">Fernando Henrique Martins</a>
 * @since Maio/2008
 * @resultado.class
 * 
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIDO"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIDO"
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="GARANTIDO_VISUALIZADOR"                      
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIDO"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIDO"
 */
public class ServicoCadastraGarantido extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {

      Logger.debug(this, ">> Entrei no servico que Cadastra Garantido...");

      RequisicaoServicoCadastraGarantido req = (RequisicaoServicoCadastraGarantido) requisicao;
      ResultadoServicoCadastraGarantido res = new ResultadoServicoCadastraGarantido();

      NumeroCestaGarantia nrCesta = req.obterGARANTIDO_NumeroCestaGarantia();
      CodigoContaCetip visualizador = req.obterGARANTIDO_CodigoContaCetip();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Cadastrando o visualisador " + visualizador + " para a Cesta " + nrCesta);
      }

      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = icg.obterCestaDeGarantias(nrCesta);

      IContaParticipante icp = ContaParticipanteFactory.getInstance();
      ContaParticipanteDO conta = icp.obterContaParticipanteDO(visualizador);

      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
      igc.cadastrarAcessoVisualizador(cesta, conta);

      return res;
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

}
