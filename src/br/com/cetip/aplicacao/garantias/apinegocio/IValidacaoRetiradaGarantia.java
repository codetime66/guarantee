package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;

public interface IValidacaoRetiradaGarantia {

   public void validaRetirada(Id idCesta, Id ativoGarantia, Quantidade quantidade, NumeroOperacao numeroOperacao);

}
