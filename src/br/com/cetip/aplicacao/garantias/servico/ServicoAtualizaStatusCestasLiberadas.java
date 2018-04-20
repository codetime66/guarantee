package br.com.cetip.aplicacao.garantias.servico;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * @requisicao.class
 * @resultado.class
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ServicoAtualizaStatusCestasLiberadas extends BaseGarantias implements Servico {

   private IGerenciadorPersistencia gp;

   private List listarCestasComRetirada() {
      StringBuffer hql = new StringBuffer(500);
      hql.append("from ");
      hql.append(CestaGarantiasDO.class.getName());
      hql.append(" c join c.movimentacoes m where");
      hql.append(" c.datExclusao is null and");
      hql.append(" m.tipoMovimentacaoGarantia = ? ");

      List cestasComRetirada = gp.find(hql.toString(), TipoMovimentacaoGarantiaDO.RETIRADA);
      return cestasComRetirada;
   }

   public Resultado executar(Requisicao req) throws Exception {
      gp = getGp();

      List cestas = listarCestasComRetirada();
      Data dataHoje = getDataHoje();

      Iterator it = cestas.iterator();
      while (it.hasNext()) {
         CestaGarantiasDO cesta = (CestaGarantiasDO) it.next();
         boolean possuiMovPendente = false;
         Iterator itMovs = cesta.getMovimentacoes().iterator();
         while (itMovs.hasNext()) {
            MovimentacaoGarantiaDO mov = null;
            mov = (MovimentacaoGarantiaDO) itMovs.next();

            TipoMovimentacaoGarantiaDO tipoMov = null;
            tipoMov = mov.getTipoMovimentacaoGarantia();

            Id idMov = tipoMov.getNumIdTipoMovGarantia();

            tipoMov = TipoMovimentacaoGarantiaDO.RETIRADA;

            Atributo compara = null;
            compara = tipoMov.getNumIdTipoMovGarantia();

            if (!idMov.mesmoConteudo(compara)) {
               continue;
            }

            StatusMovimentacaoGarantiaDO statusMov = null;
            statusMov = mov.getStatusMovimentacaoGarantia();

            IdStatusMovimentacaoGarantia id = null;
            id = statusMov.getNumIdStatusMovGarantia();

            Atributo comparaOk = null;
            comparaOk = IdStatusMovimentacaoGarantia.OK;

            if (!id.mesmoConteudo(comparaOk)) {
               possuiMovPendente = true;
            }
         }

         if (!possuiMovPendente) {
            final NumeroCestaGarantia numCestaGarantias = cesta.getNumCestaGarantias();
            final ICestaDeGarantias instanceCestaDeGarantias = getFactory().getInstanceCestaDeGarantias();
            Collection detalhes = instanceCestaDeGarantias.listarGarantiasCesta(numCestaGarantias);
            Iterator itGarantias = detalhes.iterator();
            Quantidade total = new Quantidade();
            while (itGarantias.hasNext()) {
               DetalheGarantiaDO garantia = (DetalheGarantiaDO) itGarantias.next();
               total = total.somar(garantia.getQuantidadeGarantia());
            }

            if (total.obterConteudo().equals(new BigDecimal("0"))) {
               cesta.setStatusCesta(StatusCestaDO.GRT_RETIRADAS);
               cesta.setDatAlteracaoStatusCesta(dataHoje);
            }
         }
      }

      return new ResultadoServicoAtualizaStatusCestasLiberadas();
   }

   public Resultado executarConsulta(Requisicao r) throws Exception {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}