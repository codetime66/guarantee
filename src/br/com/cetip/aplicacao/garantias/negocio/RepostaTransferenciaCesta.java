package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IRespostaTransferenciaCustodia;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.tipo.identificador.SituacaoOperacaoSelic;

public class RepostaTransferenciaCesta extends BaseGarantias 
	implements IRespostaTransferenciaCustodia {

	public void processar(MovimentacaoGarantiaDO movExterna){
		boolean lancamentoValidoTransferencia = lancamentoValidoTransferencia(movExterna);
		if (lancamentoValidoTransferencia) {
			IGarantiasSelic gSelic = getFactory().getInstanceGarantiasSelic();
			gSelic.registrarRespostaLancamentoTransferenciaCustodiaCreditoGarantido(movExterna);
		}
	}
	
	public void registrar(TiposRespostaTransferenciaCustodia tipos) {
		  //Transferencia eh iniciada pelo lancamento do Garantidor comandado pela cetip  
		  //em seguida SELIC envia notificado atraves da SEL1021R1 com status LAN 
		  //(informando que GARANTIDOR) efetuou o lancamento 
		  //o que faz com que a cetip efetue o lancamento pelo Garantido
	      tipos.registrar(SituacaoOperacaoSelic.LANCAMENTO_GARANTIDOR, this);
	}
	
	private boolean lancamentoValidoTransferencia(MovimentacaoGarantiaDO mov) {
      
      return (mov != null && temMovimentacaoTransferenciaPendente(mov.getCestaGarantias()));
	}

	private boolean temMovimentacaoTransferenciaPendente(CestaGarantiasDO cesta) {
		return getFactory().getInstanceMovimentacoesGarantias().obterUltimaMovimentacao(cesta,
	            TipoMovimentacaoGarantiaDO.TRANSFERENCIA, StatusMovimentacaoGarantiaDO.PENDENTE) != null;
	}

}
