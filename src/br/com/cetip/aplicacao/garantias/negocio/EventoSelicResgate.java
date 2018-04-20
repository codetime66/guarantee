package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.dados.aplicacao.operacao.DetalheCaucaoDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.tipo.tempo.DataHora;

class EventoSelicResgate extends EventoSelic {

   //zera a detalhe caucao
   public void processar(DetalheCaucaoDO caucao) {
      caucao.setDataAlteracao(new DataHora());
      caucao.setQtdDetalheCaucao(new QuantidadeInteiraPositiva("0"));
      getGp().update(caucao);
   }

   public void registrar(TiposEventoSelic i) {
      i.registrar(CodigoTipoOperacaoSelic.PAGAMENTO_RESGATE, this);
   }

}
