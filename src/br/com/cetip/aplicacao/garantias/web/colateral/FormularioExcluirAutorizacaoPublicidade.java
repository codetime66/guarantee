package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoExcluirAutorizacao;
import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoValidaExclusaoAutorizacaoPublicGarantias;
import br.com.cetip.aplicacao.garantias.web.AbstractFormularioGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIFContrato;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.log.Logger;

public class FormularioExcluirAutorizacaoPublicidade extends AbstractFormularioGarantias {

   public void confirmacao(GrupoDeGrupos gdg, Grupo g, Servicos s) throws Exception {
      GrupoDeAtributos gda = gdg.grupoDeAtributos(2);
      gda.atributo(g.obterAtributo(CodigoContaCetip.class, Contexto.CONTA_GARANTIDOR_MANUT_AUTORIZ));
      gda.atributo(g.obterAtributo(CPFOuCNPJ.class, Contexto.CPF_CNPJ_GARANTIDOR_MANUT_AUTORIZ));
      gda.atributo(g.obterAtributo(CodigoContaCetip.class, Contexto.CONTA_GARANTIDO_MANUT_AUTORIZ));
      gda.atributo(g.obterAtributo(CPFOuCNPJ.class, Contexto.CPF_CNPJ_GARANTIDO_MANUT_AUTORIZ));
      gda.atributo(g.obterAtributo(CodigoIFContrato.class, Contexto.CONTRATO));
      gda.atributo(g.obterAtributo(Id.class, Contexto.GARANTIAS_CESTA));
      gda.atributoOculto(g.obterAtributo(Id.class, Contexto.AUTORIZACAO_PUBLICIDADE_GARANTIAS));
   }

   public boolean confirmacao(Grupo dados, Servicos arg1) throws Exception {
      return true;
   }
   
   public void validar(Grupo g, Servicos s) throws Exception {
	    RequisicaoServicoValidaExclusaoAutorizacaoPublicGarantias req = new RequisicaoServicoValidaExclusaoAutorizacaoPublicGarantias();
	    Logger.debug(this, "serviço ServicoValidaExclusaoAutorizacaoPublicGarantias retornou com sucesso");
	    s.executarServico(req, g);
   }

   public Notificacao chamarServico(Grupo g, Servicos s) throws Exception {
      RequisicaoServicoExcluirAutorizacao req = new RequisicaoServicoExcluirAutorizacao();
      s.executarServico(req, g);

      return new Notificacao("AutorizacaoGarantias.Sucesso");
   }

}
