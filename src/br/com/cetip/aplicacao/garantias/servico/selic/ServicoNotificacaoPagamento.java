package br.com.cetip.aplicacao.garantias.servico.selic;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMensageriaGarantiasSelic;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicOperacaoDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.NotificacaoEventoVO;
import br.com.cetip.dados.aplicacao.operacao.OperacaoDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.TipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoMensagem;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.ISPB;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
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
 * Servico responsavel pelo tratamento das mensagens de notificacao de Pagamento enviadas pelo SELIC - SEL1611 (TpInfEvtSEL="1")
 * 
 * @author <a href="mailto:daniela@summa.com.br">Daniela Pistelli Gomes</a>
 * @since Março/2009
 * 
 * @resultado.class
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
 * @requisicao.method atributo="SituacaoOperacaoSelic" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MENSAGEM"                  
 *                   
 * @requisicao.method atributo="TipoOperacaoSelic" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="MENSAGEM" 
 *                   
 * @requisicao.method atributo="NumeroControleSTR" pacote="br.com.cetip.infra.atributo.tipo.identificador"
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
 *                   contexto="INSTRUMENTO_FINANCEIRO_VALOR_FINANCEIRO_ATUALIZADO"
 *                   
 * @requisicao.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="INSTRUMENTO_FINANCEIRO_PRECO_UNITARIO_ATUALIZADO"   
 *                   
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="DATA_OCORRENCIA"                                                                                                                                                                  
*/
public class ServicoNotificacaoPagamento extends BaseGarantias implements Servico {

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   public Resultado executar(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Iniciando servico que processa mensagem SEL1611 TpInfEvtSEL=1 ***** ");
      }

      RequisicaoServicoNotificacaoPagamento req = (RequisicaoServicoNotificacaoPagamento) requisicao;
      ResultadoServicoNotificacaoPagamento response = new ResultadoServicoNotificacaoPagamento();

      NumeroOperacao numOperacao = req.obterINSTRUMENTO_FINANCEIRO_NumeroOperacao();
      CodigoContaSelic contaCedente = req.obterCEDENTE_CodigoContaSelic();
      CodigoContaSelic contaCessionario = req.obterCESSIONARIO_CodigoContaSelic();

      CodigoIF codigoIF = req.obterINSTRUMENTO_FINANCEIRO_CodigoIF();
      Data dataVenc = req.obterINSTRUMENTO_FINANCEIRO_VENCIMENTO_Data();
      Data dataMovimentacao = req.obterDATA_OCORRENCIA_Data();
      Quantidade quantidade = req.obterINSTRUMENTO_FINANCEIRO_Quantidade();
      ValorMonetario precoUnitario = req.obterINSTRUMENTO_FINANCEIRO_PRECO_UNITARIO_ATUALIZADO_ValorMonetario();
      ValorMonetario valorFinanceiro = req.obterINSTRUMENTO_FINANCEIRO_VALOR_FINANCEIRO_ATUALIZADO_ValorMonetario();

      CodigoTipoMensagem codTipoMsg = req.obterMENSAGEM_CodigoTipoMensagem();
      ISPB ISPBIF = req.obterMENSAGEM_ISPB();
      CodigoTipoOperacaoSelic codOperSelic = req.obterMENSAGEM_CodigoTipoOperacaoSelic();
      SituacaoOperacaoSelic sitOperacao = req.obterMENSAGEM_SituacaoOperacaoSelic();
      TipoOperacaoSelic tpOperSelic = req.obterMENSAGEM_TipoOperacaoSelic();
      NumeroControleSTR nroControleSTR = req.obterMENSAGEM_NumeroControleSTR();

      NotificacaoEventoVO notificacao = new NotificacaoEventoVO(codTipoMsg, ISPBIF, codOperSelic, numOperacao,
            contaCedente, contaCessionario, tpOperSelic, codigoIF, dataVenc, precoUnitario, quantidade,
            valorFinanceiro, nroControleSTR, sitOperacao);

      IGarantias gf = getFactory();
      //Armazena os dados da mensagem para controle
      IMensageriaGarantiasSelic imsgSelic = gf.getInstanceMensageriaSelic();
      MensagemSelicDO msgSelic = imsgSelic.incluirNotificacaoEvento(notificacao);

      //CodigoIF recebido na msg nao corresponde ao codigoIF do ativo na cetip (codigoIF+dataVenc)
      Id numIf = msgSelic.getNumIF();

      IGarantiasSelic gs = gf.getInstanceGarantiasSelic();
      List operacoes = gs.processarNotificacaoEvento(notificacao, numIf, dataMovimentacao);

      //Armazena os dados da mensagem x operacao para controle
      if (operacoes != null) {
         Iterator it = operacoes.iterator();
         while (it.hasNext()) {
            OperacaoDO op = (OperacaoDO) it.next();
            MensagemSelicOperacaoDO msgSelicOper = new MensagemSelicOperacaoDO();
            msgSelicOper.setMensagemSelic(msgSelic);
            msgSelicOper.setNumIdOperacao(op.getId());
            getGp().save(msgSelicOper);
         }
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Finalizando servico que processa mensagem SEL1611 TpInfEvtSEL=1 ***** ");
      }
      return response;
   }

}
