package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoValidarConsultaAutorizacoes;
import br.com.cetip.base.web.acao.Filtro;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIFContrato;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSituacaoAutorizacaoGarantias;
import br.com.cetip.infra.data.element.DataElement.Domain;

public class FiltroManutAutorizPublicGarantias extends Filtro {

   public void informarCampos(GrupoDeAtributos gda, Grupo g, Servicos s) throws Exception {
      // PARTE
      gda.atributo(new CodigoContaCetip(Contexto.CONTA_GARANTIDOR_MANUT_AUTORIZ));
      gda.atributo(new CPFOuCNPJ(Contexto.CPF_CNPJ_GARANTIDOR_MANUT_AUTORIZ));

      // CONTRA PARTE
      gda.atributo(new CodigoContaCetip(Contexto.CONTA_GARANTIDO_MANUT_AUTORIZ));
      gda.atributo(new CPFOuCNPJ(Contexto.CPF_CNPJ_GARANTIDO_MANUT_AUTORIZ));

      // CONTRATO
      gda.atributo(new CodigoIFContrato(Contexto.CONTRATO));

      // SITUACAO
      final CodigoSituacaoAutorizacaoGarantias situacoesAutorizacao = getSituacoes();
      gda.atributoObrigatorio(situacoesAutorizacao);
   }

   private CodigoSituacaoAutorizacaoGarantias getSituacoes() {
      final CodigoSituacaoAutorizacaoGarantias situacoesAutorizacao = new CodigoSituacaoAutorizacaoGarantias(
            Contexto.SITUACAO);
      final Domain domain = situacoesAutorizacao.getDomain();
      domain.add(new CodigoSituacaoAutorizacaoGarantias(""));
      domain.add(CodigoSituacaoAutorizacaoGarantias.ATIVO);
      domain.add(CodigoSituacaoAutorizacaoGarantias.PENDENTE_CONFIRMACAO_GARANTIDOR);
      domain.add(CodigoSituacaoAutorizacaoGarantias.PENDENTE_CONFIRMACAO_GARANTIDO);
      domain.add(CodigoSituacaoAutorizacaoGarantias.PENDENTE_CONFIRMACAO_DESAUTORIZACAO_GARANTIDOR);
      domain.add(CodigoSituacaoAutorizacaoGarantias.PENDENTE_CONFIRMACAO_DESAUTORIZACAO_GARANTIDO);
      return situacoesAutorizacao;
   }

   public Class obterDestino(Grupo g, Servicos s) throws Exception {
      return RelacaoAutorizacaoPublicaGarantias.class;
   }

   public void validar(Grupo g, Servicos s) throws Exception {
      RequisicaoServicoValidarConsultaAutorizacoes req = new RequisicaoServicoValidarConsultaAutorizacoes();
      s.executarServico(req, g);
   }

}
