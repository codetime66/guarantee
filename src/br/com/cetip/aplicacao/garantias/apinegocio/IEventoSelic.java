package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.operacao.DetalheCaucaoDO;
import br.com.cetip.dados.aplicacao.operacao.OperacaoDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.ValorMonetario;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

public interface IEventoSelic {

   public OperacaoDO criarOperacao(Id cesta, CodigoTipoOperacaoSelic codOperacao, Id numIf,
         Booleano direitosGarantidor, Quantidade quantidade, ValorMonetario precoUnitario,
         ValorMonetario valorFinanceiro, Data dataMovimentacao);

   public void processar(DetalheCaucaoDO caucao);

}
