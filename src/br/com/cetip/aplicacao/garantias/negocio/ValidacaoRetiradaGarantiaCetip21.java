package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoRetiradaGarantia;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.utilitario.Condicional;

class ValidacaoRetiradaGarantiaCetip21 extends BaseGarantias implements IValidacaoRetiradaGarantia {

   public void validaRetirada(Id idCesta, Id idGarantia, Quantidade quantidade, NumeroOperacao numeroOperacao) {
      if (!Condicional.vazio(numeroOperacao)) {
         throw new Erro(CodigoErro.CESTA_OPERACAO_RETIRADA_INVALIDA);
      }

   }

}
