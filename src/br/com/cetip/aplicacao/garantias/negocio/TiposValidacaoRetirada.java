package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoRetirada;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

final class TiposValidacaoRetirada {

   public TiposValidacaoRetirada() {
      new ValidacaoRetiradaSTA().registrar(this);
      new ValidacaoRetiradaGarantidores().registrar(this);
   }

   private Map mapa = new HashMap();

   public void registrar(CodigoTipoIF tipoIF, IValidacaoRetirada instance) {
      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      mapa.put(key, instance);
   }

   public IValidacaoRetirada obter(CodigoTipoIF tipoIF) {
      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      Object o = mapa.get(key);
      if (o == null) {
         o = mapa.get(null);
      }

      return (IValidacaoRetirada) o;
   }

}
