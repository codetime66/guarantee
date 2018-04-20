package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

public interface IAportarGarantia {

   public MovimentacaoGarantiaDO aportarItem(CestaGarantiasDO cesta, GarantiaVO garantia, Funcao tipoAcesso);

   public void acionaAporte(MovimentacaoGarantiaDO movimentacao);

}
