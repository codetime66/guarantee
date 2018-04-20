package br.com.cetip.aplicacao.garantias.servico.selic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoExcluirCestaGarantias;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.roteador.Roteador;

/**
 * Servico executado na abertura da grade CTP11 com o objetivo de cancelar as cestas
 * nas quais o processo de vinculacao nao foi finalizado. 
 * 
 * Situacao que ocorre com cesta que contenham garantias selic.
 * Garantias ja processadas sao retiradas (lancamento do garantido) e deverao ser confirmadas (SELIC)
 * pelo garantidor
 * Garantias aguardando notificacao do (SELIC) sao canceladas.
 * Apos cancelar/retirar todas as garantias a cesta eh cancelada enquanto isto nao ocorre o status da cesta
 * permanece como GRT_EXPIRADAS   
 * 
 * @requisicao.class
 * 
 * 
 * @resultado.class
 * 
 */
public class ServicoCancelaCestasVinculacaoIncompleta extends BaseGarantias implements Servico {
	
	public Resultado executarConsulta(Requisicao arg0) throws Exception {
		throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
	}

	public Resultado executar(Requisicao requisicao) throws Exception {
		Collection listaCestas = listarCestasVinculacaoIncompleta();
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
		return new ResultadoServicoCancelaCestasVinculacaoIncompleta();
	}
	
	private Collection listarCestasVinculacaoIncompleta() {
	      StringBuffer hql = new StringBuffer();
	      hql.append("select c.numIdCestaGarantias ");
	      hql.append(" from CestaGarantiasDO c ");
	      hql.append(" where c.datExclusao is null ");
	      hql.append(" and c.statusCesta.numIdStatusCesta in (:status)");

	      List status = new ArrayList(2);
          status.add(StatusCestaDO.EM_VINCULACAO);
          status.add(StatusCestaDO.GRT_EXPIRADAS);
          
	      IConsulta c = getGp().criarConsulta(hql.toString());
	      c.setParameterList("status", status);

	      return c.list();
	   }

}
