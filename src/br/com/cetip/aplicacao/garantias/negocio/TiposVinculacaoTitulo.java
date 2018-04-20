package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.IVincularTitulo;
import br.com.cetip.infra.atributo.tipo.identificador.Id;

class TiposVinculacaoTitulo {

   public TiposVinculacaoTitulo() {
      new VincularTituloCETIP21().registrar(this);
      new VincularTituloSNA().registrar(this);
   }

   private Map mapa = new HashMap();

   public void registrar(Id id, IVincularTitulo vincularTitulo) {
      Object key = null;
      if (id != null) {
         key = id.obterConteudo();
      }

      mapa.put(key, vincularTitulo);
   }

   public IVincularTitulo obter(Id id) {
      Object key = null;
      if (id != null) {
         key = id.obterConteudo();
      }

      Object o = mapa.get(key);
      if (o == null) {
         o = mapa.get(null);
      }

      return (IVincularTitulo) o;
   }

}