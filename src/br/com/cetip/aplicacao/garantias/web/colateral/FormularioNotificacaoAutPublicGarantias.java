package br.com.cetip.aplicacao.garantias.web.colateral;

import java.util.ArrayList;
import java.util.HashMap;

import br.com.cetip.base.web.acao.Formulario;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoOperacao;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

/**
 * @author debora
 *
 */
public class FormularioNotificacaoAutPublicGarantias extends Formulario {

   private ArrayList codEnviados;

   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servico) throws Exception {

      FormularioRelacaoIncluiAutPublicGarantias formRelacao = (FormularioRelacaoIncluiAutPublicGarantias) servico
            .obterTela(FormularioRelacaoIncluiAutPublicGarantias.class);
      codEnviados = formRelacao.getCodigosEnviados();

      CodigoIF codContrato = new CodigoIF();
      CodigoOperacao codOperacao = new CodigoOperacao();

      GrupoDeGrupos geral = layout.grupoDeGrupos(1);
      GrupoDeAtributos gpart = geral.grupoDeAtributos(2);
      //gpart.contexto(Contexto.);
      gpart.posicaoTitulos(GrupoDeAtributos.TITULOS_A_ESQUERDA);
      Texto msg = new Texto("Operação incluída com sucesso.");
      msg.atribuirContexto(Contexto.AUTORIZACAO_PUBLICIDADE_GARANTIAS);
      gpart.atributoNaoEditavel(msg);
      gpart.atributoNaoEditavel(null);

      HashMap mapaOperacoes = formRelacao.getMapOperacoes();
      for (int j = 0; j < codEnviados.size(); j++) {
         codContrato = (CodigoIF) codEnviados.get(j);
         codOperacao = (CodigoOperacao) mapaOperacoes.get(codContrato);
         codOperacao.atribuirContexto(Contexto.AUTORIZACAO_PUBLICIDADE_GARANTIAS);
         gpart.atributoNaoEditavel(codContrato);
         gpart.atributoNaoEditavel(codOperacao);
      }
   }

   public void validar(Grupo dados, Servicos servico) throws Exception {

   }

   public boolean confirmacao(Grupo arg0, Servicos arg1) throws Exception {
      return false;
   }

   public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos servico) throws Exception {

   }

   public Notificacao chamarServico(Grupo dados, Servicos servico) throws Exception {

      return null;
   }

   public Class obterDestino(Grupo arg0, Servicos arg1) throws Exception {
      return null;
   }

   public boolean ciencia(Grupo arg0, Servicos arg1) throws Exception {
      return false;
   }

   public void ciencia(GrupoDeGrupos layout, Grupo grupo, Servicos servicos) throws Exception {
      GrupoDeGrupos geral = layout.grupoDeGrupos(1);
      geral.grupoDeAtributos(1);
   }

}
