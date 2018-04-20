package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;

/**
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public interface IVincularTitulo {

   /**
    * Valida a requisicao da vinculacao de cesta
    * 
    * @param req
    */
   public void vincularTitulo(MovimentacaoGarantiaDO movVinculacao, CestaGarantiasIFDO vinculo);

}
