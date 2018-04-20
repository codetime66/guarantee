package br.com.cetip.aplicacao.garantias.negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IExcluirGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.persistencia.IConsulta;

/**
 * Delecao da cesta eh na verdade o cancelamento/garantias expiradas da cesta 
 * Qdo houverem garantias selic ja confirmadas estas garantias deverao ser RETIRADAS
 * e a cesta so estara no status cancelada apos a confirmacao da retirada destas garantias
 * @author Daniela Pistelli Gomes
 *
 */
public class DeletaCestaVinculacaoIncompleta extends DeletaCesta {
	
	private List detalheGarantiasMovimentadasSelic = Collections.EMPTY_LIST;
	
	public void deletaCestaGarantias(CestaGarantiasDO c, Booleano iBatch, Data dataOperacao) {
      dataHoje = dataOperacao == null ? getDataHoje() : dataOperacao;
      cesta = c;
      indBatch = iBatch;

      verificaPendencias();
      cancelaMovimentacoesNaoProcessadas();
      detalheGarantiasMovimentadasSelic = obtemDetalheGarantiasMovimentadasSelic();
      desbloqueiaGarantias();
      
      if ( !detalheGarantiasMovimentadasSelic.isEmpty()) {
    	  comandarRetiradaGarantiaSelic();
    	  alteraStatusCesta();
      }
      else {
    	  cancelaCesta();
      }
   }
	
	protected void cancelaMovimentacoesNaoProcessadas(){
		super.cancelaMovimentacoesNaoProcessadas();
		IMovimentacoesGarantias img = getFactory().getInstanceMovimentacoesGarantias();
		List movsPendentesSelic = img.listarMovimentacoes(cesta, new Object[] {TipoMovimentacaoGarantiaDO.CONTROLE_FLUXO_SELIC, TipoMovimentacaoGarantiaDO.RETIRADA}, new Object[] { StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611, StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1021R1 });
		Iterator imp = movsPendentesSelic.iterator();
		while (imp.hasNext()) {
			MovimentacaoGarantiaDO mov = (MovimentacaoGarantiaDO) imp.next();
			mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.EXPIRADA);
			mov.setDataMovimentacao(getDataHoraHoje());
			mov.setNumOperacao(null);//necessario pois a data foi atualizada e nro de operacao soh eh valido por um dia
		}
	}
	
	protected void desbloqueiaGarantias() {
		ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
		List garantias = icg.listarGarantiasCesta(cesta.getNumCestaGarantias());
		garantias.removeAll(detalheGarantiasMovimentadasSelic);

		for (Iterator i = garantias.iterator(); i.hasNext();) {
			DetalheGarantiaDO dg = (DetalheGarantiaDO) i.next();
			CodigoTipoIF codTipoIF = dg.getCodigoTipoIF();

			IExcluirGarantia eg = getFactory().getInstanceExcluirGarantia(codTipoIF);
			eg.setDataOperacao(dataHoje);
			eg.setIndBatch(indBatch);
			eg.excluirGarantia(dg);
		}
	}

	private void alteraStatusCesta() {
      cesta.setStatusCesta(StatusCestaDO.GRT_EXPIRADAS);
      cesta.setDatAlteracaoStatusCesta(dataHoje);
    }
	
	private List obtemDetalheGarantiasMovimentadasSelic(){
		IMovimentacoesGarantias img = getFactory().getInstanceMovimentacoesGarantias();
		//Obtem as garantias que ja foram processadas - Status.Ok para as quais sera necessario comandar retirada
		List movSelicOk = img.listarMovimentacoes(cesta, 
	    		  new Object[] { TipoMovimentacaoGarantiaDO.CONTROLE_FLUXO_SELIC }, new Object[] { StatusMovimentacaoGarantiaDO.OK });
		
		Iterator it = movSelicOk.iterator();
		MovimentacaoGarantiaDO movSelic = null;
		List numIdGarantiasSelic = new ArrayList(movSelicOk.size());
				
		while ( it.hasNext()){
			movSelic = (MovimentacaoGarantiaDO) it.next();
			numIdGarantiasSelic.add(movSelic.getInstrumentoFinanceiro().getId());
		}
		
		if ( numIdGarantiasSelic.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return obtemDetalheGarantias(numIdGarantiasSelic);
	}
	
	private void comandarRetiradaGarantiaSelic(){
		
		Iterator itDetalhe = detalheGarantiasMovimentadasSelic.iterator();
		IGarantiasSelic gs = getFactory().getInstanceGarantiasSelic();
		IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
		
		DetalheGarantiaDO detalhe = null;
		MovimentacaoGarantiaDO movimentacao = null;
	      
		while ( itDetalhe.hasNext()){
			detalhe = (DetalheGarantiaDO) itDetalhe.next();
			movimentacao = imov.incluirMovimentacaoRetirada(detalhe, detalhe.getQuantidadeGarantia());
	        gs.lancarTransferenciaCustodia(movimentacao, ICestaDeGarantias.FUNCAO_GARANTIDO);
		}
	}
	
	private List obtemDetalheGarantias(List garantias) {
		StringBuffer hql = new StringBuffer(200);
        hql.append("select d ");
        hql.append(" from ");
        hql.append(DetalheGarantiaDO.class.getName());
        hql.append(" d left join d.instrumentoFinanceiro ativo left join ativo.sistema sistema");
		hql.append(" where d.cestaGarantias = :cesta and ");
		hql.append(" d.cestaGarantias.datExclusao is null and");
		hql.append(" d.instrumentoFinanceiro in (:ativos) and ");
		hql.append(" d.quantidadeGarantia > 0 and ");
		hql.append(" sistema.numero = :sistema ");
		
		IConsulta consulta = getGp().criarConsulta(hql.toString());
		consulta.setAtributo("cesta", cesta);
		consulta.setParameterList("ativos", garantias.toArray());
		consulta.setAtributo("sistema", SistemaDO.SELIC);
		List l = consulta.list();
		if (l == null) {
			return Collections.EMPTY_LIST;
		}
		return l;
   }	
	
	public void registrar(TiposDelecaoCesta i) {
      i.registrar(StatusCestaDO.EM_VINCULACAO, this);
      i.registrar(StatusCestaDO.GRT_EXPIRADAS, this);
	}


}
