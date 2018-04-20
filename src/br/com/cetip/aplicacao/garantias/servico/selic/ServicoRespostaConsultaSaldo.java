package br.com.cetip.aplicacao.garantias.servico.selic;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMensageriaGarantiasSelic;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.mensageria.selic.ConsultaSaldoR1VO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoMensagem;
import br.com.cetip.infra.atributo.tipo.identificador.ISPB;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleIF;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico responsavel pelo tratamento das mensagens de resposta da consulta de operacoes SEL1081R1
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
 * @requisicao.method atributo="NumeroControleIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MENSAGEM"                                    
 *                   
 * @requisicao.method atributo="ISPB" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MENSAGEM"
 *
 * @requisicao.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 *                   
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="DATA_REFERENCIA"                  
*/

public class ServicoRespostaConsultaSaldo extends BaseGarantias implements Servico {

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   public Resultado executar(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Iniciando servico que processa mensagem SEL1081R1  ***** ");
      }

      RequisicaoServicoRespostaConsultaSaldo req = (RequisicaoServicoRespostaConsultaSaldo) requisicao;
      ResultadoServicoRespostaConsultaSaldo response = new ResultadoServicoRespostaConsultaSaldo();

      CodigoTipoMensagem codTipoMsg = req.obterMENSAGEM_CodigoTipoMensagem();
      NumeroControleIF nroControleIF = req.obterMENSAGEM_NumeroControleIF();
      ISPB ISPBIF = req.obterMENSAGEM_ISPB();
      Quantidade saldo = req.obterINSTRUMENTO_FINANCEIRO_Quantidade();
      ConsultaSaldoR1VO consultaR1 = new ConsultaSaldoR1VO(codTipoMsg, nroControleIF, ISPBIF, saldo);

      IGarantias gf = getFactory();
      //Armazena os dados da mensagem para controle
      IMensageriaGarantiasSelic imsgSelic = gf.getInstanceMensageriaSelic();
      imsgSelic.incluirRespostaConsultaDeSaldo(consultaR1);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Finalizando servico que processa mensagem SEL1081R1 ***** ");
      }
      return response;
   }

}
