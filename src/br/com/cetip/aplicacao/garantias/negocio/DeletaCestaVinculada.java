package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IDeletaCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IRetirarGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;

/**
 * <p>Desvincula uma cesta, lancando retirada automatica de todas as garantias<p>
 * 
 * @author brunob
 */
final class DeletaCestaVinculada extends BaseGarantias implements IDeletaCesta {

   public void deletaCestaGarantias(CestaGarantiasDO cesta, Booleano indBatch, Data data) {
      Data dataOperacao = data;
      if (Condicional.vazio(dataOperacao)) {
         dataOperacao = getDataHoje();
      }

      cesta.setStatusCesta(StatusCestaDO.EM_DESVINCULACAO);
      cesta.setDatAlteracaoStatusCesta(dataOperacao);
      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      int totalGarantias = icg.contarGarantias(cesta);

      if (totalGarantias == 0) {
         icg.desvinculaCestaSemGarantias(cesta);
      } else {
         IRetirarGarantias irg = getFactory().getInstanceRetirarGarantias();
         irg.retirarGarantiasPorDesvinculacao(cesta, indBatch, dataOperacao);
      }
   }

   public void registrar(TiposDelecaoCesta f) {
      f.registrar(StatusCestaDO.VINCULADA, this);
   }

}
