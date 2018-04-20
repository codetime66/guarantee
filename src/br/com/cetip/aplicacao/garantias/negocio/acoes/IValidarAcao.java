package br.com.cetip.aplicacao.garantias.negocio.acoes;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

public interface IValidarAcao {

   public boolean validarAcao(Funcao tipoAcesso, CestaGarantiasDO cesta);

   public Funcao getAcao();

}
