package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IAtualizaStatusCesta;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;

class AtualizaStatusCesta extends BaseGarantias implements IAtualizaStatusCesta {

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IAtualizaStatusCesta#atualizaStatus(br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO, br.com.cetip.dados.aplicacao.garantias.StatusCestaDO)
    */
   public void atualizaStatus(CestaGarantiasDO cesta, StatusCestaDO status) {
      cesta.setStatusCesta(status);
      cesta.setDatAlteracaoStatusCesta(getDataHoje());
   }

}
