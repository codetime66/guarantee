package br.com.cetip.aplicacao.garantias.servico.selic;

import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * @requisicao.class
 * 
 * @requisicao.method
 *     atributo="CodigoContaSelic"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CONTA"
 * 
 * @requisicao.method
 *     atributo="CodigoIF"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CODIGO_IF"
 *
 * @resultado.class
 * 
 */

public class ServicoValidarFiltroConsultaPosicaoSelic implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {

      RequisicaoServicoValidarFiltroConsultaPosicaoSelic req = (RequisicaoServicoValidarFiltroConsultaPosicaoSelic) requisicao;
      ResultadoServicoValidarFiltroConsultaPosicaoSelic res = new ResultadoServicoValidarFiltroConsultaPosicaoSelic();

      CodigoContaSelic contaSelic = req.obterCONTA_CodigoContaSelic();
      CodigoIF codigoIF = req.obterCODIGO_IF_CodigoIF();

      IContaParticipante contaParticipante = ContaParticipanteFactory.getInstance();
      ContaParticipanteDO contaSelicDO = contaParticipante.obterContaParticipanteDO(contaSelic);

      if (contaSelicDO == null) {
         throw new Erro(CodigoErro.CONTA_NAO_CADASTRADA);
      }

      ContaParticipanteDO contaPrincipalDO = contaParticipante.obterContaPrincipal(contaSelicDO.getParticipante()
            .getId());

      if (contaPrincipalDO == null) {
         throw new Erro(CodigoErro.CONTA_PRINCIPAL_NAO_ENCONTRADA);
      }

      CodigoContaCetip contaPrincipal = contaPrincipalDO.getCodContaParticipante();
      Logger.info(this, "Conta Principal: " + contaPrincipal);

      AtributosColunados termos = new AtributosColunados();
      termos.atributo(contaSelic);
      termos.atributo(codigoIF);
      termos.atributo(contaPrincipal);

      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.validaFiltroConsultaPosicaoSelic, termos);

      return res;
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      throw new ExcecaoServico(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

}
