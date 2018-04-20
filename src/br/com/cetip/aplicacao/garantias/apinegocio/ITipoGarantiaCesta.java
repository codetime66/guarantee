package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;

public interface ITipoGarantiaCesta {

   public IdTipoGarantia obterTipoGarantia(CestaGarantiasDO cesta);

}
