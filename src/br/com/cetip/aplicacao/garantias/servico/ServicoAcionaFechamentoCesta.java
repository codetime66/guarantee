package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * <p>
 * Servico que aciona o MIGAcionador para disparar o bloqueio dos itens de uma cesta de garantias.
 * </p>
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * 
 * @requisicao.class
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.class
 */
public class ServicoAcionaFechamentoCesta extends BaseGarantias implements Servico {

   private IGerenciadorPersistencia gp;

   public Resultado executar(Requisicao requisicao) throws Exception {
      gp = getGp();
      RequisicaoServicoAcionaFechamentoCesta req = (RequisicaoServicoAcionaFechamentoCesta) requisicao;

      NumeroCestaGarantia numCesta = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();

      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = icg.obterCestaDeGarantias(numCesta);

      if (!cesta.getStatusCesta().equals(StatusCestaDO.EM_FINALIZACAO)) {
         return new ResultadoServicoAcionaFechamentoCesta();
      }

      int totalErro = totalMovsErro(cesta);

      // contadores de movimentacoes apos data do fechamento
      if (totalErro > 0) {
         cesta.setStatusCesta(StatusCestaDO.INCOMPLETA);
      } else {
         int totalMovs = totalMovsAposFechamento(cesta);
         int totalMovsOK = totalMovsOKAposFechamento(cesta);

         if (totalMovs == totalMovsOK) {
            // Esta movimentacao obteve sucesso, validar todas
            // as movimentacoes apos a data de fechamento
            cesta.setStatusCesta(StatusCestaDO.FINALIZADA);
         }
      }

      cesta.setDatAlteracaoStatusCesta(getDataHoje());

      gp.save(cesta);

      return new ResultadoServicoAcionaFechamentoCesta();
   }

   private int totalMovsErro(CestaGarantiasDO cesta) {
      StringBuffer hql = new StringBuffer();
      hql.append("select count(m.numIdMovimentacaoGarantia) from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" m where m.cestaGarantias = ? ");
      hql.append(" and m.dataMovimentacao >= m.cestaGarantias.datFechamento ");
      hql.append(" and m.tipoMovimentacaoGarantia = 1 ");
      hql.append(" and m.statusMovimentacaoGarantia.numIdStatusMovGarantia not in (1,31,32,34,35,38) ");

      Integer movs = (Integer) gp.find(hql.toString(), new Object[] { cesta }).iterator().next();

      return movs.intValue();
   }

   private int totalMovsAposFechamento(CestaGarantiasDO cesta) {
      StringBuffer hql = new StringBuffer();
      hql.append("select count(m.numIdMovimentacaoGarantia) from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" m where m.cestaGarantias = ? ");
      hql.append(" and m.dataMovimentacao >= m.cestaGarantias.datFechamento ");
      hql.append(" and m.tipoMovimentacaoGarantia = 1 ");
      hql.append(" and m.statusMovimentacaoGarantia.numIdStatusMovGarantia <> 38 ");

      Integer movs = (Integer) gp.find(hql.toString(), new Object[] { cesta }).iterator().next();

      return movs.intValue();
   }

   private int totalMovsOKAposFechamento(CestaGarantiasDO cesta) {
      StringBuffer hql = new StringBuffer();
      hql.append("select count(m.numIdMovimentacaoGarantia) from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" m where m.cestaGarantias = ? ");
      hql.append(" and m.dataMovimentacao >= m.cestaGarantias.datFechamento ");
      hql.append(" and m.tipoMovimentacaoGarantia = 1 ");
      hql.append(" and m.statusMovimentacaoGarantia.numIdStatusMovGarantia = 1 ");

      Integer movs = (Integer) gp.find(hql.toString(), new Object[] { cesta }).iterator().next();

      return movs.intValue();
   }

   public Resultado executarConsulta(Requisicao req) throws Exception {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}