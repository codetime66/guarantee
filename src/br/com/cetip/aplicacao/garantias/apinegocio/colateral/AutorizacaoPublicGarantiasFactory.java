package br.com.cetip.aplicacao.garantias.apinegocio.colateral;

import br.com.cetip.aplicacao.garantias.apinegocio.FactoryGenerica;

public class AutorizacaoPublicGarantiasFactory extends FactoryGenerica {

   AutorizacaoPublicGarantiasFactory() {
      super("br.com.cetip.aplicacao.garantias.negocio.colateral.AutorizacaoPublicGarantias");
   }

   public static final IAutorizacaoPublicGarantias getInstance() {
      return (IAutorizacaoPublicGarantias) new AutorizacaoPublicGarantiasFactory().newInstance();
   }

}
