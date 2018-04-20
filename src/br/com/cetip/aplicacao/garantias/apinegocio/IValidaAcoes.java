package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

public interface IValidaAcoes {

   public void validaAcao(Funcao acao, Funcao tipo, CestaGarantiasDO cesta);

   public boolean podeExecutarAcao(Funcao acao, Funcao tipo, CestaGarantiasDO cesta);

}
