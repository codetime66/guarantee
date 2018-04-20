package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.IDeletaCesta;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;

final class TiposDelecaoCesta {

   public TiposDelecaoCesta() {
      new DeletaCestaEmManutencao().registrar(this);
      new DeletaCestaVinculada().registrar(this);
      new DeletaCesta().registrar(this);
      new DeletaCestaVinculacaoIncompleta().registrar(this);
   }

   private Map mapa = new HashMap();

   public void registrar(StatusCestaDO status, IDeletaCesta instance) {
      mapa.put(status, instance);
   }

   public IDeletaCesta obter(StatusCestaDO status) {
      Object o = mapa.get(status);
      if (o == null) {
         o = mapa.get(null);
      }

      return (IDeletaCesta) o;
   }

}
