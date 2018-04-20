package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Date;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IMensageriaGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.mensageria.apinegocio.garantias.IMensageriaSelic;
import br.com.cetip.aplicacao.mensageria.apinegocio.garantias.MensageriaSelicFactory;
import br.com.cetip.dados.aplicacao.custodia.PosicaoSelicDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.ConsultaSaldoR1VO;
import br.com.cetip.dados.aplicacao.mensageria.selic.ConsultaSaldoVO;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.NotificacaoEventoVO;
import br.com.cetip.dados.aplicacao.mensageria.selic.NotificacaoPendenciaContraParteVO;
import br.com.cetip.dados.aplicacao.mensageria.selic.TransferenciaCustodiaR1VO;
import br.com.cetip.dados.aplicacao.mensageria.selic.TransferenciaCustodiaVO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.TipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoMensagem;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleCTP;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.tempo.DataHora;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

class MensageriaGarantiasSelic extends BaseMensageriaGarantias implements IMensageriaGarantiasSelic {

   /**
    * incluir os dados da mensagem SEL1611 - Notificacao de Evento de Pagamento para controle
    */
   public MensagemSelicDO incluirNotificacaoEvento(NotificacaoEventoVO notificacao) {

      IGerenciadorPersistencia gp = getGp();
      MensagemSelicDO msgDO = criaMensagemSelic(notificacao.getCodigoTipoMensagem(), notificacao.getISPBIF(),
            notificacao.getNumeroOperacao(), notificacao.getNumeroControleSTR(), notificacao.getSituacaoOperacao());
      msgDO.setCodigoOperacao(notificacao.getCodigoOperacaoSelic());
      msgDO.setNumIdContaCedente(getNumIdContaSelic(notificacao.getContaCedente()));
      msgDO.setNumIdContaCessionaria(null); //conta cessionaria selic nao cadastrada na base
      msgDO.setTipoOperacao(notificacao.getTipoOperacaoSelic());
      msgDO.setNumIF(getNumIF(notificacao.getCodigoIF(), notificacao.getDataVencimentoIF()));
      msgDO.setValorUnitario(notificacao.getPrecoUnitario());
      msgDO.setQuantidadeOperacao(notificacao.getQuantidade());
      msgDO.setValorFinanceiro(notificacao.getValorFinanceiro());
      gp.save(msgDO);
      return msgDO;
   }

   /**
    * incluir os dados da mensagem SEL1611 - Notificacao de Pendencia de ContraParte para controle
    */
   public MensagemSelicDO incluirNotificacaoPendenciaContraParte(NotificacaoPendenciaContraParteVO notificacao) {

      IGerenciadorPersistencia gp = getGp();
      MensagemSelicDO msgDO = criaMensagemSelic(notificacao);
      gp.save(msgDO);
      MensagemSelicMovimentacaoGarantiaDO msgMov = obterNotificacaoPendenciaContraParte(notificacao.getNumeroOperacao());
      msgMov.setMensagemSelic(msgDO);
      gp.saveOrUpdate(msgMov);
      return msgDO;
   }

   public MensagemSelicMovimentacaoGarantiaDO obterNotificacaoPendenciaContraParte(NumeroOperacao numeroOperacaoSelic) {
      IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO mov = imov.obterMovimentacaoParaGarantiaExterna(numeroOperacaoSelic, null, null);

      MensagemSelicDO msgSelic = obterMensagemSelicNotificacaoPendenciaContraParte(numeroOperacaoSelic);

      if (msgSelic == null && mov == null) {
         return new MensagemSelicMovimentacaoGarantiaDO();
      }
      return obterMensagemMovimentacaoGarantia(mov, msgSelic);
   }

   public MensagemSelicMovimentacaoGarantiaDO obterNotificacaoPendenciaContraParte(MovimentacaoGarantiaDO mov) {
      if (mov == null) {
         return new MensagemSelicMovimentacaoGarantiaDO();
      }
      MensagemSelicDO msgSelic = obterMensagemSelicNotificacaoPendenciaContraParte(mov.getNumOperacao());

      return obterMensagemMovimentacaoGarantia(mov, msgSelic);
   }

   private MensagemSelicDO obterMensagemSelicNotificacaoPendenciaContraParte(NumeroOperacao numeroOperacaoSelic) {
      return obterMensagemSelic(new CodigoTipoMensagem("SEL1611"), numeroOperacaoSelic);
   }

   /**
    * Envia mensagem SEL1021 para informar a SELIC a respeito da Transferencia de Custodia
    */
   public boolean enviarNotificacaoTransferenciaCustodia(TransferenciaCustodiaVO mensagem) {

      IMensageriaSelic mensageriaSelic = MensageriaSelicFactory.getInstance();
      NumeroControleCTP numeroControleIF;
      try {
         numeroControleIF = mensageriaSelic.transferirCustodia(mensagem);
      } catch (Exception e) {
         Logger.error(this, e);
         throw new Erro(CodigoErro.ERRO, "Mensageria: " + e.getMessage());
      }

      if (numeroControleIF == null) {
         return false;
      }

      MensagemSelicDO msgSelic = new MensagemSelicDO();
      msgSelic.setNumIdTipoMensagem(getNumIdTipoMensagem("SEL1021"));
      msgSelic.setNumeroControleIF(new Texto(numeroControleIF.obterConteudo()));
      msgSelic.setNumIdISPB(getNumIdISPB(mensageriaSelic.getCodCetipISPB()));
      msgSelic.setNumeroOperacao(mensagem.getNumeroOperacao());
      msgSelic.setNumIdContaCedente(getNumIdContaSelic(mensagem.getContaCedente()));
      msgSelic.setNumIdContaCessionaria(getNumIdContaSelic(mensagem.getContaCessionario()));
      msgSelic.setTipoOperacao(mensagem.getTipoOperSelic());
      msgSelic.setNumIF(getNumIF(mensagem.getCodigoIF(), null));
      msgSelic.setQuantidadeOperacao(mensagem.getQuantidade());
      msgSelic.setValorFinanceiro(null);
      msgSelic.setDataInclusao(new DataHora(new Date()));

      getGp().save(msgSelic);
      return true;
   }

   public void enviarNotificacaoTransferenciaCustodiaDebitoGarantido(List listMov) {

      IGarantidoCesta gc = getFactory().getInstanceGarantidoCesta();
      IGarantiasSelic gs = getFactory().getInstanceGarantiasSelic();

      for (int i = 0; i < listMov.size(); i++) {
         MovimentacaoGarantiaDO mov = (MovimentacaoGarantiaDO) listMov.get(i);
         InstrumentoFinanceiroDO ifSelic = mov.getInstrumentoFinanceiro();

         ContaParticipanteDO oldGarantido = gc.obterGarantidoCesta(mov.getCestaGarantias());

         CodigoContaSelic conta60OldGarantido = null;
         CodigoContaSelic conta60NovoGarantido = null;
         try {
            conta60OldGarantido = new CodigoContaSelic(gs.obterContaGarantiaSelic(
                  oldGarantido.getCodContaParticipante()).getCodContaParticipante());
            conta60NovoGarantido = new CodigoContaSelic(gs.obterContaGarantiaSelic(
                  mov.getContaParticipante().getCodContaParticipante()).getCodContaParticipante());
         } catch (Exception e) {
            Logger.error(this, e);
            e.printStackTrace();
            throw new RuntimeException(e);
         }
         TransferenciaCustodiaVO tc = new TransferenciaCustodiaVO(ifSelic.getCodigoIF(), mov.getNumOperacao(),
               conta60OldGarantido, conta60NovoGarantido, TipoOperacaoSelic.DEBITO, ifSelic.getDataVencimento(), mov
                     .getQtdGarantia());
         enviarNotificacaoTransferenciaCustodia(tc);

      }
   }

   /**
    * incluir os dados da mensagem SEL1021R1 - Transferencia de Custodia no banco de dados para controle
    */
   public MensagemSelicDO incluirRespostaNotificacaoTransferenciaCustodia(TransferenciaCustodiaR1VO transferenciaR1) {

      IGerenciadorPersistencia gp = getGp();

      MensagemSelicDO msgDO = criaMensagemSelic(transferenciaR1.getCodTipoMsg(), transferenciaR1.getISPBIF(),
            transferenciaR1.getNumOperacao(), transferenciaR1.getNroControleSTR(), transferenciaR1.getSitOperacao());
      msgDO.setNumeroControleIF(new Texto(transferenciaR1.getNroControleIF().obterConteudo()));
      gp.save(msgDO);
      return msgDO;
   }

   /**
    * Envia mensagem SEL1081 para solicitar consulta de saldo de um ativo Selic
    * @param codigoIF 
    * @param contaSelic
    * @param dataVencimentoIF
    * @param dataReferencia
    * @return
    */
   public PosicaoSelicDO enviarConsultaDeSaldo(ConsultaSaldoVO mensagem) {
      IMensageriaSelic mensageriaSelic = MensageriaSelicFactory.getInstance();
      NumeroControleCTP nroControle = null;
      try {
         nroControle = mensageriaSelic.consultarSaldo(mensagem);
      } catch (Exception e) {
         Logger.error(this, e);
         e.printStackTrace();
         throw new RuntimeException(e);
      }
      PosicaoSelicDO msgPosSelic = new PosicaoSelicDO();
      msgPosSelic.setNumIdTipoMensagem(getNumIdTipoMensagem("SEL1081"));
      msgPosSelic.setNumIF(getNumIF(mensagem.getCodigoIF(), null));
      msgPosSelic.setNumIdContaParticipante(getNumIdContaSelic(mensagem.getContaSelic()));
      msgPosSelic.setDataRequisicao(getDataHoje());
      msgPosSelic.setNumIdISPB(getNumIdISPB(mensageriaSelic.getCodCetipISPB()));
      msgPosSelic.setNumeroControleIF(new NumeroControleIF(nroControle.obterConteudo()));
      msgPosSelic.setDataInclusao(new DataHora(new Date()));
      getGp().save(msgPosSelic);
      return msgPosSelic;
   }

   public PosicaoSelicDO incluirRespostaConsultaDeSaldo(ConsultaSaldoR1VO consultaR1) {
      Data dataHj = getDataHoje();
      PosicaoSelicDO posicao = getFactory().getInstanceGarantiasSelic().obterPosicaoSaldo(
            consultaR1.getNroControleIF(), dataHj);
      posicao.setQtdeSaldo(consultaR1.getSaldo());
      posicao.setDataResultado(dataHj);
      posicao.setDataReferencia(dataHj);
      posicao.setDataAlteracao(new DataHora());
      getGp().saveOrUpdate(posicao);
      return posicao;
   }

   public TipoOperacaoSelic obterTipoOperacaoSelic(MovimentacaoGarantiaDO mov) {
      if (mov != null) {
         return getTipoOperacaoSelic(mov.getTipoMovimentacaoGarantia());
      }
      return null;
   }

   public CodigoContaSelic obterCodigoContaSelicCedente(MensagemSelicDO mensagem) {
      if (mensagem == null) {
         return null;
      }
      return getCodigoContaSelic(mensagem.getNumIdContaCedente());
   }

   public CodigoContaSelic obterCodigoContaSelicCessionario(MensagemSelicDO mensagem) {
      if (mensagem == null) {
         return null;
      }
      return getCodigoContaSelic(mensagem.getNumIdContaCessionaria());
   }

}
