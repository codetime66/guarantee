package br.com.cetip.aplicacao.garantias.negocio;

import java.math.BigDecimal;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IPenhorNoEmissor;
import br.com.cetip.aplicacao.garantias.apinegocio.ITipoGarantiaCesta;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoGarantiaDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

final class PenhorNoEmissor extends BaseGarantias implements IPenhorNoEmissor {

   public boolean eCestaPenhorNoEmissor(CestaGarantiasDO cesta) {
      ITipoGarantiaCesta tgc = getFactory().getInstanceTipoGarantiaCesta();
      return tgc.obterTipoGarantia(cesta).mesmoConteudo(TipoGarantiaDO.PENHOR_EMISSOR);
   }

   public List obterListaGarantiasPenhorEmissorLiberadas(NumeroCestaGarantia numeroCesta) {
      IGerenciadorPersistencia gp = getGp();

      StringBuffer hqlb = new StringBuffer(500);
      hqlb.append("from " + MovimentacaoGarantiaDO.class.getName() + " lpe").append(
            " where lpe.cestaGarantias.numIdCestaGarantias = :numCesta").append(
            " and lpe.tipoMovimentacaoGarantia.numIdTipoMovGarantia = :tipo").append(
            " and lpe.statusMovimentacaoGarantia.numIdStatusMovGarantia <> :status");

      IConsulta cons = gp.criarConsulta(hqlb.toString());

      cons.setAtributo("numCesta", numeroCesta);
      cons.setAtributo("tipo", TipoMovimentacaoGarantiaDO.LIBERACAO_PENHOR_EMISSOR.getNumIdTipoMovGarantia());
      cons.setAtributo("status", StatusMovimentacaoGarantiaDO.MOVIMENTACAO_CANCELADA.getNumIdStatusMovGarantia());

      return cons.list();
   }

   public boolean temQuantidadeAtivoParaLiberacao(NumeroCestaGarantia numeroCesta, CodigoIF codIF, Quantidade quantidade) {
      String hql = "select d.quantidadeGarantia from DetalheGarantiaDO d where d.cestaGarantias.numIdCestaGarantias = ? and d.instrumentoFinanceiro.codigoIF = ?";
      List l = getGp().find(hql, new Object[] { numeroCesta, codIF });
      if (l.isEmpty()) {
         return false;
      }

      Quantidade qttTotalDetalheGarantia = (Quantidade) l.get(0);

      // se a quantidade a liberar for maior que a disponivel , retorna false
      BigDecimal qtd = quantidade.obterBigDecimal();
      BigDecimal qtdTotal = qttTotalDetalheGarantia.obterBigDecimal();
      if (qtd.compareTo(qtdTotal) > 0) {
         return false;
      }

      return true;
   }

}
