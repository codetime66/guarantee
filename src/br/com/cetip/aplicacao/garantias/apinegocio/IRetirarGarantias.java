package br.com.cetip.aplicacao.garantias.apinegocio;

import java.util.List;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

/**
 * 
 * @author Bruno Borges
 */
public interface IRetirarGarantias {

   /**
    * <p>Retira a quantidade das garantias informadas</p>
    * 
    * @param idGarantias Lista dos Ids das garantias a serem retiradas
    * @param quantidades das Garantias (em mesmo indice) a serem retiradas
    */
   public void retirarGarantias(List idGarantias, List quantidades, List numerosOperacao, Booleano indBatch,
         Data dataOperacao, Funcao tipoAcesso);

   public void retirarGarantiasPorDesvinculacao(CestaGarantiasDO cesta, Booleano indBatch, Data dataOperacao);

}
