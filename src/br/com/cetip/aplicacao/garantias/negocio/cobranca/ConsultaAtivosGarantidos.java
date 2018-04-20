package br.com.cetip.aplicacao.garantias.negocio.cobranca;

import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.cobranca.IConsultaCobrancaAtivosGarantidos;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

public class ConsultaAtivosGarantidos extends BaseGarantias implements IConsultaCobrancaAtivosGarantidos {

   public List obterAtivosGarantidos() {
      IGerenciadorPersistencia gerenciador = getGp();

      StringBuffer hql = new StringBuffer();
      hql.append("select c.codContaParticipante, ");
      hql.append("c.codTipoIF, ");
      hql.append("c.codIF, ");
      hql.append("c.numIdCestaGarantias, ");
      hql.append("c.nomStatusCesta, ");
      hql.append("c.valFinanceiro, ");
      hql.append("c.totSelic ");
      hql.append("from CobrancaTituloVDO c");

      IConsulta consulta = gerenciador.criarConsulta(hql.toString());
      List c = consulta.list();

      return c;
   }

}
