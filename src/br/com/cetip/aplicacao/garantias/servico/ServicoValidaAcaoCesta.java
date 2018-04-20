package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidaAcoes;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico para validar se determinada acao pode ser executada para a cesta informada
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="ACAO"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_ACESSO"
 * 
 * @resultado.class
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ServicoValidaAcaoCesta extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao req) throws Exception {
      RequisicaoServicoValidaAcaoCesta reqA = (RequisicaoServicoValidaAcaoCesta) req;
      Funcao acao = reqA.obterACAO_Funcao();
      Funcao tipo = reqA.obterGARANTIAS_TIPO_ACESSO_Funcao();
      NumeroCestaGarantia num = reqA.obterGARANTIAS_CODIGO_NumeroCestaGarantia();

      IGarantias factory = getFactory();

      ICestaDeGarantias icg = factory.getInstanceCestaDeGarantias();
      IValidaAcoes ima = factory.getInstanceValidaAcoes();
      CestaGarantiasDO cesta = null;

      if (acao.mesmoConteudo(ICestaDeGarantias.CONSULTAR_HISTORICO)) {
         cesta = icg.obterCestaDeGarantias(num);
      } else {
         cesta = icg.obterCestaDeGarantias(num, tipo);
      }

      boolean podeExecutarAcao = ima.podeExecutarAcao(acao, tipo, cesta);

      if (!podeExecutarAcao) {
         Erro erro = new Erro(CodigoErro.ACAO_INVALIDA_CESTA);
         throw erro;
      }

      return new ResultadoServicoValidaAcaoCesta();
   }

}