package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CESTA_GARANTIA"
 * 
 * @resultado.class
 * 
 */
public class ServicoValidarConsultaCestaPorIF extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao arg0) throws Exception {
      return null;
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      RequisicaoServicoValidarConsultaCestaPorIF req = (RequisicaoServicoValidarConsultaCestaPorIF) arg0;

      CodigoTipoIF codigoTipoIF = null;

      try {
         codigoTipoIF = InstrumentoFinanceiroFactory.getInstance().obterCodigoTipoInstrumentoFinanceiro(
               req.obterCESTA_GARANTIA_CodigoIF());
      } catch (Exception e) {
         codigoTipoIF = CodigoTipoIF.N_CTP;
      }

      AtributosColunados termos = new AtributosColunados();

      termos.atributo(req.obterCESTA_GARANTIA_CodigoIF());
      termos.atributo(codigoTipoIF);

      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.validaConsultaCestaPorIF, termos, true);

      return new ResultadoServicoValidarConsultaCestaPorIF();
   }

}
