package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;

/**
 * <p>
 * Tela de registro de Itens da Cesta de Garantias
 * </p>
 */
public class FormularioCadastroItensSequenciaCestaGarantias extends FormularioCadastroItensCestaGarantias {

   public Class obterDestino(Grupo dados, Servicos servicos) throws Exception {
      Class destino;

      if (ehIncluirGarantia() || ehExcluirGarantia()) {
         destino = FormularioCadastroItensCestaGarantias.class;
      } else {
         destino = super.obterDestino(dados, servicos);
      }

      return destino;
   }

}
