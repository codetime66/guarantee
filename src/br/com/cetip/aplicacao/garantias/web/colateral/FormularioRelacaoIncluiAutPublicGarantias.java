package br.com.cetip.aplicacao.garantias.web.colateral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoConsultaDetalhadaAutorizacaoPublicGarantias;
import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoValidaInclusaoAutorizacaoPublicGarantias;
import br.com.cetip.aplicacao.garantias.servico.colateral.ResultadoServicoConsultaDetalhadaAutorizacaoPublicGarantias;
import br.com.cetip.aplicacao.registrooperacao.servico.garantias.RequisicaoServicoRegistrarAutorizacaoPublicGarantias;
import br.com.cetip.aplicacao.registrooperacao.servico.garantias.ResultadoServicoRegistrarAutorizacaoPublicGarantias;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.RazaoSocial;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;

/**
 * <p>
 * Lista os instrumentos financeiros.
 * </p>
 * 
 */
public class FormularioRelacaoIncluiAutPublicGarantias extends Relacao {

   private CodigoIF codigoIF;
   private CodigoContaCetip contaParticipante;
   private CPFOuCNPJ cpfCnpjParticipante;
   private RazaoSocial razaoSocialParticipante;
   private GrupoDeAtributos atributos;
   private ArrayList codigosEnviados;
   private boolean chamarServico = false;
   private CodigoOperacao codigoOperacao;
   private ArrayList codOperacao;
   private HashMap mapOperacoes;
   private ArrayList listCodIFAutorizados;
   private GrupoDeAtributos atrServico = null;
   private GrupoDeAtributos atrServicoant = null;

   public void informarColunas(GrupoDeAtributos colunas, Grupo grupo, Servicos servicos) throws Exception {
      colunas.atributo(new Funcao(Contexto.AUTORIZACAO_PUBLICIDADE_GARANTIAS));
      colunas.atributo(new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO_CONTRATO));
      colunas.atributo(new NumeroCestaGarantia(Contexto.GARANTIAS_CESTA));
      colunas.atributo(new CodigoContaCetip(Contexto.CONTRA_PARTE_CONTRATO));
      colunas.atributo(new CPFOuCNPJ(Contexto.CONTRA_PARTE_CONTRATO));
   }

   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      if (servicos.chamadaInicial()) {
         contaParticipante = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class, Contexto.PARTE_CONTRATO);
         cpfCnpjParticipante = (CPFOuCNPJ) dados.obterAtributo(CPFOuCNPJ.class, Contexto.PARTE_CONTRATO);
         RequisicaoServicoConsultaDetalhadaAutorizacaoPublicGarantias req = new RequisicaoServicoConsultaDetalhadaAutorizacaoPublicGarantias();
         req.atribuirPARTE_CONTRATO_CodigoContaCetip(contaParticipante);
         ResultadoServicoConsultaDetalhadaAutorizacaoPublicGarantias resConsulta = (ResultadoServicoConsultaDetalhadaAutorizacaoPublicGarantias) servicos
               .executarServico(req);
         razaoSocialParticipante = resConsulta.obterPARTE_CONTRATO_RazaoSocial();
      }

      GrupoDeGrupos geral = layout.grupoDeGrupos(1);
      GrupoDeAtributos gpart = geral.grupoDeAtributos(3);
      gpart.contexto(Contexto.AUTORIZACAO_PUBLICIDADE_GARANTIAS);
      gpart.posicaoTitulos(GrupoDeAtributos.TITULOS_A_ESQUERDA);

      gpart.atributoNaoEditavel(contaParticipante);
      gpart.atributoNaoEditavel(razaoSocialParticipante);
      gpart.atributoNaoEditavel(cpfCnpjParticipante);

      GrupoDeAtributos gContrato = geral.grupoDeAtributos(1);
      gContrato.atributoObrigatorio(new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO_CONTRATO));
      chamarServico = false;
   }

   public void validar(Grupo dados, Servicos servicos) throws Exception {
      codigoIF = (CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.INSTRUMENTO_FINANCEIRO_CONTRATO);

      RequisicaoServicoValidaInclusaoAutorizacaoPublicGarantias req = new RequisicaoServicoValidaInclusaoAutorizacaoPublicGarantias();
      req.atribuirPARTE_CONTRATO_CodigoContaCetip(contaParticipante);
      req.atribuirPARTE_CONTRATO_CPFOuCNPJ(cpfCnpjParticipante);
      req.atribuirINSTRUMENTO_FINANCEIRO_Funcao(Funcao.INCLUSAO);
      req.atribuirINSTRUMENTO_FINANCEIRO_CONTRATO_CodigoIF(codigoIF);
      servicos.chamarServico(req, dados);
      Logger.debug(this, "serviço ServicoValidaInclusaoAutorizacaoPublicGarantias retornou com sucesso");

      chamarServico = true;
   }

   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {
      if (servicos.chamadaInicial()) {
         // reinicia objetos
         atributos = new GrupoDeAtributos();
         atrServico = null;
         atrServicoant = null;
         codigosEnviados = new ArrayList();
         codOperacao = new ArrayList();
         codigoIF = new CodigoIF();

         return atributos;
      }

      if (chamarServico) {
         RequisicaoServicoConsultaDetalhadaAutorizacaoPublicGarantias requisicao = new RequisicaoServicoConsultaDetalhadaAutorizacaoPublicGarantias();

         CodigoContaCetip conta = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
               Contexto.PARTE_CONTRATO);
         CPFOuCNPJ cpfCnpj = (CPFOuCNPJ) dados.obterAtributo(CPFOuCNPJ.class, Contexto.PARTE_CONTRATO);

         if (!Condicional.vazio(codigoIF)) {
            requisicao.atribuirINSTRUMENTO_FINANCEIRO_CONTRATO_CodigoIF(codigoIF);

            String sCodIF = codigoIF.obterConteudo();

            if (codigosEnviados.contains(sCodIF)) {
               return atributos;
            }

            codigosEnviados.add(sCodIF);
         }

         if (!Condicional.vazio(conta)) {
            requisicao.atribuirPARTE_CONTRATO_CodigoContaCetip(conta);
         }

         if (!Condicional.vazio(cpfCnpj)) {
            requisicao.atribuirPARTE_CONTRATO_CPFOuCNPJ(cpfCnpj);
         }

         atrServico = servicos.chamarServico(requisicao);
         return atrServicoant = servicos.combinarComSegundoAoFinal(atrServicoant, atrServico);
      }

      return atrServicoant;
   }

   public void informarParametros(GrupoDeAtributos gda, Grupo grupo, Servicos servicos) throws Exception {
      gda.atributo(new Funcao(Contexto.AUTORIZACAO_PUBLICIDADE_GARANTIAS));
      gda.atributo(new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO_CONTRATO));
   }

   public Class obterDestino(Atributo atributo, Grupo grupo, Servicos servicos) throws Exception {

      CodigoIF codigoIFLinha = new CodigoIF();
      CodigoIF codigoIFAtual = new CodigoIF();
      CodigoIF codigoIFExcluido = new CodigoIF();

      Iterator itFuncao = grupo.iterator(new Funcao(Contexto.AUTORIZACAO_PUBLICIDADE_GARANTIAS));
      Iterator itCodigoIF = grupo.iterator(new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO_CONTRATO));
      mapOperacoes = new HashMap();
      listCodIFAutorizados = new ArrayList();

      HashSet listCodigoIF = new HashSet();

      while (itFuncao.hasNext()) {
         Funcao funcao = (Funcao) itFuncao.next();

         while (itCodigoIF.hasNext()) {
            codigoIFAtual = (CodigoIF) itCodigoIF.next();
            if (Condicional.vazio(funcao) && !codigoIFAtual.mesmoConteudo(codigoIFExcluido)) {
               listCodigoIF.add(codigoIFAtual);
            } else {
               codigoIFExcluido = codigoIFAtual;
               if (listCodigoIF.contains(codigoIFExcluido)) {
                  listCodigoIF.remove(codigoIFExcluido);
               }
            }
            break;
         }
      }

      Iterator itCodigoIFLinha = listCodigoIF.iterator();
      while (itCodigoIFLinha.hasNext()) {
         //Chama o servico de autorização
         RequisicaoServicoRegistrarAutorizacaoPublicGarantias reqPrinc = new RequisicaoServicoRegistrarAutorizacaoPublicGarantias();
         codigoIFLinha = (CodigoIF) itCodigoIFLinha.next();
         reqPrinc.atribuirINSTRUMENTO_FINANCEIRO_CONTRATO_CodigoIF(codigoIFLinha);
         reqPrinc.atribuirPARTE_CONTRATO_CodigoContaCetip(contaParticipante);
         reqPrinc.atribuirPARTE_CONTRATO_CPFOuCNPJ(cpfCnpjParticipante);
         ResultadoServicoRegistrarAutorizacaoPublicGarantias resPrinc = (ResultadoServicoRegistrarAutorizacaoPublicGarantias) servicos
               .executarServico(reqPrinc);

         codigoOperacao = resPrinc.obterAUTORIZACAO_CodigoOperacao();

         codOperacao.add(codigoOperacao);
         listCodIFAutorizados.add(codigoIFLinha);
         mapOperacoes.put(codigoIFLinha, codigoOperacao);
      }

      if (listCodigoIF.size() == 0) {
         return null;
      }
      return FormularioNotificacaoAutPublicGarantias.class;
   }

   public void informarLinks(Links links, Grupo grupo, Servicos servicos) throws Exception {
      links.colunaEditavel(new Funcao(Contexto.AUTORIZACAO_PUBLICIDADE_GARANTIAS));
   }

   public CodigoOperacao getCodigoOperacao() {
      return codigoOperacao;
   }

   public void setCodigoOperacao(CodigoOperacao codigoOperacao) {
      this.codigoOperacao = codigoOperacao;
   }

   public ArrayList getListCodOperacao() {
      return codOperacao;
   }

   public HashMap getMapOperacoes() {
      return mapOperacoes;
   }

   public ArrayList getCodigosEnviados() {
      return listCodIFAutorizados;
   }

}
