package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoExecutarCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaExecutarCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoValidaExecutarCestaGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.TextoLimitado;

/**
 * <p>
 * Tela para LIBERACAO TOTAL das garantias de uma Cesta
 * </p>
 */
public class FormularioLiberarCestaGarantias extends AbstractFormularioGarantias {

   public boolean confirmacao(Grupo dados, Servicos servico) throws Exception {
	   if (temConfirmacao.ehVerdadeiro()){
          return true;
	   }else {
    	  return false;
      }
   }
  private Booleano temContrato;
  private CodigoContaCetip contaGarantido;
  private Booleano temConfirmacao;
  private ResultadoServicoValidaExecutarCestaGarantias res;
  
   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servico)throws Exception {
	      NumeroCestaGarantia numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
	              Contexto.GARANTIAS_CODIGO);	      
	        RequisicaoServicoValidaExecutarCestaGarantias req = new RequisicaoServicoValidaExecutarCestaGarantias();
	        req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
	        res = (ResultadoServicoValidaExecutarCestaGarantias) servico.executarServico(req);
	        temContrato = res.obterCONTRATO_Booleano();
	        contaGarantido = res.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
	        if (!temContrato.ehVerdadeiro() && contaGarantido.ehContaCliente()){
	        	desenhaTela(layout, res);
	        	temConfirmacao= Booleano.FALSO;
	        } else{
	        	temConfirmacao= Booleano.VERDADEIRO;
	        }

	   
   }
   public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos servico) throws Exception {
      desenhaTela(layout, res);
   }

   public Notificacao chamarServico(Grupo dados, Servicos servico) throws Exception {
      RequisicaoServicoExecutarCestaGarantias requisicao = null;
      String notificacao = "ExecutarCestaGarantias.Sucesso";
      requisicao = new RequisicaoServicoExecutarCestaGarantias();

      requisicao.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(
            CodigoContaCetip.class, Contexto.GARANTIAS_PARTICIPANTE));
      requisicao.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(
            NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO));
      requisicao.atribuirGARANTIAS_CONTRAPARTE_CPFOuCNPJ((CPFOuCNPJ)dados.obterAtributo(CPFOuCNPJ.class,Contexto.GARANTIAS_CONTRAPARTE));

      servico.executarServico(requisicao);

      Notificacao not = new Notificacao(notificacao);
      // marreta para exibir num cesta com 8 posicoes
      NumeroCestaGarantia numero = requisicao.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      TextoLimitado codCesta = new TextoLimitado(numero.obterConteudo().toString().length() < 8 ? "0"
            + numero.obterConteudo().toString() : numero.obterConteudo().toString());

      not.parametroMensagem(codCesta, 0);
      return not;
   }
   
   private void desenhaTela(GrupoDeGrupos layout,ResultadoServicoValidaExecutarCestaGarantias res) {
	   
       NumeroCestaGarantia numeroCesta = res.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
       CodigoContaCetip conta = res.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
       Data data = res.obterGARANTIAS_DATA_CRIACAO_Data();

       layout.contexto(Contexto.GARANTIAS_DADOS);
       GrupoDeGrupos principal = layout.grupoDeGrupos(1);

       GrupoDeGrupos grupoCadastro = principal.grupoDeGrupos(1);
       grupoCadastro.contexto(Contexto.GARANTIAS_DADOS);

       GrupoDeAtributos grupoIF = grupoCadastro.grupoDeAtributos(2);
       grupoIF.atributoNaoEditavel(numeroCesta);
       grupoIF.atributoNaoEditavel(conta);
       
       if (res.obterCONTRATO_Booleano().ehVerdadeiro()){
       	CPFOuCNPJ cpfCnpjGarantido = (new CPFOuCNPJ(br.com.cetip.infra.atributo.utilitario.Texto.nullSafeToString(res.obterGARANTIAS_CONTRAPARTE_CPFOuCNPJ())));
       	cpfCnpjGarantido.atribuirContexto(Contexto.GARANTIAS_CONTRAPARTE);
       	grupoIF.atributoNaoEditavel(cpfCnpjGarantido);
       	
       } else if (conta.ehContaCliente1() || conta.ehContaCliente2()) {
    	   
          grupoIF.atributoObrigatorio(new CPFOuCNPJ(Contexto.GARANTIAS_CONTRAPARTE));
       } 
       grupoIF.atributoNaoEditavel(data);
   }

}