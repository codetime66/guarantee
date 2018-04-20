package br.com.cetip.aplicacao.garantias.web.colateral;

import java.util.Iterator;

import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoConsultaParametroIFGarantidor;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;


public class RelacaoManutencaoParamIFGarantidor extends Relacao {

	private Grupo dadosIFGarantidores;
	

/*
    * (non-Javadoc)
    *
    * @see
    * br.com.cetip.base.web.acao.Relacao#informarColunas(br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarColunas(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {	  
	  atributos.atributo(new Funcao(Contexto.GARANTIAS_PARAM_IF_GARANTIDOR));
	  atributos.atributo(new CodigoSistema(Contexto.GARANTIAS_SISTEMA));
      atributos.atributo(new CodigoTipoIF(Contexto.GARANTIAS_TIPO_IF));
      atributos.atributo(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#chamarServico(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    * br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {

	   return servicos.chamarServico(new RequisicaoServicoConsultaParametroIFGarantidor(), dados);
      }


   /*
    * (non-Javadoc)
    *
    * @see
    * br.com.cetip.base.web.acao.Relacao#informarParametros(br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarParametros(GrupoDeAtributos parametros, Grupo dados, Servicos servicos) throws Exception {
	   parametros.atributo(new Funcao(Contexto.GARANTIAS_PARAM_IF_GARANTIDOR));
	   parametros.atributo(new CodigoSistema(Contexto.GARANTIAS_SISTEMA));
	   parametros.atributo(new CodigoTipoIF(Contexto.GARANTIAS_TIPO_IF));
	   parametros.atributo(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
	   parametros.atributo(new Id(Contexto.GARANTIAS_PARAM_IF_GARANTIDOR));
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#obterDestino(br.com.cetip.infra.atributo.Atributo,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public Class obterDestino(Atributo atributo, Grupo dados, Servicos servicos) throws Exception {
	   
	      Iterator itFuncao = dados.iterator(new Funcao(Contexto.GARANTIAS_PARAM_IF_GARANTIDOR));
          Booleano ehExclusao = Booleano.FALSO;
          dadosIFGarantidores = dados;
          
	      while (itFuncao.hasNext())  {
	         Funcao funcao = (Funcao) itFuncao.next();
	         if (funcao.ehEXCLUIR()){
	             ehExclusao = Booleano.VERDADEIRO;
	         }
	      }
	  if (ehExclusao.ehVerdadeiro()){
         return FormularioExclusaoParamIFGarantidor.class;
	  } else{
		  return null;
	  }
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#informarLinks(br.com.cetip.base.web.acao.suporte.Links,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarLinks(Links links, Grupo linha, Servicos servicos) throws Exception {
	   links.colunaEditavel(new Funcao(Contexto.GARANTIAS_PARAM_IF_GARANTIDOR));
   }

   public Grupo getDadosIFGarantidores() {
		return dadosIFGarantidores;
	}

	public void setDadosIFGarantidores(Grupo dadosIFGarantidores) {
		this.dadosIFGarantidores = dadosIFGarantidores;
	}

}