package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IRetirarGarantia;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;

class RetirarGarantiaSelic extends BaseGarantias implements IRetirarGarantia {

   private static Map mapAcesso = new HashMap(2);

   static {
      mapAcesso.put(ICestaDeGarantias.FUNCAO_GARANTIDOR, StatusMovimentacaoGarantiaDO.PENDENTE_GARANTIDO);
      mapAcesso.put(ICestaDeGarantias.FUNCAO_GARANTIDO, StatusMovimentacaoGarantiaDO.PENDENTE_GARANTIDOR);
   }

   public void retirarGarantia(DetalheGarantiaDO detalheGarantia, Quantidade quantidade, NumeroOperacao numOp,
         Booleano indBatch, Data dataOperacao, Funcao tipoAcesso) {

      IGarantiasSelic gSelic = getFactory().getInstanceGarantiasSelic();
      NumeroOperacao numeroOperacao = numOp;
      boolean lancamentoCetip = false;
      if (Condicional.vazio(numeroOperacao)) {
         numeroOperacao = gSelic.obterNumeroOperacaoCetip();
         lancamentoCetip = true;
      }

      MovimentacaoGarantiaDO movimentacao;
      if (lancamentoCetip) {
         movimentacao = incluirMovimentacaoRetirada(detalheGarantia, quantidade, numeroOperacao);
         movimentacao.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611);
      } else {
         movimentacao = executaLancamentoParticipante(detalheGarantia, quantidade, numeroOperacao);
         movimentacao.setStatusMovimentacaoGarantia(obtemProximoStatus(movimentacao, tipoAcesso));
         //so atualiza a hora se o dia da mov for hoje (nro de operacao selic esta relacionado a data)
         if (movimentacao.getDataMovimentacao().obterData().comparar(getDataHoje()) == 0) {
            movimentacao.setDataMovimentacao(getDataHoraHoje());
         }
      }

      if (confirmadaPelasPartes(movimentacao)) {
         //Verifica se a msg SEL1611 de retirada ja foi recebida
         //se sim efetua o registroLancamentoTransferenciaCustodia
         gSelic.registradoLancamentoTransferenciaCustodia(movimentacao);
      }

   }

   //realiza o lancamento do Garantido ou Garantidor (primeiro comando)
   //confirma os dados do segundo lancamento (Garantidor ou Garantido)
   private MovimentacaoGarantiaDO executaLancamentoParticipante(DetalheGarantiaDO detalheGarantia,
         Quantidade quantidade, NumeroOperacao numeroOperacao) {
      MovimentacaoGarantiaDO movimentacao;
      CestaGarantiasDO cesta = detalheGarantia.getCestaGarantias();
      InstrumentoFinanceiroDO ativo = detalheGarantia.getInstrumentoFinanceiro();

      IMovimentacoesGarantias imv = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO movPendenteGarantido = imv.obterMovimentacaoParaAtivo(cesta, ativo.getCodigoIF(),
            TipoMovimentacaoGarantiaDO.RETIRADA, StatusMovimentacaoGarantiaDO.PENDENTE_GARANTIDO);
      MovimentacaoGarantiaDO movPendenteGarantidor = imv.obterMovimentacaoParaAtivo(cesta, ativo.getCodigoIF(),
            TipoMovimentacaoGarantiaDO.RETIRADA, StatusMovimentacaoGarantiaDO.PENDENTE_GARANTIDOR);

      // valida o duplo comando
      movimentacao = movPendenteGarantidor != null ? movPendenteGarantidor : movPendenteGarantido;

      if (movimentacao != null) {
         validaMovimentacaoConfirmacao(quantidade, numeroOperacao, movimentacao);
      } else {
         movimentacao = incluirMovimentacaoRetirada(detalheGarantia, quantidade, numeroOperacao);
      }
      return movimentacao;
   }

   //insere a movimentacao
   private MovimentacaoGarantiaDO incluirMovimentacaoRetirada(DetalheGarantiaDO detalheGarantia, Quantidade quantidade,
         NumeroOperacao numeroOperacao) {

      IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO mov = imov.incluirMovimentacaoRetirada(detalheGarantia, quantidade);
      mov.setNumOperacao(numeroOperacao);
      return mov;
   }

   //valida a segunda entrada, de confirmacao. (do garantido ou do garantidor)
   //Validacao tambem e realizada pela ValidacaoRetiradaGarantiaSelic
   private void validaMovimentacaoConfirmacao(Quantidade quantidade, NumeroOperacao numeroOperacao,
         MovimentacaoGarantiaDO movimentacao) {
      if (!movimentacao.getQtdGarantia().mesmoConteudo(quantidade)) {
         throw new Erro(CodigoErro.CESTA_OPERACAO_RETIRADA_NAO_CONFERE, "Quantidade nao confere");
      }
      if (!movimentacao.getNumOperacao().mesmoConteudo(numeroOperacao)) {
         throw new Erro(CodigoErro.CESTA_OPERACAO_RETIRADA_NAO_CONFERE, "Numero de operacao nao confere");
      }
   }

   //obtem o proximo Status da movimentacao
   private StatusMovimentacaoGarantiaDO obtemProximoStatus(MovimentacaoGarantiaDO movimentacao, Funcao tipoAcesso) {
      IdStatusMovimentacaoGarantia idStatus = movimentacao.getStatusMovimentacaoGarantia().getNumIdStatusMovGarantia();
      if (!idStatus.mesmoConteudo(IdStatusMovimentacaoGarantia.PENDENTE)) {
         boolean pendenteGarantidor = idStatus.mesmoConteudo(IdStatusMovimentacaoGarantia.PENDENTE_GARANTIDOR);
         boolean pendenteGarantido = idStatus.mesmoConteudo(IdStatusMovimentacaoGarantia.PENDENTE_GARANTIDO);

         boolean acessoGarantidor = tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDOR);
         boolean acessoGarantido = tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDO);

         if ((pendenteGarantidor && acessoGarantido) || (pendenteGarantido && acessoGarantidor)) {
            throw new Erro(CodigoErro.CESTA_OPERACAO_RETIRADA_INVALIDA, "Retirada pendente da outra ponta");
         }
         return StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611;
      }

      return idStatus.mesmoConteudo(IdStatusMovimentacaoGarantia.AGUARDANDO_SEL1611) ? StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611
            : (StatusMovimentacaoGarantiaDO) mapAcesso.get(tipoAcesso);
   }

   //Verifica se a movimentacao ja foi comandada pelo Garantido e Garantidor
   private boolean confirmadaPelasPartes(MovimentacaoGarantiaDO movimentacao) {
      IdStatusMovimentacaoGarantia idStatus = movimentacao.getStatusMovimentacaoGarantia().getNumIdStatusMovGarantia();
      return idStatus.mesmoConteudo(IdStatusMovimentacaoGarantia.AGUARDANDO_SEL1611);
   }

   public void registrar(TiposRetiradaGarantia tipos) {
      tipos.registrar(SistemaDO.SELIC, this);
   }

}
