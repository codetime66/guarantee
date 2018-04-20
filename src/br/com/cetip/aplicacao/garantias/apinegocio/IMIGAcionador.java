/**
 * 
 */
package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

/**
 * @author marco sergio
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public interface IMIGAcionador {

   /**
     * Aciona a operacao e valida a grade CTP11 somente quando indBatch for FALSO.
     * 
     * @param idCesta
     * @param idMovimentacao
     * @param contaGarantidor
     * @param conta60Garantido
     * @param idTipoMovimentacao
     * @param codigoSistema
     * @param idNumIF
     * @param quantidade
     * @param idTipoGarantia
     * @param indDireitoGarantidor
     * @param indBatch
     */
   public void acionarOperacao(MovimentacaoGarantiaDO movimentacao, Booleano indBatch);

   /**
    * Marca a cesta vinculada ao ativo como inadimplente
    * 
    * @param ativo
    */
   public void acionarInadimplencia(InstrumentoFinanceiroDO ativo);

   /**
    * Marca a referida cesta como Inadimplente
    * 
    * @param numero
    */
   public void acionarInadimplencia(CestaGarantiasDO cesta);

   /**
    * Registra um desbloqueio ja finalizado, da movimentacao indicada
    * 
    * @param idMovimentacao de desbloqueio com status OK
    * 
    * @author <a href="bruno.borges@summa-tech.com">Bruno Borges</a>
    */
   public void acionarDesbloqueioFinalizado(MovimentacaoGarantiaDO movimentacao);

   /**
    * Registra uma retirada finalizada, da movimentacao indicada
    * 
    * @param idMovimentacao
    */
   public void acionarRetiradaFinalizada(MovimentacaoGarantiaDO movimentacao);

   /**
    * 
    * @param dataOperacao
    */
   public void setDataOperacao(Data dataOperacao);

   public void acionarDesvinculacaoAtivo(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ativo);

}
