package br.com.cetip.aplicacao.garantias.apinegocio;

/**
 * <p>
 * Fábrica usada para expor MIGAcionador
 * <p>
 */
public class MIGResultadoFactory {

   public static IMIGResultado getInstance() {
      return GarantiasFactory.getInstance().getInstanceMIGResultado();
   }
}
