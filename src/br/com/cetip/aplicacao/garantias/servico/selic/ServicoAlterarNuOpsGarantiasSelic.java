package br.com.cetip.aplicacao.garantias.servico.selic;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico para RETIRAR garantias de uma determinada Cesta
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIA"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="GARANTIA"
 *  
 * @requisicao.method atributo="NumeroOperacao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="OPERACAO"                 
 * 
 * @resultado.class
 * 
 */
public class ServicoAlterarNuOpsGarantiasSelic extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      RequisicaoServicoAlterarNuOpsGarantiasSelic req = (RequisicaoServicoAlterarNuOpsGarantiasSelic) requisicao;

      List idMovimentacoes = req.obterListaGARANTIA_Id();
      Booleano sohValidacao = req.obterGARANTIA_Booleano();
      List numerosOperacao = req.obterListaOPERACAO_NumeroOperacao();

      if (idMovimentacoes.isEmpty()) {
         return new ResultadoServicoAlterarNuOpsGarantiasSelic();
      }

      IGarantiasSelic gSelic = getFactory().getInstanceGarantiasSelic();
      Iterator i = numerosOperacao.iterator();
      while (i.hasNext()) {
         NumeroOperacao nuOp = (NumeroOperacao) i.next();
         if (Condicional.vazio(nuOp)) {

            throw new Erro(CodigoErro.CESTA_ALTERACAO_NUMERO_OPERACAO_INVALIDA);
         }

         if (nuOp.obterConteudo().length() != 6) {
            throw new Erro(CodigoErro.NUMERO_OPERACAO_DEVE_CONTER_SEIS_DIGITOS);
         }
         if (!gSelic.numeroOperacaoEhValido(nuOp)) {
            throw new Erro(CodigoErro.NUMERO_OPERACAO_INVALIDO);
         }
      }
      // Se era soh para validar, retorna
      if (!Condicional.vazio(sohValidacao) && sohValidacao.ehVerdadeiro()) {
         return new ResultadoServicoAlterarNuOpsGarantiasSelic();
      }

      gSelic.alterarNumerosOperacao(idMovimentacoes, numerosOperacao);

      return new ResultadoServicoAlterarNuOpsGarantiasSelic();
   }

   public Resultado executarConsulta(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

}
