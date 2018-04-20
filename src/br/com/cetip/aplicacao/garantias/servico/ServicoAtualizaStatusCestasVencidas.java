package br.com.cetip.aplicacao.garantias.servico;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.roteador.Roteador;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.class
 */
public class ServicoAtualizaStatusCestasVencidas extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      RequisicaoServicoAtualizaStatusCestasVencidas r = (RequisicaoServicoAtualizaStatusCestasVencidas) req;
      Data data = r.obterGARANTIAS_CODIGO_Data();

      atualizarCestasVencidas(data);

      return new ResultadoServicoAtualizaStatusCestasVencidas();
   }

   private void atualizarCestasVencidas(Data dataParam) {
      Data data = Condicional.vazio(dataParam) ? getDataHoje() : dataParam;

      Collection listaCestas = listarCestasExpiradas(data);
      Iterator itCestas = listaCestas.iterator();

      ContextoAtivacaoVO ca = getContextoAtivacao();
      while (itCestas.hasNext()) {
         Id idCesta = (Id) itCestas.next();

         // Executa a exclusao/desvinculacao de cada cesta numa transacao separada
         RequisicaoServicoExcluirCestaGarantias req = new RequisicaoServicoExcluirCestaGarantias();
         req.atribuirGARANTIAS_CESTA_Id(idCesta);
         req.atribuirBATCH_Booleano(new Booleano(Booleano.FALSO));

         Roteador.executarAssincrono(req, ca);
      }
   }

   /**
    * Obtem cestas cuja data da ultima alteracao do status da cesta excedeu o prazo para vinculacao
    * 
    * Cestas nas seguintes situacoes sao obtidas:
    * <ul>
    * <li>01 - FINALIZADA</li>
    * <li>02 - INCOMPLETA</li>
    * <li>04 - EM MANUTENCAO</li>
    * <li>05 - EM EDICAO</li>
    * <li>12 - VNC PEND GRTDO </li>
    * <li>13 - VNC PEND GRTDOR</li>
    * <li>15 - VNC REJEITADA</li>
    * <li>18 - VNC ATIVO GRTDO</li>
    * <li>20 - VNC PENDENTE</li>
    * </ul>
    * 
    * <p>Tambem remove cestas ainda na situacao VINCULADA, mas que por algum motivo nao possuem mais ativos garantidos</p>
    * 
    * @param data
    * @return
    */
   private Collection listarCestasExpiradas(Data data) {
      StringBuffer hql = new StringBuffer();
      hql.append("select c.numIdCestaGarantias ");
      hql.append(" from CestaGarantiasDO c ");
      hql.append(" where c.datExclusao is null and ((c.datAlteracaoStatusCesta + c.numPrazoExpiracao < :data");
      hql.append(" and c.statusCesta.numIdStatusCesta in (:status)) or (c.statusCesta = :statusVinculada and size(c.ativosVinculados) = 0))");

      List status = new ArrayList();
      status.add(StatusCestaDO.FINALIZADA);
      status.add(StatusCestaDO.INCOMPLETA);
      status.add(StatusCestaDO.EM_MANUTENCAO);
      status.add(StatusCestaDO.EM_EDICAO);
      status.add(StatusCestaDO.VNC_PEND_GRTDO);
      status.add(StatusCestaDO.VNC_PEND_GRTDOR);
      status.add(StatusCestaDO.VINCULACAO_FALHOU);
      status.add(StatusCestaDO.VINCULADA_AO_ATIVO);
      status.add(StatusCestaDO.VNC_PENDENTE);

      IConsulta c = getGp().criarConsulta(hql.toString());
      c.setAtributo("data", data);
      c.setParameterList("status", status);
      c.setAtributo("statusVinculada", StatusCestaDO.VINCULADA);

      return c.list();
   }

   public Resultado executarConsulta(Requisicao r) throws Exception {
      throw new UnsupportedOperationException();
   }

}
