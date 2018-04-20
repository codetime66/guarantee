package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.roteador.Roteador;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * <p>
 * Este servico eh executado apos o usuario confirmar na tela FormularioEfetuarFechamentoCesta.
 * </p>
 * 
 * <p>
 * Movimentacoes de bloqueio serao localizadas por este servico e consequentemente serao atualizadas da seguinte forma:<br>
 * - IFs NAO Cetipados: serao automaticamente cadastrados como Garantia e o status da movimentacao serah alterado para
 * OK<br>
 * - IFs CETIPADOS: serah acionado o MIGAcionador para cada movimentacao deste tipo.
 * </p>
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIAS_CESTA"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @author <a href="bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ServicoFechamentoCestaGarantias extends BaseGarantias implements Servico {

   private static final int LOTE_MIG = 50;

   private IGerenciadorPersistencia gp;

   /*
    * Execucao do ServicoFechamentoCestaGarantias
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "-- begin ServicoFechamento --");
         Logger.debug(this, "Instanciando DAO");
      }

      // DAOs
      gp = getGp();
      IGarantias factory = getFactory();

      ICestaDeGarantias dao = factory.getInstanceCestaDeGarantias();

      // Requisicao
      RequisicaoServicoFechamentoCestaGarantias req = null;
      req = (RequisicaoServicoFechamentoCestaGarantias) requisicao;

      NumeroCestaGarantia codCesta = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      CodigoContaCetip contraParte = req.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
      Booleano sohMudaStatus = req.obterGARANTIAS_CESTA_Booleano();
      Booleano voltaEdicao = req.obterGARANTIAS_CODIGO_Booleano();

      // Objetos de Dados
      CestaGarantiasDO cesta = dao.obterCestaDeGarantias(codCesta);

      IContaParticipante icp = ContaParticipanteFactory.getInstance();
      ContaParticipanteDO conta = null;
      if (!Condicional.vazio(contraParte)) {
         conta = icp.obterContaParticipanteDO(contraParte);
      }

      if (sohMudaStatus.ehVerdadeiro()) {
         cesta.setStatusCesta(StatusCestaDO.EM_FINALIZACAO);
      } else if (voltaEdicao != null && voltaEdicao.ehVerdadeiro()) {
         cesta.setStatusCesta(StatusCestaDO.EM_EDICAO);
      } else {
         try {
            finalizaCesta(cesta, conta);
         } catch (Exception e) {
            req.atribuirGARANTIAS_CODIGO_Booleano(new Booleano(Booleano.VERDADEIRO));
            Roteador.executarAssincrono(req, getContextoAtivacao());

            throw e;
         }
      }

      cesta.setDatAlteracaoStatusCesta(getDataHoje());

      // Resultado do Servico
      ResultadoServicoFechamentoCestaGarantias res = null;
      res = new ResultadoServicoFechamentoCestaGarantias();

      res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(codCesta);
      res.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contraParte);

      return res;
   }

   private void finalizaCesta(CestaGarantiasDO cesta, ContaParticipanteDO contraParte) {
      Data dataHoje = getDataHoje();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, cesta);
         Logger.debug(this, "Garantido: " + contraParte);
         Logger.debug(this, "Data Fechamento: " + dataHoje);
      }

      cesta.setDatFechamento(dataHoje);

      // Adiciona (ou altera) a contra-parte
      if (!Condicional.vazio(contraParte)) {
         IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
         igc.associarGarantidoNaCesta(cesta, contraParte);
      }

      Iterator movimentacoes = listarMovimentacoesParaFinalizar(cesta);
      StatusCestaDO statusAtual = cesta.getStatusCesta();

      if (movimentacoes.hasNext() == false
            && (statusAtual.isIncompleta() || statusAtual.isEmEdicao() || statusAtual.isEmManutencao())) {
         // nao tem nenhuma movimentacao
         cesta.setStatusCesta(StatusCestaDO.FINALIZADA);
         cesta.setDatAlteracaoStatusCesta(dataHoje);
         gp.save(cesta);

         return;
      }

      RequisicaoServicoChamaMIGAcionador rMig = new RequisicaoServicoChamaMIGAcionador();

      ICestaDeGarantias dao = getFactory().getInstanceCestaDeGarantias();
      boolean temProcessamentoAdicional = false;
      while (movimentacoes.hasNext()) {
         Object[] mov = (Object[]) movimentacoes.next();

         Id idMov = (Id) mov[0];
         Booleano cetipado = (Booleano) mov[1];
         Booleano selicado = null;
         if (mov[2] != null) {
            selicado = ((Id) mov[2]).mesmoConteudo(SistemaDO.SELIC) ? Booleano.VERDADEIRO : Booleano.FALSO;
         } else {
            selicado = Booleano.FALSO;
         }

         // Inclui na DetalheGarantia, movimentacoes de IFs nao-cetipados
         if (cetipado.ehFalso() || selicado.ehVerdadeiro()) {
            insereGarantiaExterna(dao, idMov);
         } else {
            temProcessamentoAdicional = true;
            insereGarantiaCetip(rMig, idMov);
         }

         int contador = rMig.obterNumeroDeLinhas();
         if ((contador % LOTE_MIG == 0 || !movimentacoes.hasNext()) && rMig.obterNumeroDeLinhas() > 0) {
            Roteador.executarAssincrono(rMig, getContextoAtivacao());
            rMig = new RequisicaoServicoChamaMIGAcionador();
         }
      }

      if (!temProcessamentoAdicional) {
         // nenhum processamento na cesta indica que a cesta pode ser imediatamente finalizada
         cesta.setStatusCesta(StatusCestaDO.FINALIZADA);
         cesta.setDatAlteracaoStatusCesta(dataHoje);
         gp.save(cesta);
      }
   }

   private void insereGarantiaCetip(RequisicaoServicoChamaMIGAcionador rMig, Id idMov) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Movimentacao " + idMov + " 'CETIPada' encontrada, acionando MIG...");
      }

      rMig.atribuirGARANTIAS_CODIGO_Id(idMov);
      rMig.atribuirBATCH_Booleano(new Booleano(Booleano.VERDADEIRO));
      rMig.novaLinha();
   }

   private void insereGarantiaExterna(ICestaDeGarantias img, Id idMov) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Movimentacao " + idMov
               + " 'NAO CETIPada' encontrada ou SELIC, cadastrando direto na Detalhe...");
      }

      MovimentacaoGarantiaDO m = (MovimentacaoGarantiaDO) getGp().load(MovimentacaoGarantiaDO.class, idMov);
      img.incluirGarantiaExterna(m);
   }

   public Resultado executarConsulta(Requisicao req) throws Exception {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   private Iterator listarMovimentacoesParaFinalizar(CestaGarantiasDO cesta) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Localizando movimentacoes...");
      }

      StringBuffer hql = new StringBuffer("select m.numIdMovimentacaoGarantia, m.indCetipado, ");
      hql.append("m.instrumentoFinanceiro.sistema.numero ");
      hql.append(" from ").append(MovimentacaoGarantiaDO.class.getName() + " m");
      hql.append(" left join m.instrumentoFinanceiro ");
      hql.append(" left join m.instrumentoFinanceiro.sistema ");
      hql.append(" where ");
      hql.append(" m.statusMovimentacaoGarantia.numIdStatusMovGarantia = :statusMovimentacao ");
      hql.append(" and m.tipoMovimentacaoGarantia = :tipoMovimentacao ");
      hql.append(" and m.cestaGarantias = :cesta ");

      IConsulta consulta = gp.criarConsulta(hql.toString());
      consulta.setAtributo("statusMovimentacao", StatusMovimentacaoGarantiaDO.PENDENTE);
      consulta.setAtributo("tipoMovimentacao", TipoMovimentacaoGarantiaDO.BLOQUEIO);
      consulta.setAtributo("cesta", cesta);

      List l = consulta.list();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Total de Movimentacoes: " + l.size());
      }

      return l.iterator();
   }

}
