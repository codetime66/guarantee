package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaAtivosVinculados;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoListaAtivosVinculados;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.log.Logger;

/**
 * Relacao de Cestas de Garantia para Manutencao
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class RelacaoAtivosVinculadosCestas extends Relacao {

   private Funcao tipoAcesso = null;

   private boolean tipoAcessoVazio = false;

   public void informarColunas(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {
      tipoAcesso = (Funcao) dados.obterAtributo(Funcao.class, Contexto.GARANTIAS_TIPO_ACESSO);
      atributos.atributo(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));
      atributos.atributo(new CodigoIF(Contexto.PARTICIPANTE));
      atributos.atributo(new CodigoTipoIF(Contexto.PARTICIPANTE));
      atributos.atributo(new Booleano(Contexto.INADIMPLENTE));
   }

   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoListaAtivosVinculados req = new RequisicaoServicoListaAtivosVinculados();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(
            NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO));

      ResultadoServicoListaAtivosVinculados res = (ResultadoServicoListaAtivosVinculados) servicos.executarServico(req);

      AtributosColunados ac = res.obterAtributosColunados();
      GrupoDeAtributos ga = new GrupoDeAtributos(ac);

      return ga;
   }

   public void informarParametros(GrupoDeAtributos parametros, Grupo dados, Servicos servicos) throws Exception {
      parametros.atributo(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));
      parametros.atributo(new CodigoIF(Contexto.PARTICIPANTE));
      parametros.atributo(new CodigoTipoIF(Contexto.PARTICIPANTE));
   }

   public Class obterDestino(Atributo atributo, Grupo parametros, Servicos servicos) throws Exception {
      if (atributo instanceof NumeroCestaGarantia) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Click NumeroCestaGarantia(" + atributo.obterContexto() + ") = " + atributo);
         }

         return RelacaoConsultaGarantiasDeCesta.class;
      }

      return FormularioDesvincularAtivoCesta.class;
   }

   public void informarLinks(Links links, Grupo linha, Servicos servicos) throws Exception {
      Booleano inadimplente = (Booleano) linha.obterAtributo(Booleano.class, Contexto.INADIMPLENTE);
      if (!tipoAcessoVazio && tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDOR) && inadimplente.ehFalso()) {
         links.funcaoDoLinkDestaLinha(new Funcao(Contexto.ACAO));
         links.funcaoDoLinkDestaLinha(ICestaDeGarantias.DESVINCULAR_GARANTIDO);
      }

      links.coluna(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));
      links.exibirFuncao(true);
   }

}