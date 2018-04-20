package br.com.cetip.aplicacao.garantias.web.agro;

import java.util.List;

import br.com.cetip.aplicacao.instrumentofinanceiro.servico.consulta.RequisicaoMontaComboTipoIF;
import br.com.cetip.base.web.acao.Filtro;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.data.element.DataElement.Domain;

public class FiltroConsultaIFGarantidorHabilitado extends Filtro {

   /**
    * Metodo para informar o destino do formulario, que pode ser nulo. O destino sera chamado ao final, caso exista.
    */
   public Class obterDestino(Grupo grupo, Servicos servicos) throws Exception {
      return RelacaoIFGarantidorHabilitado.class;
   }

   public void informarCampos(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {
//	   atributos.atributoObrigatorio(montaComboTipoIF(dados, servicos));
	   atributos.atributo(montaComboTipoIF(dados, servicos));
	   atributos.atributo(new CodigoIF(Contexto.GARANTIAS_CESTA));
   }

   /**
    * Metodo que permite validar os dados do formulario. Recebe os dados da requisicao e a interface que permite chamar
    * um servico.
    */
   public void validar(Grupo dados, Servicos servicos) throws Exception {
	   /*
	   RequisicaoServicoValidaHabilitacaoIFGarantidor req = new RequisicaoServicoValidaHabilitacaoIFGarantidor();
	      
	   //req.atribuirGARANTIAS_CESTA_TipoHabilitacao((TipoHabilitacao) dados.obterAtributo(TipoHabilitacao.class, Contexto.GARANTIAS_CESTA));
	   req.atribuirGARANTIAS_CESTA_CodigoTipoIF((CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CESTA));
	   req.atribuirGARANTIAS_CESTA_CodigoIF((CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CESTA));

       servicos.chamarServico(req, dados);
       */
   }
   
   protected CodigoTipoIF montaComboTipoIF(Grupo dados, Servicos servicos) throws Exception {
		CodigoTipoIF codTipoIF = null;
		CodigoTipoIF codigoTipoIF = new CodigoTipoIF();
		String cod = null;
		
		RequisicaoMontaComboTipoIF requisicao = new RequisicaoMontaComboTipoIF();
		GrupoDeAtributos atributos = servicos.chamarServico(requisicao, dados);
		
		codTipoIF = (CodigoTipoIF) atributos.obterAtributo(
				CodigoTipoIF.class, Contexto.TIPO_IF);
		
		Domain domain = codTipoIF.obterDominio();
		List tipoIF = domain.getDataElements();
		
		for(int i =0; i< tipoIF.size(); i++){
			cod = (String)tipoIF.get(i).toString();
			codigoTipoIF.getDomain().add(new CodigoTipoIF(cod));
		}
		
		codigoTipoIF.atribuirContexto(Contexto.GARANTIAS_CESTA);
		
		return codigoTipoIF;
}

}
