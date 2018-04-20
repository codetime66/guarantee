package br.com.cetip.aplicacao.garantias.negocio;

import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasNaoCetip;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;

public class GarantiasNaoCetip extends BaseGarantias implements IGarantiasNaoCetip {

   public boolean cestaPossuiAtivosNaoCetipados(CestaGarantiasDO codCesta) {
      StringBuffer hql = new StringBuffer();
      hql.append("select count(*) ");
      hql.append("  from MovimentacaoGarantiaDO mg ");
      hql.append(" where mg.cestaGarantias = :idCesta and");
      hql.append("       mg.indCetipado = 'N' and");
      hql.append("       mg.qtdGarantia > 0");

      List l = getGp().find(hql.toString(), codCesta);
      Integer qtd = (Integer) l.get(0);

      return qtd.intValue() > 0;
   }

   public boolean possuiSomenteNaoCetipados(CestaGarantiasDO codCesta) {
      StringBuffer hql = new StringBuffer();
      hql.append("select count(*), mg.indCetipado ");
      hql.append("  from MovimentacaoGarantiaDO mg ");
      hql.append(" where mg.cestaGarantias = :idCesta and");
      hql.append("       mg.qtdGarantia > 0");
      hql.append(" group by mg.indCetipado");

      List l = getGp().find(hql.toString(), codCesta);
      if (l.isEmpty() || l.size() == 2) {
         return false;
      }

      Object[] linha = (Object[]) l.get(0);
      Booleano indCetipado = (Booleano) linha[1];

      return indCetipado.ehVerdadeiro();
   }

}
