package br.com.cetip.aplicacao.garantias.web.agro;

import br.com.cetip.aplicacao.garantias.servico.agro.RequisicaoServicoConsultaIFGarantidor;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.expressao.TipoHabilitacao;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.tempo.DataHora;
import br.com.cetip.infra.atributo.tipo.texto.NomeUsuario;
import br.com.cetip.infra.atributo.utilitario.Condicional;


public class RelacaoIFGarantidorHabilitado extends Relacao {

	private Grupo dadosIFGarantidores;
	

/*
    * (non-Javadoc)
    *
    * @see
    * br.com.cetip.base.web.acao.Relacao#informarColunas(br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarColunas(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {	  
	  
      atributos.atributo(new CodigoTipoIF(Contexto.GARANTIAS_CESTA));
      atributos.atributo(new CodigoIF(Contexto.GARANTIAS_CESTA));
      atributos.atributo(new TipoHabilitacao(Contexto.GARANTIAS_CESTA));
      atributos.atributo(new DataHora(Contexto.GARANTIAS_CESTA));
      atributos.atributo(new NomeUsuario(Contexto.GARANTIAS_CESTA));
      atributos.atributo(new Booleano(Contexto.GARANTIAS_CESTA));
      
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#chamarServico(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    * br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {
	   return servicos.chamarServico(new RequisicaoServicoConsultaIFGarantidor(), dados);
      }


   /*
    * (non-Javadoc)
    *
    * @see
    * br.com.cetip.base.web.acao.Relacao#informarParametros(br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarParametros(GrupoDeAtributos parametros, Grupo dados, Servicos servicos) throws Exception {
	   parametros.atributo(new Funcao(Contexto.GARANTIAS_CESTA));	   
	   parametros.atributo(new CodigoTipoIF(Contexto.GARANTIAS_CESTA));
	   parametros.atributo(new CodigoIF(Contexto.GARANTIAS_CESTA));
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#obterDestino(br.com.cetip.infra.atributo.Atributo,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public Class obterDestino(Atributo atributo, Grupo dados, Servicos servicos) throws Exception {
	   
	   Class retClass = null;
	   
	   Funcao funcao = (Funcao) dados.obterAtributo(Funcao.class, Contexto.GARANTIAS_CESTA);
	   
	   if (funcao.ehATIVAR()){
		   retClass = FormularioAtivaIFGarantidor.class; 
	   }else if (funcao.ehDESATIVAR()){
		   retClass = FormularioDesativarIFGarantidor.class;
	   }else if (funcao.ehEXCLUIR()){
		   retClass = FormularioExcluirIFGarantidor.class;
	   }
	   
	   return retClass; 
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#informarLinks(br.com.cetip.base.web.acao.suporte.Links,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarLinks(Links links, Grupo linha, Servicos servicos) throws Exception {
	   
	   Booleano indAtivo = (Booleano) linha.obterAtributo(Booleano.class, Contexto.GARANTIAS_CESTA);
   	
       links.funcaoDoLinkDestaLinha(new Funcao(Contexto.GARANTIAS_CESTA));            
       
       if (!Condicional.vazio(indAtivo)){
	       if (indAtivo.ehVerdadeiro()){
	    	   links.funcaoDoLinkDestaLinha(new Funcao(Contexto.GARANTIAS_CESTA, Funcao.DESATIVAR ));  
	       }else if (indAtivo.ehFalso()){
	    	   links.funcaoDoLinkDestaLinha(new Funcao(Contexto.GARANTIAS_CESTA, Funcao.ATIVAR ));
	       }
	       links.funcaoDoLinkDestaLinha(new Funcao(Contexto.GARANTIAS_CESTA, Funcao.EXCLUIR));
       }else {
    	   links.funcaoDoLinkDestaLinha(new Funcao(Contexto.GARANTIAS_CESTA, "---------"));
       }   
       
       links.exibirFuncao(true);
   }

   public Grupo getDadosIFGarantidores() {
		return dadosIFGarantidores;
	}

	public void setDadosIFGarantidores(Grupo dadosIFGarantidores) {
		this.dadosIFGarantidores = dadosIFGarantidores;
	}

}