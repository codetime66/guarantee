package br.com.cetip.aplicacao.garantias.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaCestasGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaStatusCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaAcaoCesta;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoListaCestasGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoListaStatusCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.web.selic.RelacaoManutencaoNumeroOperacaoGarantiasDeCesta;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;

/**
 * Tela Generica de Manutencao de Cesta
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public abstract class FormularioManutencaoCesta extends AbstractFormularioGarantias {

   protected static final Funcao BRANCO = new Funcao(Contexto.ACAO, "           ");

   private static final Id BRANCO_ID = new Id("         ", "");

   private static final Map MAPA_DESTINO = new HashMap();

   static {
      MAPA_DESTINO.put(BRANCO, RelacaoCestasGarantias.class);
      MAPA_DESTINO.put(ICestaDeGarantias.FINALIZAR_CESTA, FormularioEfetuarFechamentoCesta.class);
      MAPA_DESTINO.put(ICestaDeGarantias.ALTERAR_CESTA, FormularioAlteraCestaGarantias.class);
      MAPA_DESTINO.put(ICestaDeGarantias.EXCLUIR_CESTA, FormularioExcluirCestaGarantias.class);
      MAPA_DESTINO.put(ICestaDeGarantias.LIBERAR_GARANTIAS, FormularioLiberarCestaGarantias.class);
      MAPA_DESTINO.put(ICestaDeGarantias.LIBERAR_GARANTIAS_PARCIAL, FormularioLiberarCestaGarantiasParcial.class);
      MAPA_DESTINO.put(ICestaDeGarantias.RETIRAR_GARANTIAS, RelacaoRetirarGarantiasDeCesta.class);
      MAPA_DESTINO.put(ICestaDeGarantias.INCLUIR_GARANTIAS, FormularioCadastroItensCestaGarantias.class);
      MAPA_DESTINO.put(ICestaDeGarantias.EXCLUIR_GARANTIAS, FormularioCadastroItensCestaGarantias.class);
      MAPA_DESTINO.put(ICestaDeGarantias.CONSULTAR_GARANTIAS, RelacaoConsultaGarantiasDeCesta.class);
      MAPA_DESTINO.put(ICestaDeGarantias.LIBERAR_CESTA, FormularioLiberarCestaManutencao.class);
      MAPA_DESTINO.put(ICestaDeGarantias.CONSULTAR_HISTORICO, RelacaoHistoricoCesta.class);
      MAPA_DESTINO.put(ICestaDeGarantias.LIBERAR_PENHOR_EMISSOR, FormularioLiberaGarantiaPenhorEmissor.class);
      MAPA_DESTINO.put(ICestaDeGarantias.DESVINCULAR_GARANTIDO, RelacaoAtivosVinculadosCestas.class);
      MAPA_DESTINO
            .put(ICestaDeGarantias.ALTERAR_NUMERO_OPERACAO, RelacaoManutencaoNumeroOperacaoGarantiasDeCesta.class);
   }

   private transient Class destinoClass;

   private Map mapaDestino = new HashMap(MAPA_DESTINO);

   private ResultadoServicoListaCestasGarantias resultadoLista;

   private String token;

   private Id idToken;

   /**
    * As telas Garantido e Garantidor devem retornar as possiveis acoes atravez deste metodo
    * 
    * @return
    */
   protected abstract Funcao[] obterAcoesTela();

   protected abstract Funcao obterNomeFuncaoTela();

   protected void alterarDestino(Funcao funcao, Class classe) {
      mapaDestino.put(funcao, classe);
   }

   private void newToken() {
      token = hashCode() + "_" + System.currentTimeMillis();
      if (idToken == null) {
         idToken = new Id(Contexto.ACAO, token);
      } else {
         idToken.atribuirConteudo(token);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.base.web.acao.Formulario#entrada(br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos,
    *      br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void entrada(GrupoDeGrupos layout, Grupo parametros, Servicos servicos) throws Exception {
      newToken();

      layout.contexto(Contexto.GARANTIAS_MANUTENCAO);
      GrupoDeGrupos grupoDados = layout.grupoDeGrupos(1);
      GrupoDeAtributos principal = grupoDados.grupoDeAtributos(1);
      principal.atributo(new CodigoContaCetip(Contexto.GARANTIAS_PARTICIPANTE));
      principal.atributo(new CodigoContaCetip(Contexto.GARANTIAS_CONTRAPARTE));
      principal.atributo(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));
      principal.atributo(getStatusCesta(servicos));
      principal.atributo(new Booleano(Contexto.INADIMPLENTE_ATIVO));
      principal.atributo(new Booleano(Contexto.INADIMPLENTE_EMISSOR));

      Funcao acao = new Funcao(Contexto.ACAO);
      acao.getDomain().clear();
      acao.getDomain().add(BRANCO);

      Funcao[] acoes = obterAcoesTela();
      for (int i = 0; i < acoes.length; i++) {
         acao.getDomain().add(acoes[i]);
      }

      principal.atributo(acao);
      principal.atributo(FormularioVinculaCestaContrato.getRegraLiberacao());
      principal.atributo(getTiposIF(servicos));
      principal.atributoOculto(obterNomeFuncaoTela());
      principal.atributoOculto(idToken);

      // nome da tela escondido
      String nomeTela = getClass().getName();
      principal.atributoOculto(new Texto(Contexto.FUNCAO, nomeTela));
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.base.web.acao.Formulario#chamarServico(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    *      br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      atualizaDestino(dados, servicos);

      RequisicaoServicoListaCestasGarantias req = new RequisicaoServicoListaCestasGarantias();
      req.atribuirGARANTIAS_TIPO_ACESSO_Funcao((Funcao) dados.obterAtributo(Funcao.class,
            Contexto.GARANTIAS_TIPO_ACESSO));
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(
            NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO));
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_CONTRAPARTE));
      req.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(
            CodigoContaCetip.class, Contexto.GARANTIAS_PARTICIPANTE));
      req.atribuirINADIMPLENTE_ATIVO_Booleano((Booleano) dados.obterAtributo(Booleano.class,
            Contexto.INADIMPLENTE_ATIVO));
      req.atribuirINADIMPLENTE_EMISSOR_Booleano((Booleano) dados.obterAtributo(Booleano.class,
            Contexto.INADIMPLENTE_EMISSOR));
      req.atribuirGARANTIAS_STATUS_Id((Id) dados.obterAtributo(Id.class, Contexto.GARANTIAS_STATUS));
      req.atribuirRESET_Funcao((Funcao) dados.obterAtributo(Funcao.class, Contexto.RESET));
      req.atribuirTIPO_IF_GARANTIDO_CodigoTipoIF((CodigoTipoIF) dados.obterAtributo(CodigoTipoIF.class,
            Contexto.TIPO_IF_GARANTIDO));

      resultadoLista = (ResultadoServicoListaCestasGarantias) servicos.executarServico(req);

      return null;
   }

   public ResultadoServicoListaCestasGarantias getResultadoLista() {
      ResultadoServicoListaCestasGarantias retorno = resultadoLista;
      resultadoLista = null;
      return retorno;
   }

   /**
    * Atualiza o destino relacionado a acao selecionada
    * 
    * @param dados
    * @throws Exception
    */
   private void atualizaDestino(Grupo dados, Servicos servicos) throws Exception {
      Funcao acao = (Funcao) dados.obterAtributo(Funcao.class, Contexto.ACAO);
      Funcao tipoAcesso = (Funcao) dados.obterAtributo(Funcao.class, Contexto.GARANTIAS_TIPO_ACESSO);
      NumeroCestaGarantia num = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      destinoClass = (Class) mapaDestino.get(acao);

      if (!acao.mesmoConteudo(BRANCO)) {
         if (Condicional.vazio(num)) {
            Erro erro = new Erro(CodigoErro.CAMPO_OBRIGATORIO);
            erro.parametroMensagem("Código da Cesta", 0);
            throw erro;
         }

         RequisicaoServicoValidaAcaoCesta req = new RequisicaoServicoValidaAcaoCesta();
         req.atribuirACAO_Funcao(acao);
         req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(tipoAcesso);
         req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(num);

         servicos.chamarServico(req);
      } else {
         newToken();
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.base.web.acao.Formulario#obterDestino(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    *      br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public Class obterDestino(Grupo dados, Servicos servicos) throws Exception {
      return destinoClass;
   }

   private CodigoTipoIF getTiposIF(Servicos servicos) throws Exception {
      RequisicaoServicoObterCombosTipoInstrumentoFinanceiro reqObterCombo = new RequisicaoServicoObterCombosTipoInstrumentoFinanceiro();
      ResultadoServicoObterCombosTipoInstrumentoFinanceiro resTipoIf = (ResultadoServicoObterCombosTipoInstrumentoFinanceiro) servicos
            .executarServico(reqObterCombo);

      CodigoTipoIF codigoTipoIf = resTipoIf.obterTIPO_IF_GARANTIDO_CodigoTipoIF();
      return codigoTipoIf;
   }

   /**
    * Obtem lista de descricao dos status de cesta
    * 
    * @param servicos
    * @return
    * @throws Exception
    */
   private Id getStatusCesta(Servicos servicos) throws Exception {
      Id status = new Id(Contexto.GARANTIAS_STATUS);
      status.getDomain().add(BRANCO_ID);

      RequisicaoServicoListaStatusCestaGarantias req;
      req = new RequisicaoServicoListaStatusCestaGarantias();

      ResultadoServicoListaStatusCestaGarantias res;
      res = (ResultadoServicoListaStatusCestaGarantias) servicos.executarServico(req);

      List listaStatus = res.obterListaGARANTIAS_STATUS_Id();
      for (Iterator it = listaStatus.iterator(); it.hasNext();) {
         Id _status = (Id) it.next();
         status.getDomain().add(_status);
      }

      return status;
   }

   public String getToken() {
      String t = token;
      token = null;
      return t;
   }

}
