package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularTitulo;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaIFDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

/**
 * 
 * @author brunob
 *
 */
class VincularCesta extends BaseGarantias implements IVincularCesta {

   public void vincularCesta(CestaGarantiasDO cesta) {
      IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();

      MovimentacaoGarantiaDO movVinculacao = imov.obterUltimaMovimentacao(cesta, TipoMovimentacaoGarantiaDO.VINCULACAO,
            StatusMovimentacaoGarantiaDO.PENDENTE);

      CestaGarantiasIFDO vinculo = icg.obterIFDOVinculado(cesta, movVinculacao.getInstrumentoFinanceiro());
      if (vinculo != null && vinculo.getStatus().equals(StatusCestaIFDO.VINCULADA_AO_ATIVO)) {
         cesta.setStatusCesta(StatusCestaDO.VINCULADA_AO_ATIVO);
      }

      boolean cestaVinculadaAoAtivo = cesta.getStatusCesta().equals(StatusCestaDO.VINCULADA_AO_ATIVO);

      if (cestaVinculadaAoAtivo) {
         vincularGarantias(movVinculacao);
         return;
      }

      boolean cestaEmVinculacao = cesta.getStatusCesta().equals(StatusCestaDO.EM_VINCULACAO);
      boolean ehPrimeiraVinculacao = cesta.getAtivosVinculados().isEmpty();

      if (ehPrimeiraVinculacao) {
         if (cestaEmVinculacao) {
            // Cesta jah estah em vinculacao, entao algum processo externo deu OK
            // e chamou novamente para continuar com a vinculacao
            primeiraVinculacao(movVinculacao);
         } else {
            boolean segueComVinculacao = verificaELancaControleAtivosExternos(cesta);
            if (!segueComVinculacao) {
               return;
            }

            primeiraVinculacao(movVinculacao);
         }

         return;
      }

      // vinculacao simples de um ativo (vinculacao multipla de ativos a uma cesta)
      vincularAtivo(movVinculacao);
   }

   /*
    * (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.apinegocio.IVincularCesta#cancelarVinculacaoCesta(br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO, br.com.cetip.infra.atributo.tipo.texto.Texto)
    */
   public void cancelarVinculacaoCesta(CestaGarantiasDO cesta, Texto descricao) {
      IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO movVinc = imov.obterUltimaMovimentacao(cesta, TipoMovimentacaoGarantiaDO.VINCULACAO,
            StatusMovimentacaoGarantiaDO.PENDENTE);

      movVinc.setStatusMovimentacaoGarantia(new StatusMovimentacaoGarantiaDO(
            IdStatusMovimentacaoGarantia.MOVIMENTACAO_CANCELADA));

      movVinc.setTxtDescricao(descricao);

      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      CestaGarantiasIFDO vinculo = icg.obterIFDOVinculado(cesta, movVinc.getInstrumentoFinanceiro());

      if (vinculo != null) {
         cesta.getAtivosVinculados().remove(vinculo);
         getGp().delete(vinculo);
      }

      cesta.setStatusCesta(StatusCestaDO.FINALIZADA);

      getGp().save(movVinc);
      getGp().save(cesta);
      getGp().save(vinculo);
   }

   private void primeiraVinculacao(MovimentacaoGarantiaDO movVinculacao) {
      vincularPrimeiroAtivo(movVinculacao);
      vincularGarantias(movVinculacao);
   }

   private CestaGarantiasIFDO vincularAtivo(MovimentacaoGarantiaDO movVinculacao) {
      CestaGarantiasIFDO vinculo = inserirVinculo(movVinculacao.getCestaGarantias(), movVinculacao
            .getInstrumentoFinanceiro());

      String erroVinculacao = null;
      try {
         vincularInstrumentoFinanceiro(movVinculacao, vinculo);
      } catch (Exception e) {
         erroVinculacao = e.getMessage();
      }

      if (vinculo.getStatus().equals(StatusCestaIFDO.VINCULADA_AO_ATIVO)) {
         movVinculacao.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);
         vinculo.setStatus(StatusCestaIFDO.VINCULADA);
      } else if (erroVinculacao != null || vinculo.getStatus().equals(StatusCestaIFDO.VINCULACAO_FALHOU)) {
         movVinculacao.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.MOVIMENTACAO_CANCELADA);
         movVinculacao.setTxtDescricao(erroVinculacao != null ? new Texto(erroVinculacao) : null);
      }

      return vinculo;
   }

   private CestaGarantiasIFDO vincularPrimeiroAtivo(MovimentacaoGarantiaDO movVinculacao) {
      CestaGarantiasDO cesta = movVinculacao.getCestaGarantias();
      CestaGarantiasIFDO vinculo = inserirVinculo(cesta, movVinculacao.getInstrumentoFinanceiro());
      vincularInstrumentoFinanceiro(movVinculacao, vinculo);

      if (vinculo.getStatus().equals(StatusCestaIFDO.VINCULADA_AO_ATIVO)) {
         cesta.setStatusCesta(StatusCestaDO.VINCULADA_AO_ATIVO);
      }

      return vinculo;
   }

   /**
    * Se tem ativos externos na cesta, o processo da vinculacao soh continuará 
    * quando do retorno da mensageria.
    * 
    * @param movVinculacao
    * @return true se segue com a vinculacao. false se tem selic
    */
   protected boolean verificaELancaControleAtivosExternos(CestaGarantiasDO cesta) {
      IGarantiasSelic selic = getFactory().getInstanceGarantiasSelic();
      boolean temSelic = selic.temSelicNaCesta(cesta);

      if (temSelic) {
         cesta.setStatusCesta(StatusCestaDO.EM_VINCULACAO, getDataHoje());
         selic.geraMovsControleVinculacao(cesta);
         return false;
      }

      return true;
   }

   protected final void vincularGarantias(MovimentacaoGarantiaDO movVinculacao) {
      CestaGarantiasDO cesta = movVinculacao.getCestaGarantias();

      // soh aciona a vinculacao das garantias 
      // se a cesta estah vinculada ao ativo
      if (cesta.getStatusCesta().equals(StatusCestaDO.VINCULADA_AO_ATIVO)) {
         ICestaDeGarantias dao = getFactory().getInstanceCestaDeGarantias();
         dao.acionaMIG(movVinculacao, Booleano.FALSO, getDataHoje());
      } else {
         cesta.setStatusCesta(StatusCestaDO.EM_VINCULACAO, getDataHoje());
      }
   }

   protected void vincularInstrumentoFinanceiro(MovimentacaoGarantiaDO movVinculacao, CestaGarantiasIFDO vinculo) {
      CestaGarantiasDO cesta = movVinculacao.getCestaGarantias();
      InstrumentoFinanceiroDO ativo = movVinculacao.getInstrumentoFinanceiro();

      // Garante que implementacoes de IVincularTitulo nao alterem o status da cesta
      StatusCestaDO statusOriginal = cesta.getStatusCesta();

      IVincularTitulo vinc = getFactory().getInstanceVincularAtivo(ativo.getSistema().getNumero());
      vinc.vincularTitulo(movVinculacao, vinculo);

      // volta o status original da cesta antes de vincular ao ativo por prevencao
      cesta.setStatusCesta(statusOriginal);
   }

   private CestaGarantiasIFDO inserirVinculo(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ativo) {
      CestaGarantiasIFDO vinculo = new CestaGarantiasIFDO();
      vinculo.setCestaGarantia(cesta.getNumIdCestaGarantias());
      vinculo.setInstrumentoFinanceiro(ativo);
      vinculo.setStatus(StatusCestaIFDO.EM_VINCULACAO);
      vinculo.setDatInclusao(getDataHoje());

      cesta.getAtivosVinculados().add(vinculo);
      getGp().save(vinculo);

      return vinculo;
   }

}
