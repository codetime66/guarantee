package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;

public interface IConsultaGarantias {

   public boolean cestaContemGarantia(CestaGarantiasDO cesta, InstrumentoFinanceiroDO garantia);

   public boolean existeGarantiasAltaPlataforma(CestaGarantiasDO cesta);

}
