package br.com.cetip.aplicacao.garantias.servico.agro;

import br.com.cetip.aplicacao.garantias.apinegocio.agro.IFGarantidorFactory;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.garantias.negocio.agro.Parametros;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * Servico para validacao para Habilitação de IF Garantidor para Agro-Negócio 
 * 
 * @author <a href="mailto:reinaldosantana@cetip.com">Reinaldo Santana</a>
 * @since Maio/2010
 * 
 * @requisicao.class 
 * 
 * @requisicao.method atributo="TipoHabilitacao" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="GARANTIAS_CESTA"
 * 
 * @requisicao.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CESTA"
 *                   
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CESTA"                  
 * 
 * @requisicao.method atributo="NumeroInteiro" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="ACAO" 
 * @resultado.class
 * 
 */
public class ServicoValidaHabilitacaoIFGarantidor extends BaseGarantias implements Servico {

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      return null;
   }

   /*
    * Realiza a validacao dos dados digitados via motor de regras.
    * 
    * @param requisicao requisicao que contem os dados para validacao
    * 
    * @return o Resultado contendo se a validacao foi aceita ou nao
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoValidaHabilitacaoIFGarantidor req = (RequisicaoServicoValidaHabilitacaoIFGarantidor) requisicao;
      ResultadoServicoValidaHabilitacaoIFGarantidor res = new ResultadoServicoValidaHabilitacaoIFGarantidor();
      
      CodigoTipoIF codigoTipoIF = req.obterGARANTIAS_CESTA_CodigoTipoIF();
      CodigoIF codigoIF = req.obterGARANTIAS_CESTA_CodigoIF();
      NumeroInteiro acao = req.obterACAO_NumeroInteiro();

      if (acao.mesmoConteudo(Parametros.INCLUIR)){    	  
    	  validarInclusao(codigoTipoIF, codigoIF);
      }else if (acao.mesmoConteudo(Parametros.ATIVAR)){
    	  validarAtivacao(codigoTipoIF, codigoIF);
      }else if (acao.mesmoConteudo(Parametros.DESATIVAR)){
    	  validarDesativacao(codigoTipoIF, codigoIF);
      }else if (acao.mesmoConteudo(Parametros.EXCLUIR)){
    	  validarExclusao(codigoTipoIF, codigoIF);
      }	   
      return res;
   }
   
   private void validarInclusao(CodigoTipoIF codigoTipoIF, CodigoIF codigoIF) throws Exception {

	   CodigoTipoIF codTipo = InstrumentoFinanceiroFactory.getInstance().obterCodigoTipoInstrumentoFinanceiro(codigoIF);
//     Logger.info("[[[ if.CodigoTipoIF=" + codTipo  
//	             + " par.CodigoTipo=" + codigoTipoIF
//	             + " cond=" + codTipo.toString().compareTo(codigoTipoIF.toString()) );
	   if ( codTipo.toString().compareTo(codigoTipoIF.toString()) != 0 ) {
          throw new Erro (CodigoErro.INSTRUMENTO_FINANCEIRO_INEXISTENTE);
     }
      
	   Booleano ehHabilitado = IFGarantidorFactory.getInstance().existeGarantidorHabilitado(codigoTipoIF, codigoIF);
       if (ehHabilitado.ehVerdadeiro()) {
           throw new Erro (CodigoErro.MMG_GARANTIDOR_AGRO_JA_HABILITADO);
       }    
   }
   
   private void validarAtivacao(CodigoTipoIF codigoTipoIF, CodigoIF codigoIF)throws Exception {
	  AtributosColunados ac = new AtributosColunados();      
	  ac.atributo(codigoTipoIF);
	  ac.atributo(codigoIF);
	  // FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeHabilitarIFGarantidor, ac, true);
   }
   
   private void validarDesativacao(CodigoTipoIF codigoTipoIF, CodigoIF codigoIF)throws Exception {
	  AtributosColunados ac = new AtributosColunados();      
	  ac.atributo(codigoTipoIF);
	  ac.atributo(codigoIF);
	  // FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeHabilitarIFGarantidor, ac, true);   
   }
   
   private void validarExclusao(CodigoTipoIF codigoTipoIF, CodigoIF codigoIF)throws Exception {
	  AtributosColunados ac = new AtributosColunados();      
	  ac.atributo(codigoTipoIF);
	  ac.atributo(codigoIF);
	  FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeExcluirIFGarantidorHabilitado, ac, true);   
   }
}
