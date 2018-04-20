package br.com.cetip.aplicacao.garantias.servico.colateral;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiasFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.colateral.AutorizacaoPublicGarantiasFactory;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.swap.IParametroPonta;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.swap.ParametroPontaFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.TipoParametroPublicGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ComplementoContratoDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ParametroPontaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoParametroPublicGarantias;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.RazaoSocial;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="INSTRUMENTO_FINANCEIRO_CONTRATO"
 * 
 * @requisicao.method atributo="CodigoContaCetip"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="PARTE_CONTRATO"
 * 
 * @requisicao.method atributo="CPFOuCNPJ"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="PARTE_CONTRATO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO_CONTRATO"
 * 
 * @resultado.method atributo="CodigoTipoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="PARTE_CONTRATO"
 * 
 * @resultado.method atributo="NomeSimplificado"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="PARTE_CONTRATO"
 * 
 * @resultado.method atributo="RazaoSocial"
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="PARTE_CONTRATO"
 * 
 * @resultado.method atributo="CPFOuCNPJ"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="PARTICIPANTE"
 * 
 * @resultado.method atributo="NumeroCestaGarantia"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CESTA"
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CONTRA_PARTE_CONTRATO"
 * 
 * @resultado.method atributo="CPFOuCNPJ"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CONTRA_PARTE_CONTRATO"
 * 
 * @resultado.method atributo="NomeSimplificado"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CONTRA_PARTE_CONTRATO"
 * 
 *@resultado.method atributo="Funcao"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="AUTORIZACAO_PUBLICIDADE_GARANTIAS"
 * 
 * 
 */
public class ServicoConsultaDetalhadaAutorizacaoPublicGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new UnsupportedOperationException();
   }

   private ContaParticipanteDO cpDOP1 = null;
   private ContaParticipanteDO cpDOP2 = null;
   private NomeSimplificado nomeP1 = new NomeSimplificado();
   private NomeSimplificado nomeP2 = new NomeSimplificado();
   private CodigoContaCetip contaP1 = new CodigoContaCetip();
   private CodigoContaCetip contaP2 = new CodigoContaCetip();
   private CPFOuCNPJ cpfCnpjComitenteParte = new CPFOuCNPJ();
   private CPFOuCNPJ cpfCnpjComitenteContraParte = new CPFOuCNPJ();
   private NumeroCestaGarantia cestaGarantia = new NumeroCestaGarantia();
   private NumeroCestaGarantia cestaGarantiaP2 = new NumeroCestaGarantia();
   private CodigoIF codigoIF = new CodigoIF();
   private CodigoTipoIF codTipoIF = new CodigoTipoIF();
   private CodigoContaCetip contaParticipante = new CodigoContaCetip();
   private IContaParticipante contaParticipanteDAO = null;

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
	   RequisicaoServicoConsultaDetalhadaAutorizacaoPublicGarantias req = (RequisicaoServicoConsultaDetalhadaAutorizacaoPublicGarantias) requisicao;
	      ResultadoServicoConsultaDetalhadaAutorizacaoPublicGarantias resultado = new ResultadoServicoConsultaDetalhadaAutorizacaoPublicGarantias();
	      codigoIF = req.obterINSTRUMENTO_FINANCEIRO_CONTRATO_CodigoIF();
	      contaParticipante = req.obterPARTE_CONTRATO_CodigoContaCetip();
	      cpfCnpjComitenteParte = req.obterPARTE_CONTRATO_CPFOuCNPJ();	      

	      TipoParametroPublicGarantiasDO tipoParam = AutorizacaoPublicGarantiasFactory.getInstance().obterParametroPublicAtivo();

	      InstrumentoFinanceiroDO ifDO = null;
	      ComplementoContratoDO complContratoDO = null;
	      ContaParticipanteDO cpDO = null;
	      contaParticipanteDAO = ContaParticipanteFactory.getInstance();
	      if (!Condicional.vazio(codigoIF)) {
	         ifDO = InstrumentoFinanceiroFactory.getInstance().obterInstrumentoFinanceiro(codigoIF);
	         complContratoDO = (ComplementoContratoDO) ifDO;

	         // obtendo pontas do contrato
	         IGarantias garantias = GarantiasFactory.getInstance();         

	         CestaGarantiasDO cestas = null;
	         cpDO = ContaParticipanteFactory.getInstance().obterContaParticipanteDO(req.obterPARTE_CONTRATO_CodigoContaCetip());
	         ParametroPontaDO paramPontaP1 = GarantiasFactory.getInstance().getInstanceContratosCesta().obterPonta(complContratoDO, cpDO,cpfCnpjComitenteParte);
	         ParametroPontaDO[] paramPontas = garantias.getInstanceContratosCesta().obterPontas(complContratoDO);
	         
	         if  ( tipoParam.getId().mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_1)||
	        	   tipoParam.getId().mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_2)) {
	        	 
	               //Recuperar a cesta do Garantidor        
	               CestaGarantiasDO cestaDO = garantias.getInstanceContratosCesta().obterCestaPorPonta(paramPontaP1);
	               contaP1 = cestaDO.getGarantidor().getCodContaParticipante();
	               contaP2 = cestaDO.getGarantido().getCodContaParticipante();
	               cestaGarantia = cestaDO.getNumCestaGarantias();
	               if (!Condicional.vazio(cpfCnpjComitenteParte)){
	                   cpfCnpjComitenteParte= paramPontaP1.getCPFOuCNPJ();
	               }
	         } else if( tipoParam.getId().mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_3) ||
	             tipoParam.getId().mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_4)){
	        	   //Recuperar a ponta do Garantido        	  
		        	  if (!paramPontaP1.getIdParametroPonta().mesmoConteudo(paramPontas[0].getIdParametroPonta())){        		  
		        		  cestas = garantias.getInstanceContratosCesta().obterCestaPorPonta(paramPontas[0].getIdParametroPonta());
		        		  cpfCnpjComitenteParte= paramPontas[0].getCPFOuCNPJ();  
		        	  } else {
		        		  cestas = garantias.getInstanceContratosCesta().obterCestaPorPonta(paramPontas[1].getIdParametroPonta());
		        		  cpfCnpjComitenteParte= paramPontas[1].getCPFOuCNPJ();
		        	  }
		               contaP1 = cestas.getGarantido().getCodContaParticipante();
		               contaP2 = cestas.getGarantidor().getCodContaParticipante();
		               cestaGarantia = cestas.getNumCestaGarantias();
	         }
	         
	         
	         if(tipoParam.getId().mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_5)){
	        	 
	        	 // term duas pontas verifica se e o garantidor
	        	 CestaGarantiasDO cestaDO = garantias.getInstanceContratosCesta().obterCestaPorPonta(paramPontaP1);
	        	    if (!Condicional.vazio(cestaDO)) {
		        		 contaP1 = cestaDO.getGarantidor().getCodContaParticipante();
			             contaP2 = cestaDO.getGarantido().getCodContaParticipante();
			             cestaGarantia = cestaDO.getNumCestaGarantias();
			             CestaGarantiasDO cestaP2 = null;
			             
			             //verifica se tem cesta nas duas pontas
			             if (temCestaNasDuasPontas(paramPontas, garantias).ehVerdadeiro()){
			            	 // pega a outra ponta do contrato
			            	 if (!paramPontaP1.getIdParametroPonta().mesmoConteudo(paramPontas[0].getIdParametroPonta())){
				        	    	//e a outra ponta do contrato
				        	    	cestaP2 = GarantiasFactory.getInstance().getInstanceContratosCesta().obterCestaPorPonta(paramPontas[0].getIdParametroPonta());
				        	 } else {
				        	    	cestaP2 = GarantiasFactory.getInstance().getInstanceContratosCesta().obterCestaPorPonta(paramPontas[1].getIdParametroPonta());
				        	 }
			            	 //preenche a outra linha com a outra cesta
			            	 preencheResultado(resultado, cestaP2);
			             }
	        	    } else {
                        //não entrou com o garantidor então é o garantido
	        		    Id idParamPontaP2 = null;
		        	    if (!paramPontaP1.getIdParametroPonta().mesmoConteudo(paramPontas[0].getIdParametroPonta())){
		        	    	 cestas = GarantiasFactory.getInstance().getInstanceContratosCesta().obterCestaPorPonta(paramPontas[0].getIdParametroPonta());
		        	    	 idParamPontaP2 = paramPontaP1.getIdParametroPonta();
		        	    } else {
		        	    	cestas = GarantiasFactory.getInstance().getInstanceContratosCesta().obterCestaPorPonta(paramPontas[1].getIdParametroPonta());
		        	    	idParamPontaP2 = paramPontas[0].getIdParametroPonta();
		        	    }
		        	    //inversão das pontas
		        	    contaP1 = cestas.getGarantido().getCodContaParticipante();
			            contaP2 = cestas.getGarantidor().getCodContaParticipante();
			            cestaGarantia = cestas.getNumCestaGarantias();
		        	    
			            if (temCestaNasDuasPontas(paramPontas, garantias).ehVerdadeiro()){
			              //com a outra ponta do contrato
			            	
			              IParametroPonta iPonta = ParametroPontaFactory.getInstance(idParamPontaP2);
			              CestaGarantiasDO cestaP2 = GarantiasFactory.getInstance().getInstanceContratosCesta().obterCestaPorPonta(iPonta.getParametroPontaDO());
		        	      preencheResultado(resultado, cestaP2);
			            }
		            }
	           }
	         
		         cpDOP1 = contaParticipanteDAO.obterContaParticipanteDO(contaP1);
		         cpDOP2 = contaParticipanteDAO.obterContaParticipanteDO(contaP2);
		         nomeP1 = cpDOP1.getParticipante().getNomSimplificadoEntidade();
		         nomeP2 = cpDOP2.getParticipante().getNomSimplificadoEntidade();
		         codTipoIF = ifDO.getTipoIF().getCodigoTipoIF();

	         }
	        
	        

	      if (!Condicional.vazio(contaParticipante)) {
	         cpDO = contaParticipanteDAO.obterContaParticipanteDO(contaParticipante);
	         resultado.atribuirPARTE_CONTRATO_RazaoSocial(new RazaoSocial(cpDO.getNomeContaParticipante().obterConteudo()));
	      } else {
	         resultado
	               .atribuirPARTE_CONTRATO_RazaoSocial(new RazaoSocial(cpDOP1.getNomeContaParticipante().obterConteudo()));
	      }

	      if (!Condicional.vazio(cpfCnpjComitenteParte)) {
	         resultado.atribuirPARTICIPANTE_CPFOuCNPJ(cpfCnpjComitenteParte);
	      } else {
	         resultado.atribuirPARTICIPANTE_CPFOuCNPJ(new CPFOuCNPJ());
	      }

	      resultado.atribuirINSTRUMENTO_FINANCEIRO_CONTRATO_CodigoIF(codigoIF);
	      resultado.atribuirINSTRUMENTO_FINANCEIRO_CodigoTipoIF(codTipoIF);
	      resultado.atribuirPARTE_CONTRATO_CodigoContaCetip(contaP1);
	      resultado.atribuirCONTRA_PARTE_CONTRATO_CodigoContaCetip(contaP2);
	      resultado.atribuirPARTE_CONTRATO_NomeSimplificado(nomeP1);
	      resultado.atribuirCONTRA_PARTE_CONTRATO_NomeSimplificado(nomeP2);
	      resultado.atribuirCONTRA_PARTE_CONTRATO_CPFOuCNPJ(cpfCnpjComitenteContraParte);
	      resultado.atribuirGARANTIAS_CESTA_NumeroCestaGarantia(cestaGarantia);

	      Funcao acoes = new Funcao(Contexto.AUTORIZACAO_PUBLICIDADE_GARANTIAS);
	      acoes.getDomain().add(new Funcao());
	      acoes.getDomain().add(Funcao.EXCLUIR);
	      resultado.atribuirAUTORIZACAO_PUBLICIDADE_GARANTIAS_Funcao(acoes);

	      return resultado;
	   }

	   private Booleano temCestaNasDuasPontas(ParametroPontaDO[] paramPontas,IGarantias garantias) {
	      Booleano temCesta = Booleano.FALSO;

	         CestaGarantiasDO cestaP1 = null;
	         CestaGarantiasDO cestaP2 = null;
	         cestaP1 = garantias.getInstanceContratosCesta().obterCestaPorPonta(paramPontas[0].getIdParametroPonta());
	         cestaP2 = garantias.getInstanceContratosCesta().obterCestaPorPonta(paramPontas[1].getIdParametroPonta());

	         if (!Condicional.vazio(cestaP1) && !Condicional.vazio(cestaP2)) {
	            temCesta = Booleano.VERDADEIRO;
	         }

	      return temCesta;
	   }

	   private void preencheResultado(ResultadoServicoConsultaDetalhadaAutorizacaoPublicGarantias resultado, CestaGarantiasDO cesta) {
		   
		  cpDOP1 = contaParticipanteDAO.obterContaParticipanteDO(contaP1);
	      cpDOP2 = contaParticipanteDAO.obterContaParticipanteDO(contaP2);
      	  nomeP1 = cpDOP1.getParticipante().getNomSimplificadoEntidade();
	      nomeP2 = cpDOP2.getParticipante().getNomSimplificadoEntidade();
	         
	      resultado.novaLinha();
	      resultado.atribuirINSTRUMENTO_FINANCEIRO_CONTRATO_CodigoIF(codigoIF);
	      resultado.atribuirINSTRUMENTO_FINANCEIRO_CodigoTipoIF(codTipoIF);
	      resultado.atribuirPARTE_CONTRATO_CodigoContaCetip(contaP1);
	      resultado.atribuirCONTRA_PARTE_CONTRATO_CodigoContaCetip(contaP2);
	      resultado.atribuirPARTE_CONTRATO_NomeSimplificado(nomeP1);
	      resultado.atribuirCONTRA_PARTE_CONTRATO_NomeSimplificado(nomeP2);
	      resultado.atribuirPARTICIPANTE_CPFOuCNPJ(cpfCnpjComitenteParte);
	      resultado.atribuirCONTRA_PARTE_CONTRATO_CPFOuCNPJ(cpfCnpjComitenteContraParte);
	      resultado.atribuirPARTE_CONTRATO_RazaoSocial(new RazaoSocial(cpDOP1.getNomeContaParticipante().obterConteudo()));
	      resultado.atribuirGARANTIAS_CESTA_NumeroCestaGarantia(cesta.getNumCestaGarantias());

	      Funcao acoes = new Funcao(Contexto.AUTORIZACAO_PUBLICIDADE_GARANTIAS);
	      acoes.getDomain().add(new Funcao());
	      acoes.getDomain().add(Funcao.EXCLUIR);
	      resultado.atribuirAUTORIZACAO_PUBLICIDADE_GARANTIAS_Funcao(acoes);

	   }

	   /*private Booleano ehPontaDoContratoP1(CodigoContaCetip conta, CPFOuCNPJ cpfCnpjComitente,
	         CodigoContaCetip contaParticP1, CPFOuCNPJ cpfCnpjP1) {
	      Booleano res = Booleano.FALSO;

	      if (conta.mesmoConteudo(contaParticP1)) {
	         if (!Condicional.vazio(cpfCnpjComitente) && cpfCnpjComitente.toString().equals(cpfCnpjP1.toString())) {
	            res = Booleano.VERDADEIRO;
	         }
	      }

	      return res;
	   }*/
}