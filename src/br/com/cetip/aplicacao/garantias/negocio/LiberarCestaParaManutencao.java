package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.ILiberarCestaParaManutencao;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.motorderegra.nucleo.motor.MotorDeRegra;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

class LiberarCestaParaManutencao extends BaseGarantias implements ILiberarCestaParaManutencao {

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.ILiberarCestaParaManutencao#liberarCestaParaManutencao(br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia)
    */
   public void liberarCestaParaManutencao(NumeroCestaGarantia numero) {
      Data dataHoje = getDataHoje();

      ICestaDeGarantias dao = getFactory().getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = dao.obterCestaDeGarantias(numero);

      if (!cesta.getStatusCesta().equals(StatusCestaDO.FINALIZADA)) {
         throw new Erro(CodigoErro.CESTA_NAO_PODE_LIBERAR);
      }

      // Valida
      MotorDeRegra motorRegra = FabricaDeMotorDeRegra.getMotorDeRegra();
      AtributosColunados acs = new AtributosColunados();
      acs.atributo(numero);

      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
      ContaParticipanteDO garantido = igc.obterGarantidoCesta(cesta);

      if (garantido != null) {
         acs.atributo(garantido.getCodContaParticipante());
      }

      try {
         motorRegra.avalia(ConstantesDeNomeDeRegras.podeLiberarCestaParaManutencao, acs, true);
      } catch (Exception e) {
         Logger.error(this, e);

         if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
         }

         throw new RuntimeException(e);
      }

      cesta.setStatusCesta(StatusCestaDO.EM_EDICAO);
      cesta.setDatAlteracaoStatusCesta(dataHoje);
   }

}
