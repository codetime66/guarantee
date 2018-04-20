package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Collections;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoGarantidorNatEcon;
import br.com.cetip.aplicacao.sap.apinegocio.INaturezaEconomica;
import br.com.cetip.aplicacao.sap.apinegocio.NaturezaEconomicaFactory;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sap.NaturezaEconomicaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;

class ValidacaoGarantidoresNatEcon extends BaseGarantias implements IValidacaoGarantidorNatEcon {

   public void validarNaturezaGarantido(CestaGarantiasDO cesta) {
      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
      ContaParticipanteDO garantido = igc.obterGarantidoCesta(cesta);

      INaturezaEconomica ine;
      NaturezaEconomicaDO natureza = null;
      try {
         ine = NaturezaEconomicaFactory.getNaturezaEconomica();
         natureza = ine.obterNaturezaEconomica(garantido.getCodContaParticipante());
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (natureza == null) {
         throw new Erro(CodigoErro.NATUREZA_INVALIDA);
      }
      String nomSimplificado = natureza.getNomeSimplificadoNaturezaEconomica().obterConteudo();

      Set naturezasValidas = getNaturezasEconomicas();
      boolean naturezaValida = naturezasValidas.contains(nomSimplificado);

      if (!naturezaValida) {
         throw new Erro(CodigoErro.NATUREZA_INVALIDA);
      }
   }

   /**
    * Lista com o nome simplificado das naturezas economicas
    * @return
    */
   protected Set getNaturezasEconomicas() {
      return Collections.EMPTY_SET;
   }
}
