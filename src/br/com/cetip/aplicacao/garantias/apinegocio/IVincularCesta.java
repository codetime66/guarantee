package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

public interface IVincularCesta {

   public void vincularCesta(CestaGarantiasDO cesta);

   /**
    * <p>Cancela a movimentação de Vinculação e marca a cesta com situação FINALIZADA.</p>
    * 
    * @param cesta
    * @param descricao do motivo do cancelamento da movimentação.
    */
   public void cancelarVinculacaoCesta(CestaGarantiasDO cesta, Texto descricao);
}
