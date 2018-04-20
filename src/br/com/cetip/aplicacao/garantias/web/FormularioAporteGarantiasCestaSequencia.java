package br.com.cetip.aplicacao.garantias.web;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

/**
 * <p>
 * Tela de registro de Itens da Cesta de Garantias
 * </p>
 */
public class FormularioAporteGarantiasCestaSequencia extends FormularioAporteGarantias {

   private Map mapaTelas = new HashMap();

   public FormularioAporteGarantiasCestaSequencia() {
      mapaTelas.put(ICestaDeGarantias.FUNCAO_GARANTIDO, FormularioAporteGarantiasCestaGarantido.class);
      mapaTelas.put(ICestaDeGarantias.FUNCAO_GARANTIDOR, FormularioAporteGarantiasCestaGarantidor.class);
   }

   protected Funcao obterTipoAcesso(Grupo dados) {
      return (Funcao) dados.obterAtributo(Funcao.class, Contexto.GARANTIAS_TIPO_ACESSO);
   }

   public Class obterDestino(Grupo dados, Servicos servicos) throws Exception {
      if (ehAportarGarantia()) {
         return (Class) mapaTelas.get(dados.obterAtributo(Funcao.class, Contexto.GARANTIAS_TIPO_ACESSO));
      }

      return super.obterDestino(dados, servicos);
   }
}
