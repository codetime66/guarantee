package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Date;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IDetalheGarantia;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.operacao.OperacaoDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.DataHora;
import br.com.cetip.infra.atributo.tipo.tempo.Hora;

/**
 * @author marco sergio
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class DetalheGarantia extends BaseGarantias implements IDetalheGarantia {

   /**
    * <p>
    * Insere ou altera detalhes de operacoes na tabela DETALHE_GARANTIA
    * </p>
    * 
    * <p>
    * Operacoes:<br>
    * <ul>
    * <li><b>BLOQUEIO</b> Operacao 889</li>
    * <li><b>DESBLOQUEIO</b> Operacao 890</li>
    * <li><b>APORTE</b> Operacao 893</li>
    * <li><b>RETIRADA</b> Operacao 894</li>
    * <li><b>LIBERACAO</b> Operacao 895</li>
    * </ul>
    * </p>
    */
   public void insereAlteraDetalheGarantia(OperacaoDO operacaoDO) {
      NumeroCestaGarantia numCesta = new NumeroCestaGarantia(operacaoDO.getIdCestaGarantias());
      CestaGarantiasDO cestaGarantiasDO = getFactory().getInstanceCestaDeGarantias().obterCestaDeGarantias(numCesta);

      DetalheGarantiaDO detGarantia = obterDetalheGarantia(cestaGarantiasDO, operacaoDO);

      Quantidade quantidade = calculaQuantidade(operacaoDO, detGarantia);
      detGarantia.setQuantidadeGarantia(quantidade);

      getGp().saveOrUpdate(detGarantia);
   }

   private Quantidade calculaQuantidade(OperacaoDO operacaoDO, DetalheGarantiaDO detGarantia) {
      CodigoTipoOperacao codTipoOperacao = operacaoDO.getTipoOperObjetoServ().getTipoOperacao().getCodTipoOperacao();
      Quantidade quantidade = null;

      boolean aporte = codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_APORTE_GARANTIA);
      boolean bloqueio = codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_BLOQUEIO_GARANTIA);
      boolean retirada = codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_RETIRADA_GARANTIA);
      boolean liberacao = codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_LIBERACAO_GARANTIA);
      boolean desbloqueio = codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_DESBLOQUEIO_GARANTIA);
      boolean retiradaLiberacaoDesbloqueio = retirada || liberacao || desbloqueio;

      if (aporte) {
         quantidade = operacaoDO.getQtdOperacaoDecimal().somar(detGarantia.getQuantidadeGarantia());
      } else if (bloqueio) {
         quantidade = operacaoDO.getQtdOperacaoDecimal();
      } else if (retiradaLiberacaoDesbloqueio) {
         quantidade = detGarantia.getQuantidadeGarantia().subtrair(new Quantidade(operacaoDO.getQtdOperacaoDecimal()));
      }

      return quantidade;
   }

   private DetalheGarantiaDO obterDetalheGarantia(CestaGarantiasDO cesta, OperacaoDO operacao) {
      Booleano indDireitosGarantido = operacao.getIndDireitoCaucionante();
      InstrumentoFinanceiroDO ativo = operacao.getInstrumentoFinanceiro();

      String query = "from DetalheGarantiaDO dg where dg.cestaGarantias = ? and dg.indDireitosGarantidor = ? and dg.instrumentoFinanceiro = ? ";

      List detalhesGarantia = getGp().find(query, new Object[] { cesta, indDireitosGarantido, ativo });

      DetalheGarantiaDO retorno;
      DataHora agora = new DataHora(getDataHoje(), new Hora(new Date()));
      if (detalhesGarantia.isEmpty()) {
         retorno = novoDetalheGarantia(cesta, indDireitosGarantido, ativo, agora);
      } else {
         retorno = (DetalheGarantiaDO) detalhesGarantia.get(0);
         retorno.setDataAlteracao(agora);
      }

      return retorno;
   }

   private DetalheGarantiaDO novoDetalheGarantia(CestaGarantiasDO cesta, Booleano indDireitosGarantido,
         InstrumentoFinanceiroDO ativo, DataHora agora) {
      DetalheGarantiaDO retorno;
      retorno = new DetalheGarantiaDO();
      retorno.setQuantidadeGarantia(new Quantidade("0"));
      retorno.setDataInclusao(agora);
      retorno.setIndCetipado(new Booleano(Booleano.VERDADEIRO));
      retorno.setCestaGarantias(cesta);
      retorno.setIndDireitosGarantidor(indDireitosGarantido);
      retorno.setInstrumentoFinanceiro(ativo);
      return retorno;
   }

}
