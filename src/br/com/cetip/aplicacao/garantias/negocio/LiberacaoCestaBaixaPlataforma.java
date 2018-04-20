package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.ILiberacaoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

/**
 * Libera uma cesta vinculada a um ativo da baixa plataforma
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
class LiberacaoCestaBaixaPlataforma extends BaseGarantias implements ILiberacaoCesta {

   /**
    * @see ILiberacaoCesta#liberar(CestaGarantiasDO, MovimentacaoGarantiaDO)
    */
   public void liberar(CestaGarantiasDO cesta, Data data) {
      ICestaDeGarantias cg = getFactory().getInstanceCestaDeGarantias();

      // Recupera a Movimentacao de Liberacao Total
      IMovimentacoesGarantias im = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO m = im.obterUltimaMovimentacao(cesta, TipoMovimentacaoGarantiaDO.LIBERACAO,
            StatusMovimentacaoGarantiaDO.PENDENTE);

      if (m != null) {
         // Chama MIG para a operacao de Liberacao Total
         cg.acionaMIG(m, Booleano.FALSO, data);
      } else {
         // Recupera a Movimentacao de Desvinculacao gerada pela Liberacao
         // Parcial
         m = im.obterUltimaMovimentacao(cesta, TipoMovimentacaoGarantiaDO.DESVINCULACAO,
               StatusMovimentacaoGarantiaDO.PENDENTE);
         if (m != null) {
            m.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);
         }

         cesta.setStatusCesta(StatusCestaDO.GRT_LIBERADAS);
         cesta.setDatAlteracaoStatusCesta(getDataHoje());
      }
   }

   public void registrar(TiposLiberacao f) {
      f.registrar(SistemaDO.CETIP21, null, this);
   }

}
