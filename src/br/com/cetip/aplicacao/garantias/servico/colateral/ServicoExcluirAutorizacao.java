package br.com.cetip.aplicacao.garantias.servico.colateral;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiasFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.colateral.AutorizacaoPublicGarantiasFactory;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.swap.IParametroPonta;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.swap.ParametroPontaFactory;
import br.com.cetip.aplicacao.registrooperacao.servico.garantias.RequisicaoServicoRegistrarDesautorizacaoPublicGarantias;
import br.com.cetip.dados.aplicacao.garantias.AutorizacaoPublicGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.TipoParametroPublicGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ComplementoContratoDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ParametroPontaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoParametroPublicGarantias;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.roteador.Roteador;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="AUTORIZACAO_PUBLICIDADE_GARANTIAS"
 */
public class ServicoExcluirAutorizacao extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao r) throws Exception {
      RequisicaoServicoExcluirAutorizacao req = (RequisicaoServicoExcluirAutorizacao) r;

      Id idAutorizacao = req.obterAUTORIZACAO_PUBLICIDADE_GARANTIAS_Id();

      AutorizacaoPublicGarantiasDO autorizacao = (AutorizacaoPublicGarantiasDO) getGp().load(
            AutorizacaoPublicGarantiasDO.class, idAutorizacao);

      TipoParametroPublicGarantiasDO parametroAtivoDO = AutorizacaoPublicGarantiasFactory.getInstance().obterParametroPublicAtivo();
      IdTipoParametroPublicGarantias idTipoParamPublicGarantias = parametroAtivoDO.getId();
      IGarantias garantias = GarantiasFactory.getInstance();
     
      final ParametroPontaDO ponta = autorizacao.getParametroPonta(); //ponta do garantidor
      final ComplementoContratoDO contrato = ponta.getContrato();
      final ContaParticipanteDO contaParticipante = ponta.getContaParticipante();
      CestaGarantiasDO cestaDO = null;
      CodigoContaCetip contaGarantias = new CodigoContaCetip();
      IParametroPonta iPonta = null;
      CPFOuCNPJ cpfCnpjParte= new CPFOuCNPJ();
      
      if ((idTipoParamPublicGarantias.mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_1) ||
	       idTipoParamPublicGarantias.mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_2) ||
	       idTipoParamPublicGarantias.mesmoConteudo(IdTipoParametroPublicGarantias.PARAMETRO_5) ) )
	  {
    	  //Garantidor
    	  contaGarantias = contaParticipante.getCodContaParticipante();
    	  cpfCnpjParte = ponta.getCPFOuCNPJ();
	  } else{
		  //parametro 3 e 4 
		  //contas tem que ser a conta do Garantido
		  ParametroPontaDO[] paramPontas = garantias.getInstanceContratosCesta().obterPontas(contrato);
		  if (!ponta.getIdParametroPonta().mesmoConteudo(paramPontas[0].getIdParametroPonta())){    
			  iPonta = ParametroPontaFactory.getInstance(paramPontas[0].getIdParametroPonta());
    	  } else {
			  iPonta = ParametroPontaFactory.getInstance(paramPontas[1].getIdParametroPonta());
			  
    	  }
		  cpfCnpjParte = iPonta.getParametroPontaDO().getCPFOuCNPJ();
		  //Carregar o parametro ponta
		  contaGarantias = iPonta.getParametroPontaDO().getContaParticipante().getCodContaParticipante();
	  }
     

      RequisicaoServicoRegistrarDesautorizacaoPublicGarantias reqDesautoriza;
      reqDesautoriza = new RequisicaoServicoRegistrarDesautorizacaoPublicGarantias();
      reqDesautoriza.atribuirINSTRUMENTO_FINANCEIRO_CodigoTipoIF(contrato.getTipoIF().getCodigoTipoIF());
      reqDesautoriza.atribuirINSTRUMENTO_FINANCEIRO_CONTRATO_CodigoIF(contrato.getCodigoIF());
      reqDesautoriza.atribuirPARTE_CONTRATO_CodigoContaCetip(contaGarantias);
      reqDesautoriza.atribuirPARTE_CONTRATO_CPFOuCNPJ(cpfCnpjParte);

      Roteador.executarMesmaTransacao(reqDesautoriza, getContextoAtivacao());

      return new ResultadoServicoExcluirAutorizacao();
   }

   public Resultado executarConsulta(Requisicao r) throws Exception {
      throw new UnsupportedOperationException();
   }

}
