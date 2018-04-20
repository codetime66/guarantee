package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ILiberarCestasInadimplentes;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * <p>
 * Servico executado via batch para LIBERAR automaticamente cestas inadimplentes que venceram dentro do prazo passivel
 * de liberacao manual pelo participante.
 * </p>
 * 
 * <p>
 * Este servico chama o metodo <code>liberarCestasInadimplentes</code> da API de Negocio do MMG em
 * {@link br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias ICestaDeGarantias}.
 * </p>
 * 
 * @see br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias#liberarCestasInadimplentes()
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                    contexto="OPERACAO"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CESTA"
 * 
 * @resultado.class
 * 
 * @since 24/08/2007
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ServicoLiberaCestasInadimplentes extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      RequisicaoServicoLiberaCestasInadimplentes r = (RequisicaoServicoLiberaCestasInadimplentes) req;

      Id id = r.obterGARANTIAS_CESTA_Id();
      Data data = r.obterOPERACAO_Data();

      ILiberarCestasInadimplentes dao = getFactory().getInstanceLiberarCestasInadimplentes();

      if (!Condicional.vazio(id)) {
         dao.liberarCestaInadimplente(id, data);
      } else {
         dao.liberarCestasInadimplentes(data);
      }

      return new ResultadoServicoAtualizaStatusCestasVencidas();
   }

   public Resultado executarConsulta(Requisicao r) throws Exception {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}
