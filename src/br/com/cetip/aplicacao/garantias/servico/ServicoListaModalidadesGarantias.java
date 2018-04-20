package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico que lista as modalidades disponiveis para a interface grafica de vinculacao de cesta de garantias.
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vinicius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @since Maio/2006
 * 
 * @requisicao.class
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MODALIDADE_LIQUIDACAO"
 * 
 */
public class ServicoListaModalidadesGarantias extends BaseGarantias implements Servico {

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      return null;
   }

   /*
    * Retorna a listagem de operacoes disponiveis.
    * 
    * @param requisicao requisicao que contem os dados para consulta
    * @return o Resultado contendo a lista de operacoes
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      ResultadoServicoListaModalidadesGarantias resultado = new ResultadoServicoListaModalidadesGarantias();
      resultado.novaLinha();
      resultado.atribuirMODALIDADE_LIQUIDACAO_Id(new Id("", ""));
      resultado.novaLinha();
      resultado.atribuirMODALIDADE_LIQUIDACAO_Id(new Id("SEM MODALIDADE DE LIQUIDACAO", "6"));
      resultado.novaLinha();
      resultado.atribuirMODALIDADE_LIQUIDACAO_Id(new Id("CETIP", "1"));
      resultado.novaLinha();
      resultado.atribuirMODALIDADE_LIQUIDACAO_Id(new Id("BRUTA", "2"));
      return resultado;
   }

}
