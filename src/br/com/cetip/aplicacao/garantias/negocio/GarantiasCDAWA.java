package br.com.cetip.aplicacao.garantias.negocio;

import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasCDAWA;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoCDA;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoWA;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoMovimentacaoGarantia;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

final class GarantiasCDAWA extends BaseGarantias implements IGarantiasCDAWA {

   /*
    * (non-Javadoc)
    * 
    * @see
    * br.com.cetip.aplicacao.garantias.negocio.IGarantiasCDAWA#encontrarCDA(br.com.cetip.infra.atributo.tipo.identificador
    * .CodigoIF, br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia)
    */
   public boolean encontrarCDA(CodigoIF codigoIF, CestaGarantiasDO cesta) {
      CodigoWA codigoWA = new CodigoWA(codigoIF.obterConteudo());
      CodigoCDA respectivo = codigoWA.obterRespectivoCDA();
      return existeRespectivo(cesta, respectivo);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * br.com.cetip.aplicacao.garantias.negocio.IGarantiasCDAWA#encontrarWA(br.com.cetip.infra.atributo.tipo.identificador
    * .CodigoIF, br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia)
    */
   public boolean encontrarWA(CodigoIF codigoIF, CestaGarantiasDO cesta) {
      CodigoCDA codigoCDA = new CodigoCDA(codigoIF.obterConteudo());
      CodigoWA respectivo = codigoCDA.obterRespectivoWA();
      return existeRespectivo(cesta, respectivo);
   }

   private boolean existeRespectivo(CestaGarantiasDO cesta, CodigoIF codigoIF) {
      IGerenciadorPersistencia gp = getGp();

      StringBuffer hql = new StringBuffer(350);
      hql.append("select distinct mg.instrumentoFinanceiro.codigoIF from ");
      hql.append(MovimentacaoGarantiaDO.class.getName() + " mg");
      hql.append(" where ");
      hql.append(" mg.instrumentoFinanceiro.codigoIF = :codigoIF and ");
      hql.append(" mg.tipoMovimentacaoGarantia.numIdTipoMovGarantia = :tipoMov and ");
      hql.append(" mg.statusMovimentacaoGarantia.numIdStatusMovGarantia = :statusMov and ");
      hql.append(" mg.cestaGarantias = :cesta ");

      IConsulta consulta = gp.criarConsulta(hql.toString());
      consulta.setAtributo("codigoIF", codigoIF);
      consulta.setAtributo("cesta", cesta);
      consulta.setAtributo("tipoMov", IdTipoMovimentacaoGarantia.BLOQUEIO);
      consulta.setAtributo("statusMov", IdStatusMovimentacaoGarantia.PENDENTE);
      List lista = consulta.list();

      hql.setLength(0);
      hql.append("select distinct dg.instrumentoFinanceiro.codigoIF from ");
      hql.append(DetalheGarantiaDO.class.getName() + " dg ");
      hql.append(" where ");
      hql.append(" dg.instrumentoFinanceiro.codigoIF = :codigoIF and ");
      hql.append(" dg.cestaGarantias = :cesta and ");
      hql.append(" dg.quantidadeGarantia > 0 ");

      consulta = gp.criarConsulta(hql.toString());
      consulta.setAtributo("codigoIF", codigoIF);
      consulta.setAtributo("cesta", cesta);
      lista.addAll(consulta.list());

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Respectivo encontrado " + codigoIF + " ? " + (lista.size() > 0));
      }

      return lista.size() > 0;
   }

}
