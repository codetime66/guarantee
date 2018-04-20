package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

public interface IDeletaCesta {

   public void deletaCestaGarantias(CestaGarantiasDO cesta, Booleano indBatch, Data dataOperacao);

}
