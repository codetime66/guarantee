package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoExecutarParcialCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidarCadastroLiberacaoPenhorEmissor;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;

/**
 * Formulario para liberacao de determinada quantidade de um ativo para um dos garantidos de uma cesta
 * 
 * @author <a href="mailto:cabreva@summa-tech.com">Daniel A. "Cabreva" Alfenas</a>
 * @since Maio/2008
 */
public class FormularioLiberaGarantiaPenhorEmissor extends AbstractFormularioGarantias {

   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoObterCombosTipoInstrumentoFinanceiro reqObterCombo = new RequisicaoServicoObterCombosTipoInstrumentoFinanceiro();
      reqObterCombo.atribuirTIPO_INSTRUMENTO_FINANCEIRO_Texto(new Texto("TITULO"));
      ResultadoServicoObterCombosTipoInstrumentoFinanceiro resTipoIf = (ResultadoServicoObterCombosTipoInstrumentoFinanceiro) servicos
            .executarServico(reqObterCombo);

      layout.contexto(Contexto.GARANTIAS_EXECUCAO_PARCIAL);
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeGrupos grupoCadastro = principal.grupoDeGrupos(1);
      grupoCadastro.contexto(Contexto.GARANTIAS_EXECUCAO_PARCIAL);

      GrupoDeAtributos grupoAt = grupoCadastro.grupoDeAtributos(1);
      NumeroCestaGarantia nrCesta = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      if (Condicional.vazio(nrCesta)) {
         grupoAt.atributoObrigatorio(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));
      } else {
         grupoAt.atributoNaoEditavel(nrCesta);
      }

      grupoAt.atributoObrigatorio(new CodigoContaCetip(Contexto.GARANTIAS_GARANTIDO));
      grupoAt.atributo(new CPFOuCNPJ(Contexto.GARANTIAS_CONTRAPARTE));

      GrupoDeAtributos grupoIF = grupoCadastro.grupoDeAtributos(2);
      CodigoTipoIF codigoTipoIf = resTipoIf.obterTIPO_IF_GARANTIDOR_CodigoTipoIF();
      codigoTipoIf.atribuirContexto(Contexto.GARANTIAS_CODIGO_TIPO);
      grupoIF.atributoObrigatorio(codigoTipoIf);
      grupoIF.atributoObrigatorio(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
      grupoIF.atributoObrigatorio(new Quantidade(Contexto.GARANTIAS_QUANTIDADE));

   }

   public void validar(Grupo dados, Servicos servicos) throws Exception {
      CodigoIF codIF = (CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF);
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Codigo IF: " + codIF);
         Logger.debug(this, "Codigo IF: " + codIF);
      }

      servicos.chamarServico(new RequisicaoServicoValidarCadastroLiberacaoPenhorEmissor(), dados);
   }

   public boolean confirmacao(Grupo grupo, Servicos servicos) throws Exception {
      return true;
   }

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {

      RequisicaoServicoExecutarParcialCestaGarantias req = new RequisicaoServicoExecutarParcialCestaGarantias();

      req.atribuirGARANTIAS_CODIGO_IF_CodigoIF((CodigoIF) dados.obterAtributo(CodigoIF.class,
            Contexto.GARANTIAS_CODIGO_IF));

      req.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF((CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class,
            Contexto.GARANTIAS_CODIGO_TIPO));

      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(
            NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO));

      req.atribuirGARANTIAS_QUANTIDADE_Quantidade((Quantidade) dados.obterAtributo(Quantidade.class,
            Contexto.GARANTIAS_QUANTIDADE));

      req.atribuirGARANTIAS_GARANTIDO_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_GARANTIDO));

      req.atribuirGARANTIAS_CONTRAPARTE_CPFOuCNPJ((CPFOuCNPJ) dados.obterAtributo(CPFOuCNPJ.class,
            Contexto.GARANTIAS_CONTRAPARTE));

      servicos.executarServico(req);

      String notificacao = "FormularioLiberaGarantiaPenhorEmissor.Sucesso";
      Notificacao not = new Notificacao(notificacao);

      return not;
   }

}
