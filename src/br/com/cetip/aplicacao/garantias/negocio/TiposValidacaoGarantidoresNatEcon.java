package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoGarantidorNatEcon;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

final class TiposValidacaoGarantidoresNatEcon {

   public TiposValidacaoGarantidoresNatEcon() {
      new ValidacaoGarantidoresNatEconDI().registrar(this);
   }

   private Map mapa = new HashMap();

   public void registrar(CodigoTipoIF tipoIF, IValidacaoGarantidorNatEcon instance) {
      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      mapa.put(key, instance);
   }

   public IValidacaoGarantidorNatEcon obter(CodigoTipoIF tipoIF) {
      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      Object o = mapa.get(key);
      if (o == null) {
         o = mapa.get(null);
      }

      return (IValidacaoGarantidorNatEcon) o;
   }

}
