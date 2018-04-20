package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoValidaConsultaAtivosGarantia;
import br.com.cetip.base.web.acao.Filtro;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoMarcacaoMercado;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;

public class ConsultaConsolidadaAtivosGarantia extends Filtro {

	public void informarCampos(GrupoDeAtributos gda, Grupo grupo, Servicos servico) throws Exception {

		RequisicaoServicoObterCombosTipoInstrumentoFinanceiro req = new RequisicaoServicoObterCombosTipoInstrumentoFinanceiro();
		ResultadoServicoObterCombosTipoInstrumentoFinanceiro res = (ResultadoServicoObterCombosTipoInstrumentoFinanceiro) servico.executarServico(req);
		
		gda.atributo(new NomeSimplificado(Contexto.NOME_SIMPLIFICADO_GARANTIDO));
		gda.atributo(new Natureza(Contexto.NATUREZA_GARANTIDO));
		gda.atributo(res.obterSISTEMA_GARANTIDOR_CodigoSistema());
		gda.atributo(CodigoTipoMarcacaoMercado.obtemTipoGarantidoGarantidor());
		gda.atributo(getTipoGarantia());
		gda.atributo(CodigoTipoIF.obterTipoIFGarantido());
		gda.atributo(CodigoTipoIF.obterTipoIFGarantidor());
;

	}

	public Class obterDestino(Grupo g, Servicos s) throws Exception {
		return RelacaoConsultaConsolidadaAtivosGarantia.class;
	}

	public void validar(Grupo dados, Servicos servicos) throws Exception {
		RequisicaoServicoValidaConsultaAtivosGarantia requisicao = new RequisicaoServicoValidaConsultaAtivosGarantia();
		servicos.chamarServico(requisicao, dados);
	}

	private IdTipoGarantia getTipoGarantia() {
		IdTipoGarantia tipoGarantia = new IdTipoGarantia(Contexto.TIPO_GARANTIA);
		tipoGarantia.getDomain().clear();
		tipoGarantia.getDomain().add(new IdTipoGarantia("Cessao Fiduciaria", IdTipoGarantia.CESSAO_FIDUCIARIA.toString()));
		tipoGarantia.getDomain().add(new IdTipoGarantia("Penhor no Emissor", IdTipoGarantia.PENHOR_NO_EMISSOR.toString()));

		return tipoGarantia;
	}

}