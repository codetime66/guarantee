package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidarConsultaCestaPorIF;
import br.com.cetip.base.web.acao.Filtro;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;

public class FiltroConsultaCestaPorIF extends Filtro {

   /**
    * Metodo para informar o destino do formulario, que pode ser nulo. O destino sera chamado ao final, caso exista.
    */
   public Class obterDestino(Grupo grupo, Servicos servicos) throws Exception {
      return RelacaoConsultaCestasPorIF.class;
   }

   public void informarCampos(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {
      atributos.atributo(new CodigoIF(Contexto.CESTA_GARANTIA));
   }

   /**
    * Metodo que permite validar os dados do formulario. Recebe os dados da requisicao e a interface que permite chamar
    * um servico.
    */
   public void validar(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoValidarConsultaCestaPorIF requisicao = new RequisicaoServicoValidarConsultaCestaPorIF();
      servicos.chamarServico(requisicao, dados);
   }

}
