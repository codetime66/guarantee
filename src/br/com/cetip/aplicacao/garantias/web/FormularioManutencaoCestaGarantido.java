package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

/**
 * Manutencao de Cestas - Visao do Garantido
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class FormularioManutencaoCestaGarantido extends FormularioManutencaoCesta {

   public FormularioManutencaoCestaGarantido() {
      alterarDestino(ICestaDeGarantias.APORTAR_GARANTIAS, FormularioAporteGarantiasCestaGarantido.class);
   }

   protected Funcao[] obterAcoesTela() {
      return new Funcao[] { ICestaDeGarantias.LIBERAR_CESTA, ICestaDeGarantias.RETIRAR_GARANTIAS,
            ICestaDeGarantias.LIBERAR_GARANTIAS, ICestaDeGarantias.LIBERAR_GARANTIAS_PARCIAL,
            ICestaDeGarantias.APORTAR_GARANTIAS, ICestaDeGarantias.LIBERAR_PENHOR_EMISSOR };
   }

   protected Funcao obterNomeFuncaoTela() {
      return ICestaDeGarantias.FUNCAO_GARANTIDO;
   }

}