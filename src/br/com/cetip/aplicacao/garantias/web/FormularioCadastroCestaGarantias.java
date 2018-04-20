package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaCadastroCestaGarantias;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;

/**
 * Formulario de especificacao de Cadastro de Cesta de Garantias
 */
public class FormularioCadastroCestaGarantias extends AbstractFormularioGarantias {

   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      layout.contexto(Contexto.GARANTIAS_TITULO);
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeGrupos grupoCadastro = principal.grupoDeGrupos(1);
      grupoCadastro.contexto(Contexto.GARANTIAS_CADASTRO);

      GrupoDeAtributos grupoIF = grupoCadastro.grupoDeAtributos(1);
      grupoIF.atributoObrigatorio(new CodigoContaCetip(Contexto.GARANTIAS_PARTICIPANTE));
      grupoIF.atributo(new CodigoContaCetip(Contexto.GARANTIAS_CONTRAPARTE));
      grupoIF.atributoOculto(ICestaDeGarantias.INCLUIR_GARANTIA);
   }

   public void validar(Grupo dados, Servicos servicos) throws Exception {
      CodigoContaCetip codParte = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_PARTICIPANTE);
      CodigoContaCetip codContraParte = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_CONTRAPARTE);

      RequisicaoServicoValidaCadastroCestaGarantias req = new RequisicaoServicoValidaCadastroCestaGarantias();
      req.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(codParte);
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(codContraParte);

      servicos.chamarServico(req);
   }

   public Class obterDestino(Grupo dados, Servicos servicos) throws Exception {
      return FormularioCadastroItensCestaGarantias.class;
   }

}
