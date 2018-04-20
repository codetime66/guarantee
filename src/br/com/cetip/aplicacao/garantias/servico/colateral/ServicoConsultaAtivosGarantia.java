package br.com.cetip.aplicacao.garantias.servico.colateral;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.colateral.ConsultaConsolidadaGarantiaFactory;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoMarcacaoMercado;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.ValorBase;
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
 * @requisicao.method atributo="Natureza"
 *                    pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="NATUREZA_GARANTIDO"
 * 
 * @requisicao.method atributo="CodigoSistema"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="SISTEMA_GARANTIDOR"
 * 
 * @requisicao.method atributo="CodigoTipoMarcacaoMercado"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="COLATERAL_TIPO_MARCACAO_MERCADO"
 * 
 * @requisicao.method atributo="IdTipoGarantia"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="TIPO_GARANTIA"
 * 
 * @requisicao.method atributo="CodigoTipoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="TIPO_IF_GARANTIDO2"
 * 
 * @requisicao.method atributo="CodigoTipoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="TIPO_IF_GARANTIDOR2"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoTipoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_ATIVO"
 * 
 * @resultado.method atributo="Quantidade"
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="COLATERAL_QUANTIDADE_CESTAS_GARANTIAS"
 * 
 * @resultado.method atributo="Quantidade"
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="COLATERAL_SOMATORIO_CESTAS_GARANTIAS"
 * 
 * @resultado.method atributo="ValorMonetario"
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="COLATERAL_SOMATORIO_CESTAS_GARANTIAS_VINCULADA"
 * 
 */

public class ServicoConsultaAtivosGarantia implements Servico {

	public Resultado executar(Requisicao arg0) throws Exception {
		return null;
	}

	public Resultado executarConsulta(Requisicao requisicao) throws Exception {

		RequisicaoServicoConsultaAtivosGarantia req = (RequisicaoServicoConsultaAtivosGarantia) requisicao;
		ResultadoServicoConsultaAtivosGarantia res = new ResultadoServicoConsultaAtivosGarantia();

		NomeSimplificado participante = req.obterNOME_SIMPLIFICADO_GARANTIDO_NomeSimplificado();
		Natureza natureza = req.obterNATUREZA_GARANTIDO_Natureza();
		IdTipoGarantia tipoGarantia = req.obterTIPO_GARANTIA_IdTipoGarantia();
		CodigoTipoMarcacaoMercado codigoTipoMarcacaoMercado = req.obterCOLATERAL_TIPO_MARCACAO_MERCADO_CodigoTipoMarcacaoMercado();
		CodigoSistema codigoSistema = req.obterSISTEMA_GARANTIDOR_CodigoSistema();
		CodigoTipoIF codigoTipoIFGarantido = req.obterTIPO_IF_GARANTIDO2_CodigoTipoIF();
		CodigoTipoIF codigoTipoIFGarantidor = req.obterTIPO_IF_GARANTIDOR2_CodigoTipoIF();

		List listaAtivosGarantia = ConsultaConsolidadaGarantiaFactory.getInstance().obterAtivosEmGarantia(participante, natureza, codigoSistema, codigoTipoMarcacaoMercado, tipoGarantia,
				codigoTipoIFGarantido, codigoTipoIFGarantidor);

		if (listaAtivosGarantia != null) {
			Iterator itTodasCestas = listaAtivosGarantia.iterator();
			while (itTodasCestas.hasNext()) {
				Object[] objAtivosGarantia = (Object[]) itTodasCestas.next();

				CodigoTipoIF codigoTipoIF = new CodigoTipoIF(objAtivosGarantia[0].toString());
				Quantidade qtdCestas = new Quantidade(objAtivosGarantia[1].toString());
				qtdCestas.atribuirTamanho(10, 0);
				Quantidade valTotalCestas = objAtivosGarantia[2]!= null? new Quantidade(objAtivosGarantia[2].toString()):new Quantidade("0,00");
				valTotalCestas.atribuirTamanho(10, 2);
				
				res.atribuirTIPO_ATIVO_CodigoTipoIF(codigoTipoIF);
				res.atribuirCOLATERAL_QUANTIDADE_CESTAS_GARANTIAS_Quantidade(qtdCestas);
				res.atribuirCOLATERAL_SOMATORIO_CESTAS_GARANTIAS_Quantidade(valTotalCestas);
				
				List listaAtivosGarantiaVinculada = ConsultaConsolidadaGarantiaFactory.getInstance().obterAtivosEmGarantiaVinculada(participante, natureza, codigoSistema, codigoTipoMarcacaoMercado, tipoGarantia,
						codigoTipoIFGarantido, codigoTipoIF);
				
				ValorMonetario valTotalCestasVinculada =  new ValorMonetario("0");
				if (listaAtivosGarantiaVinculada.size() > 0) {
					Object[] objAtivosGarantiaVinculada = (Object[]) listaAtivosGarantiaVinculada.get(0);
					valTotalCestasVinculada.atribuirTamanho(10, 2);
					valTotalCestasVinculada =  objAtivosGarantiaVinculada[2]!= null? (ValorMonetario) objAtivosGarantiaVinculada[2]:new ValorMonetario("0,00");
				}
				res.atribuirCOLATERAL_SOMATORIO_CESTAS_GARANTIAS_VINCULADA_ValorMonetario(valTotalCestasVinculada);
			}
		}
		return res;
	}
}
