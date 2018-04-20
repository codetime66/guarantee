package br.com.cetip.aplicacao.garantias.servico.colateral;

import java.util.Date;

import br.com.cetip.aplicacao.garantias.apinegocio.colateral.AutorizacaoPublicGarantiasFactory;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.dados.aplicacao.garantias.TipoParametroPublicGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoGrade;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIFContrato;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoGrade;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoParametroPublicGarantias;
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
 *     atributo="Id"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="GARANTIAS_CESTA"
 *    
 * @requisicao.method
 *     atributo="Id"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="AUTORIZACAO_PUBLICIDADE_GARANTIAS"     
 *     
 * @requisicao.method
 *     atributo="CodigoIFContrato"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CONTRATO" 
 *     
 * @requisicao.method
 *     atributo="CodigoContaCetip"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CONTA_GARANTIDOR_MANUT_AUTORIZ"
 *     
 *  @requisicao.method
 *     atributo="CodigoContaCetip"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CONTA_GARANTIDO_MANUT_AUTORIZ"    
 *     
 *  @requisicao.method 
 *    atributo="CPFOuCNPJ"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="CPF_CNPJ_GARANTIDOR_MANUT_AUTORIZ"   
 *     
 *  @requisicao.method 
 *    atributo="CPFOuCNPJ"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="CPF_CNPJ_GARANTIDO_MANUT_AUTORIZ"
 *    
 *    
  * @resultado.class
 */
public class ServicoValidaExclusaoAutorizacaoPublicGarantias implements Servico {

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
      RequisicaoServicoValidaExclusaoAutorizacaoPublicGarantias req = (RequisicaoServicoValidaExclusaoAutorizacaoPublicGarantias) requisicao;
      ResultadoServicoValidaExclusaoAutorizacaoPublicGarantias res = new ResultadoServicoValidaExclusaoAutorizacaoPublicGarantias();

      CodigoContaCetip contaGarantidor = req.obterCONTA_GARANTIDOR_MANUT_AUTORIZ_CodigoContaCetip();
      CodigoContaCetip contaGarantido = req.obterCONTA_GARANTIDO_MANUT_AUTORIZ_CodigoContaCetip();
      
      CPFOuCNPJ cpfCnpjGarantidor = req.obterCPF_CNPJ_GARANTIDOR_MANUT_AUTORIZ_CPFOuCNPJ();
      CPFOuCNPJ cpfCnpjGarantido = req.obterCPF_CNPJ_GARANTIDO_MANUT_AUTORIZ_CPFOuCNPJ();
      
      CodigoIFContrato contrato = req.obterCONTRATO_CodigoIFContrato();
      
      CodigoTipoIF codigoTipoIF = new CodigoTipoIF(Contexto.INSTRUMENTO_FINANCEIRO_CONTRATO);

      if (!Condicional.vazio(contrato)) {
         InstrumentoFinanceiroDO ifDO = InstrumentoFinanceiroFactory.getInstance().obterInstrumentoFinanceiro(contrato);
         codigoTipoIF = ifDO.getTipoIF().getCodigoTipoIF();
         codigoTipoIF.atribuirContexto(Contexto.INSTRUMENTO_FINANCEIRO_CONTRATO);
      }

      CodigoContaCetip contaPart = new CodigoContaCetip();
      CPFOuCNPJ cpfCnpjParte = new CPFOuCNPJ();
      
      // buscar o parametro ativo
      TipoParametroPublicGarantiasDO paramDO = AutorizacaoPublicGarantiasFactory.getInstance().obterParametroPublicAtivo();
      
     if ( paramDO.getId().mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_1) ||
    	  paramDO.getId().mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_2) ||
    	  paramDO.getId().mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_5)) {
    	  
    	  contaPart  = contaGarantidor;
    	  cpfCnpjParte = cpfCnpjGarantidor;
    	  
      } else 
    	  if (paramDO.getId().mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_3)||
    		  paramDO.getId().mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_4)){
    	contaPart = contaGarantido;		 
    	cpfCnpjParte = cpfCnpjGarantido;
     } 
      
      //eParametroAutPermitidoParaPonta(CodigoContaCetip?,CPFOuCNPJ?,CodigoIF?)
      
      AtributosColunados termos = new AtributosColunados();

      //Acrescentar o parametro para fazer valiações
      NomeRegra predicado = null;
     
      predicado = ConstantesDeNomeDeRegras.podeExcluirAutorizacaoPublicGarantias;

      
  	termos.atributo(new CodigoGrade(CodigoTipoGrade.CTP11.toString()));
	termos.atributo(new Hora(Contexto.OPERACAO, new Date()));
    termos.atributo(contaPart);
    termos.atributo(cpfCnpjParte);

    if (!Condicional.vazio(cpfCnpjParte)) {
    	termos.atributo(cpfCnpjParte.obterNatureza());
    } else {
    	termos.atributo(new Natureza());
    }
    termos.atributo(contrato);
  	termos.atributo(codigoTipoIF);
      

      try {
         Logger.info("### ServicoValidaExclusaoAutorizacaoPublicGarantias : AtributosColunados (termos) : ");
         Logger.info(termos.toString());
         FabricaDeMotorDeRegra.getMotorDeRegra().avalia(predicado, termos);
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }

      return res;
   }

}
