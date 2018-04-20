package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoVinculacaoTitulo;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

final class TiposValidacaoVinculacaoTitulo {

   public TiposValidacaoVinculacaoTitulo() {
      new ValidacaoVinculacaoCDB().registrar(this);
      new ValidacaoVinculacaoDI().registrar(this);
      new ValidacaoVinculacaoDIM().registrar(this);
      new ValidacaoVinculacaoLCA().registrar(this);
      new ValidacaoVinculacaoSTA().registrar(this);
      new ValidacaoVinculacaoDEB().registrar(this);
   }

   private Map mapa = new HashMap();

   public void registrar(CodigoTipoIF tipoIF, IValidacaoVinculacaoTitulo instance) {
      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      mapa.put(key, instance);
   }

   public IValidacaoVinculacaoTitulo obter(CodigoTipoIF tipoIF) {
      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      Object o = mapa.get(key);
      if (o == null) {
         o = mapa.get(null);
      }

      return (IValidacaoVinculacaoTitulo) o;
   }

}
