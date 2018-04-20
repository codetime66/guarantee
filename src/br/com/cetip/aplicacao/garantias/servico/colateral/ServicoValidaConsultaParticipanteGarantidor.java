package br.com.cetip.aplicacao.garantias.servico.colateral;

import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.texto.NomeRegra;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="NomeSimplificado"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="NOME_SIMPLIFICADO_GARANTIDO"
 * 
 * @resultado.class
 */
public class ServicoValidaConsultaParticipanteGarantidor implements Servico {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cetip.infra.servico.interfaces.Servico#executar(br.com.cetip.infra
	 * .servico.interfaces.Requisicao)
	 */
	public Resultado executar(Requisicao arg0) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cetip.infra.servico.interfaces.Servico#executarConsulta(br.com
	 * .cetip.infra.servico.interfaces.Requisicao)
	 */
	public Resultado executarConsulta(Requisicao requisicao) throws Exception {
		RequisicaoServicoValidaConsultaParticipanteGarantidor req = (RequisicaoServicoValidaConsultaParticipanteGarantidor) requisicao;
		ResultadoServicoValidaConsultaParticipanteGarantidor res = new ResultadoServicoValidaConsultaParticipanteGarantidor();

		NomeSimplificado nomeGarantidor = req.obterNOME_SIMPLIFICADO_GARANTIDO_NomeSimplificado();

		NomeRegra predicado = ConstantesDeNomeDeRegras.validaConsultaParticipanteGarantidor;

		AtributosColunados termos = new AtributosColunados();

		termos.atributo(nomeGarantidor);

		if (Logger.estaHabilitadoDebug(this)) {
			Logger.debug(this, "### ServicoValidaConsultaParticipanteGarantidor : AtributosColunados (termos) : ");
			Logger.debug(this, termos.toString());
		}

		FabricaDeMotorDeRegra.getMotorDeRegra().avalia(predicado, termos);

		return res;
	}

}
