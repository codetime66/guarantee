package br.com.cetip.aplicacao.garantias.negocio;

import java.math.BigDecimal;

import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IRetirarGarantia;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

public class RetirarGarantiaNaoCetipada extends BaseGarantias implements IRetirarGarantia {

   public void retirarGarantia(DetalheGarantiaDO garantia, Quantidade quantidade, NumeroOperacao numerosOperacao,
         Booleano indBatch, Data dataOperacao, Funcao tipoAcesso) {

      IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO mov = imov.incluirMovimentacaoRetirada(garantia, quantidade);
      mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);

      Quantidade resto = garantia.getQuantidadeGarantia().subtrair(quantidade);
      if (resto.obterBigDecimal().compareTo(new BigDecimal("0")) == -1) {
         resto = new Quantidade("0");
      }

      garantia.setQuantidadeGarantia(resto);
   }

   public void registrar(TiposRetiradaGarantia tipos) {
      tipos.registrar(null, this);
   }

}
