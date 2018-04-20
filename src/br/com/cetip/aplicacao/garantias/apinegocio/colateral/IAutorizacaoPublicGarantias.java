package br.com.cetip.aplicacao.garantias.apinegocio.colateral;

import java.util.List;

import br.com.cetip.dados.aplicacao.garantias.AutorizacaoPublicGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.TipoParametroPublicGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ParametroPontaDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;

public interface IAutorizacaoPublicGarantias {

   public void incluirAutorizacaoPublicGarantias(ParametroPontaDO paramPonta);

   public AutorizacaoPublicGarantiasDO obterAutorizacaoPublicGarantias(ParametroPontaDO paramPonta);

   public TipoParametroPublicGarantiasDO obterParametroPublicAtivo();

   public List obterAutorizacoesInativasPorContrato(CodigoIF codigoContrato);

   public List obterAutorizacaoPendentePorContrato(CodigoIF codigoContrato);

}
