package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoValidaCamposFiltroAutorizacaoPublicGarantias;
import br.com.cetip.aplicacao.garantias.web.colateral.FormularioRelacaoIncluiAutPublicGarantias;
import br.com.cetip.base.web.acao.Filtro;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;

public class FiltroInclusaoAutorizacaoPublicGarantias extends Filtro {

   /**
    * Metodo para informar o destino do formulario, que pode ser nulo. O destino sera chamado ao final, caso exista.
    */
   public Class obterDestino(Grupo grupo, Servicos servicos) throws Exception {
      return FormularioRelacaoIncluiAutPublicGarantias.class;
   }

   public void informarCampos(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {
      atributos.atributoObrigatorio(new CodigoContaCetip(Contexto.PARTE_CONTRATO));
      atributos.atributo(new CPFOuCNPJ(Contexto.PARTE_CONTRATO));
   }

   /**
    * Metodo que permite validar os dados do formulario. Recebe os dados da requisicao e a interface que permite chamar
    * um servico.
    */
   public void validar(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoValidaCamposFiltroAutorizacaoPublicGarantias requisicao = new RequisicaoServicoValidaCamposFiltroAutorizacaoPublicGarantias();
      servicos.chamarServico(requisicao, dados);
   }

}
