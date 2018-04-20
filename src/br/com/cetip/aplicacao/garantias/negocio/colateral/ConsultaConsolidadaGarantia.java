package br.com.cetip.aplicacao.garantias.negocio.colateral;

import java.util.ArrayList;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.colateral.IConsultaConsolidadaGarantia;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.PosicaoIFGarantiaVDO;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoMarcacaoMercado;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.GerenciadorPersistenciaFactory;

public class ConsultaConsolidadaGarantia extends BaseGarantias implements IConsultaConsolidadaGarantia {

	private Booleano ehPrimeiroParametro = new Booleano();;
	private StringBuffer hql = new StringBuffer();
	private ArrayList hqlParams = new ArrayList();

	public List obterCestasParticipantesGarantidores(NomeSimplificado participante, CodigoSistema modulo, IdTipoGarantia tipoGarantia, CodigoTipoIF codigoTipoIF) {

		Logger.info("obterTodasCestasParticipantesGarantidores ->");

		List vValores = new ArrayList();

		StringBuffer sql = new StringBuffer();
		sql.append("select dg.cestaGarantias.garantidor.participante.nomSimplificadoEntidade, ");
		sql.append("sum(dg.quantidadeGarantia), ");
		sql.append("sum(dg.instrumentoFinanceiro.valorNominalAtual * dg.quantidadeGarantia) ");
		sql.append("from ").append(DetalheGarantiaDO.class.getName()).append(" as dg ");
		sql.append("where dg.quantidadeGarantia > 0 ");
		// sql.append("and dg.cestaGarantias.tipoGarantia.numIdTipoGarantia in (5,16) ");
		sql.append("and dg.cestaGarantias.datExclusao is null ");
		sql.append("and dg.instrumentoFinanceiro.dataHoraExclusao is null ");
		sql.append("and dg.indDireitosGarantidor = 'S'");
		sql.append("and dg.cestaGarantias.tipoGarantia.numIdTipoGarantia = ?");
		vValores.add(tipoGarantia);

		if (!Condicional.vazio(participante)) {
			sql.append("and dg.cestaGarantias.garantidor.participante.nomSimplificadoEntidade = ? ");
			vValores.add(participante);
		}
		if (!Condicional.vazio(modulo)) {
			sql.append("and dg.instrumentoFinanceiro.sistema.codSistema = ? ");
			vValores.add(modulo);
		}
		if (!Condicional.vazio(codigoTipoIF)) {
			sql.append("and dg.instrumentoFinanceiro.tipoIF.codigoTipoIF = ? ");
			vValores.add(codigoTipoIF);
		}

		sql.append("group by dg.cestaGarantias.garantidor.participante.nomSimplificadoEntidade ");
		sql.append("order by dg.cestaGarantias.garantidor.participante.nomSimplificadoEntidade asc");

		return GerenciadorPersistenciaFactory.getGerenciadorPersistencia().find(sql.toString(), vValores.toArray());
	}

	public List obterCestasVinculadasParticipantesGarantidores(NomeSimplificado participante, CodigoSistema modulo, IdTipoGarantia tipoGarantia, CodigoTipoIF codigoTipoIF) {
		Logger.info("obterTodasCestasVinculadasParticipantesGarantidores ->");

		List vValores = new ArrayList();

		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("sum( trunc(dg.instrumentoFinanceiro.valorNominalAtual * dg.quantidadeGarantia,2)) ");
		sql.append("from ").append(DetalheGarantiaDO.class.getName()).append(" as dg ");
		sql.append("where dg.quantidadeGarantia > 0 ");
		// sql.append("and dg.cestaGarantias.tipoGarantia.numIdTipoGarantia in (5,16) ");
		sql.append("and dg.cestaGarantias.datExclusao is null ");
		sql.append("and dg.instrumentoFinanceiro.dataHoraExclusao is null ");
		sql.append("and dg.cestaGarantias.statusCesta = 14 "); // Vinculada
		sql.append("and dg.indDireitosGarantidor = 'S'");
		sql.append("and dg.cestaGarantias.tipoGarantia.numIdTipoGarantia = ?");
		vValores.add(tipoGarantia);

		if (!Condicional.vazio(participante)) {
			sql.append("and dg.cestaGarantias.garantidor.participante.nomSimplificadoEntidade = ? ");
			vValores.add(participante);
		}
		if (!Condicional.vazio(modulo)) {
			sql.append("and dg.instrumentoFinanceiro.sistema.codSistema = ? ");
			vValores.add(modulo);
		}
		if (!Condicional.vazio(codigoTipoIF)) {
			sql.append("and dg.instrumentoFinanceiro.tipoIF.codigoTipoIF = ? ");
			vValores.add(codigoTipoIF);
		}

		sql.append("group by dg.cestaGarantias.garantidor.participante.nomSimplificadoEntidade");

		return GerenciadorPersistenciaFactory.getGerenciadorPersistencia().find(sql.toString(), vValores.toArray());
	}

	public List obterAtivosEmGarantia(NomeSimplificado participante, Natureza natureza, CodigoSistema codigoSistema, CodigoTipoMarcacaoMercado codigoTipoMarcacaoMercado, IdTipoGarantia tipoGarantia,
			CodigoTipoIF codigoTipoIFGarantido, CodigoTipoIF codigoTipoIFGarantidor) {
		Logger.info("obterAtivosEmGarantia ->");
		return obterAtivosEmGarantia(participante, natureza, codigoSistema, codigoTipoMarcacaoMercado, tipoGarantia, codigoTipoIFGarantido, codigoTipoIFGarantidor, Booleano.FALSO);
	}

	public List obterAtivosEmGarantiaVinculada(NomeSimplificado participante, Natureza natureza, CodigoSistema codigoSistema, CodigoTipoMarcacaoMercado codigoTipoMarcacaoMercado,
			IdTipoGarantia tipoGarantia, CodigoTipoIF codigoTipoIFGarantido, CodigoTipoIF codigoTipoIFGarantidor) {
		Logger.info("obterAtivosEmGarantiaVinculada ->");

		return obterAtivosEmGarantia(participante, natureza, codigoSistema, codigoTipoMarcacaoMercado, tipoGarantia, codigoTipoIFGarantido, codigoTipoIFGarantidor, Booleano.VERDADEIRO);

	}

	private List obterAtivosEmGarantia(NomeSimplificado participante, Natureza natureza, CodigoSistema codigoSistema, CodigoTipoMarcacaoMercado codigoTipoMarcacaoMercado,
			IdTipoGarantia tipoGarantia, CodigoTipoIF codigoTipoIFGarantido, CodigoTipoIF codigoTipoIFGarantidor, Booleano vinculada) {

		hql.append("select pif.codTipoIFGarantidor, ");
		hql.append("sum(pif.qtdGarantidor), ");
		hql.append("sum(pif.valorIFCurva * pif.qtdGarantidor) ");
		hql.append("from ").append(PosicaoIFGarantiaVDO.class.getName()).append(" as pif ");

		ehPrimeiroParametro = Booleano.FALSO;

		if (codigoTipoMarcacaoMercado.mesmoConteudo(codigoTipoMarcacaoMercado.IF_GARANTIDOR)) {
			param(participante, "pif.nomeSimpGarantidor = ?");
		} else {
			param(participante, "pif.nomeSimpGarantido = ?");
		}
		param(natureza, "pif.naturezaGarantidor = ?");
		param(codigoSistema, "pif.moduloGarantidor = ?");
		
		Booleano indDireitosGarantidor;
		if(codigoTipoMarcacaoMercado.mesmoConteudo(CodigoTipoMarcacaoMercado.IF_GARANTIDO)){
			indDireitosGarantidor = Booleano.FALSO;
		}else{
			indDireitosGarantidor = Booleano.VERDADEIRO;
		}
			
		param(indDireitosGarantidor, "pif.indDireitosGarantidor = ?");
		param(tipoGarantia, "pif.tipoGarantia = ?");
		param(codigoTipoIFGarantido, "pif.codTipoIFGarantido = ?");
		param(codigoTipoIFGarantidor, "pif.codTipoIFGarantidor = ?");
		
		if(vinculada.ehVerdadeiro()){
			param(new Id("14"), "pif.statusCesta = ?");
		}
			
		hql.append("group by pif.codTipoIFGarantidor ");
		hql.append("order by pif.codTipoIFGarantidor");
		List listaCestasConsolidada = GerenciadorPersistenciaFactory.getGerenciadorPersistencia().find(hql.toString(), hqlParams.toArray());
		return listaCestasConsolidada;
	}

	private void param(Atributo param, String s) {

		Texto montaQuery = null;

		if (!Condicional.vazio(param)) {

			if (ehPrimeiroParametro.ehFalso()) {
				ehPrimeiroParametro = Booleano.VERDADEIRO;
				montaQuery = new Texto("Where " + s);
			} else {
				montaQuery = new Texto("and " + s);
			}
			hql.append(' ').append(montaQuery);
			hqlParams.add(param);

		}
	}
}
