package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IDeletaCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * <p>Servico para exclusao de cestas de garantias</p>
 * 
 * <p>A exclusao da cesta eh realizada de diferentes formas, dependendo da situacao da mesma:
 * <ul>
 *    <li>Cestas Vinculadas tem suas garantias retiradas</li>
 *    <li>Cestas em manutencao sao excluidas do banco de dados</li>
 *    <li>Cestas em outras situacoes, as garantias sao desbloqueadas e a cesta marcada como cancelada</li>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vincius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @since Abril/2006
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CESTA"
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CESTA"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="BATCH"
 * 
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                    contexto="OPERACAO"
 * 
 */
public class ServicoExcluirCestaGarantias extends BaseGarantias implements Servico {

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "*** Servico de Exclusao/Desvinculacao de Cestas de Garantias ***");
      }

      RequisicaoServicoExcluirCestaGarantias req = (RequisicaoServicoExcluirCestaGarantias) requisicao;
      ResultadoServicoExcluirCestaGarantias res = new ResultadoServicoExcluirCestaGarantias();

      IGarantias factory = getFactory();

      Id idCesta = req.obterGARANTIAS_CESTA_Id();
      Booleano indBatch = req.obterBATCH_Booleano();
      Data dataOperacao = req.obterOPERACAO_Data();

      if (Condicional.vazio(indBatch)) {
         indBatch = Booleano.FALSO;
      }

      ICestaDeGarantias dao = factory.getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = dao.obterCestaDeGarantias(new NumeroCestaGarantia(idCesta.obterConteudo()));

      StatusCestaDO stCesta = cesta.getStatusCesta();

      IDeletaCesta deletaCesta = factory.getInstanceDeletaCesta(stCesta);

      if (deletaCesta != null) {
         deletaCesta.deletaCestaGarantias(cesta, indBatch, dataOperacao);
      } else {
         dao.verificaNecessidadeDesvincularCesta(cesta);
      }

      res.atribuirGARANTIAS_CESTA_Id(idCesta);

      return res;
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      return null;
   }

}