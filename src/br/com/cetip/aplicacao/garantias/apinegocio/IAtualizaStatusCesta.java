package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;

public interface IAtualizaStatusCesta {

   public void atualizaStatus(CestaGarantiasDO cesta, StatusCestaDO status);

}