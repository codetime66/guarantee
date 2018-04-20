package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;

public interface IRespostaTransferenciaCustodia {
	
	public void processar(MovimentacaoGarantiaDO mov);

}
