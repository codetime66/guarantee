package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Set;

import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

class ValidacaoVinculacaoCDB extends ValidacaoVinculacaoTitulo {

   protected Set getTiposGarantidores() {
      Set s = super.getTiposGarantidores();
      complementaTiposGarantidoresSelic(s);
      s.remove(CodigoTipoIF.CDB);
      return s;
   }

   public void registrar(TiposValidacaoVinculacaoTitulo f) {
      f.registrar(CodigoTipoIF.CDB, this);
   }

}
