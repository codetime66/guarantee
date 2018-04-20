package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoCadastraGarantido;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaCadastroGarantido;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;

/**
 * Formulario de especificacao de Cadastro Garantido
 * 
 * @author <a href="mailto:fernando@summa-tech.com">Fernando Henrique</a>
 * @since Maio/2008
 */
public class FormularioCadastroGarantido extends AbstractFormularioGarantias {

   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      layout.contexto(Contexto.GARANTIDO);
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeAtributos atts = principal.grupoDeAtributos(1);

      atts.atributoObrigatorio(new NumeroCestaGarantia(Contexto.GARANTIDO));
      atts.atributoObrigatorio(new CodigoContaCetip(Contexto.GARANTIDO));
   }

   public void validar(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoValidaCadastroGarantido req = new RequisicaoServicoValidaCadastroGarantido();

      req.atribuirGARANTIDO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIDO));
      req.atribuirGARANTIDO_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIDO));

      servicos.chamarServico(req);
   }

   public boolean confirmacao(Grupo grupo, Servicos servicos) throws Exception {
      return true;
   }

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoCadastraGarantido req = null;
      req = new RequisicaoServicoCadastraGarantido();

      req.atribuirGARANTIDO_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIDO));

      req.atribuirGARANTIDO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIDO));

      servicos.executarServico(req);

      String notificacao = "FormularioCadastroGarantido.Sucesso";
      Notificacao not = new Notificacao(notificacao);
      not.parametroMensagem(req.obterGARANTIDO_CodigoContaCetip(), 0);
      not.parametroMensagem(req.obterGARANTIDO_NumeroCestaGarantia(), 1);

      return not;
   }

}