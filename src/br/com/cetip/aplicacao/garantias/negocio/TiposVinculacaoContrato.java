package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.IVincularContrato;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

class TiposVinculacaoContrato {

   public TiposVinculacaoContrato() {
      new VincularContratoSwap().registrar(this);
      new VincularContratoOpcao().registrar(this);
      new VincularContratoTermo().registrar(this);
   }

   private Map mapa = new HashMap();

   public void registrar(CodigoTipoIF tipoIF, IVincularContrato instance) {
      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      mapa.put(key, instance);
   }

   public IVincularContrato obter(CodigoTipoIF tipoIF) {
      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      Object o = mapa.get(key);
      if (o == null) {
         o = mapa.get(null);
      }

      return (IVincularContrato) o;
   }

}