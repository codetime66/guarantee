package br.com.cetip.aplicacao.garantias.apinegocio.agro;

import br.com.cetip.aplicacao.garantias.apinegocio.FactoryGenerica;

public class IFGarantidorFactory extends FactoryGenerica {

   IFGarantidorFactory() {
      super("br.com.cetip.aplicacao.garantias.negocio.agro.IFGarantidor");
   }

   public static final IIFGarantidor getInstance() {
      return (IIFGarantidor) new IFGarantidorFactory().newInstance();
   }

}
