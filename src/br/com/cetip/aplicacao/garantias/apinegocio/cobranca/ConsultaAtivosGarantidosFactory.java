package br.com.cetip.aplicacao.garantias.apinegocio.cobranca;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiasFactory;

public final class ConsultaAtivosGarantidosFactory {

   private ConsultaAtivosGarantidosFactory() {
   }

   public static IConsultaCobrancaAtivosGarantidos getInstanceTitulos() {
      return GarantiasFactory.getInstance().getInstanceConsultaCobrancaTitulos();
   }

   public static IConsultaCobrancaAtivosGarantidos getInstanceContratos() {
      return GarantiasFactory.getInstance().getInstanceConsultaCobrancaContratos();
   }

}
