package br.com.cetip.aplicacao.garantias.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.Descricao;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.tipo.web.Tabela;
import br.com.cetip.infra.log.Logger;

/**
 * Relacao de Garantias da Cesta
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class RelacaoExcluirGarantiasDeCesta extends Relacao {

   private Tabela tabelaConfirmacao = null;

   private List parametros = null;

   private AtributosColunados atrColunados;

   private List linhas = null;

   private List colunas = null;

   private void reset() {
      tabelaConfirmacao = null;
      linhas = new ArrayList();
      colunas = new ArrayList();
      parametros = new ArrayList();

      // Atribui aos atributos colunados colunas q serao visualizadas na tela
      // de confirmacao
      colunas.add(new Funcao(Contexto.ACAO));
      colunas.add(new Nome(Contexto.TIPO_IF));
      colunas.add(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
      colunas.add(new Descricao(Contexto.TIPO_GARANTIA));
      colunas.add(new Quantidade(Contexto.GARANTIAS_QUANTIDADE));
      colunas.add(new Booleano(Contexto.GARANTIAS_ITENS));

      atrColunados = new AtributosColunados(colunas);
   }

   /**
    * 
    */
   public void informarColunas(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {
      reset();

      atributos.atributo(new Funcao(Contexto.ACAO));
      atributos.atributo(new Nome(Contexto.TIPO_IF));
      atributos.atributo(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
      atributos.atributo(new Descricao(Contexto.TIPO_GARANTIA));
      atributos.atributo(new Quantidade(Contexto.GARANTIAS_QUANTIDADE));
      atributos.atributo(new Booleano(Contexto.GARANTIAS_ITENS));
      atributos.atributo(new DescricaoLimitada(Contexto.GARANTIAS_ITENS));

      GrupoDeAtributos grupo = new GrupoDeAtributos(1);
      grupo.atributo(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));

      atributos.grupoDeColunas(new Funcao(Contexto.ACAO), grupo);
      atributos.grupoDeColunas(new Nome(Contexto.TIPO_IF), grupo);
      atributos.grupoDeColunas(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF), grupo);
      atributos.grupoDeColunas(new Descricao(Contexto.TIPO_GARANTIA), grupo);
      atributos.grupoDeColunas(new Quantidade(Contexto.GARANTIAS_QUANTIDADE), grupo);
      atributos.grupoDeColunas(new Booleano(Contexto.GARANTIAS_ITENS), grupo);
      atributos.grupoDeColunas(new DescricaoLimitada(Contexto.GARANTIAS_ITENS), grupo);

      atributos.simplificarLabelsAgrupados(false);
   }

   /**
    * 
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
      }

      RequisicaoServicoListaGarantiasCesta req = new RequisicaoServicoListaGarantiasCesta();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
      req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(ICestaDeGarantias.FUNCAO_GARANTIDOR);

      GrupoDeAtributos retorno = servicos.chamarServico(req);
      return retorno;
   }

   /**
    * 
    */
   public void informarParametros(GrupoDeAtributos grupoAtributos, Grupo dados, Servicos servicos) throws Exception {
      grupoAtributos.atributo(new Funcao(Contexto.ACAO));
      grupoAtributos.atributo(new Nome(Contexto.TIPO_IF));
      grupoAtributos.atributo(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
      grupoAtributos.atributo(new Descricao(Contexto.TIPO_GARANTIA));
      grupoAtributos.atributo(new Quantidade(Contexto.GARANTIAS_QUANTIDADE));
      grupoAtributos.atributo(new Booleano(Contexto.GARANTIAS_ITENS));
      grupoAtributos.atributo(new Texto(Contexto.GARANTIAS_ITENS));
      grupoAtributos.atributo(new Id(Contexto.GARANTIAS_ITENS));
   }

   /**
    * 
    */
   public Class obterDestino(Atributo atributo, Grupo grupo, Servicos servicos) throws Exception {
      Iterator iLinhas = linhas.iterator();
      int funcoes = 0;
      boolean temCombo = false;

      Funcao acaoUsuario = (Funcao) grupo.obterAtributo(Funcao.class, Contexto.ACAO);
      Id id = (Id) grupo.obterAtributo(Id.class, Contexto.GARANTIAS_ITENS);
      Texto txt = (Texto) grupo.obterAtributo(Texto.class, Contexto.GARANTIAS_ITENS);
      while (iLinhas.hasNext()) {
         Grupo linha = (Grupo) iLinhas.next();
         Funcao funcao = (Funcao) linha.obterAtributo(Funcao.class, Contexto.ACAO);

         if (!temCombo && funcao != null && funcao.getDomain().getDataElements().size() > 1) {
            temCombo = true;
         }

         if (acaoUsuario != null && !acaoUsuario.vazio() && acaoUsuario.obterConteudo().trim().length() > 0) {
            addAtributosColunados(linha, acaoUsuario, id, txt);
            funcoes++;
         }

         acaoUsuario = (Funcao) grupo.obterAtributoSeguinte(Funcao.class, Contexto.ACAO);
         id = (Id) grupo.obterAtributoSeguinte(Id.class, Contexto.GARANTIAS_ITENS);
         txt = (Texto) grupo.obterAtributoSeguinte(Texto.class, Contexto.GARANTIAS_ITENS);
      }

      if (temCombo && funcoes == 0) {
         reset();
         throw new Erro(CodigoErro.ACAO_NAO_INDICADA);
      }

      tabelaConfirmacao = new Tabela(Contexto.GARANTIAS_CESTA, atrColunados);

      return ConfirmacaoRelacaoExcluirGarantiasCesta.class;
   }

   private void addAtributosColunados(Grupo linha, Funcao acao, Id id, Texto txt) {
      atrColunados.novaLinha();

      atrColunados.atributo(acao);

      atrColunados.atributo(linha.obterAtributo(Nome.class, Contexto.TIPO_IF));
      atrColunados.atributo(linha.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF));
      atrColunados.atributo(linha.obterAtributo(Descricao.class, Contexto.TIPO_GARANTIA));
      atrColunados.atributo(linha.obterAtributo(Quantidade.class, Contexto.GARANTIAS_QUANTIDADE));
      atrColunados.atributo(linha.obterAtributo(Booleano.class, Contexto.GARANTIAS_ITENS));
      atrColunados.atributo(linha.obterAtributo(DescricaoLimitada.class, Contexto.GARANTIAS_ITENS));

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Parametros da garantia >");
         Logger.debug(this, "   Id: " + id);
         Logger.debug(this, "   Texto: " + txt);
      }

      Map param = new HashMap();
      param.put(Id.class, id);
      param.put(Texto.class, txt);
      parametros.add(param);
   }

   public void informarLinks(Links links, Grupo linha, Servicos servicos) throws Exception {
      links.celulaEditavel(new Funcao(Contexto.ACAO));
      linhas.add(linha);
   }

   public Tabela obterTabela() {
      return tabelaConfirmacao;
   }

   public List obterParametros() {
      return parametros;
   }

}