package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

/**
 * <p>
 * Tela de registro de Itens da Cesta de Garantias
 * </p>
 */
public class FormularioAporteGarantiasCestaGarantidor extends FormularioAporteGarantias {

   /**
    * @param dados  
    */
   protected Funcao obterTipoAcesso(Grupo dados) {
      return ICestaDeGarantias.FUNCAO_GARANTIDOR;
   }

}
