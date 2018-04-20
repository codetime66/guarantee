package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoExecutarParcialCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaExecucaoParcialCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoExecutarParcialCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

/**
 * Formulario de especificacao de Cadastro de Cesta de Garantias
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vincius S. Fernandes</a>
 * @since December/2006
 */
public class FormularioLiberarCestaGarantiasParcial extends AbstractFormularioGarantias {

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.base.web.acao.Formulario#entrada(br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos,
    *      br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoObterCombosTipoInstrumentoFinanceiro reqObterCombo = new RequisicaoServicoObterCombosTipoInstrumentoFinanceiro();
      reqObterCombo.atribuirTIPO_INSTRUMENTO_FINANCEIRO_Texto(new Texto("TITULO"));
      ResultadoServicoObterCombosTipoInstrumentoFinanceiro resTipoIf = (ResultadoServicoObterCombosTipoInstrumentoFinanceiro) servicos
            .executarServico(reqObterCombo);

      layout.contexto(Contexto.GARANTIAS_EXECUCAO_PARCIAL);
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeGrupos grupoCadastro = principal.grupoDeGrupos(1);
      grupoCadastro.contexto(Contexto.GARANTIAS_EXECUCAO_PARCIAL);

      GrupoDeAtributos grupoIF = grupoCadastro.grupoDeAtributos(2);

      NumeroCestaGarantia nrCesta = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);
      grupoIF.atributoNaoEditavel(nrCesta);
      CodigoTipoIF codigoTipoIf = resTipoIf.obterTIPO_IF_GARANTIDOR_CodigoTipoIF();
      codigoTipoIf.getDomain().add(CodigoTipoIF.NAO_CETIPADO);

      codigoTipoIf.atribuirContexto(Contexto.GARANTIAS_CODIGO_TIPO);
      grupoIF.atributoObrigatorio(codigoTipoIf);
      grupoIF.atributoObrigatorio(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
      grupoIF.atributoObrigatorio(new Quantidade(Contexto.GARANTIAS_QUANTIDADE));
      grupoIF.atributo(new NumeroOperacao(Contexto.OPERACAO));
      grupoIF.atributo(new CPFOuCNPJ(Contexto.GARANTIAS_CONTRAPARTE));
   }

   public void validar(Grupo dados, Servicos servicos) throws Exception {
      NumeroCestaGarantia numCesta = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);
      CodigoTipoIF codTipoIF = (CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CODIGO_TIPO);
      CodigoIF codIF = (CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF);
      Quantidade qtdade = (Quantidade) dados.obterAtributo(Quantidade.class, Contexto.GARANTIAS_QUANTIDADE);
      NumeroOperacao numOperacao = (NumeroOperacao) dados.obterAtributo(NumeroOperacao.class, Contexto.OPERACAO);
      CPFOuCNPJ cpfOuCnpj = (CPFOuCNPJ) dados.obterAtributo(CPFOuCNPJ.class, Contexto.GARANTIAS_CONTRAPARTE);

      RequisicaoServicoValidaExecucaoParcialCestaGarantias req = new RequisicaoServicoValidaExecucaoParcialCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numCesta);
      req.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(codTipoIF);
      req.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codIF);
      req.atribuirGARANTIAS_QUANTIDADE_Quantidade(qtdade);
      req.atribuirOPERACAO_NumeroOperacao(numOperacao);
      req.atribuirGARANTIAS_CONTRAPARTE_CPFOuCNPJ(cpfOuCnpj);

      servicos.chamarServico(req);
   }

   public boolean confirmacao(Grupo grupo, Servicos servicos) throws Exception {
      return true;
   }

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoExecutarParcialCestaGarantias req = new RequisicaoServicoExecutarParcialCestaGarantias();

      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(
            NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO));
      req.atribuirGARANTIAS_QUANTIDADE_Quantidade((Quantidade) dados.obterAtributo(Quantidade.class,
            Contexto.GARANTIAS_QUANTIDADE));
      req.atribuirGARANTIAS_CODIGO_IF_CodigoIF((CodigoIF) dados.obterAtributo(CodigoIF.class,
            Contexto.GARANTIAS_CODIGO_IF));
      req.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF((CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class,
            Contexto.GARANTIAS_CODIGO_TIPO));
      req
            .atribuirOPERACAO_NumeroOperacao((NumeroOperacao) dados.obterAtributo(NumeroOperacao.class,
                  Contexto.OPERACAO));

      ResultadoServicoExecutarParcialCestaGarantias res = (ResultadoServicoExecutarParcialCestaGarantias) servicos
            .executarServico(req);

      String notificacao = "FormularioExecutarCestaGarantiasParcial.Sucesso";
      Notificacao not = new Notificacao(notificacao);
      not.parametroMensagem(res.obterGARANTIAS_CODIGO_IF_CodigoIF(), 0);
      not.parametroMensagem(res.obterGARANTIAS_CODIGO_NumeroCestaGarantia(), 1);

      return not;
   }

}
