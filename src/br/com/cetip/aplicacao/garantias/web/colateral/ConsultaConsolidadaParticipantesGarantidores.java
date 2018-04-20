package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoValidaConsultaParticipanteGarantidor;
import br.com.cetip.base.web.acao.Filtro;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;

public class ConsultaConsolidadaParticipantesGarantidores extends Filtro {

	public void informarCampos(GrupoDeAtributos gda, Grupo parametros, Servicos servico) throws Exception {

		RequisicaoServicoObterCombosTipoInstrumentoFinanceiro req = new RequisicaoServicoObterCombosTipoInstrumentoFinanceiro();
		ResultadoServicoObterCombosTipoInstrumentoFinanceiro res = (ResultadoServicoObterCombosTipoInstrumentoFinanceiro) servico.executarServico(req);
		
		gda.atributo(new NomeSimplificado(Contexto.NOME_SIMPLIFICADO_GARANTIDO));
		gda.atributo(res.obterSISTEMA_GARANTIDOR_CodigoSistema());
		gda.atributo(getTipoGarantia());
		gda.atributo(CodigoTipoIF.obterTipoIFGarantidor());

	}

	public void validar(Grupo dados, Servicos servicos) throws Exception {
		RequisicaoServicoValidaConsultaParticipanteGarantidor requisicao = new RequisicaoServicoValidaConsultaParticipanteGarantidor();
		servicos.chamarServico(requisicao, dados);
	}

	public Class obterDestino(Grupo dados, Servicos servicos) throws Exception {
		return RelacaoConsultaConsolidadaParticipantesGarantidores.class;
	}

	private IdTipoGarantia getTipoGarantia() {
		IdTipoGarantia tipoGarantia = new IdTipoGarantia(Contexto.TIPO_GARANTIA);
		tipoGarantia.getDomain().clear();
		tipoGarantia.getDomain().add(new IdTipoGarantia("Cessao Fiduciaria", IdTipoGarantia.CESSAO_FIDUCIARIA.toString()));
		tipoGarantia.getDomain().add(new IdTipoGarantia("Penhor no Emissor", IdTipoGarantia.PENHOR_NO_EMISSOR.toString()));

		return tipoGarantia;
	}
}
