package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;

public interface IValidaAtivoGarantidor {

   public void validar(CestaGarantiasDO cesta, InstrumentoFinanceiroDO garantidor);

}
