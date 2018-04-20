package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoRetiradaGarantia;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.persistencia.IConsulta;

class ValidacaoRetiradaGarantiaSelic extends BaseGarantias implements IValidacaoRetiradaGarantia {

   public void validaRetirada(Id idCesta, Id ativoGarantia, Quantidade quantidade, NumeroOperacao numeroOperacao) {

      List movRetiradaAtivo = obtemMovimentacoesRetiradaAtivo(idCesta, ativoGarantia);
      if (movRetiradaAtivo.isEmpty()) {
         validaNumeroOperacao(numeroOperacao);
         validaQuantidade(quantidade);
      } else {
         validaDuploComando(quantidade, numeroOperacao, movRetiradaAtivo);
      }

   }

   private void validaDuploComando(Quantidade quantidade, NumeroOperacao numeroOperacao, List movRetiradaAtivo) {
      boolean ehSegundoComandoMov = false;
      Iterator i = movRetiradaAtivo.iterator();
      while (i.hasNext()) {
         MovimentacaoGarantiaDO movRetirada = (MovimentacaoGarantiaDO) i.next();
         if (numeroOperacao.mesmoConteudo(movRetirada.getNumOperacao())
               && quantidade.mesmoConteudo(movRetirada.getQtdGarantia())) {
            ehSegundoComandoMov = true;
            break;
         }
      }
      if (!ehSegundoComandoMov) {
         throw new Erro(CodigoErro.MMG_SELIC_CONFIRMACAO_RETIRADA_INVALIDA);
      }
   }

   private List obtemMovimentacoesRetiradaAtivo(Id cesta, Id ativoGarantia) {
      Object[] statusPendentes = new Object[] { StatusMovimentacaoGarantiaDO.PENDENTE_GARANTIDO,
            StatusMovimentacaoGarantiaDO.PENDENTE_GARANTIDOR };

      StringBuffer hql = new StringBuffer(300);
      hql.append("select mov from ").append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" mov where mov.cestaGarantias.numIdCestaGarantias = :idCesta");
      hql.append(" and mov.instrumentoFinanceiro.id = :idAtivo");
      hql.append(" and mov.tipoMovimentacaoGarantia = :tipoMov");
      hql.append(" and mov.statusMovimentacaoGarantia in (:status)");

      IConsulta consulta = getGp().criarConsulta(hql.toString());
      consulta.setAtributo("idCesta", cesta);
      consulta.setAtributo("idAtivo", ativoGarantia);
      consulta.setAtributo("tipoMov", TipoMovimentacaoGarantiaDO.RETIRADA);
      consulta.setParameterList("status", statusPendentes);

      List l = consulta.list();
      if (l == null) {
         return Collections.EMPTY_LIST;
      }
      return l;
   }

   private void validaQuantidade(Quantidade qtde) {
      if (!qtde.obterParteDecimal().ehZero()) {
         throw new Erro(CodigoErro.QUANTIDADE_DEVE_SER_INTEIRA);
      }
   }

   private void validaNumeroOperacao(NumeroOperacao nuOp) {
      if (!getFactory().getInstanceGarantiasSelic().numeroOperacaoEhValido(nuOp)) {
         throw new Erro(CodigoErro.NUMERO_OPERACAO_INVALIDO);
      }
   }

}
