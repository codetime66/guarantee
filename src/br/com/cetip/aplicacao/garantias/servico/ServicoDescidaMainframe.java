package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.IAtualizaStatusCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMIGAcionador;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.ITransferirCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.MIGAcionadorFactory;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.garantias.negocio.mainframe.CtpDadosMF;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.IInstrumentoFinanceiro;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaIFDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * <p>
 * Servico que recebe as notificacoes da plataforma alta referentes a negociacao
 * ou resgate do ativo garantido.
 * </p>
 * 
 * @author Daniela Pistelli Gomes
 * @author Bruno Borges
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Texto"
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_MOVIMENTACAO"
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="Texto"
 *                    pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                    contexto="GARANTIAS_MOVIMENTACAO"
 * 
 */
public class ServicoDescidaMainframe extends BaseGarantias implements Servico {

   private String xmlMainframe;
   private IInstrumentoFinanceiro iif;
   private CestaGarantiasDO cesta;
   private CtpDadosMF dadosMF;

   public Resultado executar(Requisicao requisicao) throws Exception {
      iif = InstrumentoFinanceiroFactory.getInstance();

      RequisicaoServicoDescidaMainframe req = (RequisicaoServicoDescidaMainframe) requisicao;

      xmlMainframe = req.obterGARANTIAS_MOVIMENTACAO_Texto().obterConteudo();
      if (Condicional.vazio(xmlMainframe)) {
         return null;
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, xmlMainframe);
      }

      dadosMF = new CtpDadosMF(xmlMainframe);

      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      cesta = icg.obterCestaDeGarantias(new NumeroCestaGarantia(dadosMF.getNumCesta()));

      if (dadosMF.ehNegociacao()) {
         negociacao();
      } else if (dadosMF.ehResgate()) {
         resgate();
      } else if (dadosMF.ehRetornoVinculacao()) {
         retornoVinculacao();
      } else if (dadosMF.ehRetornoDesvinculacao()) {
         retornoDesvinculacao();
      } else if (dadosMF.ehInadimplencia()) {
         inadimplencia();
      }

      return obterResultado();
   }

   private void inadimplencia() {
      IMIGAcionador mig = MIGAcionadorFactory.getInstance();
      mig.acionarInadimplencia(cesta);
   }

   private void retornoDesvinculacao() {
      if (dadosMF.getCodResposta().equalsIgnoreCase("001")) {
         if (cesta.getStatusCesta().equals(StatusCestaDO.EM_LIBERACAO)) {
            desvinculacaoPorLiberacao();
         } else {
            IAtualizaStatusCesta iasc = getFactory().getInstanceAtualizaStatusCesta();
            iasc.atualizaStatus(cesta, StatusCestaDO.DESVINCULADA);
         }
      } else if (dadosMF.getCodResposta().equalsIgnoreCase("002") || (dadosMF.getCodResposta().equalsIgnoreCase("003"))) {
         erroDesvinculacao();
      }
   }

   private void desvinculacaoPorLiberacao() {
      IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO mov = imov.obterUltimaMovimentacao(cesta, TipoMovimentacaoGarantiaDO.LIBERACAO,
            StatusMovimentacaoGarantiaDO.PENDENTE);

      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      icg.acionaMIG(mov, Booleano.FALSO, getDataHoje());
   }

   private void erroDesvinculacao() {
      IAtualizaStatusCesta iasc = getFactory().getInstanceAtualizaStatusCesta();
      iasc.atualizaStatus(cesta, StatusCestaDO.DESVNC_FALHOU);
   }

   private void negociacao() {
      ContaParticipanteDO conta = null;
      try {
         conta = ContaParticipanteFactory.getInstance().obterContaParticipanteNullDO(
               new CodigoContaCetip(dadosMF.getCodContaGarantido()));
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
      CodigoIF codigoIF = new CodigoIF(dadosMF.getCodIF());
      Data dataOperacao = new Data(dadosMF.getDataOperacao());

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Numero da cesta    " + dadosMF.getNumCesta());
         Logger.debug(this, "Conta Participante " + conta.getCodContaParticipante());
         Logger.debug(this, "CodigoIF           " + codigoIF);
         Logger.debug(this, "Data da Operacao   " + dataOperacao);
      }

      InstrumentoFinanceiroDO ifDO = obterInstrumentoFinanceiro(codigoIF);

      ITransferirCesta tc = getFactory().getInstanceTransferirCesta();
      tc.acionarTransferencia(cesta, conta, dataOperacao, ifDO);
   }

   private InstrumentoFinanceiroDO obterInstrumentoFinanceiro(CodigoIF codigoIF) {
      InstrumentoFinanceiroDO ifDO;
      try {
         ifDO = iif.obterInstrumentoFinanceiro(codigoIF);
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
      return ifDO;
   }

   private void resgate() {
      NumeroCestaGarantia nroCesta = new NumeroCestaGarantia(dadosMF.getNumCesta());
      Data dataOperacao = new Data(dadosMF.getDataOperacao());
      CodigoIF codIF = new CodigoIF(dadosMF.getCodIF());

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Numero da cesta    " + nroCesta);
         Logger.debug(this, "CodigoIF           " + codIF);
         Logger.debug(this, "Data da Operacao   " + dataOperacao);
      }

      InstrumentoFinanceiroDO ativo = obterInstrumentoFinanceiro(codIF);

      IMIGAcionador mig = getFactory().getInstanceMIGAcionador();
      mig.setDataOperacao(dataOperacao);
      mig.acionarDesvinculacaoAtivo(cesta, ativo);
   }

   private void retornoVinculacao() {
      CodigoIF codigo = new CodigoIF(dadosMF.getCodIF());
      Id resposta = new Id(dadosMF.getCodResposta());

      InstrumentoFinanceiroDO titulo = obterInstrumentoFinanceiro(codigo);

      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      CestaGarantiasIFDO vinculo = icg.obterIFDOVinculado(cesta, titulo);

      int numeroResposta = Integer.parseInt(resposta.obterConteudo());

      if (numeroResposta == 1) {
         vinculo.setStatus(StatusCestaIFDO.VINCULADA_AO_ATIVO);
         getGp().save(vinculo);

         IVincularCesta vincCesta = getFactory().getInstanceVincularCesta();
         vincCesta.vincularCesta(cesta);
      } else {
         IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
         MovimentacaoGarantiaDO movVinc = imov.obterUltimaMovimentacao(cesta, TipoMovimentacaoGarantiaDO.VINCULACAO,
               StatusMovimentacaoGarantiaDO.PENDENTE);
         movVinc.setStatusMovimentacaoGarantia(new StatusMovimentacaoGarantiaDO(
               IdStatusMovimentacaoGarantia.FALHA_VINCULACAO_MAINFRAME));

         Texto descricao = new Texto(dadosMF.getTxtResposta());
         movVinc.setTxtDescricao(descricao);

         vinculo.setStatus(StatusCestaIFDO.VINCULACAO_FALHOU);
         cesta.setStatusCesta(StatusCestaDO.VINCULACAO_FALHOU);

         getGp().save(movVinc);
         getGp().save(cesta);
         getGp().save(vinculo);
      }
   }

   private ResultadoServicoDescidaMainframe obterResultado() {
      ResultadoServicoDescidaMainframe resultado = new ResultadoServicoDescidaMainframe();
      resultado.atribuirGARANTIAS_MOVIMENTACAO_Texto(new Texto(xmlMainframe));
      return resultado;
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      throw new ExcecaoServico(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

}
