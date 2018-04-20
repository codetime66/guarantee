package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.DetalheGarantiaFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMIGResultado;
import br.com.cetip.aplicacao.garantias.apinegocio.MIGResultadoFactory;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.operacao.apinegocio.DetalheCaucaoFactory;
import br.com.cetip.aplicacao.operacao.apinegocio.IOperacao;
import br.com.cetip.aplicacao.operacao.apinegocio.OperacaoFactory;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaIFDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.operacao.OperacaoDO;
import br.com.cetip.dados.aplicacao.operacao.SituacaoOperacaoDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiroPositivo;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * @requisicao.class
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_MOVIMENTACAO"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_STATUS_MOV"
 * 
 * @requisicao.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_CESTA"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CESTA"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_DADOS_OPERACAO"
 * 
 * @resultado.class
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vinicius Fernandes</a>
 */
public class ServicoChamaMIGResultado extends BaseGarantias implements Servico {

   private IGerenciadorPersistencia gp;

   private IGarantias gf;

   public Resultado executar(Requisicao req) throws Exception {
      gp = getGp();
      gf = getFactory();

      RequisicaoServicoChamaMIGResultado requisicao = (RequisicaoServicoChamaMIGResultado) req;
      Id idMov = requisicao.obterGARANTIAS_MOVIMENTACAO_Id();
      Id statusOpMF = requisicao.obterGARANTIAS_STATUS_MOV_Id();
      Id cestaId = requisicao.obterGARANTIAS_CESTA_Id();
      Id codigoOperacao = requisicao.obterGARANTIAS_DADOS_OPERACAO_Id();
      Texto txtOperacao = requisicao.obterGARANTIAS_CESTA_Texto();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "====== ServicoChamaMIGResultado ======");
         Logger.debug(this, "Id(GARANTIAS_MOVIMENTACAO): [" + idMov + "]");
         Logger.debug(this, "Id(GARANTIAS_STATUS_MOV): [" + statusOpMF + "]");
         Logger.debug(this, "Texto(GARANTIAS_CESTA): [" + txtOperacao + "]");
         Logger.debug(this, "Id(GARANTIAS_CESTA): [" + cestaId + "]");
         Logger.debug(this, "Id(GARANTIAS_DADOS_OPERACAO): [" + codigoOperacao + "]");
         Logger.debug(this, "======================================");
      }

      MovimentacaoGarantiaDO movimentacao = (MovimentacaoGarantiaDO) gp.load(MovimentacaoGarantiaDO.class, idMov);
      movimentacao.setTxtDescricao(txtOperacao);

      // Valida o codigo de cesta com a movimentacao
      NumeroInteiroPositivo numCestaMovimento = new NumeroInteiroPositivo(movimentacao.getCestaGarantias()
            .getNumIdCestaGarantias().obterConteudo());
      if (!numCestaMovimento.mesmoConteudo(new NumeroInteiroPositivo(cestaId.obterConteudo()))) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.error(this, "Cod. Cesta Invalido: " + movimentacao.getCestaGarantias().getNumIdCestaGarantias()
                  + "/" + cestaId);
         }
         throw new ExcecaoServico(CodigoErro.CESTA_INVALIDA);
      }

      // Traduz o status de operacao do Mainframe para status correspondente do
      // MIGResultado
      IdStatusMovimentacaoGarantia status = null;

      if (statusOpMF == null) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Status de Operacao do Mainframe NULO");
         }
         throw new ExcecaoServico(CodigoErro.CONTEUDO_CAMPO_INVALIDO);
      } else if (statusOpMF.mesmoConteudo(new Id("43"))) {
         status = IdStatusMovimentacaoGarantia.OK;
      } else if (statusOpMF.mesmoConteudo(new Id("99")) || statusOpMF.mesmoConteudo(new Id("136"))) {
         status = IdStatusMovimentacaoGarantia.IF_INEXISTENTE;
      } else if (statusOpMF.mesmoConteudo(new Id("72"))) {
         status = IdStatusMovimentacaoGarantia.IF_INADIMPLENTE;
      } else if (statusOpMF.mesmoConteudo(new Id("56"))) {
         status = IdStatusMovimentacaoGarantia.OPERACAO_CANCELADA_ANTES_DA_FINALIZACAO;
      } else if (statusOpMF.mesmoConteudo(new Id("60"))) {
         status = IdStatusMovimentacaoGarantia.OPERACAO_REJEITADA_PENDENTE_SALDO;
      } else {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Status de Operacao do Mainframe Invalido: " + statusOpMF.obterConteudo());
         }
         throw new ExcecaoServico(CodigoErro.CONTEUDO_CAMPO_INVALIDO);
      }

      //
      // Chama a acao correspondente se necessario
      //
      IOperacao operacao;
      OperacaoDO opDO = null;
      try {
         operacao = OperacaoFactory.getInstance();
         opDO = operacao.obterOperacao(new CodigoOperacao(codigoOperacao.obterConteudo()));
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
      CodigoTipoOperacao codTipoOperacao = opDO.getTipoOperObjetoServ().getTipoOperacao().getCodTipoOperacao();

      if (statusOpMF.mesmoConteudo(new Id("43"))
            && (codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_BLOQUEIO_GARANTIA) || codTipoOperacao
                  .mesmoConteudo(CodigoTipoOperacao.COD_DESBLOQUEIO_GARANTIA))) {

         insereAlteraDetalheGarantia(opDO);

         if (codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_DESBLOQUEIO_GARANTIA)) {
            cancelaMovimentacaoBloqueio(movimentacao);
         }
      }

      // Atualiza Movimentacao, exceto para liberacao que tem uma unica movimentacao
      // para todos as operacoes
      if (!codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_LIBERACAO_GARANTIA) && // 895
            !codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_RETIRADA_POR_LIBERACAO_CESTA_GARANTIAS)) { // 888
         IMIGResultado dao = MIGResultadoFactory.getInstance();
         dao.resultadoOperacao(movimentacao, status, txtOperacao);
      }

      // Chama a acao correspondente se necessario
      if (statusOpMF.mesmoConteudo(new Id("43"))) {
         if (codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_APORTE_GARANTIA)
               || codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_RETIRADA_GARANTIA)
               || codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_RETIRADA_POR_LIBERACAO_CESTA_GARANTIAS) || // 888
               codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_LIBERACAO_GARANTIA)) { // 895

            if (codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_APORTE_GARANTIA)) {
               insereAlteraDetalheGarantia(opDO);
               insereDetalheCaucao(opDO);
            } else if (codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_RETIRADA_GARANTIA)) {
               insereAlteraDetalheGarantia(opDO);
               atualizaDetalheCaucaoLiberacaoGarantias(opDO);
               verificaDesvinculaCestaGarantias(cestaId);
            } else if (codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_LIBERACAO_GARANTIA)) {
               insereAlteraDetalheGarantia(opDO);
               atualizaDetalheCaucaoLiberacaoGarantias(opDO);
               atualizaStatusCesta(opDO, cestaId);
               if (verificaSeExecucaoParcial(opDO).ehVerdadeiro() && !temOperacoesPendentes(opDO)) {
                  verificaDesvinculaCestaGarantias(cestaId);
               }
            } else { // COD_RETIRADA_POR_LIBERACAO_CESTA_GARANTIAS
               atualizaStatusCesta(opDO, cestaId);
               if (verificaSeExecucaoParcial(opDO).ehVerdadeiro() && !temOperacoesPendentes(opDO)) {
                  if (Logger.estaHabilitadoDebug(this)) {
                     Logger.debug(this, "Vou desvincular");
                  }
                  verificaDesvinculaCestaGarantias(cestaId);
               }
            }
         } else if (codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_VINCULACAO_CESTA_GARANTIAS)) {
            atualizaStatusCesta(opDO, cestaId);
         }
      }

      return new ResultadoServicoChamaMIGResultado();
   }

   private void insereAlteraDetalheGarantia(OperacaoDO operacaoDO) {
      DetalheGarantiaFactory.getInstance().insereAlteraDetalheGarantia(operacaoDO);
   }

   private void insereDetalheCaucao(OperacaoDO operacaoDO) {
      try {
         DetalheCaucaoFactory.getInstance().insereDetalheCaucao(operacaoDO);
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }

   private void atualizaDetalheCaucaoLiberacaoGarantias(OperacaoDO operacaoDO) {
      try {
         DetalheCaucaoFactory.getInstance().atualizaDetalheCaucaoLiberacaoGarantias(operacaoDO);
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }

   private void verificaDesvinculaCestaGarantias(Id cestaId) {
      ICestaDeGarantias cestagarantias = gf.getInstanceCestaDeGarantias();
      CestaGarantiasDO cestaDO = cestagarantias.obterCestaDeGarantias(new NumeroCestaGarantia(cestaId.toString()));
      cestagarantias.verificaNecessidadeDesvincularCesta(cestaDO);
   }

   private void cancelaMovimentacaoBloqueio(MovimentacaoGarantiaDO movDesbloq) {
      ICestaDeGarantias cestagarantias = gf.getInstanceCestaDeGarantias();
      cestagarantias.cancelaMovimentacaoBloqueio(movDesbloq);
   }

   /*
    * Verifica operacao de vinculacao - neste caso, todas as operacoes devem estar finalizadas para que
    * a cesta correspondente esteja finalmente vinculada
    */
   private boolean temOperacoesPendentes(OperacaoDO opDO) {
      String query = "select count(*) from OperacaoDO op where op.idCestaGarantias = ? and op.numControleLancamentoOriginalP1 = ? and op.situacaoOperacao.codSituacaoOperacao <> ? ";
      List operacoes = gp.find(query, new Object[] { opDO.getIdCestaGarantias(),
            opDO.getNumControleLancamentoOriginalP1(), SituacaoOperacaoDO.FINALIZADA });

      Integer count = (Integer) operacoes.get(0);
      return count.intValue() > 0;
   }

   private void atualizaStatusCesta(OperacaoDO opDO, Id cestaId) {
      if (!temOperacoesPendentes(opDO)) {
         ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
         CestaGarantiasDO cesta = icg.obterCestaDeGarantias(new NumeroCestaGarantia(cestaId.obterConteudo()));
         StatusCestaDO statusCesta = cesta.getStatusCesta();

         if (statusCesta.equals(StatusCestaDO.VINCULADA_AO_ATIVO)) {
            cesta.setStatusCesta(StatusCestaDO.VINCULADA, getDataHoje());
            Iterator vinculados = cesta.getAtivosVinculados().iterator();
            while (vinculados.hasNext()) {
               CestaGarantiasIFDO vinculado = (CestaGarantiasIFDO) vinculados.next();
               if (!vinculado.getStatus().equals(StatusCestaIFDO.VINCULADA)) {
                  vinculado.setStatus(StatusCestaIFDO.VINCULADA);
               }
            }
         } else if (statusCesta.equals(StatusCestaDO.EM_LIBERACAO) || statusCesta.equals(StatusCestaDO.INADIMPLENTE)) {
            if (statusCesta.equals(StatusCestaDO.EM_LIBERACAO)) {
               cesta.setStatusCesta(StatusCestaDO.GRT_LIBERADAS, getDataHoje());
            }

            Id idMovimentacao = new Id(opDO.getNumControleLancamentoOriginalP1().obterConteudo());
            MovimentacaoGarantiaDO mov = (MovimentacaoGarantiaDO) gp.load(MovimentacaoGarantiaDO.class, idMovimentacao);
            mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);
         }
      }
   }

   private Booleano verificaSeExecucaoParcial(OperacaoDO opDO) {
      String query = "select count(*) from MovimentacaoGarantiaDO mg where mg.numIdMovimentacaoGarantia = ? and mg.tipoMovimentacaoGarantia = ? ";
      List movs = gp.find(query, new Object[] { opDO.getNumControleLancamentoOriginalP1(),
            TipoMovimentacaoGarantiaDO.LIBERACAO_PARCIAL });

      Integer count = (Integer) movs.get(0);

      if (count.intValue() > 0) {
         return Booleano.VERDADEIRO;
      }

      return Booleano.FALSO;
   }

   public Resultado executarConsulta(Requisicao r) throws Exception {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}
