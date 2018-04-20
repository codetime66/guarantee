package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.base.web.acao.Filtro;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;

public class ConsultaVisualizadores extends Filtro {

   public void informarCampos(GrupoDeAtributos atributos, Grupo parametros, Servicos servicos) throws Exception {
      atributos.atributoObrigatorio(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));
   }

   public Class obterDestino(Grupo arg0, Servicos arg1) throws Exception {
      return RelacaoVisualizadoresCestaGarantias.class;
   }

   public void validar(Grupo arg0, Servicos arg1) throws Exception {
   }

}
