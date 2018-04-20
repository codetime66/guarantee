package br.com.cetip.aplicacao.garantias.apinegocio;

public class DetalheGarantiaFactory extends FactoryGenerica {

   protected DetalheGarantiaFactory() {
      super("br.com.cetip.aplicacao.garantias.negocio.DetalheGarantia");
   }

   public static final IDetalheGarantia getInstance() {
      return (IDetalheGarantia) new DetalheGarantiaFactory().newInstance();
   }

}
