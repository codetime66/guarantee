package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.base.web.acao.Formulario;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;

public abstract class AbstractFormularioGarantias extends Formulario {

   public Notificacao chamarServico(Grupo arg0, Servicos arg1) throws Exception {
      return null;
   }

   public void entrada(GrupoDeGrupos arg0, Grupo arg1, Servicos arg2) throws Exception {
   }

   public boolean ciencia(Grupo arg0, Servicos arg1) throws Exception {
      return false;
   }

   public void ciencia(GrupoDeGrupos arg0, Grupo arg1, Servicos arg2) throws Exception {
   }

   public boolean confirmacao(Grupo arg0, Servicos arg1) throws Exception {
      return false;
   }

   public void confirmacao(GrupoDeGrupos arg0, Grupo arg1, Servicos arg2) throws Exception {
   }

   public Class obterDestino(Grupo arg0, Servicos arg1) throws Exception {
      return null;
   }

   public void validar(Grupo arg0, Servicos arg1) throws Exception {
   }

}
