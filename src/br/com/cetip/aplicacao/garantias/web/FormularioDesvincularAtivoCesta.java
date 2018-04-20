package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoRegistraDesvinculacaoCestaGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;

public class FormularioDesvincularAtivoCesta extends AbstractFormularioGarantias {

   private CodigoIF codIF;
   private NumeroCestaGarantia numCesta;

   public Notificacao chamarServico(Grupo grupo, Servicos servicos) throws Exception {
      RequisicaoServicoRegistraDesvinculacaoCestaGarantias r;
      r = new RequisicaoServicoRegistraDesvinculacaoCestaGarantias();

      r.atribuirCESTA_GARANTIA_Id(numCesta.copiarParaId());
      r.atribuirINSTRUMENTO_FINANCEIRO_CodigoIF(codIF);
      r.atribuirGARANTIAS_TIPO_ACESSO_Funcao(ICestaDeGarantias.FUNCAO_GARANTIDOR);
      r.atribuirSOMENTE_VALIDACAO_Booleano(new Booleano(Booleano.FALSO));

      servicos.executarServico(r);

      Notificacao notificacao = new Notificacao("FormularioDesvincularAtivoCesta.Sucesso");
      notificacao.parametroMensagem(codIF, 0);
      notificacao.parametroMensagem(numCesta, 1);
      return notificacao;
   }

   public void confirmacao(GrupoDeGrupos layout, Grupo grupo, Servicos arg2) throws Exception {
      layout.contexto(Contexto.CESTA_GARANTIA);
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeAtributos atts = principal.grupoDeAtributos(1);

      numCesta = (NumeroCestaGarantia) grupo.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);
      codIF = (CodigoIF) grupo.obterAtributo(CodigoIF.class, Contexto.PARTICIPANTE);

      atts.atributoNaoEditavel(numCesta);
      atts.atributoNaoEditavel(codIF);
   }

   public boolean confirmacao(Grupo arg0, Servicos arg1) throws Exception {
      return true;
   }

   public void validar(Grupo grupo, Servicos servicos) throws Exception {
      RequisicaoServicoRegistraDesvinculacaoCestaGarantias r;
      r = new RequisicaoServicoRegistraDesvinculacaoCestaGarantias();

      r.atribuirCESTA_GARANTIA_Id(numCesta.copiarParaId());
      r.atribuirINSTRUMENTO_FINANCEIRO_CodigoIF(codIF);
      r.atribuirGARANTIAS_TIPO_ACESSO_Funcao(ICestaDeGarantias.FUNCAO_GARANTIDOR);
      r.atribuirSOMENTE_VALIDACAO_Booleano(new Booleano(Booleano.VERDADEIRO));

      servicos.executarServico(r);
   }

}
