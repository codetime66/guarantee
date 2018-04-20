package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Set;

import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

class ValidacaoVinculacaoDIM extends ValidacaoVinculacaoTitulo {

   protected Set getTiposGarantidores() {
      Set s = super.getTiposGarantidores();
      complementaTiposGarantidoresSelic(s);
      s.remove(CodigoTipoIF.DIM);
      return s;
   }

   public void registrar(TiposValidacaoVinculacaoTitulo f) {
      f.registrar(CodigoTipoIF.DIM, this);
   }

}
