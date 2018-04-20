package br.com.cetip.aplicacao.garantias.web;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiasFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMapaAcoes;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoExcluirCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoExecutarCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoFechamentoCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoLiberarCestaParaManutencao;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaExcluirCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaExecutarCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaFechamentoCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoValidaExecutarCestaGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoNotificacao;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.TextoLimitado;
import br.com.cetip.infra.atributo.tipo.web.Tabela;

/**
 * Confirmacao para as cestas de garantias marcadas com acoes na tela de relacao
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ConfirmacaoRelacaoCestasGarantias extends AbstractFormularioGarantias {

   /**
    * Mapa de destino para acoes unicas
    */
   private static final Map MAPA = new HashMap();
   static {
      HashMap APORTE = new HashMap();
      APORTE.put(ICestaDeGarantias.FUNCAO_GARANTIDO, FormularioAporteGarantiasCestaGarantido.class);
      APORTE.put(ICestaDeGarantias.FUNCAO_GARANTIDOR, FormularioAporteGarantiasCestaGarantidor.class);

      MAPA.put(ICestaDeGarantias.APORTAR_GARANTIAS, APORTE);
      MAPA.put(ICestaDeGarantias.RETIRAR_GARANTIAS, RelacaoRetirarGarantiasDeCesta.class);
      MAPA.put(ICestaDeGarantias.LIBERAR_GARANTIAS_PARCIAL, FormularioLiberarCestaGarantiasParcial.class);
      MAPA.put(ICestaDeGarantias.EXCLUIR_GARANTIAS, RelacaoExcluirGarantiasDeCesta.class);
      MAPA.put(ICestaDeGarantias.CONSULTAR_GARANTIAS, RelacaoConsultaGarantiasDeCesta.class);
      MAPA.put(ICestaDeGarantias.ALTERAR_CESTA, FormularioAlteraCestaGarantias.class);
      MAPA.put(ICestaDeGarantias.INCLUIR_GARANTIAS, FormularioCadastroItensCestaGarantias.class);
      MAPA.put(ICestaDeGarantias.CONSULTAR_GARANTIAS, RelacaoConsultaGarantiasDeCesta.class);
      MAPA.put(ICestaDeGarantias.CONSULTAR_HISTORICO, RelacaoHistoricoCesta.class);
      MAPA.put(ICestaDeGarantias.DESVINCULAR_GARANTIDO, RelacaoAtivosVinculadosCestas.class);
      MAPA.put(ICestaDeGarantias.LIBERAR_PENHOR_EMISSOR, FormularioLiberaGarantiaPenhorEmissor.class);

   }

   private Tabela tabela;

   public boolean confirmacao(Grupo parametros, Servicos servicos) throws Exception {
      return true;
   }

   /**
    * Monta tela de confirmacao
    */
   public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      GrupoDeGrupos grupo = layout.grupoDeGrupos(1);
      GrupoDeAtributos grupo1 = grupo.grupoDeAtributos(1);

      RelacaoCestasGarantias tela = ((RelacaoCestasGarantias) servicos.obterTela(RelacaoCestasGarantias.class));
      tabela = tela.obterTabela();
      grupo1.atributoNaoEditavel(tabela);
      Atributo tipoAcesso = tela.getTipoAcesso();

      Funcao acaoUnica = ehAcaoUnica();
      if (acaoUnica != null) {
         Atributo numero = tabela.obterAtributosColunados().obterAtributo(NumeroCestaGarantia.class,
               Contexto.GARANTIAS_CODIGO);
         grupo1.atributoOculto(numero);
         grupo1.atributoOculto(acaoUnica);
         grupo1.atributoOculto(tipoAcesso);
      }
   }

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      // Primeira Linha
      AtributosColunados ac = tabela.obterAtributosColunados();

      // Se o primeiro item eh funcao unica,
      // provavelmente nenhum servico serah chamado
      if (ehAcaoUnica() == null) {
         Notificacao notificacoes = new Notificacao(CodigoNotificacao.VAZIO);

         while (ac.avancarLinha()) {
            Funcao funcaoLinha = (Funcao) ac.obterAtributo(Funcao.class, Contexto.ACAO);
            if (funcaoLinha.mesmoConteudo(ICestaDeGarantias.FINALIZAR_CESTA)) {
               finalizarCesta(ac, servicos, notificacoes);
            } else if (funcaoLinha.mesmoConteudo(ICestaDeGarantias.EXCLUIR_CESTA)) {
               excluirCesta(ac, servicos, notificacoes);
            } else if (funcaoLinha.mesmoConteudo(ICestaDeGarantias.LIBERAR_GARANTIAS)) {
               liberarGarantiasCesta(ac, servicos, notificacoes);
            } else if (funcaoLinha.mesmoConteudo(ICestaDeGarantias.LIBERAR_CESTA)) {
               liberarCestaParaManutencao(ac, servicos, notificacoes);
            }
         }

         return notificacoes;
      }

      return null;
   }

   /**
    * Libera a Cesta para Manutencao
    * 
    * @param ac
    * @param servicos
    * @param notificacao
    * @throws Exception
    */
   private void liberarCestaParaManutencao(AtributosColunados ac, Servicos servicos, Notificacao notificacao)
         throws Exception {
      NumeroCestaGarantia num = (NumeroCestaGarantia) ac.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      RequisicaoServicoLiberarCestaParaManutencao req = new RequisicaoServicoLiberarCestaParaManutencao();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(num);

      servicos.executarServico(req);

      Notificacao n = new Notificacao("LiberarCestaManutencao.sucesso");
      n.parametroMensagem(obterCodCestaFormatado(num), 0);
      notificacao.adicionar(n);
   }

   /**
    * Libera as Garantias da Cesta
    * 
    * @param ac
    * @param servicos
    * @throws Exception
    */
   private void liberarGarantiasCesta(AtributosColunados ac, Servicos servicos, Notificacao notificacao)
         throws Exception {
      NumeroCestaGarantia numero = (NumeroCestaGarantia) ac.obterAtributo(NumeroCestaGarantia.class,Contexto.GARANTIAS_CODIGO);
      CodigoContaCetip contaParticipante = (CodigoContaCetip)ac.obterAtributo(CodigoContaCetip.class, Contexto.GARANTIAS_CONTRAPARTE);

      RequisicaoServicoValidaExecutarCestaGarantias reqValida = null;
      ResultadoServicoValidaExecutarCestaGarantias resValida = null;

      reqValida = new RequisicaoServicoValidaExecutarCestaGarantias();
      reqValida.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
      reqValida.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(contaParticipante);

      RequisicaoServicoExecutarCestaGarantias reqExecuta = null;
      reqExecuta = new RequisicaoServicoExecutarCestaGarantias();
      reqExecuta.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);

      resValida = (ResultadoServicoValidaExecutarCestaGarantias) servicos.executarServico(reqValida);
      reqExecuta.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(resValida
            .obterGARANTIAS_CONTRAPARTE_CodigoContaCetip());

      servicos.executarServico(reqExecuta);

      Notificacao n = new Notificacao("ExecutarCestaGarantias.Sucesso");
      n.parametroMensagem(obterCodCestaFormatado(numero), 0);
      notificacao.adicionar(n);
   }

   /**
    * Exclui a Cesta de Garantia
    * 
    * @param ac
    * @param servicos
    * @param notificacoes
    * @throws Exception
    */
   private void excluirCesta(AtributosColunados ac, Servicos servicos, Notificacao notificacao) throws Exception {
      NumeroCestaGarantia num = (NumeroCestaGarantia) ac.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      RequisicaoServicoExcluirCestaGarantias req = null;
      req = new RequisicaoServicoExcluirCestaGarantias();
      Id idCesta = num.copiarParaId();
      idCesta.atribuirContexto(Contexto.GARANTIAS_CESTA);
      req.atribuirGARANTIAS_CESTA_Id(idCesta);

      RequisicaoServicoValidaExcluirCestaGarantias reqValida = null;
      reqValida = new RequisicaoServicoValidaExcluirCestaGarantias();
      reqValida.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(num);

      servicos.executarServico(reqValida);
      servicos.executarServico(req);

      Notificacao n = new Notificacao("ExcluirCestaGarantias.Sucesso");
      n.parametroMensagem(obterCodCestaFormatado(num), 0);
      notificacao.adicionar(n);
   }

   /**
    * Finaliza a Cesta de Garantia
    * 
    * @param ac
    * @param servicos
    * @param notificacao
    * @throws Exception
    */
   private void finalizarCesta(AtributosColunados ac, Servicos servicos, Notificacao notificacao) throws Exception {
      RequisicaoServicoValidaFechamentoCestaGarantias reqValida;
      reqValida = new RequisicaoServicoValidaFechamentoCestaGarantias();

      RequisicaoServicoFechamentoCestaGarantias req;

      NumeroCestaGarantia num = (NumeroCestaGarantia) ac.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      reqValida.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(num);

      // valida
      servicos.chamarServico(reqValida);

      // marca status Em Finalizacao
      req = new RequisicaoServicoFechamentoCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(num);
      req.atribuirGARANTIAS_CESTA_Booleano(new Booleano(Booleano.VERDADEIRO));
      servicos.chamarServico(req);

      // lanca o mig
      req = new RequisicaoServicoFechamentoCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(num);
      req.atribuirGARANTIAS_CESTA_Booleano(new Booleano(Booleano.FALSO));
      servicos.chamarServicoAssincrono(req);

      Notificacao n = new Notificacao("FechamentoCestaGarantias.Sucesso");
      n.parametroMensagem(obterCodCestaFormatado(num), 0);
      notificacao.adicionar(n);
   }

   private Funcao ehAcaoUnica() throws Exception {
      // Primeira Linha
      AtributosColunados ac = tabela.obterAtributosColunados();
      ac.avancarLinha();

      // Primeira funcao
      Funcao funcao = (Funcao) ac.obterAtributo(Funcao.class, Contexto.ACAO);

      // Volta para a primeira linha
      ac.reiniciarLinha();

      // Se o primeiro item eh funcao unica, deve redirecionar
      // para uma tela que trata especificamente este caso
      IMapaAcoes acoes = GarantiasFactory.getInstance().getInstanceMapaAcoes();
      return acoes.ehAcaoUnica(funcao) ? funcao : null;
   }

   public Class obterDestino(Grupo grupo, Servicos servicos) throws Exception {
      // Se o primeiro item eh funcao unica, deve redirecionar
      // para uma tela que trata especificamente este caso
      Funcao tipoAcesso = (Funcao) grupo.obterAtributo(Funcao.class, Contexto.GARANTIAS_TIPO_ACESSO);
      Funcao funcao = ehAcaoUnica();
      if (funcao != null) {
         Class destino = null;
         Object objetoMapa = MAPA.get(funcao);

         if (objetoMapa instanceof Class) {
            destino = (Class) objetoMapa;
         } else if (objetoMapa instanceof Map) {
            destino = (Class) ((Map) objetoMapa).get(tipoAcesso);
         }

         return destino;
      }

      return null;
   }

   public Tabela obterTabela() {
      return tabela;
   }

   public TextoLimitado obterCodCestaFormatado(NumeroCestaGarantia numero) {
      return new TextoLimitado(numero.obterConteudo().toString().length() < 8 ? "0" + numero.obterConteudo().toString()
            : numero.obterConteudo().toString());
   }

}