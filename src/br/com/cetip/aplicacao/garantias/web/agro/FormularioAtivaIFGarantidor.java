package br.com.cetip.aplicacao.garantias.web.agro;

import br.com.cetip.aplicacao.garantias.negocio.agro.Parametros;
import br.com.cetip.aplicacao.garantias.servico.agro.RequisicaoServicoHabilitaIFGarantidor;
import br.com.cetip.aplicacao.garantias.servico.agro.RequisicaoServicoValidaHabilitacaoIFGarantidor;
import br.com.cetip.aplicacao.garantias.web.AbstractFormularioGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.TipoHabilitacao;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

/**
 * Formulário de Habilitação de Ceta para Agro-Negócio
 * 
 * @author <a href="mailto:reinaldosantana@cetip.com">Reinaldo Santana</a>
 * @since Maio/20010
 */
public class FormularioAtivaIFGarantidor extends AbstractFormularioGarantias {

	public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos servico) throws Exception {
		 TipoHabilitacao tipoHabilitacao = (TipoHabilitacao) dados.obterAtributo(TipoHabilitacao.class, Contexto.GARANTIAS_CESTA);
		 CodigoTipoIF codigoTipoIF = (CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CESTA);
		 CodigoIF codigoIF = (CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CESTA);
		 Funcao funcao = (Funcao) dados.obterAtributo(Funcao.class, Contexto.GARANTIAS_CESTA);
		   
		 layout.contexto(Contexto.GARANTIDO);
		 GrupoDeGrupos principal = layout.grupoDeGrupos(1);
		 GrupoDeAtributos atts = principal.grupoDeAtributos(1);
		   
		 atts.atributoNaoEditavel(tipoHabilitacao);
		 atts.atributoNaoEditavel(codigoTipoIF);
		 atts.atributoNaoEditavel(codigoIF);
		 atts.atributoOculto(funcao);
  }
   
   public void validar(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoValidaHabilitacaoIFGarantidor req = new RequisicaoServicoValidaHabilitacaoIFGarantidor();
      
      req.atribuirACAO_NumeroInteiro( Parametros.ATIVAR );
      req.atribuirGARANTIAS_CESTA_TipoHabilitacao((TipoHabilitacao) dados.obterAtributo(TipoHabilitacao.class, Contexto.GARANTIAS_CESTA));
      req.atribuirGARANTIAS_CESTA_CodigoTipoIF((CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CESTA));
      req.atribuirGARANTIAS_CESTA_CodigoIF((CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CESTA));

      servicos.chamarServico(req);
   }

   public boolean confirmacao(Grupo grupo, Servicos servicos) throws Exception {
      return true;
   }

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoHabilitaIFGarantidor req = null;
      req = new RequisicaoServicoHabilitaIFGarantidor();

      req.atribuirACAO_NumeroInteiro( Parametros.ATIVAR );
      req.atribuirGARANTIAS_CESTA_TipoHabilitacao((TipoHabilitacao) dados.obterAtributo(TipoHabilitacao.class, Contexto.GARANTIAS_CESTA));
      req.atribuirGARANTIAS_CESTA_CodigoTipoIF((CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CESTA));
      req.atribuirGARANTIAS_CESTA_CodigoIF((CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CESTA));

      servicos.executarServico(req);

      String notificacao = "FormularioAtivaIFGarantidor.Sucesso";
      Notificacao not = new Notificacao(notificacao);
      not.parametroMensagem((CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CESTA), 0);
      not.parametroMensagem((CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CESTA), 1);

      return not;
   }
   
}