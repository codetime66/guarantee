package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.NomeRegra;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CESTA_GARANTIA"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="SOLICITANTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="DESTINATARIO"
 * 
 * @resultado.class
 * 
 */

public class ServicoValidaSolicitacaoArquivoCaracteristicasCesta extends BaseGarantias implements Servico {

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {

      RequisicaoServicoValidaSolicitacaoArquivoCaracteristicasCesta req = (RequisicaoServicoValidaSolicitacaoArquivoCaracteristicasCesta) requisicao;

      NumeroCestaGarantia numCestaGarantias = req.obterCESTA_GARANTIA_NumeroCestaGarantia();
      CodigoContaCetip codigoContaSolicitante = req.obterSOLICITANTE_CodigoContaCetip();
      CodigoContaCetip codigoContaDestinatario = req.obterDESTINATARIO_CodigoContaCetip();

      AtributosColunados termos = new AtributosColunados();
      NomeRegra predicado = ConstantesDeNomeDeRegras.validaSolicitacaoArquivoCaracteristicasCesta;

      termos.atributo(numCestaGarantias);
      termos.atributo(codigoContaSolicitante);
      termos.atributo(codigoContaDestinatario);

      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(predicado, termos, true);

      return new ResultadoServicoValidaSolicitacaoArquivoCaracteristicasCesta();
   }

   public Resultado executar(Requisicao arg0) throws Exception {
      return null;
   }

}
