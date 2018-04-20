package br.com.cetip.aplicacao.garantias.servico.selic;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMensageriaGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IRespostaTransferenciaCustodia;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.TransferenciaCustodiaR1VO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoMensagem;
import br.com.cetip.infra.atributo.tipo.identificador.ISPB;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleSTR;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.SituacaoOperacaoSelic;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico responsavel pelo tratamento das mensagens de resposta do SELIC para a operacao de transferencia de custodia - SEL1021R1
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
 * @requisicao.method atributo="NumeroControleIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MENSAGEM"                                                                                                      
 *                   
 * @requisicao.method atributo="ISPB" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MENSAGEM"
 *  
 * @requisicao.method atributo="NumeroOperacao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 *                   
 * @requisicao.method atributo="SituacaoOperacaoSelic" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MENSAGEM"   
 *                                     
 * @requisicao.method atributo="NumeroControleSTR" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MENSAGEM"                  
 * 
 * @requisicao.method atributo="DataHora" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="SITUACAO_LANCAMENTO"
 * 
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="DATA_REFERENCIA"                        
 *                 
*/
public class ServicoRespostaTransferenciaCustodia extends BaseGarantias implements Servico {

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   public Resultado executar(Requisicao requisicao) throws Exception {

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Iniciando servico que processa mensagem SEL1021R1 ***** ");
      }

      RequisicaoServicoRespostaTransferenciaCustodia req = (RequisicaoServicoRespostaTransferenciaCustodia) requisicao;
      ResultadoServicoRespostaTransferenciaCustodia response = new ResultadoServicoRespostaTransferenciaCustodia();

      CodigoTipoMensagem codTipoMsg = req.obterMENSAGEM_CodigoTipoMensagem();
      NumeroControleIF nroControleIF = req.obterMENSAGEM_NumeroControleIF();
      ISPB ISPBIF = req.obterMENSAGEM_ISPB();
      NumeroControleSTR nroControleSTR = req.obterMENSAGEM_NumeroControleSTR();
      SituacaoOperacaoSelic sitOperacao = req.obterMENSAGEM_SituacaoOperacaoSelic();
      NumeroOperacao numOperacao = req.obterINSTRUMENTO_FINANCEIRO_NumeroOperacao();

      TransferenciaCustodiaR1VO transferenciaR1 = new TransferenciaCustodiaR1VO(codTipoMsg, nroControleIF, ISPBIF,
            numOperacao, nroControleSTR, sitOperacao);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug("Numero da Operacao: " + numOperacao);
         Logger.debug("Situacao da Operacao: " + sitOperacao);
      }

      IGarantias gf = getFactory();
      IMensageriaGarantiasSelic imsgSelic = gf.getInstanceMensageriaSelic();
      imsgSelic.incluirRespostaNotificacaoTransferenciaCustodia(transferenciaR1);

      IMovimentacoesGarantias mg = gf.getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO movExterna = mg.obterMovimentacaoParaGarantiaExterna(numOperacao, null,
            StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1021R1);

      //Nao encontrou movimentacao para a operacao 
      if (movExterna == null) {
         Logger.warn(this, "Nao foi encontrada movimentacao referente ao numero de operacao: " + numOperacao);
         return response;
      }
      
      IRespostaTransferenciaCustodia resposta = gf.getInstanceRespostaTransferenciaCustodia(sitOperacao);
      if ( resposta != null) {
    	  resposta.processar(movExterna);
      }
      return response;
   }
}
