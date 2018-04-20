package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

/**
 * Manutencao de Cesta - Visao do Garantidor
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class FormularioManutencaoCestaGarantidor extends FormularioManutencaoCesta {

   public FormularioManutencaoCestaGarantidor() {
      alterarDestino(ICestaDeGarantias.APORTAR_GARANTIAS, FormularioAporteGarantiasCestaGarantidor.class);
   }

   protected Funcao[] obterAcoesTela() {
      return new Funcao[] { ICestaDeGarantias.FINALIZAR_CESTA, ICestaDeGarantias.ALTERAR_CESTA,
            ICestaDeGarantias.EXCLUIR_CESTA, ICestaDeGarantias.INCLUIR_GARANTIAS, ICestaDeGarantias.EXCLUIR_GARANTIAS,
            ICestaDeGarantias.APORTAR_GARANTIAS, ICestaDeGarantias.ALTERAR_NUMERO_OPERACAO,
            ICestaDeGarantias.RETIRAR_GARANTIAS, ICestaDeGarantias.DESVINCULAR_GARANTIDO };
   }

   protected Funcao obterNomeFuncaoTela() {
      return ICestaDeGarantias.FUNCAO_GARANTIDOR;
   }

}