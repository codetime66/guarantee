package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashSet;
import java.util.Set;

import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

class ValidacaoGarantidoresNatEconDI extends ValidacaoGarantidoresNatEcon {

   /**
    * Lista com o nome simplificado das naturezas economicas
    * @return
    */
   public Set getNaturezasEconomicas() {
      Set s = new HashSet();
      s.add("CFI");
      s.add("BM");
      s.add("BC");
      s.add("CE");
      s.add("BD");
      s.add("CI");
      s.add("CH");
      s.add("AP");
      s.add("COOPC");
      s.add("AM");
      s.add("CC");
      s.add("COR");
      s.add("DTVM");
      s.add("COR");
      s.add("BI");
      return s;
   }

   public void registrar(TiposValidacaoGarantidoresNatEcon f) {
      f.registrar(CodigoTipoIF.DI, this);
   }

}
