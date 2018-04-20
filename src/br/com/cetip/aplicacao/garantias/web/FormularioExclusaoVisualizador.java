package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoExcluiVisualizadorCesta;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidarExclusaoVisualizador;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.CodigoNotificacao;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;

public class FormularioExclusaoVisualizador extends AbstractFormularioGarantias {

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      servicos.executarServico(new RequisicaoServicoExcluiVisualizadorCesta(), dados);
      return new Notificacao(CodigoNotificacao.VISUALIZADOR_CESTA_EXCLUIDA_COM_SUCESSO);
   }

   public boolean confirmacao(Grupo arg0, Servicos arg1) throws Exception {
      return true;
   }

   public void confirmacao(GrupoDeGrupos layout, Grupo grupo, Servicos servicos) throws Exception {
      GrupoDeAtributos dados = layout.grupoDeAtributos(1);
      dados.posicaoTitulos(GrupoDeAtributos.TITULOS_ACIMA);

      NumeroCestaGarantia cesta = (NumeroCestaGarantia) grupo.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIA);
      CodigoContaCetip conta = (CodigoContaCetip) grupo.obterAtributo(CodigoContaCetip.class, Contexto.GARANTIA);
      NomeSimplificado nome = (NomeSimplificado) grupo.obterAtributo(NomeSimplificado.class, Contexto.GARANTIA);

      dados.atributoNaoEditavel(cesta);
      dados.atributoNaoEditavel(conta);
      dados.atributoNaoEditavel(nome);
   }

   public void validar(Grupo grupo, Servicos servicos) throws Exception {
      RequisicaoServicoValidarExclusaoVisualizador req = new RequisicaoServicoValidarExclusaoVisualizador();

      req.atribuirGARANTIA_NumeroCestaGarantia((NumeroCestaGarantia) grupo.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIA));
      req.atribuirGARANTIA_CodigoContaCetip((CodigoContaCetip) grupo.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIA));

      servicos.chamarServico(req);
   }

}
