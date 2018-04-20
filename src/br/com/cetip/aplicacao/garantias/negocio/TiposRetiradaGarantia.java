package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.IRetirarGarantia;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.tipo.identificador.Id;

final class TiposRetiradaGarantia {

   public TiposRetiradaGarantia() {
      new RetirarGarantiaSelic().registrar(this);
      new RetirarGarantiaCetip21().registrar(this);
      new RetirarGarantiaMainframe().registrar(this);
      new RetirarGarantiaNaoCetipada().registrar(this);
   }

   private Map mapa = new HashMap();

   public void registrar(Id sistema, IRetirarGarantia retirarGarantia) {
      mapa.put(sistema, retirarGarantia);
   }

   public IRetirarGarantia obter(Id sistema) {
      Object o = mapa.get(sistema);
      if (o == null) {
         o = mapa.get(null);
      }
      /* Tratamento para implementacao DEFAULT como CETIP21 */ 
      if (o == null) {
          o = mapa.get(SistemaDO.CETIP21);
      }
      return (IRetirarGarantia) o;
   }

}
