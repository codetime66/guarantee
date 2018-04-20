package br.com.cetip.aplicacao.garantias.web.colateral;

import java.util.ArrayList;
import java.util.Iterator;

import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoExcluirParametroIFGarantidor;
import br.com.cetip.aplicacao.garantias.web.AbstractFormularioGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.utilitario.Condicional;

/**
 * @author debora
 *
 */
public class FormularioExclusaoParamIFGarantidor extends AbstractFormularioGarantias {

  
   private ArrayList listIdHabilitaParamIF =null;

   public void validar(Grupo dados, Servicos servico) throws Exception {

   }

   public boolean confirmacao(Grupo arg0, Servicos arg1) throws Exception {
      return true;
   }

   public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos servico) throws Exception {

	   Iterator itIdHabilitaIFParam = dados.iterator(new Id(Contexto.GARANTIAS_PARAM_IF_GARANTIDOR));
	   Iterator itFuncao = dados.iterator(new Funcao(Contexto.GARANTIAS_PARAM_IF_GARANTIDOR));
	   Iterator itModulo = dados.iterator(new CodigoSistema(Contexto.GARANTIAS_SISTEMA));
	   Iterator itTipoIF = dados.iterator(new CodigoTipoIF(Contexto.GARANTIAS_TIPO_IF));
	   Iterator itCodigoIF = dados.iterator(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
	   
	   CodigoSistema codSistema = new CodigoSistema();
	   CodigoTipoIF codTipoIF = new CodigoTipoIF();
	   CodigoIF codIF = new CodigoIF();
	   
	   listIdHabilitaParamIF = new ArrayList();
	   
	   GrupoDeAtributos gda = layout.grupoDeAtributos(4);
	      
	   while (itIdHabilitaIFParam.hasNext()){
		  Id  idHabilitaParamIF = (Id)itIdHabilitaIFParam.next();
		   	
		   Funcao funcao = (Funcao) itFuncao.next();
		   if (!Condicional.vazio(funcao)){
			   codSistema = (CodigoSistema) itModulo.next();
			   codTipoIF = (CodigoTipoIF)itTipoIF.next();
			   codIF = (CodigoIF)itCodigoIF.next();	
			   funcao.atribuirContexto(Contexto.GARANTIAS_CODIGO_IF_COLATERAL);
			   codSistema.atribuirContexto(Contexto.GARANTIAS_PARAM_IF_GARANTIDOR);
			   codTipoIF.atribuirContexto(Contexto.GARANTIAS_PARAM_IF_GARANTIDOR);
		       codIF.atribuirContexto(Contexto.GARANTIAS_PARAM_IF_GARANTIDOR);	   
			   gda.atributoNaoEditavel(funcao);
			   gda.atributoNaoEditavel(codSistema);
			   gda.atributoNaoEditavel(codTipoIF);
			   gda.atributoNaoEditavel(codIF);
			   listIdHabilitaParamIF.add(idHabilitaParamIF);
		   } else {
			   itModulo.next();
			   itTipoIF.next();
			   itCodigoIF.next();
		   }
	   }
	     
	      
   }

   public Notificacao chamarServico(Grupo dados, Servicos servico) throws Exception {
	   RequisicaoServicoExcluirParametroIFGarantidor req = new RequisicaoServicoExcluirParametroIFGarantidor();
	   for (int i = 0; i < listIdHabilitaParamIF.size(); i ++){
	      req.atribuirGARANTIAS_PARAM_IF_GARANTIDOR_Id(new Id(listIdHabilitaParamIF.get(i).toString()));
	   }
	      servico.executarServico(req, dados);

	      return new Notificacao("ExclusaoPamametroIFGarantidor.Sucesso");   
   }


   public Class obterDestino(Grupo arg0, Servicos arg1) throws Exception {
      return null;
   }


   

}
