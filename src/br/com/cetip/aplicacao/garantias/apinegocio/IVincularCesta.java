package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

public interface IVincularCesta {

   public void vincularCesta(CestaGarantiasDO cesta);

   /**
    * <p>Cancela a movimenta��o de Vincula��o e marca a cesta com situa��o FINALIZADA.</p>
    * 
    * @param cesta
    * @param descricao do motivo do cancelamento da movimenta��o.
    */
   public void cancelarVinculacaoCesta(CestaGarantiasDO cesta, Texto descricao);
}
