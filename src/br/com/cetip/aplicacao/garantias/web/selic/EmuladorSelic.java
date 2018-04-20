package br.com.cetip.aplicacao.garantias.web.selic;

import br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoEmuladorSelic;
import br.com.cetip.base.web.acao.Formulario;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

public class EmuladorSelic extends Formulario {

   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {

      layout.contexto(Contexto.GARANTIAS_EMULADOR);

      // Grupo 1 - Informações do Título
      GrupoDeGrupos grupoTitulo = layout.grupoDeGrupos(2);
      grupoTitulo.contexto(Contexto.GARANTIAS_EMULADOR);

      GrupoDeAtributos grupoUm = grupoTitulo.grupoDeAtributos(1);
      grupoUm.posicaoTitulos(GrupoDeAtributos.TITULOS_A_ESQUERDA);

      grupoUm.atributoObrigatorio(new NumeroOperacao(Contexto.INSTRUMENTO_FINANCEIRO));
      grupoUm.atributoObrigatorio(new CodigoContaSelic(Contexto.GARANTIDOR));
      grupoUm.atributoObrigatorio(new CodigoContaSelic(Contexto.GARANTIDO));
      grupoUm.atributoObrigatorio(new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO));
      grupoUm.atributoObrigatorio(new Data(Contexto.INSTRUMENTO_FINANCEIRO_VENCIMENTO));
      grupoUm.atributoObrigatorio(new QuantidadeInteiraPositiva(Contexto.INSTRUMENTO_FINANCEIRO));
   }

   public Notificacao chamarServico(Grupo grupo, Servicos servico) throws Exception {

      RequisicaoServicoEmuladorSelic requisicao = new RequisicaoServicoEmuladorSelic();
      servico.executarServico(requisicao, grupo);

      Notificacao notificacao = new Notificacao("EmuladorSelic.Sucesso");

      return notificacao;
   }

   public boolean ciencia(Grupo arg0, Servicos arg1) throws Exception {
      return false;
   }

   public void ciencia(GrupoDeGrupos arg0, Grupo arg1, Servicos arg2) throws Exception {
   }

   public boolean confirmacao(Grupo arg0, Servicos arg1) throws Exception {
      return true;
   }

   public void confirmacao(GrupoDeGrupos arg0, Grupo arg1, Servicos arg2) throws Exception {

   }

   public Class obterDestino(Grupo arg0, Servicos arg1) throws Exception {
      return null;
   }

   public void validar(Grupo grupo, Servicos servicos) throws Exception {
   }

}
