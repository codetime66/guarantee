package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IExcluirGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

/**
 * 
 * @author brunob
 * 
 */
class ExcluirGarantia extends BaseGarantias implements IExcluirGarantia {

   private Data dataOperacao;

   private Booleano indBatch;

   public void setIndBatch(Booleano indBatch) {
      this.indBatch = indBatch;
   }

   public void setDataOperacao(Data dataOperacao) {
      this.dataOperacao = dataOperacao;
   }

   public void excluirGarantia(DetalheGarantiaDO garantia) {
      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      IMovimentacoesGarantias imovs = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO mov = imovs.incluirMovimentacaoDesbloqueio(garantia);
      icg.acionaMIG(mov, indBatch, dataOperacao);
   }

   public void excluirGarantia(MovimentacaoGarantiaDO mov) {
      mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.MOVIMENTACAO_CANCELADA);
   }

   public void excluirItemEspelho(MovimentacaoGarantiaDO itemCestaDO) {
      IGerenciadorPersistencia gp = getGp();
      List l = null;

      StringBuffer hql = new StringBuffer(500);
      hql.append("from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" cg where cg.cestaGarantias.numIdCestaGarantias = ?");

      if (itemCestaDO.getIndCetipado().ehFalso()) {
         hql.append(" and cg.codIfNCetipado = ?");
         l = gp.find(hql.toString(), new Object[] { itemCestaDO.getCestaGarantias().getNumIdCestaGarantias(), itemCestaDO.getCodIfNCetipado() });
      } else {
         hql.append(" and cg.instrumentoFinanceiro = ?");
         hql.append(" and cg.statusMovimentacaoGarantia.numIdStatusMovGarantia <> ?");
         if (Condicional.vazio(itemCestaDO.getNumOperacao())) {
            l = gp.find(hql.toString(), new Object[] { itemCestaDO.getCestaGarantias().getNumIdCestaGarantias(),
                  itemCestaDO.getInstrumentoFinanceiro(), StatusMovimentacaoGarantiaDO.MOVIMENTACAO_CANCELADA });
         } else {
            hql.append(" and cg.numOperacao = ?");
            l = gp.find(hql.toString(), new Object[] { itemCestaDO.getCestaGarantias().getNumIdCestaGarantias(),
                  itemCestaDO.getInstrumentoFinanceiro(), StatusMovimentacaoGarantiaDO.MOVIMENTACAO_CANCELADA,
                  itemCestaDO.getNumOperacao() });
         }
      }

      if (l.size() == 1) {
         Iterator iter = l.iterator();

         MovimentacaoGarantiaDO element = (MovimentacaoGarantiaDO) iter.next();
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Id: " + element.getNumIdMovimentacaoGarantia());
         }
         validarItem(element, itemCestaDO);

         CestaGarantiasDO cestaGarantias = itemCestaDO.getCestaGarantias();
         StatusCestaDO status = cestaGarantias.getStatusCesta();
         TipoMovimentacaoGarantiaDO tipoMov = element.getTipoMovimentacaoGarantia();
         StatusMovimentacaoGarantiaDO statusMov = element.getStatusMovimentacaoGarantia();
         if ((status.isEmManutencao() || (status.isEmEdicao() || status.isIncompleta())
               && tipoMov.equals(TipoMovimentacaoGarantiaDO.BLOQUEIO)
               && !statusMov.equals(StatusMovimentacaoGarantiaDO.OK))) {
            excluirGarantia(element);
            return;
         } else if ((status.isEmEdicao() || status.isIncompleta())
               && element.getTipoMovimentacaoGarantia().equals(TipoMovimentacaoGarantiaDO.BLOQUEIO)
               && element.getStatusMovimentacaoGarantia().equals(StatusMovimentacaoGarantiaDO.OK)) {

            List garantias = obtemGarantia(itemCestaDO, cestaGarantias);

            if (garantias.size() == 1) {
               DetalheGarantiaDO garantia = (DetalheGarantiaDO) garantias.get(0);
               excluirGarantia(garantia);
            } else {
               throw new Erro(CodigoErro.CESTA_ITEM_NAO_PODE_MOVIMENTAR);
            }
         } else {
            throw new Erro(CodigoErro.CESTA_ITEM_NAO_PODE_MOVIMENTAR);
         }
      } else {
         if (itemCestaDO.getInstrumentoFinanceiro() != null) {
            throw new Erro(CodigoErro.CESTA_ITEM_NAO_EXISTE, itemCestaDO.getInstrumentoFinanceiro().getCodigoIF()
                  .toString());
         }
         throw new Erro(CodigoErro.CESTA_ITEM_NAO_EXISTE, itemCestaDO.getCodIfNCetipado().toString());
      }
   }

   public void registrar(TiposExclusaoGarantia f) {
      f.registrar(null, this);
   }

   private void validarItem(MovimentacaoGarantiaDO element, MovimentacaoGarantiaDO itemCesta) {
      if (element == null || itemCesta == null) {
         return;
      }

      if (!element.getIndDireitosGarantidor().mesmoConteudo(itemCesta.getIndDireitosGarantidor())) {
         throw new Erro(CodigoErro.CESTA_OPERACAO_RETIRADA_NAO_CONFERE, "Eventos para Garantidor nao confere.");
      }

      if (!element.getQtdGarantia().mesmoConteudo(itemCesta.getQtdGarantia())) {
         throw new Erro(CodigoErro.CESTA_OPERACAO_RETIRADA_NAO_CONFERE, "Quantidade nao confere.");
      }

      if (!element.getCestaGarantias().getTipoGarantia().getNumIdTipoGarantia().mesmoConteudo(
            itemCesta.getCestaGarantias().getTipoGarantia().getNumIdTipoGarantia())) {
         throw new Erro(CodigoErro.CESTA_OPERACAO_RETIRADA_NAO_CONFERE, "Tipo de Garantia nao confere.");
      }

      if (!Condicional.vazio(element.getNumOperacao())
            && !element.getNumOperacao().mesmoConteudo(itemCesta.getNumOperacao())) {
         throw new Erro(CodigoErro.CESTA_OPERACAO_RETIRADA_NAO_CONFERE, "Numero de Operacao nao confere.");
      }
   }

   private List obtemGarantia(MovimentacaoGarantiaDO itemCestaDO, CestaGarantiasDO cestaGarantias) {
      IGerenciadorPersistencia gp = getGp();
      StringBuffer hqld = new StringBuffer();
      hqld.append("from ");
      hqld.append(DetalheGarantiaDO.class.getName());
      hqld
            .append(" d where ((d.instrumentoFinanceiro = :insFin and d.quantidadeGarantia = :qtdGarantia and d.indCetipado = :indCetipado)");
      hqld.append(" or ");
      hqld.append(" (d.codIfNCetipado = :codNCetip and d.indCetipado = :indCetipado))");
      hqld.append(" and d.indDireitosGarantidor = :direitoGarantidor");
      hqld.append(" and d.cestaGarantias = :cesta ");

      IConsulta consulta = gp.criarConsulta(hqld.toString());
      consulta.setAtributo("cesta", cestaGarantias);
      consulta.setAtributo("codNCetip", itemCestaDO.getCodIfNCetipado());
      consulta.setAtributo("insFin", itemCestaDO.getInstrumentoFinanceiro());
      consulta.setAtributo("qtdGarantia", itemCestaDO.getQtdGarantia());
      consulta.setAtributo("direitoGarantidor", itemCestaDO.getIndDireitosGarantidor());
      consulta.setAtributo("indCetipado", itemCestaDO.getIndCetipado());

      return consulta.list();

   }

}
