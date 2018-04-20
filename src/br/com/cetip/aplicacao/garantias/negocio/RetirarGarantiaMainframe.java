package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IRetirarGarantia;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;

public class RetirarGarantiaMainframe extends RetirarGarantiaCetip21 implements
		IRetirarGarantia {
	
	public void registrar(TiposRetiradaGarantia tipos) {
		  tipos.registrar(SistemaDO.MOP, this);
	      tipos.registrar(SistemaDO.SNA, this);
	      tipos.registrar(SistemaDO.SND, this);
	}

}
