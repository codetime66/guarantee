package br.com.cetip.aplicacao.garantias.apinegocio.colateral;

import java.util.List;

import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoMarcacaoMercado;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;

public interface IConsultaConsolidadaGarantia {

	public List obterCestasParticipantesGarantidores(NomeSimplificado participante, CodigoSistema modulo, IdTipoGarantia tipoGarantia, CodigoTipoIF codigoTipoIF);

	public List obterCestasVinculadasParticipantesGarantidores(NomeSimplificado participante, CodigoSistema modulo, IdTipoGarantia tipoGarantia, CodigoTipoIF codigoTipoIF);
	
	public List obterAtivosEmGarantia(NomeSimplificado participante, Natureza natureza, CodigoSistema codigoSistema, CodigoTipoMarcacaoMercado codigoTipoMarcacaoMercado,	IdTipoGarantia tipoGarantia, CodigoTipoIF codigoTipoIFGarantido, CodigoTipoIF codigoTipoIFGarantidor);

	public List obterAtivosEmGarantiaVinculada(NomeSimplificado participante, Natureza natureza, CodigoSistema codigoSistema, CodigoTipoMarcacaoMercado codigoTipoMarcacaoMercado,
			IdTipoGarantia tipoGarantia, CodigoTipoIF codigoTipoIFGarantido, CodigoTipoIF codigoTipoIF);
	
}
