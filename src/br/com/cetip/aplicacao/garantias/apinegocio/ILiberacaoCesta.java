package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

/**
 * Registra operacao de liberacao-894 de garantia.
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public interface ILiberacaoCesta {

   /**
    * 
    * @param cesta
    *           de garantias a ser liberada
    * @param data 
    * @param movimentacao
    *           de liberacao previamente cadastrada
    */
   public void liberar(CestaGarantiasDO cesta, Data data);

}
