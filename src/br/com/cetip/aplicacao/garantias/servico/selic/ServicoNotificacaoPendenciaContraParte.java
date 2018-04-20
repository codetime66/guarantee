package br.com.cetip.aplicacao.garantias.servico.selic;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMensageriaGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.NotificacaoPendenciaContraParteVO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.TipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoMensagem;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.ISPB;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleSTR;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.SituacaoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.ValorMonetario;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico responsavel pelo tratamento das mensagens de notificacao de Pendencia de ContraParte enviadas pelo SELIC - SEL1611 (TpInfEvtSEL="2")
 * 
 * @author <a href="mailto:daniela@summa.com.br">Daniela Pistelli Gomes</a>
 * @since Março/2009
 * 
 * @resultado.class
 * 
 * 
 *                   
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoTipoMensagem" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MENSAGEM"                    
 *                   
 * @requisicao.method atributo="ISPB" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MENSAGEM"   
 *                   
 * @requisicao.method atributo="TipoNotificacaoSelic" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="MENSAGEM"                  
 *                   
 * @requisicao.method atributo="CodigoTipoOperacaoSelic" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MENSAGEM"
 * 
 * @requisicao.method atributo="NumeroOperacao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 *                   
 * @requisicao.method atributo="CodigoContaSelic" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CEDENTE"
 *                   
 * @requisicao.method atributo="CodigoContaSelic" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CESSIONARIO"
 * 
 * @requisicao.method atributo="TipoOperacaoSelic" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="MENSAGEM"                  
 *                   
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"  
 *                   
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="INSTRUMENTO_FINANCEIRO_VENCIMENTO"
 *                   
 * @requisicao.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @requisicao.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="INSTRUMENTO_FINANCEIRO_PRECO_UNITARIO_ATUALIZADO"                  
 *                   
 * @requisicao.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="INSTRUMENTO_FINANCEIRO_VALOR_FINANCEIRO_ATUALIZADO"
 *                   
 * @requisicao.method atributo="NumeroControleSTR" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MENSAGEM"                                                                                                      
 *                   
 * @requisicao.method atributo="SituacaoOperacaoSelic" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MENSAGEM"
*/
public class ServicoNotificacaoPendenciaContraParte extends BaseGarantias implements Servico {

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   public Resultado executar(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Iniciando servico que processa mensagem SEL1611 TpInfEvtSEL=2 ***** ");
      }

      RequisicaoServicoNotificacaoPendenciaContraParte req = (RequisicaoServicoNotificacaoPendenciaContraParte) requisicao;
      ResultadoServicoNotificacaoPendenciaContraParte response = new ResultadoServicoNotificacaoPendenciaContraParte();

      NumeroOperacao numOperacao = req.obterINSTRUMENTO_FINANCEIRO_NumeroOperacao();
      CodigoContaSelic contaCedente = req.obterCEDENTE_CodigoContaSelic();
      CodigoContaSelic contaCessionario = req.obterCESSIONARIO_CodigoContaSelic();

      CodigoIF codigoIF = req.obterINSTRUMENTO_FINANCEIRO_CodigoIF();
      Data dataVenc = req.obterINSTRUMENTO_FINANCEIRO_VENCIMENTO_Data();
      Quantidade quantidade = req.obterINSTRUMENTO_FINANCEIRO_Quantidade();
      ValorMonetario precoUnitario = req.obterINSTRUMENTO_FINANCEIRO_PRECO_UNITARIO_ATUALIZADO_ValorMonetario();
      ValorMonetario valorFinanceiro = req.obterINSTRUMENTO_FINANCEIRO_VALOR_FINANCEIRO_ATUALIZADO_ValorMonetario();

      CodigoTipoMensagem codTipoMsg = req.obterMENSAGEM_CodigoTipoMensagem();
      ISPB ISPBIF = req.obterMENSAGEM_ISPB();
      CodigoTipoOperacaoSelic codOperSelic = req.obterMENSAGEM_CodigoTipoOperacaoSelic();
      SituacaoOperacaoSelic sitOperacao = req.obterMENSAGEM_SituacaoOperacaoSelic();
      TipoOperacaoSelic tpOperSelic = req.obterMENSAGEM_TipoOperacaoSelic();
      NumeroControleSTR nroControleSTR = req.obterMENSAGEM_NumeroControleSTR();

      NotificacaoPendenciaContraParteVO notificacao = new NotificacaoPendenciaContraParteVO(codTipoMsg, ISPBIF,
            codOperSelic, numOperacao, contaCedente, contaCessionario, tpOperSelic, codigoIF, dataVenc, precoUnitario,
            quantidade, valorFinanceiro, nroControleSTR, sitOperacao);

      IGarantias gf = getFactory();
      //Armazena os dados da mensagem para controle
      IMensageriaGarantiasSelic imsgSelic = gf.getInstanceMensageriaSelic();
      MensagemSelicDO msgSelic = imsgSelic.incluirNotificacaoPendenciaContraParte(notificacao);

      //Obtem a movimentacao de garantia referente a mensagem recebida
      IMovimentacoesGarantias mg = gf.getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO movExterna = mg.obterMovimentacaoParaGarantiaExterna(numOperacao, null,
            StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611);

      //Nao encontrou movimentacao para a operacao 
      if (movExterna == null) {
         Logger.warn(this, "Nao foi encontrada movimentacao referente ao numero de operacao: " + numOperacao);
         return response;
      }

      IGarantiasSelic gSelic = gf.getInstanceGarantiasSelic();
      gSelic.registrarLancamentoTransferenciaCustodia(msgSelic, movExterna);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Finalizando servico que processa mensagem SEL1611 TpInfEvtSEL=2 ***** ");
      }
      return response;
   }

}
