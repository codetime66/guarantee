package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoConsultaParticipanteGarantidor;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.ValorBase;
import br.com.cetip.infra.atributo.tipo.numero.ValorMonetario;

public class RelacaoConsultaConsolidadaParticipantesGarantidores extends Relacao {

	public RelacaoConsultaConsolidadaParticipantesGarantidores() {
		super(30);
	}

	public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {
		GrupoDeAtributos atributos = servicos.chamarServico(new RequisicaoServicoConsultaParticipanteGarantidor(), dados);

		if (atributos.obterAtributosColunados().obterNumeroDeAtributos() == 0) {
			throw new Erro(CodigoErro.SEM_RESULTADO_PARA_FILTRO);
		}
		return atributos;
		
	}

	public void informarColunas(GrupoDeAtributos colunas, Grupo dados, Servicos servicos) throws Exception {
		colunas.atributo(new NomeSimplificado(Contexto.NOME_SIMPLIFICADO_GARANTIDO));
		colunas.atributo(new Quantidade(Contexto.COLATERAL_QUANTIDADE_CESTAS_GARANTIAS));
		colunas.atributo(new ValorMonetario(Contexto.COLATERAL_SOMATORIO_CESTAS_GARANTIAS));
		colunas.atributo(new ValorBase(Contexto.COLATERAL_SOMATORIO_CESTAS_GARANTIAS_VINCULADA));

	}

	public void informarLinks(Links arg0, Grupo arg1, Servicos arg2) throws Exception {
		// TODO Auto-generated method stub

	}

	public void informarParametros(GrupoDeAtributos parametros, Grupo dados, Servicos servicos) throws Exception {
		parametros.atributo(new NomeSimplificado(Contexto.NOME_SIMPLIFICADO_GARANTIDO));
		parametros.atributo(new CodigoSistema(Contexto.GARANTIAS_SISTEMA));
		parametros.atributo(new IdTipoGarantia(Contexto.TIPO_GARANTIA));
		parametros.atributo(new CodigoTipoIF(Contexto.TIPO_IF_GARANTIDOR2));


	}

	public Class obterDestino(Atributo arg0, Grupo arg1, Servicos arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
