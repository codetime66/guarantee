package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashSet;

import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

public class SetCodigoTipoIF extends HashSet {

   private void validaObjeto(Object o) {
      if (o instanceof CodigoTipoIF == false) {
         throw new IllegalArgumentException("Este Set soh aceita objetos do tipo CodigoTipoIF");
      }
   }

   public boolean add(Object o) {
      validaObjeto(o);

      CodigoTipoIF tipoIF = (CodigoTipoIF) o;
      if (o == null) {
         return add(null);
      }

      return super.add(tipoIF.obterConteudo());
   }

   public boolean remove(Object o) {
      validaObjeto(o);

      CodigoTipoIF tipoIF = (CodigoTipoIF) o;
      if (o == null) {
         return remove(null);
      }

      return super.remove(tipoIF.obterConteudo());
   }

   public boolean contains(Object o) {
      validaObjeto(o);

      CodigoTipoIF tipoIF = (CodigoTipoIF) o;
      if (o == null) {
         return contains(null);
      }

      return super.contains(tipoIF.obterConteudo());
   }

}
