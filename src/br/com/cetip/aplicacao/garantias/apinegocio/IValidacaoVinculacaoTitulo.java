package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaVinculacaoCestaGarantias;

/**
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public interface IValidacaoVinculacaoTitulo {

   /**
    * Valida a requisicao da vinculacao de cesta
    * 
    * @param req
    */
   public void validar(RequisicaoServicoValidaVinculacaoCestaGarantias req);

}
