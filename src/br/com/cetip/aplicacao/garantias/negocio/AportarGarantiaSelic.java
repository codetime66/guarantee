package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiaVO;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;

class AportarGarantiaSelic extends AportarGarantia {

   private NumeroOperacao numeroOperacaoSelic;

   public MovimentacaoGarantiaDO aportarItem(CestaGarantiasDO cesta, GarantiaVO garantia, Funcao tipoAcesso) {
      numeroOperacaoSelic = garantia.numeroOperacao;
      IGarantiasSelic gSelic = getFactory().getInstanceGarantiasSelic();

      //se primeira movimentacao de aporte, valida o nro de operacao
      if (super.obtemMovimentacaoAporte(cesta, garantia.getCodGarantia()) == null
            && !gSelic.numeroOperacaoEhValido(garantia.numeroOperacao)) {
         throw new Erro(CodigoErro.NUMERO_OPERACAO_INVALIDO);
      }

      MovimentacaoGarantiaDO mov = super.aportarItem(cesta, garantia, tipoAcesso);

      if (confirmadaPelasPartes(mov)) {
         mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611);
         mov.setDataMovimentacao(getDataHoraHoje());
         getGp().saveOrUpdate(mov);

         //Verifica se a msg SEL1611 de aporte ja foi recebida,
         //se sim efetua o registroLancamentoTransferenciaCustodia
         gSelic.registradoLancamentoTransferenciaCustodia(mov);
      }

      return mov;
   }

   public void acionaAporte(MovimentacaoGarantiaDO movimentacao) {
      movimentacao.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);
      getGp().save(movimentacao);
      super.acionaAporte(movimentacao);
   }

   protected void validaMovimentacaoConfirmacao(GarantiaVO garantia, MovimentacaoGarantiaDO movimentacao) {
      super.validaMovimentacaoConfirmacao(garantia, movimentacao);

      if (!movimentacao.getNumOperacao().mesmoConteudo(numeroOperacaoSelic)) {
         throw new Erro(CodigoErro.CESTA_ITEM_JA_INCLUIDO, "Numero de operacao nao confere");
      }

      //verificar se movimentacao esta aguardando selic
      IdStatusMovimentacaoGarantia idStatus = movimentacao.getStatusMovimentacaoGarantia().getNumIdStatusMovGarantia();
      if (idStatus.mesmoConteudo(IdStatusMovimentacaoGarantia.AGUARDANDO_SEL1611)
            || idStatus.mesmoConteudo(IdStatusMovimentacaoGarantia.AGUARDANDO_SEL1021R1)) {
         throw new Erro(CodigoErro.CESTA_ITEM_JA_INCLUIDO, "Aguardando notificacao Selic");
      }
   }

   /**
    * Verifica se a movimentacao ja foi duplo comandada pelo GARANTIDO E GARANTIDOR,
    * ou seja com status Pendente
    * @param mov
    * @return
    */
   private boolean confirmadaPelasPartes(MovimentacaoGarantiaDO movimentacao) {
      IdStatusMovimentacaoGarantia idStatus = movimentacao.getStatusMovimentacaoGarantia().getNumIdStatusMovGarantia();
      return idStatus.mesmoConteudo(IdStatusMovimentacaoGarantia.PENDENTE);
   }

}
