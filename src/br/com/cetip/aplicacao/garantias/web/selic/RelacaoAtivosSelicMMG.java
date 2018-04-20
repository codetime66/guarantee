package br.com.cetip.aplicacao.garantias.web.selic;

import br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoConsultaAtivosSelicMMG;
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
import br.com.cetip.infra.log.Logger;

public class RelacaoAtivosSelicMMG extends Relacao {

   public static final Funcao EDITAR = new Funcao(Contexto.OPERACAO, "EDITAR");
   public static final Funcao REMOVER = new Funcao(Contexto.OPERACAO, "REMOVER");
   public static final Funcao ADICIONAR = new Funcao(Contexto.OPERACAO, "ADICIONAR");

   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoConsultaAtivosSelicMMG req = new RequisicaoServicoConsultaAtivosSelicMMG();
      GrupoDeAtributos gda = servicos.chamarServico(req, dados);
      return gda;
   }

   public void informarColunas(GrupoDeAtributos colunas, Grupo arg1, Servicos arg2) throws Exception {
      colunas.atributo(new CodigoTipoIF(Contexto.CODIGO_TIPO_IF));
      colunas.atributo(new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO));
      colunas.atributo(new CodigoSistema(Contexto.SISTEMA));
      colunas.atributo(new Booleano(Contexto.ATIVO_GARANTIA));
      colunas.atributo(new Id(Contexto.TIPO_COLATERAL));
      colunas.atributo(new CodigoTipoIF(Contexto.GARANTIAS_CODIGO_TIPO));
   }

   public void informarLinks(Links links, Grupo atributos, Servicos arg2) throws Exception {
      //links.coluna(new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO));

      links.funcaoDoLinkDestaLinha(new Funcao(Contexto.OPERACAO));
      Booleano indAtivo = (Booleano) atributos.obterAtributo(Booleano.class, Contexto.ATIVO_GARANTIA);
      if (indAtivo != null && !indAtivo.vazio()) {

         links.funcaoDoLinkDestaLinha(EDITAR);
         links.funcaoDoLinkDestaLinha(REMOVER);
      } else {
         links.funcaoDoLinkDestaLinha(ADICIONAR);
      }

      links.exibirFuncao(true);
   }

   public void informarParametros(GrupoDeAtributos parametros, Grupo arg1, Servicos arg2) throws Exception {
      parametros.atributo(new Funcao(Contexto.OPERACAO));
      parametros.atributo(new CodigoTipoIF(Contexto.CODIGO_TIPO_IF));
      parametros.atributo(new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO));
      parametros.atributo(new CodigoSistema(Contexto.SISTEMA));
      parametros.atributo(new Booleano(Contexto.ATIVO_GARANTIA));
      parametros.atributo(new Id(Contexto.TIPO_COLATERAL));
      parametros.atributo(new CodigoTipoIF(Contexto.GARANTIAS_CODIGO_TIPO));

   }

   public Class obterDestino(Atributo coluna, Grupo grupo, Servicos servicos) throws Exception {

      if (coluna == null) {
         Funcao funcao = (Funcao) grupo.obterAtributo(Funcao.class, Contexto.OPERACAO);
         Logger.debug(this, "Função: " + funcao);

         if (funcao.mesmoConteudo(REMOVER)) {
            return servicos.obterDestino("Garantias", "FormularioExclusaoSelicMMG");
         } else if (funcao.mesmoConteudo(EDITAR)) {
            return servicos.obterDestino("Garantias", "FormularioAdicaoEdicaoSelicMMG");
         } else if (funcao.mesmoConteudo(ADICIONAR)) {
            return servicos.obterDestino("Garantias", "FormularioAdicaoEdicaoSelicMMG");
         }
      }

      return null;
   }

}
