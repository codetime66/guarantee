package br.com.cetip.aplicacao.garantias.servico.colateral;

import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIFContrato;
import br.com.cetip.infra.atributo.tipo.texto.NomeRegra;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.contexto.ContextoAtivacao;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTA_GARANTIDOR_MANUT_AUTORIZ"
 *                    
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTA_GARANTIDO_MANUT_AUTORIZ"
 *                    
 * @requisicao.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CPF_CNPJ_GARANTIDOR_MANUT_AUTORIZ"
 *                    
 * @requisicao.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CPF_CNPJ_GARANTIDO_MANUT_AUTORIZ"
 *                    
 * @requisicao.method atributo="CodigoIFContrato" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTRATO"
 *                    
 * @requisicao.method atributo="CodigoSituacaoAutorizacaoGarantias" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="SITUACAO"
 *                    
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="ACAO"
 *                    
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CESTA"
 */
public class ServicoValidarConsultaAutorizacoes implements Servico {

   public Resultado executar(Requisicao arg0) throws Exception {
      throw new UnsupportedOperationException();
   }

   public Resultado executarConsulta(Requisicao r) throws Exception {
      RequisicaoServicoValidarConsultaAutorizacoes req = (RequisicaoServicoValidarConsultaAutorizacoes) r;

      CodigoContaCetip parte = req.obterCONTA_GARANTIDOR_MANUT_AUTORIZ_CodigoContaCetip();
      CodigoContaCetip contraparte = req.obterCONTA_GARANTIDO_MANUT_AUTORIZ_CodigoContaCetip();

      CPFOuCNPJ docParte = req.obterCPF_CNPJ_GARANTIDOR_MANUT_AUTORIZ_CPFOuCNPJ();
      CPFOuCNPJ docContraparte = req.obterCPF_CNPJ_GARANTIDO_MANUT_AUTORIZ_CPFOuCNPJ();

      CodigoIFContrato contrato = req.obterCONTRATO_CodigoIFContrato();

      AtributosColunados ac = new AtributosColunados();
      ac.atributo(contrato);
      ac.atributo(parte);
      ac.atributo(docParte);
      ac.atributo(contraparte);
      ac.atributo(docContraparte);
      
      final NomeRegra regra = ConstantesDeNomeDeRegras.validaFiltroManutencaoAutorizacaoPublicaGarantias;
      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(regra, ac);

      return new ResultadoServicoValidarConsultaAutorizacoes();
   }

}
