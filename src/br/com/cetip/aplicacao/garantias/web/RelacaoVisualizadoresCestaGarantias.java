package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoConsultaVisualizadores;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;

public class RelacaoVisualizadoresCestaGarantias extends Relacao {

   public GrupoDeAtributos chamarServico(Grupo parametros, Servicos servicos) throws Exception {
      return servicos.chamarServico(new RequisicaoServicoConsultaVisualizadores(), parametros);
   }

   public void informarColunas(GrupoDeAtributos colunas, Grupo dados, Servicos servicos) throws Exception {
      colunas.atributo(new CodigoContaCetip(Contexto.GARANTIA));
      colunas.atributo(new NomeSimplificado(Contexto.GARANTIA));
      colunas.atributoOculto(new NumeroCestaGarantia(Contexto.GARANTIA));
   }

   public void informarLinks(Links links, Grupo atributos, Servicos servicos) throws Exception {
      links.funcaoDoLinkDeQualquerLinha(new Funcao(Contexto.FUNCAO, " "));
      links.funcaoDoLinkDeQualquerLinha(new Funcao(Contexto.FUNCAO, "EXCLUIR"));
      links.exibirFuncao(true);
   }

   public void informarParametros(GrupoDeAtributos parametros, Grupo dados, Servicos servicos) throws Exception {
      parametros.atributo(new CodigoContaCetip(Contexto.GARANTIA));
      parametros.atributo(new NomeSimplificado(Contexto.GARANTIA));
      parametros.atributo(new NumeroCestaGarantia(Contexto.GARANTIA));
   }

   public Class obterDestino(Atributo coluna, Grupo grupo, Servicos servicos) throws Exception {
      //Funcao funcao = (Funcao) grupo.obterAtributo(Funcao.class, Contexto.FUNCAO);
      //CodigoContaCetip visualizador = (CodigoContaCetip) grupo.obterAtributo(CodigoContaCetip.class, Contexto.GARANTIA);

      return FormularioExclusaoVisualizador.class;
   }

}
