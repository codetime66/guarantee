package br.com.cetip.aplicacao.garantias.apinegocio;

/**
 * <p>
 * F�brica usada para expor MIGAcionador
 * <p>
 */
public class MIGAcionadorFactory {

   public static IMIGAcionador getInstance() {
      return GarantiasFactory.getInstance().getInstanceMIGAcionador();
   }
}
