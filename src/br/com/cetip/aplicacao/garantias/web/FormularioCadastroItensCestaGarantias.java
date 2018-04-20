package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoConsultaCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoDelecaoItensCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaTiposDeGarantia;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoObterDataContaCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoRegistraCadastroCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoRegistraItensCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaCadastroItensCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoConsultaCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoObterDataContaCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoRegistraCadastroCestaGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Resultado;

/**
 * <p>
 * Tela de registro de Itens da Cesta de Garantias
 * </p>
 */
public class FormularioCadastroItensCestaGarantias extends AbstractFormularioGarantias {

   private NumeroInteiro qt = null;
   protected Funcao acaoFormulario;
   private Id codigo = null;
   private boolean criacaoDeCesta;
   private NumeroCestaGarantia numeroCesta;

   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servico) throws Exception {
      acaoFormulario = null;

      RequisicaoServicoObterCombosTipoInstrumentoFinanceiro reqCombo = new RequisicaoServicoObterCombosTipoInstrumentoFinanceiro();
      reqCombo.atribuirTIPO_INSTRUMENTO_FINANCEIRO_Texto(new Texto("TITULO"));
      ResultadoServicoObterCombosTipoInstrumentoFinanceiro resTipoIf = (ResultadoServicoObterCombosTipoInstrumentoFinanceiro) servico
            .executarServico(reqCombo);

      CodigoContaCetip contaCetip = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_PARTICIPANTE);
      CodigoContaCetip contraParte = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_CONTRAPARTE);
      Nome nomeParte = (Nome) dados.obterAtributo(Nome.class, Contexto.GARANTIAS_PARTICIPANTE);
      Nome nomeContraParte = (Nome) dados.obterAtributo(Nome.class, Contexto.GARANTIAS_CONTRAPARTE);
      Data d0 = (Data) dados.obterAtributo(Data.class, Contexto.GARANTIAS_DATA_CRIACAO);

      codigo = (Id) dados.obterAtributo(Id.class, Contexto.GARANTIAS_CODIGO);
      numeroCesta = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);

      qt = (NumeroInteiro) dados.obterAtributo(NumeroInteiro.class, Contexto.GARANTIAS_ITENS);
      qt = (qt != null) ? qt.somar(new NumeroInteiro(1)) : new NumeroInteiro(1);

      layout.contexto(Contexto.GARANTIAS_DADOS);

      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeGrupos grupoCadastro = principal.grupoDeGrupos(1);

      grupoCadastro.contexto(Contexto.GARANTIAS_CADASTRO);
      GrupoDeAtributos grupoIF = grupoCadastro.grupoDeAtributos(2);
      grupoIF.posicaoTitulos(GrupoDeAtributos.TITULOS_A_ESQUERDA);

      criacaoDeCesta = false;
      if (codigo == null && numeroCesta != null) {
         RequisicaoServicoConsultaCestaGarantias req = new RequisicaoServicoConsultaCestaGarantias();
         req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numeroCesta);
         ResultadoServicoConsultaCestaGarantias res = (ResultadoServicoConsultaCestaGarantias) servico
               .executarServico(req);

         grupoIF.atributoNaoEditavel(res.obterGARANTIAS_CODIGO_NumeroCestaGarantia());
         grupoIF.atributoNaoEditavel(res.obterGARANTIAS_CODIGO_IdTipoGarantia());
         grupoIF.atributoNaoEditavel(res.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip());
         grupoIF.atributoNaoEditavel(res.obterGARANTIAS_PARTICIPANTE_Nome());
         grupoIF.atributoNaoEditavel(res.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip());
         grupoIF.atributoNaoEditavel(res.obterGARANTIAS_CONTRAPARTE_Nome());
         grupoIF.atributoNaoEditavel(res.obterGARANTIAS_DATA_CRIACAO_Data());

         grupoIF.atributoOculto(res.obterGARANTIAS_CODIGO_Id());

      } else if (numeroCesta == null) {
         criacaoDeCesta = true;
         RequisicaoServicoObterDataContaCestaGarantias req = new RequisicaoServicoObterDataContaCestaGarantias();
         req.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(contaCetip);
         req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contraParte);
         ResultadoServicoObterDataContaCestaGarantias res = (ResultadoServicoObterDataContaCestaGarantias) servico
               .executarServico(req);

         nomeParte = res.obterGARANTIAS_PARTICIPANTE_Nome();
         nomeContraParte = res.obterGARANTIAS_CONTRAPARTE_Nome();
         d0 = res.obterGARANTIAS_DATA_CRIACAO_Data();

         grupoIF.atributoNaoEditavel(d0);
         grupoIF.atributoNaoEditavel(contaCetip);
         grupoIF.atributoNaoEditavel(nomeParte);
         grupoIF.atributoNaoEditavel(contraParte);
         grupoIF.atributoNaoEditavel(nomeContraParte);
         numeroCesta = new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO);
         grupoIF.atributoOculto(numeroCesta);
      } else {
         grupoIF.atributoNaoEditavel(numeroCesta);
         grupoIF.atributoNaoEditavel(contaCetip);
         grupoIF.atributoNaoEditavel(nomeParte);
         grupoIF.atributoNaoEditavel(contraParte);
         grupoIF.atributoNaoEditavel(nomeContraParte);
         grupoIF.atributoNaoEditavel(d0);
         grupoIF.atributoOculto(codigo);
      }

      GrupoDeGrupos grupos = grupoCadastro.grupoDeGrupos(1);
      GrupoDeAtributos grupoParte = grupos.grupoDeAtributos(2);
      grupoParte.contexto(Contexto.GARANTIAS_ITENS);

      CodigoTipoIF codigoTipoIf = resTipoIf.obterTIPO_IF_GARANTIDOR_CodigoTipoIF();
      codigoTipoIf.getDomain().add(CodigoTipoIF.NAO_CETIPADO);

      codigoTipoIf.atribuirContexto(Contexto.GARANTIAS_CODIGO_TIPO);
      grupoParte.atributo(codigoTipoIf);
      grupoParte.atributo(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
      grupoParte.atributo(getTipoGarantia(servico));

      Quantidade quantidade = new Quantidade(Contexto.GARANTIAS_QUANTIDADE);
      quantidade.atribuirTamanho(ICestaDeGarantias.PRECISAO_PARTE_INTEIRA_QUANTIDADE,
            ICestaDeGarantias.PRECISAO_DECIMAL_QUANTIDADE);
      grupoParte.atributo(quantidade);
      grupoParte.atributo(new Booleano(Contexto.GARANTIAS_ITENS));
      grupoParte.atributoObrigatorio(montaComboAcao(dados));
      grupoParte.atributo(new NumeroOperacao(Contexto.OPERACAO));
      grupoParte.atributoOculto(new NumeroInteiro(Contexto.GARANTIAS_ITENS, qt.toString()));

      GrupoDeAtributos grDescricao = grupoCadastro.grupoDeAtributos(1);
      if (!ehExcluirGarantia()) {
         grDescricao.atributo(new DescricaoLimitada(Contexto.GARANTIAS_ITENS));
      }

      GrupoDeGrupos ultimo = grupoCadastro.grupoDeGrupos(1);
      GrupoDeAtributos grupoUltimo = ultimo.grupoDeAtributos(6);
      grupoUltimo.contexto(Contexto.GARANTIAS_ULTIMO);

      // ***** INICIO popula os campos que contem o ultimo ativo enviado
      grupoUltimo.atributoNaoEditavel(dados.obterAtributo(NumeroInteiro.class, Contexto.GARANTIAS_ITENS));
      grupoUltimo.atributoNaoEditavel(dados.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CODIGO_TIPO));
      grupoUltimo.atributoNaoEditavel(dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF));
      grupoUltimo.atributoNaoEditavel(dados.obterAtributo(Quantidade.class, Contexto.GARANTIAS_QUANTIDADE));
      grupoUltimo.atributoNaoEditavel(dados.obterAtributo(Booleano.class, Contexto.GARANTIAS_ITENS));
      // ***** FIM popula os campos que contem os ultimo ativo enviado
   }

   private Funcao montaComboAcao(Grupo dados) {
      Funcao acaoRequisitada = (Funcao) dados.obterAtributo(Funcao.class, Contexto.ACAO);
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Acao Selecionada: " + acaoRequisitada);
      }

      acaoFormulario = acaoRequisitada;

      if (acaoFormulario.mesmoConteudo(ICestaDeGarantias.INCLUIR_GARANTIAS)) {
         acaoFormulario = ICestaDeGarantias.INCLUIR_GARANTIA;
      } else if (acaoFormulario.mesmoConteudo(ICestaDeGarantias.EXCLUIR_GARANTIAS)) {
         acaoFormulario = ICestaDeGarantias.EXCLUIR_GARANTIA;
      } else if (acaoFormulario.mesmoConteudo(ICestaDeGarantias.APORTAR_GARANTIAS)) {
         acaoFormulario = ICestaDeGarantias.APORTAR_GARANTIA;
      }

      Funcao f = new Funcao(Contexto.ACAO);

      if (!criacaoDeCesta) {
         f.getDomain().add(new Funcao(""));
         f.getDomain().add(acaoFormulario);

         if (!ehAportarGarantia()) {
            f.getDomain().add(ICestaDeGarantias.FINALIZAR_CESTA);
         }

         f.getDomain().add(ICestaDeGarantias.ENCERRAR_CADASTRO_ITENS_CESTA);
      } else {
         f.getDomain().add(acaoFormulario);
      }

      return f;
   }

   public void validar(Grupo dados, Servicos servico) throws Exception {
      acaoFormulario = (Funcao) dados.obterAtributo(Funcao.class, Contexto.ACAO);

      if (ehEncerrarCadastro() || ehFinalizarCesta()) {
         return;
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Acao Selecionada em validar : " + acaoFormulario);
      }

      NumeroCestaGarantia numCesta = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);
      CodigoContaCetip parte = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_PARTICIPANTE);
      CodigoContaCetip contraparte = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_CONTRAPARTE);
      Data dataCriacao = (Data) dados.obterAtributo(Data.class, Contexto.GARANTIAS_DATA_CRIACAO);
      Booleano direitosGarantidor = (Booleano) dados.obterAtributo(Booleano.class, Contexto.GARANTIAS_ITENS);
      CodigoTipoIF codTipoIF = (CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CODIGO_TIPO);
      CodigoIF codIF = (CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF);
      IdTipoGarantia tipoGarantia = (IdTipoGarantia) dados.obterAtributo(IdTipoGarantia.class,
            Contexto.GARANTIAS_CODIGO);
      Quantidade quantidade = (Quantidade) dados.obterAtributo(Quantidade.class, Contexto.GARANTIAS_QUANTIDADE);
      DescricaoLimitada descricao = (DescricaoLimitada) dados.obterAtributo(DescricaoLimitada.class,
            Contexto.GARANTIAS_ITENS);
      Funcao tipoAcesso = (Funcao) dados.obterAtributo(Funcao.class, Contexto.GARANTIAS_TIPO_ACESSO);
      NumeroOperacao numOperacao = (NumeroOperacao) dados.obterAtributo(NumeroOperacao.class, Contexto.OPERACAO);

      RequisicaoServicoValidaCadastroItensCestaGarantias req = new RequisicaoServicoValidaCadastroItensCestaGarantias();

      if (!criacaoDeCesta) {
         req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numCesta);
      }

      req.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(parte);
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contraparte);
      req.atribuirGARANTIAS_DATA_CRIACAO_Data(dataCriacao);
      req.atribuirGARANTIAS_ITENS_Booleano(direitosGarantidor);
      req.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(codTipoIF);
      req.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codIF);
      req.atribuirGARANTIAS_CODIGO_IdTipoGarantia(tipoGarantia);
      req.atribuirGARANTIAS_QUANTIDADE_Quantidade(quantidade);
      req.atribuirGARANTIAS_ITENS_DescricaoLimitada(descricao);
      req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(tipoAcesso);
      req.atribuirACAO_Funcao(acaoFormulario);
      req.atribuirOPERACAO_NumeroOperacao(numOperacao);

      servico.chamarServico(req);
   }

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      if (criacaoDeCesta) {
         return criarCestaComGarantia(dados, servicos);
      } else if (ehIncluirGarantia() || ehAportarGarantia()) {
         return adicionarItem(dados, servicos);
      } else if (ehExcluirGarantia()) {
         return excluirGarantia(dados, servicos);
      }

      return new Notificacao("Operacao.Sucesso");
   }

   private Notificacao criarCestaComGarantia(Grupo dados, Servicos servico) throws Exception {
      CodigoContaCetip parte = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_PARTICIPANTE);
      CodigoContaCetip contraParte = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_CONTRAPARTE);
      CodigoTipoIF tipoIF = (CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CODIGO_TIPO);
      CodigoIF codigoIF = (CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF);
      IdTipoGarantia tipoGarantia = (IdTipoGarantia) dados.obterAtributo(IdTipoGarantia.class,
            Contexto.GARANTIAS_CODIGO);
      Booleano indDireito = (Booleano) dados.obterAtributo(Booleano.class, Contexto.GARANTIAS_ITENS);
      Quantidade qtdade = (Quantidade) dados.obterAtributo(Quantidade.class, Contexto.GARANTIAS_QUANTIDADE);
      DescricaoLimitada descricao = (DescricaoLimitada) dados.obterAtributo(DescricaoLimitada.class,
            Contexto.GARANTIAS_ITENS);
      QuantidadeInteiraPositiva QTD_UM = new QuantidadeInteiraPositiva(1);
      NumeroOperacao numOperacao = (NumeroOperacao) dados.obterAtributo(NumeroOperacao.class, Contexto.OPERACAO);

      RequisicaoServicoRegistraCadastroCestaGarantias requisicao = new RequisicaoServicoRegistraCadastroCestaGarantias();
      requisicao.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(parte);
      requisicao.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contraParte);
      requisicao.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(tipoIF);
      requisicao.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);
      requisicao.atribuirGARANTIAS_CODIGO_IdTipoGarantia(tipoGarantia);
      requisicao.atribuirGARANTIAS_ITENS_Booleano(indDireito);
      requisicao.atribuirGARANTIAS_QUANTIDADE_Quantidade(qtdade);
      requisicao.atribuirGARANTIAS_ITENS_DescricaoLimitada(descricao);
      requisicao.atribuirGARANTIAS_CODIGO_QuantidadeInteiraPositiva(QTD_UM);
      requisicao.atribuirOPERACAO_NumeroOperacao(numOperacao);

      ResultadoServicoRegistraCadastroCestaGarantias res = (ResultadoServicoRegistraCadastroCestaGarantias) servico
            .executarServico(requisicao);

      codigo = res.obterGARANTIAS_CODIGO_Id();
      numeroCesta = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);
      numeroCesta.atribuirConteudo(codigo.toString());

      return new Notificacao("RegistraCadastroCestaGarantias.Sucesso");
   }

   private Notificacao excluirGarantia(Grupo dados, Servicos servico) throws Exception {
      NumeroCestaGarantia numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);
      Id id = (Id) dados.obterAtributo(Id.class, Contexto.GARANTIAS_CODIGO);
      CodigoTipoIF tipoIF = (CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CODIGO_TIPO);
      CodigoIF codigoIF = (CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF);
      Quantidade qtd = (Quantidade) dados.obterAtributo(Quantidade.class, Contexto.GARANTIAS_QUANTIDADE);
      DescricaoLimitada descricao = (DescricaoLimitada) dados.obterAtributo(DescricaoLimitada.class,
            Contexto.GARANTIAS_ITENS);
      Booleano booleano = (Booleano) dados.obterAtributo(Booleano.class, Contexto.GARANTIAS_ITENS);
      NumeroOperacao nuOp = (NumeroOperacao) dados.obterAtributo(NumeroOperacao.class, Contexto.OPERACAO);
      IdTipoGarantia idTipoGarantia = (IdTipoGarantia) dados.obterAtributo(IdTipoGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      RequisicaoServicoDelecaoItensCestaGarantias req = new RequisicaoServicoDelecaoItensCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
      req.atribuirGARANTIAS_CODIGO_Id(id);
      req.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(tipoIF);
      req.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);
      req.atribuirGARANTIAS_QUANTIDADE_Quantidade(qtd);
      req.atribuirGARANTIAS_ITENS_DescricaoLimitada(descricao);
      req.atribuirGARANTIAS_ITENS_Booleano(booleano);
      req.atribuirGARANTIAS_CODIGO_IdTipoGarantia(idTipoGarantia);
      req.atribuirOPERACAO_NumeroOperacao(nuOp);

      servico.executarServico(req);
      return new Notificacao("DelecaoItensCestaGarantias.Sucesso");
   }

   private Notificacao adicionarItem(Grupo dados, Servicos servico) throws Exception {
      CodigoContaCetip parte = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_PARTICIPANTE);
      CodigoContaCetip contraParte = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_CONTRAPARTE);
      CodigoTipoIF codTipoIF = (CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CODIGO_TIPO);
      CodigoIF codigoIF = (CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF);
      Booleano direitoGarantidor = (Booleano) dados.obterAtributo(Booleano.class, Contexto.GARANTIAS_ITENS);
      Quantidade qtdade = (Quantidade) dados.obterAtributo(Quantidade.class, Contexto.GARANTIAS_QUANTIDADE);
      DescricaoLimitada descricao = (DescricaoLimitada) dados.obterAtributo(DescricaoLimitada.class,
            Contexto.GARANTIAS_ITENS);
      Id idCesta = (Id) dados.obterAtributo(Id.class, Contexto.GARANTIAS_CODIGO);
      Funcao funcao = (Funcao) dados.obterAtributo(Funcao.class, Contexto.ACAO);
      Funcao tipoAcesso = (Funcao) dados.obterAtributo(Funcao.class, Contexto.GARANTIAS_TIPO_ACESSO);
      IdTipoGarantia idTipoGarantia = (IdTipoGarantia) dados.obterAtributo(IdTipoGarantia.class,
            Contexto.GARANTIAS_CODIGO);
      NumeroOperacao numOperacao = (NumeroOperacao) dados.obterAtributo(NumeroOperacao.class, Contexto.OPERACAO);

      RequisicaoServicoRegistraItensCestaGarantias req = new RequisicaoServicoRegistraItensCestaGarantias();
      req.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(parte);
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contraParte);
      req.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(codTipoIF);
      req.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);
      req.atribuirGARANTIAS_CODIGO_IdTipoGarantia(idTipoGarantia);
      req.atribuirGARANTIAS_ITENS_Booleano(direitoGarantidor);
      req.atribuirGARANTIAS_QUANTIDADE_Quantidade(qtdade);
      req.atribuirGARANTIAS_ITENS_DescricaoLimitada(descricao);
      req.atribuirGARANTIAS_CODIGO_Id(idCesta);
      req.atribuirACAO_Funcao(funcao);
      req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(tipoAcesso);
      req.atribuirOPERACAO_NumeroOperacao(numOperacao);

      servico.executarServico(req);

      return new Notificacao("RegistraItensCestaGarantias.Sucesso");
   }

   public Class obterDestino(Grupo dados, Servicos servicos) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "- (Obter Destino) Acao Selecionada: " + acaoFormulario);
      }

      Class destino = FormularioEfetuarFechamentoCesta.class;

      if (ehEncerrarCadastro()) {
         destino = null;
      } else {
         if (ehIncluirGarantia() || ehExcluirGarantia()) {
            destino = FormularioCadastroItensSequenciaCestaGarantias.class;
         } else if (ehAportarGarantia()) {
            destino = FormularioAporteGarantiasCestaSequencia.class;
         }
      }

      return destino;
   }

   protected final boolean ehAportarGarantia() {
      return acaoFormulario.mesmoConteudo(ICestaDeGarantias.APORTAR_GARANTIA);
   }

   protected final boolean ehExcluirGarantia() {
      return acaoFormulario.mesmoConteudo(ICestaDeGarantias.EXCLUIR_GARANTIA);
   }

   protected final boolean ehIncluirGarantia() {
      return acaoFormulario.mesmoConteudo(ICestaDeGarantias.INCLUIR_GARANTIA);
   }

   protected final boolean ehEncerrarCadastro() {
      return acaoFormulario.mesmoConteudo(ICestaDeGarantias.ENCERRAR_CADASTRO_ITENS_CESTA);
   }

   protected final boolean ehFinalizarCesta() {
      return acaoFormulario.mesmoConteudo(ICestaDeGarantias.FINALIZAR_CESTA);
   }

   protected final IdTipoGarantia getTipoGarantia(Servicos servicos) throws Exception {
      IdTipoGarantia tipoGarantia = new IdTipoGarantia(Contexto.GARANTIAS_CODIGO);
      tipoGarantia.getDomain().clear();
      RequisicaoServicoListaTiposDeGarantia requisicao = new RequisicaoServicoListaTiposDeGarantia();
      Resultado resultado = servicos.executarServico(requisicao);
      AtributosColunados ac = resultado.obterAtributosColunados();

      while (ac.avancarAtributo()) {
         tipoGarantia.getDomain().add(ac.obterAtributo());
      }

      return tipoGarantia;
   }
}
