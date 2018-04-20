package br.com.cetip.aplicacao.garantias.negocio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMapaAcoes;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

final class MapaAcoes implements IMapaAcoes {

   private final Set ACOES_GARANTIDOR = new HashSet(10);

   private final Set ACOES_GARANTIDO = new HashSet(10);

   private final Set ACOES_AMBOS = new HashSet(10);

   private final Set ACOES_CONSULTA = new HashSet(10);

   private final Set ACOES_UNICAS = new HashSet(10);

   private final Map ACOES = new HashMap(10);

   public MapaAcoes() {
      ACOES.put(null, ACOES_AMBOS);
      ACOES.put(ICestaDeGarantias.FUNCAO_GARANTIDO, ACOES_GARANTIDO);
      ACOES.put(ICestaDeGarantias.FUNCAO_GARANTIDOR, ACOES_GARANTIDOR);

      ACOES_GARANTIDOR.add(ICestaDeGarantias.FINALIZAR_CESTA);
      ACOES_GARANTIDOR.add(ICestaDeGarantias.ALTERAR_CESTA);
      ACOES_GARANTIDOR.add(ICestaDeGarantias.EXCLUIR_CESTA);
      ACOES_GARANTIDOR.add(ICestaDeGarantias.INCLUIR_GARANTIAS);
      ACOES_GARANTIDOR.add(ICestaDeGarantias.EXCLUIR_GARANTIAS);
      ACOES_GARANTIDOR.add(ICestaDeGarantias.APORTAR_GARANTIAS);
      ACOES_GARANTIDOR.add(ICestaDeGarantias.ALTERAR_NUMERO_OPERACAO);
      ACOES_GARANTIDOR.add(ICestaDeGarantias.RETIRAR_GARANTIAS);//somente para garantias selic
      ACOES_GARANTIDOR.add(ICestaDeGarantias.DESVINCULAR_GARANTIDO);

      ACOES_GARANTIDO.add(ICestaDeGarantias.LIBERAR_CESTA);
      ACOES_GARANTIDO.add(ICestaDeGarantias.RETIRAR_GARANTIAS);
      ACOES_GARANTIDO.add(ICestaDeGarantias.LIBERAR_GARANTIAS);
      ACOES_GARANTIDO.add(ICestaDeGarantias.LIBERAR_GARANTIAS_PARCIAL);
      ACOES_GARANTIDO.add(ICestaDeGarantias.APORTAR_GARANTIAS);
      ACOES_GARANTIDO.add(ICestaDeGarantias.LIBERAR_PENHOR_EMISSOR);

      ACOES_UNICAS.add(ICestaDeGarantias.ALTERAR_CESTA);
      ACOES_UNICAS.add(ICestaDeGarantias.INCLUIR_GARANTIAS);
      ACOES_UNICAS.add(ICestaDeGarantias.EXCLUIR_GARANTIAS);
      ACOES_UNICAS.add(ICestaDeGarantias.APORTAR_GARANTIAS);
      ACOES_UNICAS.add(ICestaDeGarantias.RETIRAR_GARANTIAS);
      ACOES_UNICAS.add(ICestaDeGarantias.LIBERAR_GARANTIAS_PARCIAL);
      ACOES_UNICAS.add(ICestaDeGarantias.LIBERAR_PENHOR_EMISSOR);
      ACOES_UNICAS.add(ICestaDeGarantias.CONSULTAR_GARANTIAS);
      ACOES_UNICAS.add(ICestaDeGarantias.CONSULTAR_HISTORICO);
      ACOES_UNICAS.add(ICestaDeGarantias.ALTERAR_NUMERO_OPERACAO);
      ACOES_UNICAS.add(ICestaDeGarantias.DESVINCULAR_GARANTIDO);

      ACOES_AMBOS.add(ICestaDeGarantias.CONSULTAR_HISTORICO);
      ACOES_AMBOS.add(ICestaDeGarantias.CONSULTAR_GARANTIAS);

      ACOES_CONSULTA.addAll(ACOES_AMBOS);
   }

   public boolean ehAcaoUnica(Funcao acao) {
      Funcao _acao = new Funcao(Contexto.ACAO, acao.obterConteudo());
      return ACOES_UNICAS.contains(_acao);
   }

   public Collection obterAcoesPara(Funcao tipoAcesso) {
      Collection c = new ArrayList((Collection) ACOES.get(null));

      if (tipoAcesso != null) {
         Funcao _tipoAcesso = new Funcao(Contexto.GARANTIAS_TIPO_ACESSO, tipoAcesso.obterConteudo());
         c.addAll((Collection) ACOES.get(_tipoAcesso));
      }

      return Collections.unmodifiableCollection(c);
   }

   public boolean ehAcaoConsulta(Funcao acao) {
      return ACOES_CONSULTA.contains(acao);
   }

}
