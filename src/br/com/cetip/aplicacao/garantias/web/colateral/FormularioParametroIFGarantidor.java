package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoRegistraParametroIFGarantidor;
import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoValidaParametroIFGarantidor;
import br.com.cetip.aplicacao.garantias.servico.colateral.ResultadoServicoRegistraParametroIFGarantidor;
import br.com.cetip.base.web.acao.Formulario;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

public class FormularioParametroIFGarantidor extends Formulario {

	 public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servico)throws Exception {
      
		GrupoDeGrupos geral = layout.grupoDeGrupos(1);
	    geral.posicaoTitulo(GrupoDeAtributos.TITULO_A_ESQUERDA);

		GrupoDeGrupos dadosForm = geral.grupoDeGrupos(1);

		GrupoDeAtributos att = dadosForm.grupoDeAtributos(2);
		att.posicaoTitulos(GrupoDeAtributos.TITULOS_A_ESQUERDA);
		att.contexto(Contexto.GARANTIAS_PARAM_IF_GARANTIDOR);
		
	    att.atributoObrigatorio(getModulos());
	    att.atributoNaoEditavel(null);
	    
	    att.atributoObrigatorio(CodigoTipoIF.obterDominioParamIFGarantidor());  
	    att.atributoNaoEditavel(null);
	    
        att.atributo(new  CodigoIF(Contexto.GARANTIAS_CODIGO_IF));

	    Booleano todosIFSelecionados = new Booleano(Contexto.GARANTIAS_SEL_TODOS_IF);
		todosIFSelecionados.getDomain().clear();
		todosIFSelecionados.getDomain().add(Booleano.VAZIO);
		todosIFSelecionados.getDomain().add(Booleano.FALSO);
		todosIFSelecionados.getDomain().add(Booleano.VERDADEIRO);
		att.atributoObrigatorio(todosIFSelecionados);
   }

   private CodigoSistema getModulos() {
      CodigoSistema modulos= new CodigoSistema(Contexto.GARANTIAS_SISTEMA);
      modulos.getDomain().clear();
      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA,""));
      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA,"SNA"));
      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA,"SND"));
      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA,"CETIP21"));
      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA,"SELIC"));
      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA,"MOP"));
      
      return modulos;
   }

   public Class obterDestino(Grupo g, Servicos s) throws Exception {
      return null;
   }

   public void validar(Grupo g, Servicos s) throws Exception {
      RequisicaoServicoValidaParametroIFGarantidor req = new RequisicaoServicoValidaParametroIFGarantidor();
      s.executarServico(req, g);
   }
  

   public boolean confirmacao(Grupo arg0, Servicos arg1) throws Exception {
	  return true;
   }
  
  /* (non-Javadoc)
	 * @see br.com.cetip.base.web.acao.Formulario#confirmacao(br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos, br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
	 */
	public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos arg2)
		throws Exception {
	}
   
	public Notificacao chamarServico(Grupo dados, Servicos servico)	throws Exception {
	
	  RequisicaoServicoRegistraParametroIFGarantidor req = new RequisicaoServicoRegistraParametroIFGarantidor();
	  ResultadoServicoRegistraParametroIFGarantidor res = (ResultadoServicoRegistraParametroIFGarantidor)servico.executarServico(req,dados);
	  
	  Notificacao notificacao = null;
	  notificacao = new Notificacao("PamametroIFGarantidor.Sucesso");
	
		return notificacao;
	 }
  
  	public boolean ciencia(Grupo arg0, Servicos arg1) throws Exception {
		return false;
	}

	public void ciencia(GrupoDeGrupos arg0, Grupo arg1, Servicos arg2)
			throws Exception {
	}
   
}
