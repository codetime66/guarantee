package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

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
 * @resultado.method atributo="NumeroCestaGarantia"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIA"
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIA"
 */
public class ServicoValidarExclusaoVisualizador extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {

      RequisicaoServicoValidarExclusaoVisualizador req = (RequisicaoServicoValidarExclusaoVisualizador) requisicao;
      ResultadoServicoValidarExclusaoVisualizador res = new ResultadoServicoValidarExclusaoVisualizador();

      NumeroCestaGarantia numCesta = req.obterGARANTIA_NumeroCestaGarantia();
      CodigoContaCetip vizualizador = req.obterGARANTIA_CodigoContaCetip();

      CestaGarantiasDO cestaDO = getFactory().getInstanceCestaDeGarantias().obterCestaDeGarantias(numCesta);

      Iterator i = cestaDO.getAtivosVinculados().iterator();
      if (cestaDO.getAtivosVinculados() == null || cestaDO.getAtivosVinculados().size() == 0) {
         AtributosColunados ac = new AtributosColunados();
         ac.atributo(numCesta);
         ac.atributo(vizualizador);
         ac.atributo(null);

         FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeExcluirVisualizador, ac, true);

      } else {
         while (i.hasNext()) {
            InstrumentoFinanceiroDO ifDO = ((CestaGarantiasIFDO) i.next()).getInstrumentoFinanceiro();

            CodigoIF codigoIF = ifDO.getCodigoIF();

            AtributosColunados ac = new AtributosColunados();
            ac.atributo(numCesta);
            ac.atributo(vizualizador);
            ac.atributo(codigoIF);

            FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeExcluirVisualizador, ac, true);
         }
      }

      res.atribuirGARANTIA_NumeroCestaGarantia(numCesta);
      res.atribuirGARANTIA_CodigoContaCetip(vizualizador);

      return res;
   }

}
