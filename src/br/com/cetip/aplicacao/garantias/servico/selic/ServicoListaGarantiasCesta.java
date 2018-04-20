package br.com.cetip.aplicacao.garantias.servico.selic;

import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;

/**
 * Servico de consulta SOMENTE as garantias selic de determinada cesta.
 *
 * @author <a href="mailto:daniela@summa.com.br">Daniela Pistelli Gomes</a>
 *
 * @requisicao.class
 *
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 *
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_ACESSO"
 *
 * @resultado.class
 *
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_ITENS"
 *
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="TIPO_IF"
 *
 * @resultado.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_IF"
 *
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="TIPO_GARANTIA"
 *
 * @resultado.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_QUANTIDADE"
 *
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIAS_ITENS"
 *
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 *
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 *
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 *
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_PARTICIPANTE"
 *
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_CONTRAPARTE"
 *
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="GARANTIAS_DATA_CRIACAO"
 *
 * @resultado.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_LIBERAR_QUANTIDADE"
 *
 * @resultado.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="ACAO"
 *
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_ITENS"
 *
 * @resultado.method atributo="DescricaoLimitada" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_ITENS"
 *
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="INADIMPLENTE"
 *
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="INADIMPLENTE_EMISSOR"
 *
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIDO"
 *
 * @resultado.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIDOR"
 *
 * @resultado.method atributo="TextoLimitado" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="CESTA_GARANTIA"
 *
 * @resultado.method atributo="NumeroOperacao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="OPERACAO"
 *
 */
public class ServicoListaGarantiasCesta extends br.com.cetip.aplicacao.garantias.servico.ServicoListaGarantiasCesta {

   public Resultado executar(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoListaGarantiasCesta req = (RequisicaoServicoListaGarantiasCesta) requisicao;

      br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaGarantiasCesta reqsuper = obterRequisicao(req);

      ResultadoServicoListaGarantiasCesta response = obterResultado((br.com.cetip.aplicacao.garantias.servico.ResultadoServicoListaGarantiasCesta) super
            .executarConsulta(reqsuper));

      return response;
   }

   protected String obtemHQLGarantias() {
      StringBuffer hql = new StringBuffer(2000);
      hql.append(super.obtemHQLGarantias());
      hql.append(" and ativo.sistema = " + SistemaDO.SELIC);
      return hql.toString();
   }

   protected String obtemHQLMovimentacoes() {
      StringBuffer hql = new StringBuffer(2000);
      hql.append(super.obtemHQLMovimentacoes());
      hql.append(" and ativo.sistema = " + SistemaDO.SELIC);
      return hql.toString();
   }

   private br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaGarantiasCesta obterRequisicao(
         RequisicaoServicoListaGarantiasCesta req) {
      AtributosColunados atCol = req.obterAtributosColunados();
      br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaGarantiasCesta reqsuper = new br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaGarantiasCesta();
      atCol.reiniciarLinha();
      atCol.reiniciarColuna();

      while (atCol.avancarColuna()) {
         Atributo attr = atCol.obterAtributo();
         if (attr != null) {
            reqsuper.atributo(attr);
         }
      }
      return reqsuper;
   }

   private ResultadoServicoListaGarantiasCesta obterResultado(
         br.com.cetip.aplicacao.garantias.servico.ResultadoServicoListaGarantiasCesta req) {
      AtributosColunados atCol = req.obterAtributosColunados();
      ResultadoServicoListaGarantiasCesta responseSelic = new ResultadoServicoListaGarantiasCesta();
      atCol.reiniciarLinha();
      atCol.reiniciarColuna();

      for (int i = 0; i < atCol.obterNumeroDeLinhas(); i++) {
         while (atCol.avancarColuna()) {
            Atributo attr = atCol.obterAtributo();
            if (attr != null) {
               responseSelic.atribuirAtributo(attr);
            }
         }
         atCol.avancarLinha();
         atCol.reiniciarColuna();
         responseSelic.novaLinha();
      }

      return responseSelic;
   }

}
