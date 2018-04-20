package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.AcessoCestaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIA"
 * 
 * @requisicao.method atributo="CodigoContaCetip"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIA"
 * 
 * @resultado.class
 * 
 */
public class ServicoExcluiVisualizadorCesta extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      Logger.info("Excluindo Visualizador");

      IGerenciadorPersistencia gp = getGp();

      RequisicaoServicoExcluiVisualizadorCesta r = (RequisicaoServicoExcluiVisualizadorCesta) requisicao;
      NumeroCestaGarantia cesta = r.obterGARANTIA_NumeroCestaGarantia();
      CodigoContaCetip conta = r.obterGARANTIA_CodigoContaCetip();

      gp.delete("from " + AcessoCestaDO.class.getName() + " ac where ac.cestaGarantias.numIdCestaGarantias = " + cesta
            + " and ac.contaParticipante.codContaParticipante = '" + conta + "'");

      return new ResultadoServicoExcluiVisualizadorCesta();
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

}
