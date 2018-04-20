package br.com.cetip.aplicacao.garantias.negocio;

import java.util.List;

import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoCDA;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoWA;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoMovimentacaoGarantia;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

/**
 * 
 * @author brunob
 */
final class ExcluirGarantiaCDAWA extends ExcluirGarantia {

   private IConsulta consulta;

   private IConsulta consultaMov;

   private IConsulta consGarantiaCdaWa;

   public void excluirGarantia(DetalheGarantiaDO garantia) {
      super.excluirGarantia(garantia);

      IGerenciadorPersistencia gp = getGp();
      gp.flush();

      InstrumentoFinanceiroDO ifo = garantia.getInstrumentoFinanceiro();
      CodigoTipoIF codTipoIF = ifo.getTipoIF().getCodigoTipoIF();
      CodigoIF codIF = ifo.getCodigoIF();

      CodigoIF respectivo = obterRespectivoCDAWA(codIF, codTipoIF);

      // pergunta se tem mov de desbloqueio pendente para o respectivo
      if (consulta == null) {
         StringBuffer hql = new StringBuffer(350);
         hql.append("select distinct mg.instrumentoFinanceiro.codigoIF from ");
         hql.append(MovimentacaoGarantiaDO.class.getName() + " mg");
         hql.append(" where ");
         hql.append(" mg.instrumentoFinanceiro.codigoIF = :codigoIF and ");
         hql.append(" mg.tipoMovimentacaoGarantia.numIdTipoMovGarantia = :tipoMov and ");
         hql.append(" mg.statusMovimentacaoGarantia.numIdStatusMovGarantia = :statusMov and ");
         hql.append(" mg.cestaGarantias.numIdCestaGarantias = :idCesta ");

         consulta = gp.criarConsulta(hql.toString());
         consulta.setAtributo("tipoMov", IdTipoMovimentacaoGarantia.DESBLOQUEIO);
         consulta.setAtributo("statusMov", IdStatusMovimentacaoGarantia.PENDENTE);
         consulta.setCacheable(true);
         consulta.setCacheRegion("MMG");
      }

      consulta.setAtributo("codigoIF", respectivo);
      consulta.setAtributo("idCesta", garantia.getCestaGarantias().getNumIdCestaGarantias());
      List lista = consulta.list();

      if (lista.isEmpty()) {
         if (consGarantiaCdaWa == null) {
            consGarantiaCdaWa = gp
                  .criarConsulta("from "
                        + DetalheGarantiaDO.class.getName()
                        + " d where d.instrumentoFinanceiro.codigoIF = :codIF and d.quantidadeGarantia > 0 and d.cestaGarantias = :cesta");
         }

         consGarantiaCdaWa.setAtributo("cesta", garantia.getCestaGarantias());
         consGarantiaCdaWa.setAtributo("codIF", respectivo);
         List cdaOuWA = consGarantiaCdaWa.list();

         // desbloquear o respectivo, jah que nao encontrou mov de desbloqueio pendente para ele
         if (!cdaOuWA.isEmpty()) {
            DetalheGarantiaDO garantiaCDAWA = (DetalheGarantiaDO) cdaOuWA.get(0);
            super.excluirGarantia(garantiaCDAWA);
         }
      }
   }

   public void excluirGarantia(MovimentacaoGarantiaDO mov) {
      super.excluirGarantia(mov);

      IGerenciadorPersistencia gp = getGp();

      InstrumentoFinanceiroDO ifo = mov.getInstrumentoFinanceiro();
      CodigoTipoIF codTipoIF = ifo.getTipoIF().getCodigoTipoIF();
      CodigoIF codIF = ifo.getCodigoIF();

      CodigoIF respectivo = obterRespectivoCDAWA(codIF, codTipoIF);

      if (consultaMov == null) {
         StringBuffer hql = new StringBuffer();
         hql.append("from ");
         hql.append(MovimentacaoGarantiaDO.class.getName());
         hql
               .append(" m where m.instrumentoFinanceiro.codigoIF = :codigoIF and m.cestaGarantias = :cesta and m.tipoMovimentacaoGarantia.numIdTipoMovGarantia = :tipo");

         consultaMov = gp.criarConsulta(hql.toString());
         consultaMov.setCacheable(true);
         consultaMov.setCacheRegion("MMG");
         consultaMov.setAtributo("tipo", IdTipoMovimentacaoGarantia.BLOQUEIO);
      }

      consultaMov.setAtributo("cesta", mov.getCestaGarantias());
      consultaMov.setAtributo("codigoIF", respectivo);
      List cdaOuWA = consultaMov.list();

      if (!cdaOuWA.isEmpty()) {
         MovimentacaoGarantiaDO movCDAWA = (MovimentacaoGarantiaDO) cdaOuWA.get(0);
         super.excluirGarantia(movCDAWA);
      }
   }

   private CodigoIF obterRespectivoCDAWA(CodigoIF codIF, CodigoTipoIF codTipoIF) {
      CodigoIF respectivo = null;

      if (codTipoIF.ehCDA()) {
         respectivo = new CodigoCDA(codIF.obterConteudo()).obterCDAOuWA();
      } else {
         respectivo = new CodigoWA(codIF.obterConteudo()).obterCDAOuWA();
      }

      return respectivo;
   }

   public void registrar(TiposExclusaoGarantia f) {
      f.registrar(CodigoTipoIF.CDA, this);
      f.registrar(CodigoTipoIF.WA, this);
   }

}
