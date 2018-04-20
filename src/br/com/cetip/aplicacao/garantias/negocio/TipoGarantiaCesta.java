package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.ITipoGarantiaCesta;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoGarantiaDO;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;

class TipoGarantiaCesta extends BaseGarantias implements ITipoGarantiaCesta {

   public IdTipoGarantia obterTipoGarantia(CestaGarantiasDO cesta) {
      if (cesta.getTipoGarantia() != null) {
         return cesta.getTipoGarantia().getNumIdTipoGarantia();
      }

      return TipoGarantiaDO.NAO_INFORMADO;
   }

}
