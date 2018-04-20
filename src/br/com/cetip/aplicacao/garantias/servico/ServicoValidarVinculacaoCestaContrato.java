package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoVinculacaoContrato;
import br.com.cetip.aplicacao.garantias.apinegocio.VinculacaoContratoVO;
import br.com.cetip.aplicacao.garantias.apinegocio.colateral.ParametroIFGarantidorFactory;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ComplementoContratoDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="TIPO_CONTRATO"
 *                    
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="PARTE"
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="PARTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTRA_PARTE"
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTRA_PARTE"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador" 
 *                    contexto="RESET"
 * 
 * @requisicao.method atributo="CPFOuCNPJ" 
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador" 
 *                    contexto="PARTICIPANTE"
 * 
 * @requisicao.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTRA_PARTE"
 * 
 * @resultado.class
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * 
 */
public class ServicoValidarVinculacaoCestaContrato extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao arg0) throws Exception {
      throw new UnsupportedOperationException();
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      RequisicaoServicoValidarVinculacaoCestaContrato req;
      req = (RequisicaoServicoValidarVinculacaoCestaContrato) arg0;

      CodigoIF codigoIF = req.obterINSTRUMENTO_FINANCEIRO_CodigoIF();
      ComplementoContratoDO contrato = getFactory().getInstanceContratosCesta().obterContrato(codigoIF);
      if (contrato == null) {
         throw new Erro(CodigoErro.INSTRUMENTO_FINANCEIRO_INEXISTENTE);
      }

      CodigoTipoIF codigoTipoIF = contrato.getTipoIF().getCodigoTipoIF();

      // Valida entrada da tela
      CodigoTipoIF tipoContrato = req.obterTIPO_CONTRATO_CodigoTipoIF();
      if (!Condicional.vazio(tipoContrato) && !codigoTipoIF.mesmoConteudo(tipoContrato)) {
         throw new Erro(CodigoErro.TIPO_IF_INCOMPATIVEL_CODIGO_IF);
      }

      NumeroCestaGarantia numCestaParte = req.obterPARTE_NumeroCestaGarantia();
      NumeroCestaGarantia numCestaContraparte = req.obterCONTRA_PARTE_NumeroCestaGarantia();

      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();

      CestaGarantiasDO cestaParte = null;
      if (!Condicional.vazio(numCestaParte)) {
         cestaParte = icg.obterCestaDeGarantias(numCestaParte, ICestaDeGarantias.FUNCAO_GARANTIDOR);
      }

      CestaGarantiasDO cestaContraparte = null;
      if (!Condicional.vazio(numCestaContraparte)) {
         cestaContraparte = icg.obterCestaDeGarantias(numCestaContraparte, ICestaDeGarantias.FUNCAO_GARANTIDO);
      }

      CodigoContaCetip contaParte = req.obterPARTE_CodigoContaCetip();
      CodigoContaCetip contaContraparte = req.obterCONTRA_PARTE_CodigoContaCetip();

      IContaParticipante icp = ContaParticipanteFactory.getInstance();

      ContaParticipanteDO contaDOParte = icp.obterContaParticipanteDO(contaParte);
      ContaParticipanteDO contaDOContraparte = icp.obterContaParticipanteDO(contaContraparte);

      CPFOuCNPJ comitenteParte = req.obterPARTICIPANTE_CPFOuCNPJ();
      CPFOuCNPJ comitenteContraparte = req.obterCONTRA_PARTE_CPFOuCNPJ();
      
      
      //Verifica se as garantias da cesta estão habilitadas para MtM
      Booleano ehCetipAgenteCalc =  ParametroIFGarantidorFactory.getInstance().ehAtivoCetipComoAgenteCalculo(contrato.getId());
      if (ehCetipAgenteCalc.ehVerdadeiro() && ehAtivoHabilitadoMtM(cestaParte,cestaContraparte).ehFalso()) {
       	  throw new ExcecaoServico(CodigoErro.IF_GARANTIDOR_NAO_HABILITADO_MTM);
      }

      Funcao regraLiberacao = req.obterRESET_Funcao();

      IValidacaoVinculacaoContrato ivv = getFactory().getInstanceValidacaoVinculacaoContrato(codigoTipoIF);

      VinculacaoContratoVO vc = new VinculacaoContratoVO();
      vc.ativo = contrato;
      vc.cestaContraparte = cestaContraparte;
      vc.cestaParte = cestaParte;
      vc.contaParte = contaDOParte;
      vc.contaContraparte = contaDOContraparte;

      // cria novo objeto destes atributos, pois a Infra guarda na Sessao e 
      // qualquer programa chamado daqui pra frente, pode alterar o contexto destes objetos
      // destruindo os parametros da pagina
      vc.comitenteParte = comitenteParte != null ? new CPFOuCNPJ(comitenteParte.obterContexto(), comitenteParte.obterConteudo()) : null;
      vc.comitenteContraParte = comitenteContraparte != null ? new CPFOuCNPJ(comitenteContraparte.obterContexto(), comitenteContraparte.obterConteudo())
            : null;
      vc.regraLiberacao = regraLiberacao != null ? new Funcao(regraLiberacao.obterConteudo()) : null;

      ivv.validar(vc);

      return new ResultadoServicoValidarVinculacaoCestaContrato();
   }
   
   private Booleano ehAtivoHabilitadoMtM(CestaGarantiasDO cestaParte, CestaGarantiasDO cestaContraParte){
	   Booleano res = Booleano.VERDADEIRO;
	   
	   if (!Condicional.vazio(cestaParte)){
         if (validaHabilitacaoMtM(cestaParte).ehFalso()){
        	 return validaHabilitacaoMtM(cestaParte);
         }
	   }
	   if (!Condicional.vazio(cestaContraParte)){
		  if (validaHabilitacaoMtM(cestaContraParte).ehFalso()){
			  return validaHabilitacaoMtM(cestaContraParte);
		  }
	   }
        return res;
   }
   
   private Booleano validaHabilitacaoMtM (CestaGarantiasDO cestaDO){
	   Booleano res = Booleano.VERDADEIRO;
	   if (!Condicional.vazio(cestaDO)){
		   //obter os ativos garantidores	
		   ICestaDeGarantias icesta = getFactory().getInstanceCestaDeGarantias();
		    List listGarantias = icesta.listarGarantiasCesta(cestaDO.getNumCestaGarantias());
		   
		   Iterator i = listGarantias.iterator();
	         while (i.hasNext()) {
	        	                               
        	    DetalheGarantiaDO detDO    = (DetalheGarantiaDO) i.next();
	            Booleano ehAtivoHab = ParametroIFGarantidorFactory.getInstance().existeCodigoIFJaHabilitado(detDO.getInstrumentoFinanceiro().getSistema().getCodSistema(), detDO.getInstrumentoFinanceiro().getTipoIF().getCodigoTipoIF(), detDO.getInstrumentoFinanceiro().getCodigoIF());
	            if (ehAtivoHab.ehFalso()){
	            	 Booleano ehTodosIFHabilitado = ParametroIFGarantidorFactory.getInstance().ehHabilitadoPorModuloSistema(detDO.getInstrumentoFinanceiro().getSistema().getCodSistema(), detDO.getInstrumentoFinanceiro().getTipoIF().getCodigoTipoIF());
	            	 res = ehTodosIFHabilitado;
	            }
	         }
	   }
	   return res;
	   
   }
}