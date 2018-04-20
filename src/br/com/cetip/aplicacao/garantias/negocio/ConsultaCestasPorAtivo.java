package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Collections;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IConsultaCestasPorAtivo;
import br.com.cetip.dados.aplicacao.custodia.CarteiraParticipanteDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.operacao.DetalheCaucaoDO;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

public class ConsultaCestasPorAtivo extends BaseGarantias implements IConsultaCestasPorAtivo {

   public List obterCestasContendoGarantia(Id numIF) {
      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(500);
      hql.append("select dg.cestaGarantias.numIdCestaGarantias from ");
      hql.append(DetalheGarantiaDO.class.getName());
      hql
            .append(" dg inner join fetch dg.cestaGarantias cesta inner join fetch cesta.statusCesta status inner join fetch dg.instrumentoFinanceiro ativo where");
      hql.append(" status in (:status)");
      hql.append(" and dg.quantidadeGarantia > 0");
      hql.append(" and ativo.id = :ativo");

      IConsulta c = gp.criarConsulta(hql.toString());
      c.setAtributo("ativo", numIF);
      c.setParameterList("status", new Object[] { StatusCestaDO.VINCULADA, StatusCestaDO.INADIMPLENTE });

      List l = c.list();

      return l;
   }

   public List obterCaucaoCestasContendoGarantia(Id numIF) {
      StringBuffer hql = new StringBuffer(300);

      hql.append(" select dc from ");
      hql.append(DetalheCaucaoDO.class.getName());
      hql.append(" dc, ").append(InstrumentoFinanceiroDO.class.getName()).append(" i ");
      hql.append(" where i.id = :numIF");
      hql.append(" and i.dataHoraExclusao IS NULL ");
      hql.append(" and i.id = dc.numIf ");
      hql.append(" and dc.qtdDetalheCaucao > 0");

      IConsulta consulta = getGp().criarConsulta(hql.toString());
      consulta.setAtributo("numIF", numIF);

      List l = consulta.list();
      if ( l == null){
    	  return Collections.EMPTY_LIST;
      }
      return l;
   }
   
   //Obtem as cesta que contem determinada garantia mas que nao finalizaram o processo de vinculacao (Selic)
   public List obterCestasVinculacaoImcompletaContendoGarantia(Id numIF) {
      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(500);
      hql.append("select dg from ");
      hql.append(DetalheGarantiaDO.class.getName()).append(" dg, ");
      hql.append(CestaGarantiasDO.class.getName()).append(" cesta, ");
      hql.append(CarteiraParticipanteDO.class.getName()).append(" cart ");
      hql.append(" where dg.instrumentoFinanceiro.id = :ativo");
      hql.append(" and dg.quantidadeGarantia > 0");
      hql.append(" and dg.cestaGarantias = cesta.numIdCestaGarantias");
      hql.append(" and cesta.statusCesta in (:status)");
      hql.append(" and cesta.conta60Garantido = cart.contaParticipante");
      hql.append(" and dg.instrumentoFinanceiro = cart.instrumentoFinanceiro");

      IConsulta c = gp.criarConsulta(hql.toString());
      c.setAtributo("ativo", numIF);
      c.setParameterList("status", new Object[] { StatusCestaDO.EM_VINCULACAO, StatusCestaDO.GRT_EXPIRADAS });

      List l = c.list();
      if ( l == null){
    	  return Collections.EMPTY_LIST;
      }
      return l;
   }   
   

}
