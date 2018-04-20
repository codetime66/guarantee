package br.com.cetip.aplicacao.garantias.servico.colateral;

import java.util.Date;

import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoGrade;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoGrade;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.tempo.Hora;
import br.com.cetip.infra.atributo.tipo.texto.NomeRegra;
import br.com.cetip.infra.atributo.utilitario.Condicional;
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
 *     atributo="CodigoTipoIF"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="INSTRUMENTO_FINANCEIRO_CONTRATO" 
 *
 * @requisicao.method
 *     atributo="TipoLinha"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="TIPO_LINHA"
 *
 * @requisicao.method
 *     atributo="Funcao"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @requisicao.method
 *     atributo="CodigoIF"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="INSTRUMENTO_FINANCEIRO_CONTRATO" 
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
public class ServicoValidaInclusaoAutorizacaoPublicGarantias implements Servico {

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
      RequisicaoServicoValidaInclusaoAutorizacaoPublicGarantias req = (RequisicaoServicoValidaInclusaoAutorizacaoPublicGarantias) requisicao;
      ResultadoServicoValidaInclusaoAutorizacaoPublicGarantias res = new ResultadoServicoValidaInclusaoAutorizacaoPublicGarantias();

      CodigoContaCetip codContaParticipante = req.obterPARTE_CONTRATO_CodigoContaCetip();
      CPFOuCNPJ cpfCnpjCliente = req.obterPARTE_CONTRATO_CPFOuCNPJ();
      CodigoIF contrato = req.obterINSTRUMENTO_FINANCEIRO_CONTRATO_CodigoIF();
      CodigoTipoIF codigoTipoIF = new CodigoTipoIF(Contexto.INSTRUMENTO_FINANCEIRO_CONTRATO);

      if (!Condicional.vazio(contrato)) {
         InstrumentoFinanceiroDO ifDO = InstrumentoFinanceiroFactory.getInstance().obterInstrumentoFinanceiro(contrato);
         codigoTipoIF = ifDO.getTipoIF().getCodigoTipoIF();
         codigoTipoIF.atribuirContexto(Contexto.INSTRUMENTO_FINANCEIRO_CONTRATO);
      }

      Funcao funcao = req.obterINSTRUMENTO_FINANCEIRO_Funcao();

      AtributosColunados termos = new AtributosColunados();

      //Acrescentar o parametro para fazer valiações
      NomeRegra predicado = null;
      if (funcao.mesmoConteudo(Funcao.INCLUSAO)) {
         predicado = ConstantesDeNomeDeRegras.podeIncluirAutorizacaoPublicGarantias;
      } else {
         predicado = ConstantesDeNomeDeRegras.podeExcluirAutorizacaoPublicGarantias;
      }

      termos.atributo(new CodigoGrade(CodigoTipoGrade.CTP11.toString()));
      termos.atributo(new Hora(new Date()));
      termos.atributo(codContaParticipante);
      termos.atributo(cpfCnpjCliente);
      if (!Condicional.vazio(cpfCnpjCliente)) {
         termos.atributo(cpfCnpjCliente.obterNatureza());
      } else {
         termos.atributo(new Natureza(Contexto.PARTE_CONTRATO));
      }
      termos.atributo(contrato);
      termos.atributo(codigoTipoIF);

      try {
         Logger.info("### ServicoValidaInclusaoAutorizacaoPublicGarantias : AtributosColunados (termos) : ");
         Logger.info(termos.toString());
         FabricaDeMotorDeRegra.getMotorDeRegra().avalia(predicado, termos);
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }

      return res;
   }

}
