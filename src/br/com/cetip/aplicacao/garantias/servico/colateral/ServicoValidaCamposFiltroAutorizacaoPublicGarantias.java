package br.com.cetip.aplicacao.garantias.servico.colateral;

import java.util.Date;

import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoGrade;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoGrade;
import br.com.cetip.infra.atributo.tipo.tempo.Hora;
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
 *     atributo="CodigoContaCetip"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="PARTE_CONTRATO"
 *     
 *  @requisicao.method 
 *    atributo="CPFOuCNPJ"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="PARTE_CONTRATO"
 *     
 * @resultado.class
 */
public class ServicoValidaCamposFiltroAutorizacaoPublicGarantias implements Servico {

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
      RequisicaoServicoValidaCamposFiltroAutorizacaoPublicGarantias req = (RequisicaoServicoValidaCamposFiltroAutorizacaoPublicGarantias) requisicao;
      ResultadoServicoValidaCamposFiltroAutorizacaoPublicGarantias res = new ResultadoServicoValidaCamposFiltroAutorizacaoPublicGarantias();

      CodigoContaCetip codContaParticipante = req.obterPARTE_CONTRATO_CodigoContaCetip();
      CPFOuCNPJ cpfCnpjCliente = req.obterPARTE_CONTRATO_CPFOuCNPJ();

      //Acrescentar o parametro para fazer valiações
      NomeRegra predicado = ConstantesDeNomeDeRegras.validaInformacoesGeraisAutorizacaoPublicGarantias;

      AtributosColunados termos = new AtributosColunados();
      termos.atributo(codContaParticipante);
      termos.atributo(cpfCnpjCliente);
      termos.atributo(cpfCnpjCliente.obterNatureza());
      termos.atributo(new CodigoGrade(CodigoTipoGrade.CTP11.toString()));
      termos.atributo(new Hora(new Date()));

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "### ServicoValidaCamposFiltroAutorizacaoPublicGarantias : AtributosColunados (termos) : ");
         Logger.debug(this, termos.toString());
      }

      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(predicado, termos);

      return res;
   }

}
