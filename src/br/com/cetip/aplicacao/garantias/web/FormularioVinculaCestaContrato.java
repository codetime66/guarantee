package br.com.cetip.aplicacao.garantias.web;

import java.util.Iterator;

import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidarVinculacaoCestaContrato;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoVinculacaoCestaContrato;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoVinculacaoCestaContrato;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.base.web.layout.manager.grupo.TituloGrupo;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoContrato;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Resultado;

/**
 * Formulario de especificacao de Cadastro de Contratos com Cesta de Garantias
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vincius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * @since Abril/2006
 */
public class FormularioVinculaCestaContrato extends AbstractFormularioGarantias {

   /**
    * Metodo que permite desenhar a tela de entrada. Recebe o layout a ser definido, os dados da requisicao e a
    * interface que permite chamar um servico. Retorna um booleano indicando se o formulario tem ou nao tem tela de
    * entrada.
    * 
    * @param layout
    *           grupo que contem o layout da interface grafica
    * @param grupo
    *           grupo que contem os atributos da interface grafica
    * @param servicos
    *           Classe responsavel pelo encapsulamento do servico.
    * @throws Exception
    *            caso algum erro aconteca na montagem da interface grafica.
    * @see br.com.cetip.base.web.acao.Formulario#entrada(br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos,
    *      br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      montaTela(layout, dados, false, servicos);
   }

   private void montaTela(GrupoDeGrupos layout, Grupo dados, boolean confirmacao, Servicos servicos) throws Exception {
      layout.contexto(Contexto.GARANTIAS_TITULO);
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeAtributos atts = principal.grupoDeAtributos(1);

      // Campos do CONTRATO
      atts.atributoObrigatorio(celsea(getTiposContrato(servicos), dados, confirmacao));
      atts.atributoObrigatorio(celsea(new CodigoIF(Contexto.CONTRATO), dados, confirmacao));
      atts.atributoObrigatorio(celsea(getRegraLiberacao(), dados, confirmacao));

      // Campos da PARTE
      atts = principal.grupoDeAtributos(1);
      atts.atribuirTituloGrupo(new TituloGrupo("PARTE"));

      atts.atributoObrigatorio(celsea(new CodigoContaCetip(Contexto.PARTICIPANTE), dados, confirmacao));
      NumeroCestaGarantia numero = (NumeroCestaGarantia) celsea(new NumeroCestaGarantia(Contexto.PARTICIPANTE), dados,
            confirmacao);
      if (confirmacao) {
         numero.atribuirTamanhoMinimo(0);
      }
      atts.atributo(numero);
      atts.atributo(celsea(new CPFOuCNPJ(Contexto.PARTICIPANTE), dados, confirmacao));

      // Campos da CONTRAPARTE
      atts = principal.grupoDeAtributos(1);
      atts.atribuirTituloGrupo(new TituloGrupo("CONTRAPARTE"));
      atts.atributoObrigatorio(celsea(new CodigoContaCetip(Contexto.CONTRA_PARTE), dados, confirmacao));
      numero = (NumeroCestaGarantia) celsea(new NumeroCestaGarantia(Contexto.CONTRA_PARTE), dados, confirmacao);
      if (confirmacao) {
         numero.atribuirTamanhoMinimo(0);
      }
      atts.atributo(numero);
      atts.atributo(celsea(new CPFOuCNPJ(Contexto.CONTRA_PARTE), dados, confirmacao));
   }

   private Atributo celsea(Atributo a, Grupo dados, boolean confirmacao) {
      return confirmacao ? dados.obterAtributo(a.getClass(), a.obterContexto()) : a;
   }

   /**
    * Metodo que permite validar os dados do formulario. Recebe os dados da requisicao e a interface que permite chamar
    * um servico.
    * 
    * @param grupo
    *           Representa os dados informados pelo usuario na tela.
    * @param servicos
    *           Classe responsavel pelo encapsulamento do servico.
    * @see br.com.cetip.base.web.acao.suporte.InterfaceFiltro#validar(br.com.cetip.base.web.layout.manager.grupo.Grupo)
    */
   public void validar(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoValidarVinculacaoCestaContrato req;
      req = new RequisicaoServicoValidarVinculacaoCestaContrato();

      CodigoTipoContrato tipoContrato = (CodigoTipoContrato) dados.obterAtributo(CodigoTipoContrato.class,
            Contexto.TIPO_CONTRATO);
      req.atribuirTIPO_CONTRATO_CodigoTipoIF(new CodigoTipoIF(tipoContrato.obterConteudo()));

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, dados.obterAtributo(CodigoIF.class, Contexto.CONTRATO).toString());
      }

      // Conta Cetip CONTRA_PARTE
      req.atribuirCONTRA_PARTE_CodigoContaCetip(new CodigoContaCetip(dados.obterAtributo(CodigoContaCetip.class,
            Contexto.CONTRA_PARTE).toString()));

      // Cesta CONTRA_PARTE
      if (dados.obterAtributo(NumeroCestaGarantia.class, Contexto.CONTRA_PARTE) != null
            && !dados.obterAtributo(NumeroCestaGarantia.class, Contexto.CONTRA_PARTE).vazio()) {
         req.atribuirCONTRA_PARTE_NumeroCestaGarantia(new NumeroCestaGarantia(dados.obterAtributo(
               NumeroCestaGarantia.class, Contexto.CONTRA_PARTE).toString()));
      } else {
         req.atribuirCONTRA_PARTE_NumeroCestaGarantia(new NumeroCestaGarantia(Contexto.CONTRA_PARTE));
      }

      // Conta Cetip PARTE
      req.atribuirPARTE_CodigoContaCetip(new CodigoContaCetip(dados.obterAtributo(CodigoContaCetip.class,
            Contexto.PARTICIPANTE).toString()));

      // Cesta PARTE
      if (dados.obterAtributo(NumeroCestaGarantia.class, Contexto.PARTICIPANTE) != null
            && !dados.obterAtributo(NumeroCestaGarantia.class, Contexto.PARTICIPANTE).vazio()) {
         req.atribuirPARTE_NumeroCestaGarantia(new NumeroCestaGarantia(dados.obterAtributo(NumeroCestaGarantia.class,
               Contexto.PARTICIPANTE).toString()));
      } else {
         req.atribuirPARTE_NumeroCestaGarantia(new NumeroCestaGarantia(Contexto.PARTICIPANTE));
      }

      // CPF/CNPJ PARTE
      CPFOuCNPJ cpfOuCnpjParte = (CPFOuCNPJ) dados.obterAtributo(CPFOuCNPJ.class, Contexto.PARTICIPANTE);
      req.atribuirPARTICIPANTE_CPFOuCNPJ(cpfOuCnpjParte);

      // CPF/CNPJ CONTRAPARTE
      CPFOuCNPJ cpfOuCnpjContraparte = (CPFOuCNPJ) dados.obterAtributo(CPFOuCNPJ.class, Contexto.CONTRA_PARTE);
      req.atribuirCONTRA_PARTE_CPFOuCNPJ(cpfOuCnpjContraparte);

      // Contrato IF
      req.atribuirINSTRUMENTO_FINANCEIRO_CodigoIF(new CodigoIF(dados.obterAtributo(CodigoIF.class, Contexto.CONTRATO)
            .toString()));

      // Reset
      req.atribuirRESET_Funcao((Funcao) dados.obterAtributo(Funcao.class, Contexto.RESET));

      servicos.executarServico(req);
   }

   /**
    * Indica se esse formulario possui janela de confirmacao
    * 
    * @param grupo
    *           Representa os dados informados pelo usuario na tela.
    * @param servicos
    *           Classe responsavel pelo encapsulamento do servico.
    * @throws Exception
    *            caso algum problema aconteca na obtencao da confirmacao.
    * @see br.com.cetip.base.web.acao.Formulario#confirmacao(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    *      br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public boolean confirmacao(Grupo grupo, Servicos servicos) throws Exception {
      return true;
   }

   /**
    * Metodo que permite desenhar a tela de confirmacao. Recebe o layout a ser definido, os dados da requisicao e a
    * interface que permite chamar um servico. Caso cliente 1 ou 2, obtem os dados do depositante automaticamente e os
    * exibe na interface grafica.
    * 
    * @param layout
    *           grupo que contem o layout da interface grafica
    * @param grupo
    *           Representa os dados informados pelo usuario na tela.
    * @param servicos
    *           Classe responsavel pelo encapsulamento do servico.
    * @throws Exception
    *            caso algum problema aconteca na obtencao da confirmacao.
    * @see br.com.cetip.base.web.acao.Formulario#confirmacao(br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos,
    *      br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      montaTela(layout, dados, true, servicos);
   }

   /**
    * Metodo que permite chamar um servico. Recebe os dados da requisicao e a interface que permite chamar um servico.
    * Retorna uma mensagem de notificacao ao usuario, que pode ser nula.
    * 
    * @param grupo
    *           grupo que contem os atributos da interface grafica
    * @param servicos
    *           Classe responsavel pelo encapsulamento do servico.
    * @throws Exception
    *            caso algum erro aconteca na execucao de um servico.
    * @see br.com.cetip.base.web.acao.Formulario#chamarServico(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    *      br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoVinculacaoCestaContrato req;
      req = new RequisicaoServicoVinculacaoCestaContrato();

      CodigoIF codigoIFContrato = new CodigoIF(dados.obterAtributo(CodigoIF.class, Contexto.CONTRATO).toString());

      // Conta Cetip CONTRA_PARTE
      req.atribuirCONTRA_PARTE_CodigoContaCetip(new CodigoContaCetip(dados.obterAtributo(CodigoContaCetip.class,
            Contexto.CONTRA_PARTE).toString()));

      // Cesta CONTRA_PARTE
      if (dados.obterAtributo(NumeroCestaGarantia.class, Contexto.CONTRA_PARTE) != null
            && !dados.obterAtributo(NumeroCestaGarantia.class, Contexto.CONTRA_PARTE).vazio()) {
         req.atribuirCONTRA_PARTE_NumeroCestaGarantia(new NumeroCestaGarantia(dados.obterAtributo(
               NumeroCestaGarantia.class, Contexto.CONTRA_PARTE).toString()));
      } else {
         req.atribuirCONTRA_PARTE_NumeroCestaGarantia(new NumeroCestaGarantia(Contexto.CONTRA_PARTE));
      }

      // Conta Cetip PARTE
      req.atribuirPARTE_CodigoContaCetip(new CodigoContaCetip(dados.obterAtributo(CodigoContaCetip.class,
            Contexto.PARTICIPANTE).toString()));

      // Cesta PARTE
      if (dados.obterAtributo(NumeroCestaGarantia.class, Contexto.PARTICIPANTE) != null
            && !dados.obterAtributo(NumeroCestaGarantia.class, Contexto.PARTICIPANTE).vazio()) {
         req.atribuirPARTE_NumeroCestaGarantia(new NumeroCestaGarantia(dados.obterAtributo(NumeroCestaGarantia.class,
               Contexto.PARTICIPANTE).toString()));
      } else {
         req.atribuirPARTE_NumeroCestaGarantia(new NumeroCestaGarantia(Contexto.PARTICIPANTE));
      }

      // CPF/CNPJ PARTE
      CPFOuCNPJ cpfParte = (CPFOuCNPJ) dados.obterAtributo(CPFOuCNPJ.class, Contexto.PARTICIPANTE);
      if (!Condicional.vazio(cpfParte)) {
         String cpfOuCnpj = cpfParte.toString();
         req.atribuirPARTE_CPFOuCNPJ(new CPFOuCNPJ(cpfOuCnpj));
      } else {
         req.atribuirPARTE_CPFOuCNPJ(new CPFOuCNPJ());
      }

      // CPF/CNPJ CONTRAPARTE
      CPFOuCNPJ cpfContraParte = (CPFOuCNPJ) dados.obterAtributo(CPFOuCNPJ.class, Contexto.CONTRA_PARTE);
      if (!Condicional.vazio(cpfContraParte)) {
         req.atribuirCONTRA_PARTE_CPFOuCNPJ(new CPFOuCNPJ(cpfContraParte.obterConteudo()));
      } else {
         req.atribuirCONTRA_PARTE_CPFOuCNPJ(new CPFOuCNPJ());
      }

      // Contrato IF
      req.atribuirINSTRUMENTO_FINANCEIRO_CodigoIF(new CodigoIF(dados.obterAtributo(CodigoIF.class, Contexto.CONTRATO)
            .toString()));

      // Reset
      req.atribuirRESET_Funcao((Funcao) dados.obterAtributo(Funcao.class, Contexto.RESET));

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, ">> Requisicao MMG");
         Logger.debug(this, "   Contrato: " + req.obterINSTRUMENTO_FINANCEIRO_CodigoIF());
         Logger.debug(this, "    Parte  : " + req.obterPARTE_CodigoContaCetip());
         Logger.debug(this, "    Cesta  : " + req.obterPARTE_NumeroCestaGarantia());
         Logger.debug(this, " ContraPrte: " + req.obterCONTRA_PARTE_CodigoContaCetip());
         Logger.debug(this, "    Cesta  : " + req.obterCONTRA_PARTE_NumeroCestaGarantia());
         Logger.debug(this, "    Regra  : " + req.obterRESET_Funcao());
         Logger.debug(this, "<< FIM Requisicao MMG");
      }

      Resultado r = servicos.executarServico(req);
      ResultadoServicoVinculacaoCestaContrato res;
      res = (ResultadoServicoVinculacaoCestaContrato) r;

      boolean simplesComando = res.obterOPERACAO_Booleano().ehVerdadeiro();

      String notificacao = "FormularioVinculaCestaContrato." + (simplesComando ? "Sucesso" : "Pendente");
      Notificacao not = new Notificacao(notificacao);
      not.parametroMensagem(codigoIFContrato, 0);

      return not;
   }

   /**
    * 
    * @param dados
    * @return
    */
   protected static Booleano obterReset(Grupo dados) {
      Funcao reset = (Funcao) dados.obterAtributo(Funcao.class, Contexto.RESET);
      return obterReset(reset);
   }

   protected static Booleano obterReset(Funcao reset) {
      if (reset != null) {
         if (reset.equals(IContratosCesta.VENCIMENTO)) {
            return Booleano.FALSO;
         } else if (reset.equals(IContratosCesta.AJUSTES_EVENTO_VENC)) {
            return Booleano.VERDADEIRO;
         }
      }

      return Booleano.VAZIO;
   }

   /**
    * 
    * @return
    * @throws Exception
    */
   protected static Funcao getRegraLiberacao() throws Exception {
      Funcao regra = new Funcao(Contexto.RESET);
      regra.getDomain().add(new Funcao(Contexto.RESET, ""));
      regra.getDomain().add(IContratosCesta.AJUSTES_EVENTO_VENC);
      regra.getDomain().add(IContratosCesta.AJUSTES_VENCIMENTO);
      regra.getDomain().add(IContratosCesta.EVENTOS_VENCIMENTO);
      regra.getDomain().add(IContratosCesta.VENCIMENTO);
      regra.getDomain().add(IContratosCesta.EXERCICIO);
      return regra;
   }

   private CodigoTipoContrato getTiposContrato(Servicos servicos) throws Exception {
      RequisicaoServicoObterCombosTipoInstrumentoFinanceiro reqObterCombo = new RequisicaoServicoObterCombosTipoInstrumentoFinanceiro();
      reqObterCombo.atribuirTIPO_INSTRUMENTO_FINANCEIRO_Texto(new Texto("CONTRATO"));
      ResultadoServicoObterCombosTipoInstrumentoFinanceiro resObterCombo = (ResultadoServicoObterCombosTipoInstrumentoFinanceiro) servicos
            .executarServico(reqObterCombo);
      CodigoTipoIF tiposIF = resObterCombo.obterTIPO_IF_GARANTIDO_CodigoTipoIF();
      Iterator i = tiposIF.getDomain().getDataElements().iterator();

      CodigoTipoContrato tipos = new CodigoTipoContrato(Contexto.TIPO_CONTRATO);
      while (i.hasNext()) {
         CodigoTipoIF cod = (CodigoTipoIF) i.next();
         tipos.getDomain().add(new CodigoTipoContrato(cod.obterConteudo()));
      }

      return tipos;
   }

}
