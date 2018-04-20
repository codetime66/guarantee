package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IRespostaTransferenciaCustodia;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.tipo.identificador.SituacaoOperacaoSelic;

public class RespostaTransferenciaCustodiaExpirada extends BaseGarantias 
	implements IRespostaTransferenciaCustodia {

	public void processar(MovimentacaoGarantiaDO movExterna){
		IGarantiasSelic gSelic = getFactory().getInstanceGarantiasSelic();
		gSelic.registrarRespostaLancamentoTransferenciaCustodiaExpirada(movExterna);
	}
	
	public void registrar(TiposRespostaTransferenciaCustodia tipos) {
	      tipos.registrar(SituacaoOperacaoSelic.EXPIRADO, this);
	}

}
