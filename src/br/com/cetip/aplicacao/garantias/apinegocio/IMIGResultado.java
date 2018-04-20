/**
 * 
 */
package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

/**
 * @author marco sergio
 * 
 */
public interface IMIGResultado {

   public void resultadoOperacao(MovimentacaoGarantiaDO movimentacao, IdStatusMovimentacaoGarantia idStatusMovGarantia,
         Texto txtOperacao);

}