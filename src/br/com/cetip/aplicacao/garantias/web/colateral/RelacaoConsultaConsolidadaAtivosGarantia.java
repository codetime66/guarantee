package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoConsultaAtivosGarantia;
import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoConsultaPosicaoIFGarantia;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoMarcacaoMercado;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.ValorBase;
import br.com.cetip.infra.atributo.tipo.numero.ValorMonetario;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;

public class RelacaoConsultaConsolidadaAtivosGarantia extends Relacao {

	public RelacaoConsultaConsolidadaAtivosGarantia() {
		super(30);
	}

	public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {
		GrupoDeAtributos atributos = servicos.chamarServico(new RequisicaoServicoConsultaAtivosGarantia(), dados);

		if (atributos.obterAtributosColunados().obterNumeroDeAtributos() == 0) {
			throw new Erro(CodigoErro.SEM_RESULTADO_PARA_FILTRO);
		}
		return atributos;
	}

	public void informarColunas(GrupoDeAtributos colunas, Grupo dados, Servicos servicos) throws Exception {
		colunas.atributo(new CodigoTipoIF(Contexto.TIPO_ATIVO));
		colunas.atributo(new Quantidade(Contexto.COLATERAL_QUANTIDADE_CESTAS_GARANTIAS));
		colunas.atributo(new Quantidade(Contexto.COLATERAL_SOMATORIO_CESTAS_GARANTIAS));
		colunas.atributo(new ValorMonetario(Contexto.COLATERAL_SOMATORIO_CESTAS_GARANTIAS_VINCULADA));
	}

	public void informarLinks(Links arg0, Grupo arg1, Servicos arg2) throws Exception {
	}

	public void informarParametros(GrupoDeAtributos parametros, Grupo dados, Servicos servicos) throws Exception {
		parametros.atributo(new NomeSimplificado(Contexto.NOME_SIMPLIFICADO_GARANTIDO));
		parametros.atributo(new Natureza(Contexto.NATUREZA_GARANTIDO));
		parametros.atributo(new CodigoSistema(Contexto.GARANTIAS_SISTEMA));
		parametros.atributo(new CodigoTipoMarcacaoMercado(Contexto.COLATERAL_TIPO_MARCACAO_MERCADO));
		parametros.atributo(new IdTipoGarantia(Contexto.TIPO_GARANTIA));
		parametros.atributo(new CodigoTipoIF(Contexto.TIPO_IF_GARANTIDO2));
		parametros.atributo(new CodigoTipoIF(Contexto.TIPO_IF_GARANTIDOR2));
	}

	public Class obterDestino(Atributo arg0, Grupo arg1, Servicos arg2) throws Exception {
		return null;
	}

	public boolean retornarPorArquivo(Grupo grupo, Servicos servicos) {
		ConsultaPosicaoIFGarantia filtro;

		try {
			filtro = (ConsultaPosicaoIFGarantia) obterTela(ConsultaPosicaoIFGarantia.class);
		} catch (Exception e) {
			Logger.info(this, e);
			throw new Erro(CodigoErro.ERRO);
		}

		NumeroInteiro qtdeLinhas = filtro.getQtdeLinhas();
		return (qtdeLinhas.obterInt() > 5000);
	}

	public String obterNomeDoArquivo() {
		return "MMG-CONSULTA-CONSOLIDADA-PARTICIPANTE-GARANTIDORES";
	}

	public Requisicao obterRequisicaoParaRetornoEmArquivo(Grupo grupo, Servicos servicos) {
		RequisicaoServicoConsultaPosicaoIFGarantia requisicao = new RequisicaoServicoConsultaPosicaoIFGarantia();
		CodigoContaCetip codConta = (CodigoContaCetip) grupo.obterAtributo(CodigoContaCetip.class, Contexto.MALOTE);
		requisicao.atribuirMALOTE_CodigoContaCetip(codConta); // obrigatorio
		Data data = (Data) grupo.obterAtributo(Data.class, Contexto.CONSULTA);

		if (!Condicional.vazio(data)) {
			requisicao.atribuirCONSULTA_Data(data);
		}

		return requisicao;
	}

}