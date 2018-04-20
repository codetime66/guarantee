package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.IEventoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacaoSelic;

final class TiposEventoSelic {

   private Map mapa = new HashMap();

   public TiposEventoSelic() {
      new EventoSelic().registrar(this);
      new EventoSelicResgate().registrar(this);
   }

   public void registrar(CodigoTipoOperacaoSelic tipoOperacao, IEventoSelic instance) {
      mapa.put(tipoOperacao, instance);
   }

   public IEventoSelic obter(CodigoTipoOperacaoSelic tipoOperacao) {
      Object o = mapa.get(tipoOperacao);
      if (o == null) {
         o = mapa.get(null);
      }
      return (IEventoSelic) o;
   }
}
