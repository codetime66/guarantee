package br.com.cetip.aplicacao.garantias.apinegocio;

import java.util.List;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;

public interface IPenhorNoEmissor {

   /**
    * <p>
    * Identifica se a cesta informada eh do tipo Penhor No Emissor.
    * </p>
    * <p>
    * Verificacoes efetuadas:<br>
    * <ul>
    * <li>Se cesta esta vinculada, verifica o tipo de garantia do ativo garantido</li>
    * <li>Caso contrario, procura por uma garantia bloqueada. Se encontrar, valida o tipo da garantia bloqueada na cesta
    * </li>
    * <li>Por ultimo, procura por uma movimentacao de bloqueio pendente. Se encontrar, valida o tipo da garantia
    * incluida na cesta</li>
    * </ul>
    * </p>
    * 
    * @param cesta
    * @return true Se esta cesta, de alguma forma, foi identificada como Penhor no Emissor
    */
   public boolean eCestaPenhorNoEmissor(CestaGarantiasDO cesta);

   /**
    * Retorna uma lista com todas garantias tipo penhor emissor com liberacao cadastrada para a cesta informada
    * 
    * @param numeroCesta
    *           Cesta cujas liberacoes se quer obter
    * @return Lista com as garantias cadastradas para liberacao
    */
   public List obterListaGarantiasPenhorEmissorLiberadas(NumeroCestaGarantia numeroCesta);

   /**
    * Verifica se tem quantidade suficiente do ativo garantidor para liberacao
    * 
    * Ver:
    *    existeQtddLiberacaoGrtiaPenhorEmissor.regra
    * 
    * @param numeroCesta
    *           Cesta que contem o ativo garantidor
    * @param codIF
    *           Codigo do ativo garantidor
    * @param quantidade
    *           Quantidade que se quer liberar
    * @return true se a soma das quantidades ja liberadas com o parametro quantidade nao excede a quantidade total do
    *         ativo garantidor
    */
   public boolean temQuantidadeAtivoParaLiberacao(NumeroCestaGarantia numeroCesta, CodigoIF codIF, Quantidade quantidade);

}