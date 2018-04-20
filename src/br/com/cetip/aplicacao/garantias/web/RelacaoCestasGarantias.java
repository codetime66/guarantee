package br.com.cetip.aplicacao.garantias.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiasFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMapaAcoes;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaCestasGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaAcaoCesta;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoListaCestasGarantias;
import br.com.cetip.aplicacao.garantias.web.selic.RelacaoManutencaoNumeroOperacaoGarantiasDeCesta;
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
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIFContrato;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Detalhe;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.tipo.web.Tabela;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;

/**
 * Relacao de Cestas de Garantia para Manutencao
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class RelacaoCestasGarantias extends Relacao {

   private Tabela tabelaConfirmacao = null;

   private AtributosColunados atrColunados;

   private List linhas = null;

   private List colunas = null;

   private Funcao tipoAcesso = null;

   private boolean tipoAcessoVazio = false;

   private void reset() {
      tabelaConfirmacao = null;
      linhas = new ArrayList();
      colunas = new ArrayList();
      tipoAcessoVazio = Condicional.vazio(tipoAcesso);

      // Atribui aos atributos colunados colunas q serao visualizadas na tela
      // de confirmacao
      if (!tipoAcessoVazio) {
         colunas.add(new Funcao(Contexto.ACAO));
      }

      colunas.add(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));
      colunas.add(new Data(Contexto.GARANTIAS_DATA_CRIACAO));
      colunas.add(new Texto(Contexto.GARANTIAS_STATUS));
      colunas.add(new Booleano(Contexto.INADIMPLENTE_ATIVO));
      colunas.add(new Booleano(Contexto.INADIMPLENTE_EMISSOR));
      colunas.add(new CodigoContaCetip(Contexto.GARANTIAS_PARTICIPANTE));
      colunas.add(new Nome(Contexto.GARANTIAS_PARTICIPANTE));
      colunas.add(new CodigoContaCetip(Contexto.GARANTIAS_CONTRAPARTE));
      colunas.add(new Nome(Contexto.GARANTIAS_CONTRAPARTE));
      colunas.add(new Funcao(Contexto.RESET));

      atrColunados = new AtributosColunados(colunas);
   }

   public void informarColunas(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {
      tipoAcesso = (Funcao) dados.obterAtributo(Funcao.class, Contexto.GARANTIAS_TIPO_ACESSO);

      reset();

      if (!tipoAcessoVazio) {
         atributos.atributoObrigatorio(new Funcao(Contexto.ACAO));
      }

      atributos.atributo(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));
      atributos.atributo(new Data(Contexto.GARANTIAS_DATA_CRIACAO));
      atributos.atributo(new Texto(Contexto.GARANTIAS_STATUS));
      atributos.atributo(new Detalhe(Contexto.DETALHE_ADICIONAL));
      atributos.atributo(new Booleano(Contexto.INADIMPLENTE_ATIVO));
      atributos.atributo(new Booleano(Contexto.INADIMPLENTE_EMISSOR));
      atributos.atributo(new CodigoContaCetip(Contexto.GARANTIAS_PARTICIPANTE));
      atributos.atributo(new Nome(Contexto.GARANTIAS_PARTICIPANTE));
      atributos.atributo(new CodigoContaCetip(Contexto.GARANTIAS_CONTRAPARTE));
      atributos.atributo(new Nome(Contexto.GARANTIAS_CONTRAPARTE));
      atributos.atributo(new Funcao(Contexto.RESET));
   }

   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {
      ResultadoServicoListaCestasGarantias res = null;
      Texto atributoNomeTela = (Texto) dados.obterAtributo(Texto.class, Contexto.FUNCAO);

      if (!Condicional.vazio(atributoNomeTela)) {
         String nomeTela = atributoNomeTela.obterConteudo();

         Id idToken = (Id) dados.obterAtributo(Id.class, Contexto.ACAO);

         FormularioManutencaoCesta formulario = (FormularioManutencaoCesta) servicos.obterTela(Class.forName(nomeTela));
         if (!Condicional.vazio(idToken) && formulario != null) {
            String token = formulario.getToken();

            if (token != null && token.equals(idToken.obterConteudo())) {
               res = formulario.getResultadoLista();
            }
         }
      }

      if (res == null) {
         RequisicaoServicoListaCestasGarantias req = new RequisicaoServicoListaCestasGarantias();
         req.atribuirGARANTIAS_TIPO_ACESSO_Funcao((Funcao) dados.obterAtributo(Funcao.class,
               Contexto.GARANTIAS_TIPO_ACESSO));
         req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(
               NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO));
         req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(
               CodigoContaCetip.class, Contexto.GARANTIAS_CONTRAPARTE));
         req.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(
               CodigoContaCetip.class, Contexto.GARANTIAS_PARTICIPANTE));
         req.atribuirGARANTIAS_STATUS_Id((Id) dados.obterAtributo(Id.class, Contexto.GARANTIAS_STATUS));
         req.atribuirINADIMPLENTE_ATIVO_Booleano((Booleano) dados.obterAtributo(Booleano.class,
               Contexto.INADIMPLENTE_ATIVO));
         req.atribuirINADIMPLENTE_EMISSOR_Booleano((Booleano) dados.obterAtributo(Booleano.class,
               Contexto.INADIMPLENTE_EMISSOR));
         req.atribuirRESET_Funcao((Funcao) dados.obterAtributo(Funcao.class, Contexto.RESET));
         req.atribuirTIPO_IF_GARANTIDO_CodigoTipoIF((CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class,
               Contexto.TIPO_IF_GARANTIDO));

         res = (ResultadoServicoListaCestasGarantias) servicos.executarServico(req);
      }

      AtributosColunados ac = res.obterAtributosColunados();
      GrupoDeAtributos ga = new GrupoDeAtributos(ac);

      return ga;
   }

   public void informarParametros(GrupoDeAtributos parametros, Grupo dados, Servicos servicos) throws Exception {
      parametros.atributo(new Funcao(Contexto.ACAO));
      parametros.atributo(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));
      parametros.atributo(new Data(Contexto.GARANTIAS_DATA_CRIACAO));
      parametros.atributo(new Texto(Contexto.GARANTIAS_STATUS));
      parametros.atributo(new Booleano(Contexto.INADIMPLENTE_ATIVO));
      parametros.atributo(new Booleano(Contexto.INADIMPLENTE_EMISSOR));
      parametros.atributo(new CodigoContaCetip(Contexto.GARANTIAS_PARTICIPANTE));
      parametros.atributo(new Nome(Contexto.GARANTIAS_PARTICIPANTE));
      parametros.atributo(new CodigoContaCetip(Contexto.GARANTIAS_CONTRAPARTE));
      parametros.atributo(new Nome(Contexto.GARANTIAS_CONTRAPARTE));
      parametros.atributo(new Funcao(Contexto.RESET));
      parametros.atributo(new Funcao(Contexto.GARANTIAS_TIPO_ACESSO));
      parametros.atributo(new CodigoIFContrato(Contexto.CONTRATO));
   }

   public Class obterDestino(Atributo atributo, Grupo params, Servicos servicos) throws Exception {
      if (atributo != null && Contexto.DETALHE_ADICIONAL.equals(atributo.obterContexto())) {
         return servicos.obterDestino("Garantias", "RelacaoAtivosVinculadosCestas");
      }

      NumeroCestaGarantia num;
      if (tipoAcessoVazio) {
         num = (NumeroCestaGarantia) params.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);
         Funcao acao;
         acao = (Funcao) params.obterAtributo(Funcao.class, Contexto.ACAO);

         // Valida acao selecionada para a cesta
         if (acao != null) {
            validaAcao(num, acao, servicos);
            if (ICestaDeGarantias.CONSULTAR_GARANTIAS.mesmoConteudo(acao)) {
               return RelacaoConsultaGarantiasDeCesta.class;
            } else if (ICestaDeGarantias.CONSULTAR_HISTORICO.mesmoConteudo(acao)) {
               return RelacaoHistoricoCesta.class;
            }
         } else {
            throw new Erro(CodigoErro.ACAO_NAO_INDICADA);
         }
      }

      Iterator iLinhas = linhas.iterator();
      int funcoes = 0;
      boolean temFuncaoUnica = false;
      boolean temCombo = false;
      Funcao acaoUsuario = (Funcao) params.obterAtributo(Funcao.class, Contexto.ACAO);
      NumeroCestaGarantia numero = (NumeroCestaGarantia) params.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Iterando nas linhas (total: " + linhas.size() + ")");
      }

      int intLinha = 0;
      while (iLinhas.hasNext()) {
         Grupo linha = (Grupo) iLinhas.next();
         Funcao funcao = (Funcao) linha.obterAtributo(Funcao.class, Contexto.ACAO);

         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "DEBUG para a Linha " + intLinha);
            Logger.debug(this, "Funcao : " + funcao.obterConteudo());
            Logger.debug(this, "Cesta  :" + numero.obterConteudo());
            Logger.debug(this, "AcaoUsr:" + acaoUsuario.obterConteudo());
         }

         if (!temCombo && funcao != null && funcao.getDomain().getDataElements().size() > 1) {
            temCombo = true;
         }

         IMapaAcoes ima = GarantiasFactory.getInstance().getInstanceMapaAcoes();
         if (!temFuncaoUnica && ima.ehAcaoUnica(acaoUsuario)) {
            temFuncaoUnica = true;
         }

         if (!Condicional.vazio(acaoUsuario)) {
            validaAcao(numero, acaoUsuario, servicos);
            addAtributosColunados(linha, acaoUsuario);
            funcoes++;
         }

         acaoUsuario = (Funcao) params.obterAtributoSeguinte(Funcao.class, Contexto.ACAO);
         numero = (NumeroCestaGarantia) params.obterAtributoSeguinte(NumeroCestaGarantia.class,
               Contexto.GARANTIAS_CODIGO);
         intLinha++;
      }

      if (funcoes > 1 && temFuncaoUnica) {
         throw new Erro(CodigoErro.ENCONTRADA_MAIS_DE_UMA_OPERACAO);
      } else if (!temCombo || funcoes == 0) {
         Logger.warn(this, "Acao nao indicada! Motivo: ");
         Logger.warn(this, "tem combo? " + temCombo);
         Logger.warn(this, "tem funcoes? " + (funcoes > 0));
         Logger.warn(this, " --- ");

         throw new Erro(CodigoErro.ACAO_NAO_INDICADA);
      }

      if (temFuncaoUnica) {
         Funcao acao = (Funcao) atrColunados.obterAtributo(Funcao.class, Contexto.ACAO);
         num = (NumeroCestaGarantia) atrColunados.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);
         validaAcao(num, acao, servicos);

         if (acao.mesmoConteudo(ICestaDeGarantias.ALTERAR_NUMERO_OPERACAO)) {
            return RelacaoManutencaoNumeroOperacaoGarantiasDeCesta.class;
         }
      }

      tabelaConfirmacao = new Tabela(Contexto.GARANTIAS_CESTA, atrColunados);

      return ConfirmacaoRelacaoCestasGarantias.class;
   }

   private void validaAcao(NumeroCestaGarantia num, Funcao funcao, Servicos servicos) throws Exception {
      RequisicaoServicoValidaAcaoCesta req;
      req = new RequisicaoServicoValidaAcaoCesta();
      req.atribuirACAO_Funcao(funcao);
      req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(tipoAcesso);
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(num);
      servicos.executarServico(req);
   }

   private void addAtributosColunados(Grupo linha, Funcao acao) {
      atrColunados.novaLinha();

      if (!tipoAcessoVazio) {
         atrColunados.atributo(acao);
      }

      atrColunados.atributo(linha.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO));
      atrColunados.atributo(linha.obterAtributo(Data.class, Contexto.GARANTIAS_DATA_CRIACAO));
      atrColunados.atributo(linha.obterAtributo(Texto.class, Contexto.GARANTIAS_STATUS));
      atrColunados.atributo(linha.obterAtributo(Booleano.class, Contexto.INADIMPLENTE));
      atrColunados.atributo(linha.obterAtributo(Booleano.class, Contexto.INADIMPLENTE_ATIVO));
      atrColunados.atributo(linha.obterAtributo(Booleano.class, Contexto.INADIMPLENTE_EMISSOR));
      atrColunados.atributo(linha.obterAtributo(CodigoContaCetip.class, Contexto.GARANTIAS_PARTICIPANTE));
      atrColunados.atributo(linha.obterAtributo(Nome.class, Contexto.GARANTIAS_PARTICIPANTE));
      atrColunados.atributo(linha.obterAtributo(CodigoContaCetip.class, Contexto.GARANTIAS_CONTRAPARTE));
      atrColunados.atributo(linha.obterAtributo(Nome.class, Contexto.GARANTIAS_CONTRAPARTE));
      atrColunados.atributo(linha.obterAtributo(Funcao.class, Contexto.RESET));
   }

   public void informarLinks(Links links, Grupo linha, Servicos servicos) throws Exception {
      Funcao acoes = (Funcao) linha.obterAtributo(Funcao.class, Contexto.ACAO);

      if (!tipoAcessoVazio) {
         if (acoes.getDomain().getDataElements().size() > 1) {
            links.celulaEditavel(new Funcao(Contexto.ACAO));
         }

         linhas.add(linha);
      } else {
         if (acoes.getDomain().getDataElements().size() > 1) {
            Iterator i = acoes.getDomain().getDataElements().iterator();

            while (i.hasNext()) {
               links.funcaoDoLinkDestaLinha((Funcao) i.next());
            }
         }
      }

      links.celula(new Detalhe(Contexto.DETALHE_ADICIONAL));
      links.exibirFuncao(false);
   }

   public Tabela obterTabela() {
      return tabelaConfirmacao;
   }

   /**
    * @return Returns the tipoAcesso.
    */
   public Funcao getTipoAcesso() {
      return tipoAcesso;
   }

}