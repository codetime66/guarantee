package br.com.cetip.aplicacao.garantias.apinegocio;

import java.util.List;

import br.com.cetip.dados.aplicacao.custodia.PosicaoSelicDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.ConsultaSaldoR1VO;
import br.com.cetip.dados.aplicacao.mensageria.selic.ConsultaSaldoVO;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.NotificacaoEventoVO;
import br.com.cetip.dados.aplicacao.mensageria.selic.NotificacaoPendenciaContraParteVO;
import br.com.cetip.dados.aplicacao.mensageria.selic.TransferenciaCustodiaR1VO;
import br.com.cetip.dados.aplicacao.mensageria.selic.TransferenciaCustodiaVO;
import br.com.cetip.infra.atributo.tipo.expressao.TipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;

public interface IMensageriaGarantiasSelic {

   //SEL1611 - Tipo 1
   public MensagemSelicDO incluirNotificacaoEvento(NotificacaoEventoVO notificacao);

   //SEL1611 - Tipo 2
   public MensagemSelicDO incluirNotificacaoPendenciaContraParte(NotificacaoPendenciaContraParteVO notificacao);

   public MensagemSelicMovimentacaoGarantiaDO obterNotificacaoPendenciaContraParte(NumeroOperacao numeroOperacaoSelic);

   public MensagemSelicMovimentacaoGarantiaDO obterNotificacaoPendenciaContraParte(MovimentacaoGarantiaDO mov);

   public TipoOperacaoSelic obterTipoOperacaoSelic(MovimentacaoGarantiaDO mov);

   public CodigoContaSelic obterCodigoContaSelicCedente(MensagemSelicDO mensagem);

   public CodigoContaSelic obterCodigoContaSelicCessionario(MensagemSelicDO mensagem);

   //SEL1021
   public boolean enviarNotificacaoTransferenciaCustodia(TransferenciaCustodiaVO mensagem);

   public void enviarNotificacaoTransferenciaCustodiaDebitoGarantido(List listMov);

   //SEL1021R1
   public MensagemSelicDO incluirRespostaNotificacaoTransferenciaCustodia(TransferenciaCustodiaR1VO transferenciaR1);

   //SEL1081
   public PosicaoSelicDO enviarConsultaDeSaldo(ConsultaSaldoVO mensagem);

   //SEL1081R1
   public PosicaoSelicDO incluirRespostaConsultaDeSaldo(ConsultaSaldoR1VO consultaR1);

}
