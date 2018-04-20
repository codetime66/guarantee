package br.com.cetip.aplicacao.garantias.negocio;

import java.math.BigDecimal;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IValidarTipoIF;
import br.com.cetip.aplicacao.operacao.apinegocio.ControleOperacaoFactory;
import br.com.cetip.aplicacao.operacao.apinegocio.EstimuloVO;
import br.com.cetip.aplicacao.operacao.apinegocio.IControleOperacao;
import br.com.cetip.aplicacao.operacao.apinegocio.ITipoOperacao;
import br.com.cetip.aplicacao.operacao.apinegocio.OperacaoVO;
import br.com.cetip.aplicacao.operacao.apinegocio.TipoOperacaoFactory;
import br.com.cetip.dados.aplicacao.financeiro.ModalidadeLiquidacaoDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFDO;
import br.com.cetip.dados.aplicacao.operacao.OperacaoDO;
import br.com.cetip.dados.aplicacao.operacao.SituacaoOperacaoDO;
import br.com.cetip.dados.aplicacao.operacao.TipoOperacaoDO;
import br.com.cetip.dados.aplicacao.sca.ObjetoServicoDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.expressao.CodigoControleProcessamento;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoModalidade;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoObjetoServico;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSituacaoOS;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleLancamento;
import br.com.cetip.infra.atributo.tipo.identificador.SituacaoOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Preco;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.ValorMonetario;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.NivelLock;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;

/**
 * @author marco sergio
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
abstract class MIGOperacaoUnitaria extends MIGOperacao {

   private CodigoObjetoServico codigoObjetoServico;

   private OperacaoVO operacaoVO;

   private boolean criarOperacaoFinalizada;

   /**
    * Validacao basica para Operacoes Unitarias
    */
   public IdStatusMovimentacaoGarantia validar() {
      IdStatusMovimentacaoGarantia status = super.validar();

      if (!status.mesmoConteudo(IdStatusMovimentacaoGarantia.OK)) {
         return status;
      }

      // Quantidade foi preenchida?
      MovimentacaoGarantiaDO movimentacao = getMovimentacao();
      Quantidade qtdGarantia = movimentacao.getQtdGarantia();
      if (Condicional.vazio(qtdGarantia)) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Quantidade vazia: " + qtdGarantia);
         }

         return IdStatusMovimentacaoGarantia.QUANTIDADE_INVALIDA;
      }

      // Quantidade foi preenchida?
      if (qtdGarantia.compareTo(new Quantidade("0")) <= 0) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Quantidade menor/igual que zero: " + qtdGarantia);
         }

         return IdStatusMovimentacaoGarantia.QUANTIDADE_INVALIDA;
      }

      if (Condicional.vazio(movimentacao.getIndDireitosGarantidor())) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Indicador 'indDireitoGarantidor' esta vazio");
         }

         return IdStatusMovimentacaoGarantia.IND_DIREITO_GARANTIDOR_INVALIDO;
      }

      InstrumentoFinanceiroDO ifDO = movimentacao.getInstrumentoFinanceiro();

      // IF excluido?
      if (!Condicional.vazio(ifDO.getDataHoraExclusao())) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Instrumento Financeiro esta excluido");
         }

         return IdStatusMovimentacaoGarantia.IF_INEXISTENTE;
      }

      // IF Bloqueado?
      if (!Condicional.vazio(ifDO.getCodigoSituacaoIF()) && !ifDO.getCodigoSituacaoIF().ehConfirmado()) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Instrumento Financeiro nao esta livre");
         }

         return IdStatusMovimentacaoGarantia.IF_BLOQUEADO;
      }

      // IFVencido?
      if (!Condicional.vazio(ifDO.getDataVencimento()) && ifDO.getDataVencimento().comparar(getDataOperacao()) <= 0) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Instrumento Financeiro esta vencido");
         }

         return IdStatusMovimentacaoGarantia.IF_VENCIDO;
      }

      // Tipo IF
      TipoIFDO tipoIF = ifDO.getTipoIF();
      if (!tipoIF.getObjetoServico().getCodSituacaoOS().mesmoConteudo(CodigoSituacaoOS.LIVRE)) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Tipo do Instrumento Financeiro nao esta livre");
         }

         return IdStatusMovimentacaoGarantia.TIPO_IF_BLOQUEADO;
      }

      Id numTipoIF = tipoIF.getNumTipoIF();
      Id codigoSistema = ifDO.getSistema().getNumero();

      IValidarTipoIF validaTipoIF = getGarantias().getInstanceValidarTipoIF();
      if (!validaTipoIF.validarGarantidor(numTipoIF, codigoSistema)) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this,
                  "Tipo de Instrumento Financeiro incompativel com sistemas e tipos validos para o Garantias");
         }

         return IdStatusMovimentacaoGarantia.IF_INCOMPATIVEL;
      }

      return IdStatusMovimentacaoGarantia.OK;
   }

   protected void setCriaOperacaoFinalizada(boolean finalizaOperacao) {
      criarOperacaoFinalizada = finalizaOperacao;
   }

   private void dadosOperacao() {
      Data dataOperacao = getDataOperacao();

      MovimentacaoGarantiaDO movimentacao = getMovimentacao();
      InstrumentoFinanceiroDO ifDO = movimentacao.getInstrumentoFinanceiro();

      if (ifDO.getSistema().getNumero().mesmoConteudo(SistemaDO.SELIC)) {
         codigoObjetoServico = CodigoObjetoServico.SISTEMA_SELIC;
      } else {
         codigoObjetoServico = new CodigoObjetoServico(ifDO.getTipoIF().getCodigoTipoIF().obterConteudo());
      }
      SituacaoOperacao situacao = SituacaoOperacaoDO.PEND_ATU_SALDO;
      if (isOperacaoFinalizada()) {
         situacao = SituacaoOperacaoDO.FINALIZADA;
      } else if (isIndPlataformaBaixa() && !isIndBatch()) {
         situacao = SituacaoOperacaoDO.PEND_ATUALIZA;
      }

      SituacaoOperacaoDO situacaoOperacao = new SituacaoOperacaoDO();
      situacaoOperacao.setCodSituacaoOperacao(situacao);

      ModalidadeLiquidacaoDO modalidade = new ModalidadeLiquidacaoDO();
      modalidade.setId(ModalidadeLiquidacaoDO.SEM);
      modalidade.setCodigoModalidadeLiquidacao(new CodigoModalidade("SEM MODALIDADE"));

      Id idMovimentacao = movimentacao.getNumIdMovimentacaoGarantia();

      operacaoVO = new OperacaoVO();
      operacaoVO.setIdCesta(getCesta().getNumIdCestaGarantias());
      operacaoVO.setNumControleLancamentoOriginalP1(new NumeroControleLancamento(idMovimentacao.obterConteudo()));
      operacaoVO.setNumControleLancamentoOriginalP2(new NumeroControleLancamento(idMovimentacao.obterConteudo()));
      operacaoVO.setSituacaoOperacao(situacaoOperacao);
      operacaoVO.setCodObjetoServico(codigoObjetoServico);
      operacaoVO.setDataOperacao(dataOperacao);
      operacaoVO.setIndLancadoP1(Booleano.VERDADEIRO);
      operacaoVO.setIndLancadoP2(Booleano.VERDADEIRO);
      operacaoVO.setInstrumentoFinanceiro(ifDO);
      operacaoVO.setModalidadeLiquidacao(modalidade);
      operacaoVO.setDataFinanceiro(dataOperacao);
      operacaoVO.setQtdOperacaoDecimal(movimentacao.getQtdGarantia());
      operacaoVO.setValPrecoUnitario(new Preco("0"));
      operacaoVO.setValFinanceiro(new ValorMonetario("0"));
      operacaoVO.setIndDireitoCaucionante(movimentacao.getIndDireitosGarantidor());
      operacaoVO.setTipoOperacao(getTipoOperacao());

      completarDadosOperacao();
   }

   protected final OperacaoVO getOperacaoUnitariaVO() {
      return operacaoVO;
   }

   /**
    * @return Returns the codigoObjetoServico.
    */
   public CodigoObjetoServico getCodigoObjetoServico() {
      return codigoObjetoServico;
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.aplicacao.operacao.apinegocio.IMIGOperacao#criarOperacao()
    */
   public final IdStatusMovimentacaoGarantia criarOperacao() {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Operacao '" + getClass().getName() + "' sendo criada... ");
         Logger.debug(this, "Esta em modo batch: " + isIndBatch());
         Logger.debug(this, "Plat. Baixa: " + isIndPlataformaBaixa());
      }

      dadosOperacao();

      IControleOperacao controleOperacao = getControleOperacao();
      OperacaoDO operacaoDO;
      try {
         operacaoDO = controleOperacao.criaOperacao(operacaoVO, isIndPlataformaBaixa());
      } catch (Exception e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, e.getMessage());
      }

      if (isOperacaoFinalizada()) {
         return IdStatusMovimentacaoGarantia.OK;
      }

      if (isIndPlataformaBaixa()) {
         operacaoDO.setCodCtlProcessamento(CodigoControleProcessamento.N);
         operacaoDO.setIndCtlInclusao(Booleano.FALSO);
      } else {
         operacaoDO.setCodCtlProcessamento(CodigoControleProcessamento.A);
         operacaoDO.setIndCtlInclusao(Booleano.VERDADEIRO);
      }

      identificaOperacao(operacaoDO);

      // Se for baixa plataforma, deve-se estimular a maquina de estados para
      // essa operacao em especifico
      // Caso contrario, a operacao simplesmente eh criada e fica no aguardo
      // do boy do mainframe
      if (isIndPlataformaBaixa()) {
         ContextoAtivacaoVO ca = getContextoAtivacao();
         boolean ehCanalBatch = ca.getCanal().equals(Integer.valueOf("2"));

         if (ehCanalBatch) {
            SituacaoOperacaoDO situacaoOperacao = new SituacaoOperacaoDO();
            situacaoOperacao.setCodSituacaoOperacao(SituacaoOperacaoDO.PEND_ATU_SALDO);
            operacaoDO.setSituacaoOperacao(situacaoOperacao);
         } else {
            EstimuloVO estimulo = isIndBatch() ? EstimuloVO.ATUALIZA : EstimuloVO.ACIONA_ATUALIZA;
            try {
               if (estimulo == EstimuloVO.ATUALIZA) {
                  getGp().lock(operacaoDO.getInstrumentoFinanceiro(), NivelLock.UPGRADE);
               }

               controleOperacao.proximoEstado(operacaoDO, estimulo);
            } catch (Exception e) {
               Logger.error(e);
               throw new Erro(CodigoErro.ERRO_NA_EXECUCAO);
            }
         }
      }

      IdStatusMovimentacaoGarantia migResultado = IdStatusMovimentacaoGarantia.PENDENTE_ATUALIZA;
      if (isIndBatch() && isIndPlataformaBaixa()) {
         migResultado = getMovimentacao().getStatusMovimentacaoGarantia().getNumIdStatusMovGarantia();
      }

      return migResultado;
   }

   private IControleOperacao getControleOperacao() {
      try {
         return ControleOperacaoFactory.getControleOperacao();
      } catch (Exception e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, e.getMessage());
      }
   }

   /*
    * Identifica Comitente da Operacao
    * 
    * @param operacaoDO
    */
   protected final void identificaOperacao(OperacaoDO operacaoDO) {
      Integer id = Integer.valueOf(operacaoDO.getId().toString());
      BigDecimal qtd = new BigDecimal(operacaoDO.getQtdOperacaoDecimal().toString());
      Id idCesta = getCesta().getNumIdCestaGarantias();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Identificacao no SIC para a cesta: " + idCesta + " e garantidor: "
               + getGarantidor().getCodContaParticipante());
      }

      ContextoAtivacaoVO ca = getContextoAtivacao();
      boolean ehCanalBatch = ca.getCanal().equals(Integer.valueOf("2"));
      
      if (!ehCanalBatch) {
       	  if (deveIdentificarComitente() ) {
      		  identificaOperacoes(new Integer[] { id }, new BigDecimal[] { qtd });
      	  }
      }
   }

   /**
    * Obtem o Id do ObjetoServico para o Instrumento Financeiro indicado
    * 
    * @param ifDO
    * @return
    */
   public Id obterIdObjetoServico(InstrumentoFinanceiroDO ifDO) {
      if (isIndPlataformaBaixa()) {
         return ifDO.getTipoIF().getObjetoServico().getNumero();
      }

      IConsulta consOS = getGp().criarConsulta(
            "from " + SistemaDO.class.getName() + " as s where s.numero = :numSistema");
      consOS.setCacheable(true);
      consOS.setCacheRegion("MMG");
      consOS.setAtributo("numSistema", ifDO.getSistema().getNumero());
      List lista = consOS.list();
      SistemaDO sistemaDO = (SistemaDO) lista.get(0);

      return sistemaDO.getObjetoServico().getNumero();
   }

   protected boolean isOperacaoFinalizada() {
      return criarOperacaoFinalizada;
   }

   protected abstract CodigoTipoOperacao getCodigoTipoOperacao();

   private final TipoOperacaoDO getTipoOperacao() {
      Id codigoSistema = getIfDO().getSistema().getNumero();

      Id idObjetoServico = ObjetoServicoDO.SISTEMA_SELIC;
      if (false == SistemaDO.SELIC.mesmoConteudo(codigoSistema)) {
         idObjetoServico = obterIdObjetoServico(getIfDO());
      }

      TipoOperacaoDO tipoOperacao;
      try {
         ITipoOperacao instanceTipoOperacao = TipoOperacaoFactory.getInstance();
         tipoOperacao = instanceTipoOperacao.obterTipoOperacaoDO(getCodigoTipoOperacao(), idObjetoServico);
      } catch (Exception e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, "Operacao: " + e.getMessage());
      }

      return tipoOperacao;
   }
}