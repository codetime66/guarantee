package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoConsultarAutorizacoes;
import br.com.cetip.aplicacao.garantias.web.RelacaoConsultaGarantiasDeCesta;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIFContrato;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSituacaoAutorizacaoGarantias;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;

public class RelacaoAutorizacaoPublicaGarantias extends Relacao {

   public GrupoDeAtributos chamarServico(Grupo g, Servicos s) throws Exception {
      RequisicaoServicoConsultarAutorizacoes req = new RequisicaoServicoConsultarAutorizacoes();
      return new GrupoDeAtributos(s.executarServico(req, g).obterAtributosColunados());
   }

   public void informarColunas(GrupoDeAtributos gda, Grupo g, Servicos s) throws Exception {
      gda.atributo(new CodigoContaCetip(Contexto.CONTA_GARANTIDOR_MANUT_AUTORIZ));
      gda.atributo(new CPFOuCNPJ(Contexto.CPF_CNPJ_GARANTIDOR_MANUT_AUTORIZ));
      gda.atributo(new CodigoContaCetip(Contexto.CONTA_GARANTIDO_MANUT_AUTORIZ));
      gda.atributo(new CPFOuCNPJ(Contexto.CPF_CNPJ_GARANTIDO_MANUT_AUTORIZ));
      gda.atributo(new CodigoIFContrato(Contexto.CONTRATO));
      gda.atributo(new Id(Contexto.GARANTIAS_CESTA));
      gda.atributo(new CodigoSituacaoAutorizacaoGarantias(Contexto.SITUACAO));
   }

   public void informarLinks(Links l, Grupo g, Servicos s) throws Exception {
      CodigoSituacaoAutorizacaoGarantias situacao = (CodigoSituacaoAutorizacaoGarantias) g.obterAtributo(
            CodigoSituacaoAutorizacaoGarantias.class, Contexto.SITUACAO);

      if (situacao.mesmoConteudo(CodigoSituacaoAutorizacaoGarantias.ATIVO)) {
         l.funcaoDoLinkDestaLinha(new Funcao(Contexto.ACAO));
         l.funcaoDoLinkDestaLinha(new Funcao(Contexto.ACAO, "EXCLUIR"));
      }

      l.link(new Id(Contexto.GARANTIAS_CESTA));
   }

   public void informarParametros(GrupoDeAtributos gda, Grupo g, Servicos s) throws Exception {
      informarColunas(gda, g, s);
      gda.atributo(new Id(Contexto.AUTORIZACAO_PUBLICIDADE_GARANTIAS));
   }

   public Class obterDestino(Atributo a, Grupo g, Servicos s) throws Exception {
      if (a instanceof Id) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Click NumeroCestaGarantia(" + a.obterContexto() + ") = " + a);
         }

         return RelacaoConsultaGarantiasDeCesta.class;
      }

      if (Condicional.vazio(g.obterAtributo(Funcao.class, Contexto.ACAO))) {
         throw new Erro(CodigoErro.ACAO_NAO_INDICADA);
      }

      return FormularioExcluirAutorizacaoPublicidade.class;
   }

}
