package br.com.cetip.aplicacao.garantias.negocio.acoes;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

public class ValidadoresFactory {

   private Map validadores = new HashMap(12);

   public ValidadoresFactory() {
      p(new ValidarAcaoRetirarGarantias());
      p(new ValidarAcaoDesvincularGarantido());
   }

   private void p(IValidarAcao iva) {
      validadores.put(iva.getAcao(), iva.getClass());
   }

   public IValidarAcao getInstance(Funcao acao) {
      Class c = (Class) validadores.get(acao);

      IValidarAcao o = null;
      try {
         if (c != null) {
            o = (IValidarAcao) c.newInstance();
         }
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }

      return o;
   }

}
