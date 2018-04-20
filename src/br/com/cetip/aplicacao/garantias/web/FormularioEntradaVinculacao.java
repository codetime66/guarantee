package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaAcaoCesta;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaSimplesVinculacaoCestaGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;

/**
 * Formulario de especificacao de Cadastro de Cesta de Garantias
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vincius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * @since Abril/2006
 */
public abstract class FormularioEntradaVinculacao extends AbstractFormularioGarantias {

   protected abstract Funcao obterTipoAcesso();

   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      layout.contexto(Contexto.GARANTIAS_TITULO);
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeGrupos grupoCadastro = principal.grupoDeGrupos(1);
      grupoCadastro.contexto(Contexto.GARANTIAS_CODIGO);

      GrupoDeAtributos grupoIF = grupoCadastro.grupoDeAtributos(1);
      grupoIF.atributoObrigatorio(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));
      grupoIF.atributoOculto(obterTipoAcesso());
   }

   public void validar(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoValidaAcaoCesta req = new RequisicaoServicoValidaAcaoCesta();

      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(
            NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO));
      req.atribuirACAO_Funcao(ICestaDeGarantias.VINCULAR_CESTA);
      req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(obterTipoAcesso());

      servicos.chamarServico(req);
   }

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoValidaSimplesVinculacaoCestaGarantias req;
      req = new RequisicaoServicoValidaSimplesVinculacaoCestaGarantias();

      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(
            NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO));
      req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(obterTipoAcesso());

      servicos.chamarServico(req);
      return null;
   }

   public Class obterDestino(Grupo dados, Servicos servicos) throws Exception {
      return FormularioVinculacaoAtivoCestaGarantias.class;
   }

}
