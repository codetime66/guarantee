package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;

public interface IGarantiasCDAWA {

   public boolean encontrarCDA(CodigoIF codigoIF, CestaGarantiasDO cesta);

   public boolean encontrarWA(CodigoIF codigoIF, CestaGarantiasDO cesta);

}