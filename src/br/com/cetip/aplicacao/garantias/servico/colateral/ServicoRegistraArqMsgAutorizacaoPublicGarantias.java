package br.com.cetip.aplicacao.garantias.servico.colateral;

import java.util.ArrayList;
import java.util.List;

import br.com.cetip.aplicacao.registrooperacao.servico.garantias.RequisicaoServicoRegistrarAutorizacaoPublicGarantias;
import br.com.cetip.aplicacao.registrooperacao.servico.garantias.RequisicaoServicoRegistrarDesautorizacaoPublicGarantias;
import br.com.cetip.aplicacao.registrooperacao.servico.garantias.ResultadoServicoRegistrarAutorizacaoPublicGarantias;
import br.com.cetip.aplicacao.registrooperacao.servico.garantias.ResultadoServicoRegistrarDesautorizacaoPublicGarantias;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.servico.contexto.ContextoAtivacao;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.roteador.Servicos;

/**
 * @requisicao.class
 * 
 * @requisicao.method
 *     atributo="Texto"
 *     pacote="br.com.cetip.infra.atributo.tipo.texto"
 *     contexto="TIPO_ARQUIVO"
 *
 * @requisicao.method
 *     atributo="TipoLinha"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="TIPO_LINHA"
 *
 * @requisicao.method
 *     atributo="Funcao"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @requisicao.method
 *     atributo="CodigoIF"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="INSTRUMENTO_FINANCEIRO_CONTRATO" 
 *     
 * @requisicao.method
 *     atributo="CodigoContaCetip"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="PARTE_CONTRATO"
 *     
 *  @requisicao.method 
 *    atributo="CPFOuCNPJ"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="PARTE_CONTRATO"
 *      
 * @resultado.class
 *
 * @resultado.method
 *     atributo="CodigoOperacao"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="AUTORIZACAO"
 * 
 */
public class ServicoRegistraArqMsgAutorizacaoPublicGarantias implements Servico {
   private CodigoOperacao codOperacao;

   public Resultado executar(Requisicao requisicao) throws Exception {
      RequisicaoServicoRegistraArqMsgAutorizacaoPublicGarantias req = (RequisicaoServicoRegistraArqMsgAutorizacaoPublicGarantias) requisicao;
      ResultadoServicoRegistraArqMsgAutorizacaoPublicGarantias res = new ResultadoServicoRegistraArqMsgAutorizacaoPublicGarantias();

      ContextoAtivacaoVO contextoAtivacao = ContextoAtivacao.getContexto();
      Servicos servicos = new Servicos(contextoAtivacao);

      Funcao funcao = req.obterINSTRUMENTO_FINANCEIRO_Funcao();

      if (!funcao.mesmoConteudo(Funcao.INCLUSAO) && !funcao.mesmoConteudo(Funcao.EXCLUSAO)) {
         throw new Erro(CodigoErro.ACAO_NAO_EXISTE);
      }

      List atributosFixo = obterAtributosFixo(req);

      RequisicaoServicoValidaInclusaoAutorizacaoPublicGarantias reqValida = new RequisicaoServicoValidaInclusaoAutorizacaoPublicGarantias();
      servicos.preencherRequisicao(reqValida, atributosFixo);
      servicos.executar(reqValida);

      if (funcao.mesmoConteudo(Funcao.INCLUSAO)) {

         RequisicaoServicoRegistrarAutorizacaoPublicGarantias reqPrinc = new RequisicaoServicoRegistrarAutorizacaoPublicGarantias();
         servicos.preencherRequisicao(reqPrinc, atributosFixo);
         ResultadoServicoRegistrarAutorizacaoPublicGarantias resPrinc = (ResultadoServicoRegistrarAutorizacaoPublicGarantias) servicos
               .executar(reqPrinc);
         codOperacao = resPrinc.obterAUTORIZACAO_CodigoOperacao();

      } else if (funcao.mesmoConteudo(Funcao.EXCLUSAO)) {

         RequisicaoServicoRegistrarDesautorizacaoPublicGarantias reqPrinc = new RequisicaoServicoRegistrarDesautorizacaoPublicGarantias();
         servicos.preencherRequisicao(reqPrinc, atributosFixo);
         ResultadoServicoRegistrarDesautorizacaoPublicGarantias resPrinc = (ResultadoServicoRegistrarDesautorizacaoPublicGarantias) servicos
               .executar(reqPrinc);
         codOperacao = resPrinc.obterAUTORIZACAO_CodigoOperacao();

      } else {
         throw new Erro(CodigoErro.ACAO_NAO_EXISTE);
      }

      // retorna o codigo do IF
      res.atribuirAUTORIZACAO_CodigoOperacao(codOperacao);

      return res;
   }

   /* (non-Javadoc)
    * @see br.com.cetip.infra.servico.interfaces.Servico#executarConsulta(br.com.cetip.infra.servico.interfaces.Requisicao)
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      return null;
   }

   /**
     * Retorna lista com os atributos da parte fixa (dados do titulo)
    * @param req
    * @return List<Atributo>
    */
   private List obterAtributosFixo(Requisicao req) {
      List atributos = new ArrayList();
      AtributosColunados atCol = req.obterAtributosColunados();
      atCol.reiniciarLinha();
      atCol.reiniciarColuna();
      while (atCol.avancarColuna()) {
         Atributo attr = atCol.obterAtributo();
         if (attr != null) {
            atributos.add(attr);
         }
      }
      return atributos;
   }
}