package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

/**
 * Consulta de Cestas de Garantias
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class FormularioManutencaoConsultaCestas extends FormularioManutencaoCesta {

   public FormularioManutencaoConsultaCestas() {
      alterarDestino(BRANCO, RelacaoConsultaCestasGarantias.class);
   }

   protected Funcao[] obterAcoesTela() {
      return new Funcao[] { ICestaDeGarantias.CONSULTAR_GARANTIAS, ICestaDeGarantias.CONSULTAR_HISTORICO };
   }

   protected Funcao obterNomeFuncaoTela() {
      return null;
   }

}