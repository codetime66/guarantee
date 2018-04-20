package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

public interface ILiberarCestasInadimplentes {

   public void liberarCestasInadimplentes(Data dataProcesso);

   public void liberarCestaInadimplente(Id id, Data data);

}