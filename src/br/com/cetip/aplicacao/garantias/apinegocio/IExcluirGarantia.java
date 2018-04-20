package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

public interface IExcluirGarantia {

   public void setIndBatch(Booleano indBatch);

   public void setDataOperacao(Data dataOperacao);

   public void excluirGarantia(DetalheGarantiaDO garantia);

   public void excluirGarantia(MovimentacaoGarantiaDO movimentacao);

   public void excluirItemEspelho(MovimentacaoGarantiaDO itemDO);

}
