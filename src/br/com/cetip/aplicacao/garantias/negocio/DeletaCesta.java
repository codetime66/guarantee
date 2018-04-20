package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IDeletaCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IExcluirGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

class DeletaCesta extends BaseGarantias implements IDeletaCesta {

   protected Data dataHoje;
   protected CestaGarantiasDO cesta;
   protected Booleano indBatch;

   public void deletaCestaGarantias(CestaGarantiasDO c, Booleano iBatch, Data dataOperacao) {
      dataHoje = dataOperacao == null ? getDataHoje() : dataOperacao;
      cesta = c;
      indBatch = iBatch;

      verificaPendencias();
      cancelaMovimentacoesNaoProcessadas();
      desbloqueiaGarantias();
      cancelaCesta();
   }

   protected void cancelaCesta() {
      IMovimentacoesGarantias img = getFactory().getInstanceMovimentacoesGarantias();
      img.incluirMovimentacaoExclusao(cesta);

      cesta.setStatusCesta(StatusCestaDO.CANCELADA);
      cesta.setDatAlteracaoStatusCesta(dataHoje);
      cesta.setDatExclusao(dataHoje);
   }

   protected void desbloqueiaGarantias() {
      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      List garantias = icg.listarGarantiasCesta(cesta.getNumCestaGarantias());

      for (Iterator i = garantias.iterator(); i.hasNext();) {
         DetalheGarantiaDO dg = (DetalheGarantiaDO) i.next();
         CodigoTipoIF codTipoIF = dg.getCodigoTipoIF();

         IExcluirGarantia eg = getFactory().getInstanceExcluirGarantia(codTipoIF);
         eg.setDataOperacao(dataHoje);
         eg.setIndBatch(indBatch);
         eg.excluirGarantia(dg);
      }
   }

   protected void cancelaMovimentacoesNaoProcessadas() {
      IMovimentacoesGarantias img = getFactory().getInstanceMovimentacoesGarantias();

      // cancela todas as movimentacoes pendentes
      List movsPendentes = img.listarMovimentacoes(cesta, null, new Object[] { StatusMovimentacaoGarantiaDO.PENDENTE });
      Iterator imp = movsPendentes.iterator();
      while (imp.hasNext()) {
         MovimentacaoGarantiaDO mov = (MovimentacaoGarantiaDO) imp.next();
         mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.MOVIMENTACAO_CANCELADA);
      }
   }

   protected void verificaPendencias() {
      IMovimentacoesGarantias img = getFactory().getInstanceMovimentacoesGarantias();
      List movsEmProc = img.listarMovimentacoes(cesta, null,
            new Object[] { StatusMovimentacaoGarantiaDO.PENDENTE_ATUALIZA });

      if (movsEmProc.size() > 0) {
         throw new Erro(CodigoErro.ACAO_INVALIDA_CESTA, "Existem movimentacoes em andamento para a cesta: "
               + cesta.getNumIdCestaGarantias());
      }
   }

   public void registrar(TiposDelecaoCesta i) {
      i.registrar(StatusCestaDO.FINALIZADA, this);
      i.registrar(StatusCestaDO.INCOMPLETA, this);
      i.registrar(StatusCestaDO.EM_EDICAO, this);
      i.registrar(StatusCestaDO.VNC_PEND_GRTDO, this);
      i.registrar(StatusCestaDO.VNC_PEND_GRTDOR, this);
      i.registrar(StatusCestaDO.VINCULACAO_FALHOU, this);
      i.registrar(StatusCestaDO.VINCULADA_AO_ATIVO, this);
      i.registrar(StatusCestaDO.VNC_PENDENTE, this);
   }

}
