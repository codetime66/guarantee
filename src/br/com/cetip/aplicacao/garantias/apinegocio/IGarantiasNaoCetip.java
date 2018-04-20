package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;

public interface IGarantiasNaoCetip {

   public boolean cestaPossuiAtivosNaoCetipados(CestaGarantiasDO cesta);

   public boolean possuiSomenteNaoCetipados(CestaGarantiasDO cesta);

}
