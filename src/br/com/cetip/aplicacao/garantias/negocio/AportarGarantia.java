package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiaVO;
import br.com.cetip.aplicacao.garantias.apinegocio.IAportarGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.ITipoGarantiaCesta;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;

class AportarGarantia extends BaseGarantias implements IAportarGarantia {

   public MovimentacaoGarantiaDO aportarItem(CestaGarantiasDO cesta, GarantiaVO garantia, Funcao tipoAcesso) {
      ITipoGarantiaCesta itgc = getFactory().getInstanceTipoGarantiaCesta();
      boolean ehPenhorNoEmissor = itgc.obterTipoGarantia(cesta).mesmoConteudo(IdTipoGarantia.PENHOR_NO_EMISSOR);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Validacao de Inclusao de Aporte: ");
         Logger.debug(this, "Lancador: " + tipoAcesso);
      }

      // valida o duplo comando
      IMovimentacoesGarantias imv = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO movimentacao = obtemMovimentacaoAporte(cesta, garantia.getCodGarantia());

      if (movimentacao != null) {
         // valida a segunda entrada, de confirmacao. (do garantido ou do garantidor)
         validaMovimentacaoConfirmacao(garantia, movimentacao);
      } else {
         // insere a movimentacao
         movimentacao = imv.incluirMovimentacaoAporte(cesta, garantia);
      }

      StatusMovimentacaoGarantiaDO proximoStatus = StatusMovimentacaoGarantiaDO.PENDENTE;

      IdStatusMovimentacaoGarantia idStatus = movimentacao.getStatusMovimentacaoGarantia().getNumIdStatusMovGarantia();
      if (!idStatus.mesmoConteudo(IdStatusMovimentacaoGarantia.PENDENTE)) {
         boolean pendenteGarantidor = idStatus.mesmoConteudo(IdStatusMovimentacaoGarantia.APORTE_PENDENTE_GARANTIDOR);
         boolean pendenteGarantido = idStatus.mesmoConteudo(IdStatusMovimentacaoGarantia.APORTE_PENDENTE_GARANTIDO);

         boolean acessoGarantidor = tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDOR);
         boolean acessoGarantido = tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDO);

         if ((pendenteGarantidor && acessoGarantido) || (pendenteGarantido && acessoGarantidor)) {
            throw new Erro(CodigoErro.CESTA_ITEM_JA_INCLUIDO, "Aporte pendente da outra ponta");
         }
      } else {
         Map map = new HashMap();
         map.put(ICestaDeGarantias.FUNCAO_GARANTIDOR, StatusMovimentacaoGarantiaDO.APORTE_PENDENTE_GARANTIDO);
         map.put(ICestaDeGarantias.FUNCAO_GARANTIDO, StatusMovimentacaoGarantiaDO.APORTE_PENDENTE_GARANTIDOR);

         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Tipo de Acesso para Aporte: " + tipoAcesso);
         }

         proximoStatus = (StatusMovimentacaoGarantiaDO) map.get(tipoAcesso);
      }

      movimentacao.setDataMovimentacao(getDataHoje());
      movimentacao.setStatusMovimentacaoGarantia(proximoStatus);
      getGp().update(movimentacao);

      boolean acionaAporte = ehPenhorNoEmissor || proximoStatus.equals(StatusMovimentacaoGarantiaDO.PENDENTE);
      if (!ehAporteDeSelic(movimentacao) && acionaAporte) {
         acionaAporte(movimentacao);
      }

      return movimentacao;
   }

   public void acionaAporte(MovimentacaoGarantiaDO movimentacao) {
      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();

      if (movimentacao.getIndCetipado().ehVerdadeiro()) {
         icg.acionaMIG(movimentacao, Booleano.FALSO, null);
      } else {
         icg.incluirGarantiaExterna(movimentacao);
      }
   }

   protected MovimentacaoGarantiaDO obtemMovimentacaoAporte(CestaGarantiasDO cesta, Atributo codGarantia) {
      IMovimentacoesGarantias imv = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO movPendenteGarantido = imv.obterMovimentacaoParaAtivo(cesta, codGarantia,
            TipoMovimentacaoGarantiaDO.APORTE, StatusMovimentacaoGarantiaDO.APORTE_PENDENTE_GARANTIDO);

      MovimentacaoGarantiaDO movPendenteGarantidor = imv.obterMovimentacaoParaAtivo(cesta, codGarantia,
            TipoMovimentacaoGarantiaDO.APORTE, StatusMovimentacaoGarantiaDO.APORTE_PENDENTE_GARANTIDOR);

      // valida o duplo comando
      return movPendenteGarantidor != null ? movPendenteGarantidor : movPendenteGarantido;

   }

   protected void validaMovimentacaoConfirmacao(GarantiaVO garantia, MovimentacaoGarantiaDO movimentacao) {
      if (!movimentacao.getQtdGarantia().mesmoConteudo(garantia.quantidade)) {
         throw new Erro(CodigoErro.CESTA_ITEM_JA_INCLUIDO, "Quantidade nao confere");
      }

      if (!Condicional.vazio(movimentacao.getTxtDescricao()) && !movimentacao.getTxtDescricao().mesmoConteudo(garantia.descricao)) {
          throw new Erro(CodigoErro.CESTA_ITEM_JA_INCLUIDO, "Descrição não confere"); 
      }

      if (!movimentacao.getIndDireitosGarantidor().mesmoConteudo(garantia.indDireitoGarantidor)) {
         throw new Erro(CodigoErro.CESTA_ITEM_JA_INCLUIDO, "Eventos para Garantidor nao confere");
      }
   }

   private boolean ehAporteDeSelic(MovimentacaoGarantiaDO movimentacao) {
      if (movimentacao != null && movimentacao.getInstrumentoFinanceiro() != null) {
         return SistemaDO.SELIC.mesmoConteudo(movimentacao.getInstrumentoFinanceiro().getSistema().getNumero());
      }
      return false;
   }

}
