package br.com.cetip.aplicacao.garantias.apinegocio;

import java.util.List;

public interface IValidacaoRetirada {

   public void validaRetirada(List idGarantias, List qtdadesARetirar, List numerosOperacao);

}
