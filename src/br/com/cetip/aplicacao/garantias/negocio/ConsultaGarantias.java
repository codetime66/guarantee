package br.com.cetip.aplicacao.garantias.negocio;

import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IConsultaGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.persistencia.IConsulta;

/**
 * 
 * @author brunob
 */
final class ConsultaGarantias extends BaseGarantias implements IConsultaGarantias {

   private IConsulta con;

   public boolean cestaContemGarantia(CestaGarantiasDO cesta, InstrumentoFinanceiroDO garantia) {
      String hql = "select count(*) from MovimentacaoGarantiaDO m where m.statusMovimentacaoGarantia in (:status) and m.tipoMovimentacaoGarantia in (:tipos) and m.cestaGarantias = :cesta and m.instrumentoFinanceiro = :garantia";

      IConsulta c = getGp().criarConsulta(hql);
      c.setAtributo("cesta", cesta);
      c.setAtributo("garantia", garantia);

      c.setParameterList("status", new Object[] { StatusMovimentacaoGarantiaDO.PENDENTE,
            StatusMovimentacaoGarantiaDO.PENDENTE_ATUALIZA });
      c.setParameterList("tipos",
            new Object[] { TipoMovimentacaoGarantiaDO.APORTE, TipoMovimentacaoGarantiaDO.BLOQUEIO });

      Integer count = (Integer) c.list().get(0);

      if (count.intValue() == 0) {
         hql = "select count(*) from DetalheGarantiaDO d where d.cestaGarantias = ? and d.instrumentoFinanceiro = ? and d.quantidadeGarantia > 0";
         count = (Integer) getGp().find(hql, new Object[] { cesta, garantia }).get(0);
      }

      return count.intValue() > 0;
   }

   public boolean existeGarantiasAltaPlataforma(CestaGarantiasDO cesta) {
      if (con == null) {
         StringBuffer hql = new StringBuffer(250);
         hql.append("select count(*) from ");
         hql.append(DetalheGarantiaDO.class.getName());
         hql.append(" dg where dg.cestaGarantias = :cesta ");
         hql.append(" and dg.instrumentoFinanceiro.sistema.numero not in (:sistema) ");
         hql.append(" and dg.quantidadeGarantia > 0 ");

         con = getGp().criarConsulta(hql.toString());
         con.setParameterList("sistema", new Object[] { SistemaDO.CETIP21, SistemaDO.SELIC });
      }

      con.setAtributo("cesta", cesta);
      List l = con.list();
      Integer count = (Integer) l.get(0);

      return count.intValue() > 0;
   }

}
