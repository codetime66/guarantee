package br.com.cetip.aplicacao.garantias.apinegocio;

public final class GarantiasFactory {

   private static FactoryGenerica factory = new FactoryGenerica("br.com.cetip.aplicacao.garantias.negocio.Garantias");

   public static final IGarantias getInstance() {
      return (IGarantias) factory.newInstance();
   }

   private GarantiasFactory() {
   }

}
