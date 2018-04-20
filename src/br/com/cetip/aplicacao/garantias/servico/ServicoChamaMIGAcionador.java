package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="BATCH"
 * 
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="OPERACAO"
 * 
 * @resultado.class
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vinicius Fernandes</a>
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ServicoChamaMIGAcionador extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      RequisicaoServicoChamaMIGAcionador requisicao;
      requisicao = (RequisicaoServicoChamaMIGAcionador) req;

      IGerenciadorPersistencia gp = getGp();
      IGarantias gf = getFactory();
      ICestaDeGarantias dao = gf.getInstanceCestaDeGarantias();

      List ids = requisicao.obterListaGARANTIAS_CODIGO_Id();

      if (ids.size() > 1000) {
         throw new IllegalArgumentException("Muitas movimentacoes informadas ao MMG: " + ids.size()
               + ". O limite eh 1000.");
      }

      StringBuffer hql = new StringBuffer();
      hql.append("from ").append(MovimentacaoGarantiaDO.class.getName()).append(" m ");
      hql.append("where m.numIdMovimentacaoGarantia in (:ids)");

      IConsulta c = gp.criarConsulta(hql.toString());
      c.setParameterList("ids", ids);

      List movs = c.list();

      Booleano indBatch = requisicao.obterBATCH_Booleano();
      Data dataOperacao = requisicao.obterOPERACAO_Data();

      boolean movBloqueios = false;
      boolean movTransferencia = false;
      Id numCesta = null;

      // pega a primeira pra tirar algumas informacoes
      Iterator i = movs.iterator();
      if (i.hasNext()) {
         MovimentacaoGarantiaDO movimentacao = (MovimentacaoGarantiaDO) i.next();
         CestaGarantiasDO cesta = movimentacao.getCestaGarantias();
         numCesta = cesta.getNumIdCestaGarantias();
         movBloqueios = movimentacao.getTipoMovimentacaoGarantia().equals(TipoMovimentacaoGarantiaDO.BLOQUEIO);
         movTransferencia = movimentacao.getTipoMovimentacaoGarantia().equals(TipoMovimentacaoGarantiaDO.TRANSFERENCIA);
         if (movTransferencia) {
            getFactory().getInstanceTransferirCesta().acionarTransferencia(cesta, movimentacao.getContaParticipante(),
                  null, movimentacao.getInstrumentoFinanceiro());
         } else {
            dao.acionaMIG(movimentacao, indBatch, dataOperacao);
         }
      }

      // processa o resto
      while (i.hasNext()) {
         MovimentacaoGarantiaDO movimentacao = (MovimentacaoGarantiaDO) i.next();
         dao.acionaMIG(movimentacao, indBatch, dataOperacao);
      }

      if (movBloqueios) {
         RequisicaoServicoAcionaFechamentoCesta rv = new RequisicaoServicoAcionaFechamentoCesta();
         rv.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(new NumeroCestaGarantia(numCesta));
         new ServicoAcionaFechamentoCesta().executar(rv);
      }

      return new ResultadoServicoChamaMIGAcionador();
   }

   public Resultado executarConsulta(Requisicao r) throws Exception {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}