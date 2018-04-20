package br.com.cetip.aplicacao.garantias.servico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IExcluirGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * <p>
 * Servico para excluir garantias da cesta. Este servico eh somente utilizado pela tela RelacaoExcluirGarantiasDeCesta.
 * </p>
 * 
 * <p>
 * Este servico nao exclui do banco as garantias bloqueadas, mas sim efetua a chamada ao MIGAcionador para
 * desbloquea-las. Caso o participante tenha selecionado uma movimentacao, esta sera excluida
 * </p>
 * 
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_ITENS"
 * 
 * @resultado.class
 */
public class ServicoExcluirGarantiasCesta extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      IGarantias factory = getFactory();

      // cast da requisicao
      RequisicaoServicoExcluirGarantiasCesta requisicao = null;
      requisicao = (RequisicaoServicoExcluirGarantiasCesta) req;

      // parametros
      List idGarantias = requisicao.obterListaGARANTIAS_ITENS_Id();
      List txtGarantias = requisicao.obterListaGARANTIAS_ITENS_Texto();

      List idDetalhes = new ArrayList();
      List idMovimentacoes = new ArrayList();

      separaListas(idGarantias, txtGarantias, idDetalhes, idMovimentacoes);

      // Objetos de Dados
      List garantias = Collections.EMPTY_LIST;
      if (idDetalhes.isEmpty() == false) {
         garantias = obterGarantias(idDetalhes);
      }

      List movimentacoes = Collections.EMPTY_LIST;
      if (idMovimentacoes.isEmpty() == false) {
         movimentacoes = obterMovimentacoes(idMovimentacoes);
      }

      deletaGarantias(factory, garantias);

      deletaMovimentacoes(factory, movimentacoes);

      return new ResultadoServicoExcluirGarantiasCesta();
   }

   private void deletaMovimentacoes(IGarantias factory, List movimentacoes) {
      for (Iterator itMovimentacoes = movimentacoes.iterator(); itMovimentacoes.hasNext();) {
         MovimentacaoGarantiaDO garantia = (MovimentacaoGarantiaDO) itMovimentacoes.next();
         CodigoTipoIF tipoIF = garantia.getCodigoTipoIF();

         IExcluirGarantia iexclui = factory.getInstanceExcluirGarantia(tipoIF);
         iexclui.excluirGarantia(garantia);
      }
   }

   private void deletaGarantias(IGarantias factory, List garantias) {
      // Deleta!
      for (Iterator itGarantias = garantias.iterator(); itGarantias.hasNext();) {
         DetalheGarantiaDO garantia = (DetalheGarantiaDO) itGarantias.next();
         CodigoTipoIF tipoIF = garantia.getCodigoTipoIF();

         IExcluirGarantia iexclui = factory.getInstanceExcluirGarantia(tipoIF);
         iexclui.excluirGarantia(garantia);
      }
   }

   private void separaListas(List idGarantias, List txtGarantias, List idDetalhes, List idMovimentacoes) {
      // separa movimentacoes e garantias
      for (int i = 0; i < idGarantias.size(); i++) {
         Id id = (Id) idGarantias.get(i);
         Texto txt = (Texto) txtGarantias.get(i);

         if ("M".equals(txt.obterConteudo())) {
            idMovimentacoes.add(id);
         } else {
            idDetalhes.add(id);
         }
      }
   }

   private List obterGarantias(List idGarantias) {
      StringBuffer hql = new StringBuffer(500);
      hql.append("from ");
      hql.append(DetalheGarantiaDO.class.getName());
      hql.append(" d where d.numIdDetalheGarantia in (:idGarantias)");

      IConsulta consulta = getGp().criarConsulta(hql.toString());
      consulta.setParameterList("idGarantias", idGarantias);

      return consulta.list();
   }

   private List obterMovimentacoes(List idMovimentacoes) {
      StringBuffer hql = new StringBuffer(500);
      hql.append("from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" d where d.numIdMovimentacaoGarantia in (:idMovimentacoes)");

      IConsulta consulta = getGp().criarConsulta(hql.toString());
      consulta.setParameterList("idMovimentacoes", idMovimentacoes);

      return consulta.list();
   }

   public Resultado executarConsulta(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}