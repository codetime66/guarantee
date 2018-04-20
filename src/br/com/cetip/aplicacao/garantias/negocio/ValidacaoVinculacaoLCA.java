package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Set;

import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

class ValidacaoVinculacaoLCA extends ValidacaoVinculacaoSTA {

   protected Set getTiposGarantidores() {
      Set garantidores = super.getTiposGarantidores();

      // sao os mesmos que CDCA e CRA, mas o CTRA tambem pode garantir um LCA
      garantidores.add(CodigoTipoIF.CTRA);

      return garantidores;
   }

   public void registrar(TiposValidacaoVinculacaoTitulo f) {
      f.registrar(CodigoTipoIF.LCA, this);
   }

}
