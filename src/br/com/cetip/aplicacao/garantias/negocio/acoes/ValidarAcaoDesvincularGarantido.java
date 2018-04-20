package br.com.cetip.aplicacao.garantias.negocio.acoes;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

public class ValidarAcaoDesvincularGarantido extends AbstractValidarAcao {

   public Funcao getAcao() {
      return ICestaDeGarantias.DESVINCULAR_GARANTIDO;
   }

   public boolean validarAcao(Funcao tipoAcesso, CestaGarantiasDO cesta) {
      boolean cestaVinculada = cesta.getStatusCesta().equals(StatusCestaDO.VINCULADA);

      if (!cestaVinculada) {
         throw new Erro(CodigoErro.ERRO, "Cesta deve estar vinculada");
      }

      return cestaVinculada;
   }

}
