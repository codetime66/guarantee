package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Set;

import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

class ValidacaoVinculacaoDEB extends ValidacaoVinculacaoTitulo {

   protected Set getTiposGarantidores() {
      Set s = super.getTiposGarantidores();
      complementaTiposGarantidoresSelic(s);
      s.remove(CodigoTipoIF.DEB);
      return s;
   }

   public void registrar(TiposValidacaoVinculacaoTitulo f) {
      f.registrar(CodigoTipoIF.DEB, this);
   }

}
