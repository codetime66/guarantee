package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

/**
 * 
 * @author Bruno Borges
 */
public interface IRetirarGarantia {

   /**
    * <p>Retira a quantidade da garantia informada</p>
    * @param detalheGarantia
    * @param quantidade
    * @param numerosOperacao
    * @param indBatch
    * @param dataOperacao
    */
   public void retirarGarantia(DetalheGarantiaDO detalheGarantia, Quantidade quantidade,
         NumeroOperacao numerosOperacao, Booleano indBatch, Data dataOperacao, Funcao tipoAcesso);

}
