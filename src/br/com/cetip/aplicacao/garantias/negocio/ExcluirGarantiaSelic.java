package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.ITipoIF;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.TipoIFFactory;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

/**
 * 
 * @author SummaTech
 */
final class ExcluirGarantiaSelic extends ExcluirGarantia {

   public void excluirGarantia(DetalheGarantiaDO garantia) {
      IGerenciadorPersistencia gp = getGp();
      IMovimentacoesGarantias mg = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO movBloqueio = mg.obterMovimentacaoParaAtivo(garantia.getCestaGarantias(), garantia
            .getInstrumentoFinanceiro().getCodigoIF(), TipoMovimentacaoGarantiaDO.BLOQUEIO,
            StatusMovimentacaoGarantiaDO.OK);
      movBloqueio.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.MOVIMENTACAO_CANCELADA);
      movBloqueio.setNumOperacao(new NumeroOperacao(""));
      movBloqueio.setDataMovimentacao(getDataHoraHoje());
      gp.delete(garantia);
      gp.saveOrUpdate(movBloqueio);
   }

   public void registrar(TiposExclusaoGarantia f) {
      ITipoIF t = TipoIFFactory.getInstance();
      List l = t.obterTiposSelicados();

      for (Iterator i = l.iterator(); i.hasNext();) {
         TipoIFDO tdo = (TipoIFDO) i.next();
         CodigoTipoIF codTipoIF = tdo.getCodigoTipoIF();
         f.registrar(codTipoIF, this);
      }
   }

}
