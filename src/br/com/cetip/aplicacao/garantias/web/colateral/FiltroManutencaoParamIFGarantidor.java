package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoValidaFiltroManutencaoParamIFGarantidor;
import br.com.cetip.base.web.acao.Filtro;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

public class FiltroManutencaoParamIFGarantidor extends Filtro {

   /**
    * Metodo para informar o destino do formulario, que pode ser nulo. O destino sera chamado ao final, caso exista.
    */
   public Class obterDestino(Grupo grupo, Servicos servicos) throws Exception {
      return RelacaoManutencaoParamIFGarantidor.class;
   }

   public void informarCampos(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {
      atributos.atributo(getModulos());
      atributos.atributo(CodigoTipoIF.obterDominioParamIFGarantidor());  
   }

   /**
    * Metodo que permite validar os dados do formulario. Recebe os dados da requisicao e a interface que permite chamar
    * um servico.
    */
   public void validar(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoValidaFiltroManutencaoParamIFGarantidor requisicao = new RequisicaoServicoValidaFiltroManutencaoParamIFGarantidor();
      servicos.chamarServico(requisicao, dados);
   }
   private CodigoSistema getModulos() {
	      CodigoSistema modulos= new CodigoSistema(Contexto.GARANTIAS_SISTEMA);
	      modulos.getDomain().clear();
	      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA,""));
	      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA,"SNA"));
	      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA,"SND"));
	      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA,"CETIP21"));
	      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA,"SELIC"));
	      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA,"MOP"));
	      
	      return modulos;
	   }
}
