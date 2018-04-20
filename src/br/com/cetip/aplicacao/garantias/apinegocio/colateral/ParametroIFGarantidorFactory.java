package br.com.cetip.aplicacao.garantias.apinegocio.colateral;

import br.com.cetip.aplicacao.garantias.apinegocio.FactoryGenerica;

public class ParametroIFGarantidorFactory extends FactoryGenerica {

   ParametroIFGarantidorFactory() {
      super("br.com.cetip.aplicacao.garantias.negocio.colateral.ParametroIFGarantidor");
   }

   public static final IParametroIFGarantidor getInstance() {
      return (IParametroIFGarantidor) new ParametroIFGarantidorFactory().newInstance();
   }

}
