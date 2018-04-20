package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

abstract class FormularioAporteGarantias extends FormularioCadastroItensCestaGarantias {

   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servico) throws Exception {
      super.entrada(layout, dados, servico);
      GrupoDeAtributos ga = layout.grupoDeAtributos(1);
      ga.atributoOculto(obterTipoAcesso(dados));
   }

   abstract protected Funcao obterTipoAcesso(Grupo dados);

}
