package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IDeletaCesta;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

/**
 * Deleta uma cesta em manutencao
 * 
 * @author brunob
 */
final class DeletaCestaEmManutencao extends BaseGarantias implements IDeletaCesta {

   public void deletaCestaGarantias(CestaGarantiasDO cesta, Booleano indBatch, Data dataOperacao) {
      IGerenciadorPersistencia gp = getGp();

      if (!cesta.getStatusCesta().equals(StatusCestaDO.EM_MANUTENCAO)) {
         throw new Erro(CodigoErro.CESTA_INCOMPATIVEL);
      }

      gp.delete(cesta);
   }

   public void registrar(TiposDelecaoCesta f) {
      f.registrar(StatusCestaDO.EM_MANUTENCAO, this);
   }

}
