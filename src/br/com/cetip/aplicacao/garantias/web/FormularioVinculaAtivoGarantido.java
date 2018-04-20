package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

/**
 * Formulario de especificacao de Cadastro de Cesta de Garantias
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vincius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * @since Abril/2006
 */
public class FormularioVinculaAtivoGarantido extends FormularioEntradaVinculacao {

   protected Funcao obterTipoAcesso() {
      return ICestaDeGarantias.FUNCAO_GARANTIDO;
   }

}
