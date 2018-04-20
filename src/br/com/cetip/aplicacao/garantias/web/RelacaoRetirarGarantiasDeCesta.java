package br.com.cetip.aplicacao.garantias.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaGarantiasCesta;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.Descricao;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.web.Tabela;
import br.com.cetip.infra.log.Logger;

/**
 * Relacao para RETIRADA de Garantias da Cesta
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * @since 2006
 */
public class RelacaoRetirarGarantiasDeCesta extends Relacao {

   private Tabela tabelaConfirmacao = null;

   private AtributosColunados atrColunados;

   private List linhas = null;

   private List colunas = null;

   private List listaItens = null;

   private Funcao tipoAcesso = null;

   public RelacaoRetirarGarantiasDeCesta() {
      Logger.info("RelacaoRetirarGarantiasDeCesta - init!");
   }

   /**
    * 
    * 
    */
   private void reset() {
      tabelaConfirmacao = null;
      linhas = new ArrayList();
      colunas = new ArrayList();
      listaItens = new ArrayList();

      // Atribui aos atributos colunados colunas q serao visualizadas na tela
      // de confirmacao
      colunas.add(new Funcao(Contexto.ACAO));
      colunas.add(new Nome(Contexto.TIPO_IF));
      colunas.add(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
      colunas.add(new Descricao(Contexto.TIPO_GARANTIA));
      colunas.add(new Quantidade(Contexto.GARANTIAS_LIBERAR_QUANTIDADE));
      colunas.add(new Booleano(Contexto.GARANTIAS_ITENS));
      colunas.add(new NumeroOperacao(Contexto.OPERACAO));

      atrColunados = new AtributosColunados(colunas);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * br.com.cetip.base.web.acao.Relacao#informarColunas(br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarColunas(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {
      reset();

      Quantidade quantidade = new Quantidade(Contexto.GARANTIAS_LIBERAR_QUANTIDADE);
      quantidade.atribuirTamanho(ICestaDeGarantias.PRECISAO_PARTE_INTEIRA_QUANTIDADE,
            ICestaDeGarantias.PRECISAO_DECIMAL_QUANTIDADE);

      NumeroOperacao numeroOperacao = new NumeroOperacao(Contexto.OPERACAO);

      atributos.atributo(new Funcao(Contexto.ACAO));
      atributos.atributo(new Nome(Contexto.TIPO_IF));
      atributos.atributo(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
      atributos.atributo(new Descricao(Contexto.TIPO_GARANTIA));
      atributos.atributo(quantidade);
      atributos.atributo(numeroOperacao);
      atributos.atributo(new Booleano(Contexto.GARANTIAS_ITENS));
      atributos.atributo(new DescricaoLimitada(Contexto.GARANTIAS_ITENS));

      GrupoDeAtributos grupo = new GrupoDeAtributos(1);
      grupo.atributo(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));

      atributos.grupoDeColunas(new Funcao(Contexto.ACAO), grupo);
      atributos.grupoDeColunas(new Nome(Contexto.TIPO_IF), grupo);
      atributos.grupoDeColunas(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF), grupo);
      atributos.grupoDeColunas(new Descricao(Contexto.TIPO_GARANTIA), grupo);
      atributos.grupoDeColunas(quantidade, grupo);
      atributos.grupoDeColunas(numeroOperacao, grupo);
      atributos.grupoDeColunas(new Booleano(Contexto.GARANTIAS_ITENS), grupo);
      atributos.grupoDeColunas(new DescricaoLimitada(Contexto.GARANTIAS_ITENS), grupo);

      atributos.simplificarLabelsAgrupados(false);
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#chamarServico(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    * br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {

      NumeroCestaGarantia numero = null;
      numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);

      // Veio da relacao?
      if (numero == null) {
         ConfirmacaoRelacaoCestasGarantias tela = (ConfirmacaoRelacaoCestasGarantias) servicos
               .obterTela(ConfirmacaoRelacaoCestasGarantias.class);
         AtributosColunados ac = tela.obterTabela().obterAtributosColunados();
         numero = (NumeroCestaGarantia) ac.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);
         tipoAcesso = (Funcao) ac.obterAtributo(Funcao.class, Contexto.GARANTIAS_TIPO_ACESSO);
      }

      tipoAcesso = (Funcao) dados.obterAtributo(Funcao.class, Contexto.GARANTIAS_TIPO_ACESSO);

      if (tipoAcesso == null || tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDO)) {
         tipoAcesso = ICestaDeGarantias.FUNCAO_GARANTIDO;
         RequisicaoServicoListaGarantiasCesta req = new RequisicaoServicoListaGarantiasCesta();
         req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
         req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(tipoAcesso);
         GrupoDeAtributos retorno = servicos.chamarServico(req);
         return retorno;
      }

      br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoListaGarantiasCesta req = new br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoListaGarantiasCesta();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
      req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(tipoAcesso);
      GrupoDeAtributos retorno = servicos.chamarServico(req);
      return retorno;
   }

   /*
    * (non-Javadoc)
    *
    * @see
    * br.com.cetip.base.web.acao.Relacao#informarParametros(br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarParametros(GrupoDeAtributos parametros, Grupo dados, Servicos servicos) throws Exception {
      parametros.atributo(new Funcao(Contexto.ACAO));
      parametros.atributo(new Nome(Contexto.TIPO_IF));
      parametros.atributo(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
      parametros.atributo(new Descricao(Contexto.TIPO_GARANTIA));
      parametros.atributo(new Quantidade(Contexto.GARANTIAS_LIBERAR_QUANTIDADE));
      parametros.atributo(new Booleano(Contexto.GARANTIAS_ITENS));
      parametros.atributo(new Id(Contexto.GARANTIAS_ITENS));
      parametros.atributo(new NumeroOperacao(Contexto.OPERACAO));
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#obterDestino(br.com.cetip.infra.atributo.Atributo,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public Class obterDestino(Atributo atributo, Grupo parametros, Servicos servicos) throws Exception {
      Iterator iLinhas = linhas.iterator();
      int funcoes = 0;
      boolean temCombo = false;
      Funcao acaoUsuario = (Funcao) parametros.obterAtributo(Funcao.class, Contexto.ACAO);
      Quantidade qtd = (Quantidade) parametros.obterAtributo(Quantidade.class, Contexto.GARANTIAS_LIBERAR_QUANTIDADE);
      Id id = (Id) parametros.obterAtributo(Id.class, Contexto.GARANTIAS_ITENS);
      NumeroOperacao numOperacao = (NumeroOperacao) parametros.obterAtributo(NumeroOperacao.class, Contexto.OPERACAO);
      while (iLinhas.hasNext()) {
         Grupo linha = (Grupo) iLinhas.next();
         Funcao funcao = (Funcao) linha.obterAtributo(Funcao.class, Contexto.ACAO);

         if (!temCombo && funcao != null && funcao.getDomain().getDataElements().size() > 1) {
            temCombo = true;
         }

         if (acaoUsuario != null && !acaoUsuario.vazio() && acaoUsuario.obterConteudo().trim().length() > 0) {
            if (qtd.obterBigDecimal().compareTo(new BigDecimal("0")) > 0) {
               addAtributosColunados(linha, acaoUsuario, id, qtd, numOperacao);
            }

            funcoes++;
         }

         acaoUsuario = (Funcao) parametros.obterAtributoSeguinte(Funcao.class, Contexto.ACAO);
         qtd = (Quantidade) parametros.obterAtributoSeguinte(Quantidade.class, Contexto.GARANTIAS_LIBERAR_QUANTIDADE);
         id = (Id) parametros.obterAtributoSeguinte(Id.class, Contexto.GARANTIAS_ITENS);
         numOperacao = (NumeroOperacao) parametros.obterAtributoSeguinte(NumeroOperacao.class, Contexto.OPERACAO);
      }

      if (temCombo && funcoes == 0) {
         reset();
         throw new Erro(CodigoErro.ACAO_NAO_INDICADA);
      }

      if (listaItens.isEmpty()) {
         throw new Erro(CodigoErro.QUANTIDADE_DEVE_SER_MAIOR_DO_QUE_ZERO);
      }

      tabelaConfirmacao = new Tabela(Contexto.GARANTIAS_CESTA, atrColunados);

      // VALIDA
      new ConfirmacaoRelacaoRetirarGarantiasCesta().validar(parametros, servicos);

      return ConfirmacaoRelacaoRetirarGarantiasCesta.class;
   }

   /**
    *
    * @param linha
    * @param acao
    * @param id
    * @param novoValor
    */
   private void addAtributosColunados(Grupo linha, Funcao acao, Id id, Quantidade novoValor, NumeroOperacao numOperacao) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Id ativo  : " + id);
         Logger.debug(this, "Quantidade: " + novoValor);
      }

      atrColunados.novaLinha();
      atrColunados.atributo(acao);
      atrColunados.atributo(linha.obterAtributo(Nome.class, Contexto.TIPO_IF));
      atrColunados.atributo(linha.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF));
      atrColunados.atributo(linha.obterAtributo(Descricao.class, Contexto.TIPO_GARANTIA));
      atrColunados.atributo(novoValor);
      atrColunados.atributo(linha.obterAtributo(Booleano.class, Contexto.GARANTIAS_ITENS));
      atrColunados.atributo(linha.obterAtributo(DescricaoLimitada.class, Contexto.GARANTIAS_ITENS));
      atrColunados.atributo(numOperacao);
      listaItens.add(id);
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#informarLinks(br.com.cetip.base.web.acao.suporte.Links,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarLinks(Links links, Grupo linha, Servicos servicos) throws Exception {
      links.celulaEditavel(new Funcao(Contexto.ACAO));

      Quantidade quantidade = new Quantidade(Contexto.GARANTIAS_LIBERAR_QUANTIDADE);
      quantidade.atribuirTamanho(ICestaDeGarantias.PRECISAO_PARTE_INTEIRA_QUANTIDADE,
            ICestaDeGarantias.PRECISAO_DECIMAL_QUANTIDADE);
      links.celulaEditavel(quantidade);

      NumeroOperacao nuOp = new NumeroOperacao(Contexto.OPERACAO);
      links.celulaEditavel(nuOp);

      linhas.add(linha);
   }

   /**
    *
    * @return
    */
   public List obterListaItens() {
      return listaItens;
   }

   /**
    *
    * @return
    */
   public Tabela obterTabela() {
      return tabelaConfirmacao;
   }

   /**
    * @return Returns the tipoAcesso.
    * GARANTIDO OU GARANTIDOR
    */
   public Funcao getTipoAcesso() {
      return tipoAcesso;
   }

}