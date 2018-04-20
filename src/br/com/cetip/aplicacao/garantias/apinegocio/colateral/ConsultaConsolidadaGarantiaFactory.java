package br.com.cetip.aplicacao.garantias.apinegocio.colateral;

public class ConsultaConsolidadaGarantiaFactory {
private ConsultaConsolidadaGarantiaFactory() {}
	
	public static IConsultaConsolidadaGarantia getInstance() throws Exception {
		return (IConsultaConsolidadaGarantia) Class.forName("br.com.cetip.aplicacao.garantias.negocio.colateral.ConsultaConsolidadaGarantia").newInstance();
	}
}
