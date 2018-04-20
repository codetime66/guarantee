package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoConsultaCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoRegistraVinculacaoCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaVinculacaoCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoConsultaCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleLancamento;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

/**
 * Formulario de especificacao de Fechamento de Cesta de Garantias
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vinicius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * @since Abril/2006
 */
public class FormularioVinculacaoAtivoCestaGarantias extends AbstractFormularioGarantias {

   private Funcao tipoAcesso;

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
      NumeroCestaGarantia numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      tipoAcesso = (Funcao) dados.obterAtributo(Funcao.class, Contexto.GARANTIAS_TIPO_ACESSO);

      RequisicaoServicoConsultaCestaGarantias req = new RequisicaoServicoConsultaCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);

      ResultadoServicoConsultaCestaGarantias res = (ResultadoServicoConsultaCestaGarantias) servicos
            .executarServico(req);

      RequisicaoServicoObterCombosTipoInstrumentoFinanceiro reqObterCombo = new RequisicaoServicoObterCombosTipoInstrumentoFinanceiro();
      reqObterCombo.atribuirTIPO_INSTRUMENTO_FINANCEIRO_Texto(new Texto("TITULO"));
      ResultadoServicoObterCombosTipoInstrumentoFinanceiro resTipoIf = (ResultadoServicoObterCombosTipoInstrumentoFinanceiro) servicos
            .executarServico(reqObterCombo);

      CodigoContaCetip parte = res.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();
      CodigoContaCetip contraParte = res.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
      Data dataCriacao = res.obterGARANTIAS_DATA_CRIACAO_Data();
      Nome nomeParte = res.obterGARANTIAS_PARTICIPANTE_Nome();
      Nome nomeContraParte = res.obterGARANTIAS_CONTRAPARTE_Nome();
      NumeroInteiro qtItens = res.obterGARANTIAS_CODIGO_NumeroInteiro();

      layout.contexto(Contexto.GARANTIAS_DADOS);
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeGrupos grupoCadastro = principal.grupoDeGrupos(1);
      grupoCadastro.contexto(Contexto.GARANTIAS_DADOS);

      GrupoDeAtributos codigo = grupoCadastro.grupoDeAtributos(2);
      codigo.atributoNaoEditavel(parte);
      codigo.atributoNaoEditavel(nomeParte);
      codigo.atributoNaoEditavel(contraParte);
      codigo.atributoNaoEditavel(nomeContraParte);
      codigo.atributoNaoEditavel(numero);
      codigo.atributoNaoEditavel(dataCriacao);
      codigo.atributoNaoEditavel(qtItens);

      GrupoDeGrupos grupos = grupoCadastro.grupoDeGrupos(1);
      GrupoDeAtributos grupoParte = grupos.grupoDeAtributos(1);
      grupoParte.contexto(Contexto.GARANTIAS_SISTEMA);

      grupoParte.atributoNaoEditavel(tipoAcesso);
      CodigoTipoIF codigoTipoIf = resTipoIf.obterTIPO_IF_GARANTIDO_CodigoTipoIF();
      codigoTipoIf.atribuirContexto(Contexto.GARANTIAS_CODIGO_TIPO);
      grupoParte.atributoObrigatorio(codigoTipoIf);
      grupoParte.atributoObrigatorio(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
      CodigoSistema codigoSistema = resTipoIf.obterSISTEMA_GARANTIDO_CodigoSistema();
      codigoSistema.atribuirContexto(Contexto.GARANTIAS_SISTEMA);
      grupoParte.atributoObrigatorio(codigoSistema);
      grupoParte.atributoObrigatorio(new Booleano(Contexto.GARANTIAS_DEPOSITADO));

      if (tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDOR) && parte.ehContaCliente()) {
         grupoParte.atributo(new CPFOuCNPJ(Contexto.GARANTIAS_PARTICIPANTE));
      }

      GrupoDeGrupos ultimo = grupoCadastro.grupoDeGrupos(1);
      GrupoDeAtributos grupoFinal = ultimo.grupoDeAtributos(1);
      grupoFinal.contexto(Contexto.GARANTIAS_DADOS_OPERACAO);

      grupoFinal.atributo(new NumeroControleLancamento(Contexto.GARANTIAS_QT_OPERACAO));
      grupoFinal.atributo(new Quantidade(Contexto.GARANTIAS_QT_OPERACAO));
      grupoFinal.atributo(new Quantidade(Contexto.GARANTIAS_PU));
      grupoFinal.atributo(getTipoModalidade(servicos, layout));
   }

   private void montaTelaDetalhada(GrupoDeGrupos layout, Grupo dados) throws Exception {
      // Cabecalho
      Nome parteNome = (Nome) dados.obterAtributo(Nome.class, Contexto.GARANTIAS_PARTICIPANTE);
      Nome contraNome = (Nome) dados.obterAtributo(Nome.class, Contexto.GARANTIAS_CONTRAPARTE);
      CodigoContaCetip parte = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_PARTICIPANTE);
      CodigoContaCetip contraParte = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_CONTRAPARTE);
      NumeroCestaGarantia numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);
      Data data = (Data) dados.obterAtributo(Data.class, Contexto.GARANTIAS_DATA_CRIACAO);
      NumeroInteiro qtItens = (NumeroInteiro) dados.obterAtributo(NumeroInteiro.class, Contexto.GARANTIAS_CODIGO);
      // Dados
      CodigoTipoIF tipoIF = (CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CODIGO_TIPO);
      CodigoIF codigoIF = (CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF);
      CodigoSistema codigoSistema = (CodigoSistema) dados
            .obterAtributo(CodigoSistema.class, Contexto.GARANTIAS_SISTEMA);
      Booleano depositado = (Booleano) dados.obterAtributo(Booleano.class, Contexto.GARANTIAS_DEPOSITADO);

      // Operacao
      NumeroControleLancamento nrOperacao = (NumeroControleLancamento) dados.obterAtributo(
            NumeroControleLancamento.class, Contexto.GARANTIAS_QT_OPERACAO);
      Quantidade qtOperacao = (Quantidade) dados.obterAtributo(Quantidade.class, Contexto.GARANTIAS_QT_OPERACAO);
      Quantidade puOperacao = (Quantidade) dados.obterAtributo(Quantidade.class, Contexto.GARANTIAS_PU);
      Id modalidade = (Id) dados.obterAtributo(Id.class, Contexto.MODALIDADE_LIQUIDACAO);

      layout.contexto(Contexto.GARANTIAS_DADOS);
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeGrupos grupoCadastro = principal.grupoDeGrupos(1);
      grupoCadastro.contexto(Contexto.GARANTIAS_DADOS);

      GrupoDeAtributos codigo = grupoCadastro.grupoDeAtributos(2);
      codigo.atributoNaoEditavel(parte);
      codigo.atributoNaoEditavel(parteNome);
      codigo.atributoNaoEditavel(contraParte);
      codigo.atributoNaoEditavel(contraNome);
      codigo.atributoNaoEditavel(numero);
      codigo.atributoNaoEditavel(data);
      codigo.atributoNaoEditavel(qtItens);

      GrupoDeGrupos grupos = grupoCadastro.grupoDeGrupos(1);
      GrupoDeAtributos grupoParte = grupos.grupoDeAtributos(2);
      grupoParte.contexto(Contexto.GARANTIAS_SISTEMA);

      grupoParte.atributoNaoEditavel(tipoAcesso);
      grupoParte.atributo(tipoIF);
      grupoParte.atributoObrigatorio(codigoIF);
      grupoParte.atributoObrigatorio(codigoSistema);
      grupoParte.atributoObrigatorio(depositado);

      if (tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDOR) && parte.ehContaCliente()) {
         grupoParte.atributo(dados.obterAtributo(CPFOuCNPJ.class, Contexto.GARANTIAS_PARTICIPANTE));
      }

      GrupoDeGrupos ultimo = grupoCadastro.grupoDeGrupos(1);
      GrupoDeAtributos grupoFinal = ultimo.grupoDeAtributos(2);
      grupoFinal.contexto(Contexto.GARANTIAS_DADOS_OPERACAO);

      grupoFinal.atributo(nrOperacao);
      grupoFinal.atributo(qtOperacao);
      grupoFinal.atributo(puOperacao);
      grupoFinal.atributo(modalidade);

   }

   /**
    * Obtem a lista de modalidades atraves de um servico.
    * 
    * @param servicos
    *           Classe responsavel pelo encapsulamento do servico.
    * @param layout
    *           grupo que contem o layout da interface grafica
    * @return atributo com o tipo de operacao
    * @throws Exception
    *            caso algum erro aconteca na execucao do servico.
    */
   protected Atributo getTipoModalidade(Servicos servicos, GrupoDeGrupos layout) throws Exception {
      Id tipoOperacao = new Id(Contexto.MODALIDADE_LIQUIDACAO);
      tipoOperacao.getDomain().add(new Id("", ""));
      tipoOperacao.getDomain().add(new Id("SEM MODALIDADE DE LIQUIDACAO", "6"));
      tipoOperacao.getDomain().add(new Id("CETIP", "1"));
      tipoOperacao.getDomain().add(new Id("BRUTA", "2"));
      return tipoOperacao;
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
      RequisicaoServicoValidaVinculacaoCestaGarantias req = new RequisicaoServicoValidaVinculacaoCestaGarantias();

      req.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(
            CodigoContaCetip.class, Contexto.GARANTIAS_PARTICIPANTE));
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_CONTRAPARTE));
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(
            NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO));
      req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(tipoAcesso);
      req.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF((CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class,
            Contexto.GARANTIAS_CODIGO_TIPO));
      req.atribuirGARANTIAS_CODIGO_IF_CodigoIF((CodigoIF) dados.obterAtributo(CodigoIF.class,
            Contexto.GARANTIAS_CODIGO_IF));
      req.atribuirGARANTIAS_SISTEMA_CodigoSistema((CodigoSistema) dados.obterAtributo(CodigoSistema.class,
            Contexto.GARANTIAS_SISTEMA));
      req.atribuirGARANTIAS_DEPOSITADO_Booleano((Booleano) dados.obterAtributo(Booleano.class,
            Contexto.GARANTIAS_DEPOSITADO));
      req.atribuirGARANTIAS_QT_OPERACAO_NumeroControleLancamento((NumeroControleLancamento) dados.obterAtributo(
            NumeroControleLancamento.class, Contexto.GARANTIAS_QT_OPERACAO));
      req.atribuirGARANTIAS_QT_OPERACAO_Quantidade((Quantidade) dados.obterAtributo(Quantidade.class,
            Contexto.GARANTIAS_QT_OPERACAO));
      req.atribuirGARANTIAS_PU_Quantidade((Quantidade) dados.obterAtributo(Quantidade.class, Contexto.GARANTIAS_PU));
      req.atribuirMODALIDADE_LIQUIDACAO_Id((Id) dados.obterAtributo(Id.class, Contexto.MODALIDADE_LIQUIDACAO));
      req.atribuirGARANTIAS_PARTICIPANTE_CPFOuCNPJ((CPFOuCNPJ) dados.obterAtributo(CPFOuCNPJ.class,
            Contexto.GARANTIAS_PARTICIPANTE));
      servicos.chamarServico(req);
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
      montaTelaDetalhada(layout, dados);
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
      String notificacao = "RegistraVinculacaoCestaGarantias.Sucesso";

      RequisicaoServicoRegistraVinculacaoCestaGarantias req = new RequisicaoServicoRegistraVinculacaoCestaGarantias();

      req.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(
            CodigoContaCetip.class, Contexto.GARANTIAS_PARTICIPANTE));
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_CONTRAPARTE));
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(
            NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO));
      req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(tipoAcesso);
      req.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF((CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class,
            Contexto.GARANTIAS_CODIGO_TIPO));
      req.atribuirGARANTIAS_CODIGO_IF_CodigoIF((CodigoIF) dados.obterAtributo(CodigoIF.class,
            Contexto.GARANTIAS_CODIGO_IF));
      req.atribuirGARANTIAS_SISTEMA_CodigoSistema((CodigoSistema) dados.obterAtributo(CodigoSistema.class,
            Contexto.GARANTIAS_SISTEMA));
      req.atribuirGARANTIAS_DEPOSITADO_Booleano((Booleano) dados.obterAtributo(Booleano.class,
            Contexto.GARANTIAS_DEPOSITADO));
      req.atribuirGARANTIAS_QT_OPERACAO_NumeroControleLancamento((NumeroControleLancamento) dados.obterAtributo(
            NumeroControleLancamento.class, Contexto.GARANTIAS_QT_OPERACAO));
      req.atribuirGARANTIAS_QT_OPERACAO_Quantidade((Quantidade) dados.obterAtributo(Quantidade.class,
            Contexto.GARANTIAS_QT_OPERACAO));
      req.atribuirGARANTIAS_PU_Quantidade((Quantidade) dados.obterAtributo(Quantidade.class, Contexto.GARANTIAS_PU));
      req.atribuirMODALIDADE_LIQUIDACAO_Id((Id) dados.obterAtributo(Id.class, Contexto.MODALIDADE_LIQUIDACAO));
      req.atribuirGARANTIAS_STATUS_Id((Id) dados.obterAtributo(Id.class, Contexto.GARANTIAS_STATUS));
      req.atribuirGARANTIAS_PARTICIPANTE_CPFOuCNPJ((CPFOuCNPJ) dados.obterAtributo(CPFOuCNPJ.class,
            Contexto.GARANTIAS_PARTICIPANTE));

      servicos.executarServico(req);

      Notificacao not = new Notificacao(notificacao);
      return not;

   }

}
