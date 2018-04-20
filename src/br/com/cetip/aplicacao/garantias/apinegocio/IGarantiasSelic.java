package br.com.cetip.aplicacao.garantias.apinegocio;

import java.util.List;

import br.com.cetip.dados.aplicacao.custodia.PosicaoSelicDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.GarantidorCestaIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.NotificacaoEventoVO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

/**
 * <p>Interface para tratar ativos Selic</p>
 *
 * @author brunob
 */
public interface IGarantiasSelic {

   public List obterAtivosSelicMMG(CodigoTipoIF codigoTipoIF, CodigoIF codigoIF);

   public GarantidorCestaIFDO obterAtivoSelicMMG(CodigoIF codigoIF);

   public GarantidorCestaIFDO obterQualquerAtivoSelicMMG(CodigoIF codigoIF);

   public InstrumentoFinanceiroDO obterAtivoSelic(CodigoIF codigoIF);

   public boolean numeroOperacaoEhValido(NumeroOperacao numero);

   public boolean temSelicNaCesta(CestaGarantiasDO cesta);

   public boolean temSelicEmDetalhes(Id idCesta);

   public List obterPosicaoSaldo(CodigoIF codigoIF, CodigoContaSelic contaSelic);

   public PosicaoSelicDO obterPosicaoSaldo(NumeroControleIF nroControle, Data data);

   public List obterDetalhesPosicaoSaldo(CodigoIF codigoIF);

   public List obterMovimentacaoAtivosSelicados(NumeroCestaGarantia numeroCesta);

   public void alterarNumerosOperacao(List idMovimentacoes, List numerosOperacao);

   public void geraMovsControleVinculacao(CestaGarantiasDO cesta);

   public boolean registradoLancamentoTransferenciaCustodia(MovimentacaoGarantiaDO mov);

   public void registrarLancamentoTransferenciaCustodia(MensagemSelicDO msgSelic, MovimentacaoGarantiaDO movExterna);

   public void registrarRespostaLancamentoTransferenciaCustodiaCreditoGarantido(MovimentacaoGarantiaDO movSelic);
   
   public void registrarRespostaLancamentoTransferenciaCustodia(MovimentacaoGarantiaDO movSelic);
   
   public void registrarRespostaLancamentoTransferenciaCustodiaExpirada(MovimentacaoGarantiaDO movSelic);
   
   public void lancarTransferenciaCustodia(MovimentacaoGarantiaDO movSelic, Funcao acesso);

   public List processarNotificacaoEvento(NotificacaoEventoVO notificacao, Id ativoSelic, Data dataMovimentacao);

   public NumeroOperacao obterNumeroOperacaoCetip();

   public void transferirGarantias(CestaGarantiasDO cesta);

   public void liberarGarantias(CestaGarantiasDO cesta);

   public boolean ehSelicHabilitadoMMG(CodigoIF codigoIF);

   public ContaParticipanteDO obterContaGarantiaSelic(CodigoContaCetip contaCetip);

   public ContaParticipanteDO obterContaSelic(CodigoContaCetip contaCetip);

   public List obterGarantiasSelic(CestaGarantiasDO cesta);
     
}
