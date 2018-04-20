package br.com.cetip.aplicacao.garantias.servico.colateral;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.colateral.ConsultaConsolidadaGarantiaFactory;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.ValorBase;
import br.com.cetip.infra.atributo.tipo.numero.ValorMonetario;
import br.com.cetip.infra.atributo.tipo.numero.ValorMonetario;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="NomeSimplificado"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="NOME_SIMPLIFICADO_GARANTIDO"
 * 
 * @requisicao.method atributo="CodigoSistema"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="SISTEMA_GARANTIDOR"
 * 
 * @requisicao.method atributo="IdTipoGarantia"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="TIPO_GARANTIA"
 * 
 * @requisicao.method atributo="CodigoTipoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="TIPO_IF_GARANTIDOR2"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="NomeSimplificado"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="NOME_SIMPLIFICADO_GARANTIDO"
 * 
 * @resultado.method atributo="Quantidade"
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="COLATERAL_QUANTIDADE_CESTAS_GARANTIAS"
 * 
 * @resultado.method atributo="ValorMonetario"
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="COLATERAL_SOMATORIO_CESTAS_GARANTIAS"
 * 
 * @resultado.method atributo="ValorBase"
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="COLATERAL_SOMATORIO_CESTAS_GARANTIAS_VINCULADA"
 * 
 * 
 * 
 */
public class ServicoConsultaParticipanteGarantidor implements Servico {

	public Resultado executar(Requisicao arg0) throws Exception {
		return null;
	}

	public Resultado executarConsulta(Requisicao requisicao) throws Exception {

		RequisicaoServicoConsultaParticipanteGarantidor req = (RequisicaoServicoConsultaParticipanteGarantidor) requisicao;
		ResultadoServicoConsultaParticipanteGarantidor res = new ResultadoServicoConsultaParticipanteGarantidor();

		NomeSimplificado participante = req.obterNOME_SIMPLIFICADO_GARANTIDO_NomeSimplificado();
		CodigoSistema modulo = req.obterSISTEMA_GARANTIDOR_CodigoSistema();
		IdTipoGarantia tipoGarantia = req.obterTIPO_GARANTIA_IdTipoGarantia();
		CodigoTipoIF codigoTipoIF = req.obterTIPO_IF_GARANTIDOR2_CodigoTipoIF();

		List listaTodasCestasConsolidadaParticipanteGarantidores = ConsultaConsolidadaGarantiaFactory.getInstance().obterCestasParticipantesGarantidores(participante, modulo, tipoGarantia,
				codigoTipoIF);

		if (listaTodasCestasConsolidadaParticipanteGarantidores != null) {
			Iterator itTodasCestas = listaTodasCestasConsolidadaParticipanteGarantidores.iterator();
			while (itTodasCestas.hasNext()) {
				Object[] objTodasCestas = (Object[]) itTodasCestas.next();

				NomeSimplificado nomeSimplificado = new NomeSimplificado(objTodasCestas[0].toString());
				Quantidade qtdCestas = new Quantidade(objTodasCestas[1].toString());
				qtdCestas.atribuirTamanho(10, 0);
				ValorMonetario valTotalCestas = objTodasCestas[2]!= null? new ValorMonetario(objTodasCestas[2].toString()):new ValorMonetario("0,00");
				valTotalCestas.atribuirTamanho(10, 2);
				
				res.atribuirNOME_SIMPLIFICADO_GARANTIDO_NomeSimplificado(nomeSimplificado);
				res.atribuirCOLATERAL_QUANTIDADE_CESTAS_GARANTIAS_Quantidade(qtdCestas);
				res.atribuirCOLATERAL_SOMATORIO_CESTAS_GARANTIAS_ValorMonetario(valTotalCestas);
				
				List listaTodasCestasVinculadasConsolidadaParticipanteGarantidores = ConsultaConsolidadaGarantiaFactory.getInstance().obterCestasVinculadasParticipantesGarantidores(nomeSimplificado, modulo, tipoGarantia,
						codigoTipoIF);
				
				ValorBase valTotalCestasVinculada =  new ValorBase("0");
				if (listaTodasCestasVinculadasConsolidadaParticipanteGarantidores.size() > 0) {
					valTotalCestasVinculada.atribuirTamanho(10, 2);
					valTotalCestasVinculada =  listaTodasCestasVinculadasConsolidadaParticipanteGarantidores.get(0)!= null? (ValorBase) listaTodasCestasVinculadasConsolidadaParticipanteGarantidores.get(0):new ValorBase("0,00");
				}
				res.atribuirCOLATERAL_SOMATORIO_CESTAS_GARANTIAS_VINCULADA_ValorBase(valTotalCestasVinculada);
			}
		}
		return res;
	}
}
