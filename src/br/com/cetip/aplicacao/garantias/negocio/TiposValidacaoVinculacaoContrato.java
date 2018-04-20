package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoVinculacaoContrato;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

class TiposValidacaoVinculacaoContrato {

   public TiposValidacaoVinculacaoContrato() {
      new ValidacaoVinculacaoSWAP().registrar(this);
      new ValidacaoVinculacaoTermos().registrar(this);
      new ValidacaoVinculacaoOpcoes().registrar(this);
   }

   private Map mapa = new HashMap();

   public void registrar(CodigoTipoIF tipoIF, IValidacaoVinculacaoContrato instance) {
      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      mapa.put(key, instance);
   }

   public IValidacaoVinculacaoContrato obter(CodigoTipoIF tipoIF) {
      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      Object o = mapa.get(key);
      if (o == null) {
         o = mapa.get(null);
      }

      return (IValidacaoVinculacaoContrato) o;
   }

}