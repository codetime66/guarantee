package br.com.cetip.aplicacao.garantias.web.selic;

import br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoExclusaoSelicMMG;
import br.com.cetip.base.web.acao.Formulario;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;

public class FormularioExclusaoSelicMMG extends Formulario {

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      servicos.executarServico(new RequisicaoServicoExclusaoSelicMMG(), dados);

      Notificacao not = new Notificacao("ExclusaoSelicMMG.Sucesso");
      not.parametroMensagem(dados.obterAtributo(CodigoIF.class, Contexto.INSTRUMENTO_FINANCEIRO), 0);
      return not;
   }

   public boolean ciencia(Grupo arg0, Servicos arg1) throws Exception {
      return false;
   }

   public void ciencia(GrupoDeGrupos arg0, Grupo arg1, Servicos arg2) throws Exception {

   }

   public boolean confirmacao(Grupo arg0, Servicos arg1) throws Exception {
      return true;
   }

   public void confirmacao(GrupoDeGrupos layout, Grupo grupo, Servicos servicos) throws Exception {
      GrupoDeAtributos dados = layout.grupoDeAtributos(1);
      dados.posicaoTitulos(GrupoDeAtributos.TITULOS_ACIMA);

      CodigoTipoIF codigoTipoIF = new CodigoTipoIF(Contexto.CODIGO_TIPO_IF, ""
            + grupo.obterAtributo(CodigoTipoIF.class, Contexto.CODIGO_TIPO_IF));
      CodigoIF codigoIF = new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO, ""
            + grupo.obterAtributo(CodigoIF.class, Contexto.INSTRUMENTO_FINANCEIRO));
      CodigoSistema sistema = new CodigoSistema(Contexto.SISTEMA, ""
            + grupo.obterAtributo(CodigoSistema.class, Contexto.SISTEMA));
      Booleano indAtivo = new Booleano(Contexto.ATIVO_GARANTIA, ""
            + grupo.obterAtributo(Booleano.class, Contexto.ATIVO_GARANTIA));

      Id colateral = new Id(Contexto.TIPO_COLATERAL, "" + grupo.obterAtributo(Id.class, Contexto.TIPO_COLATERAL));
      CodigoTipoIF codTipoIFCol = new CodigoTipoIF(Contexto.GARANTIAS_CODIGO_TIPO, ""
            + grupo.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CODIGO_TIPO));

      dados.atributoNaoEditavel(codigoTipoIF);
      dados.atributoNaoEditavel(codigoIF);
      dados.atributoNaoEditavel(sistema);
      dados.atributoNaoEditavel(new Id(Contexto.ACESSO, indAtivo.ehVerdadeiro() ? "ATIVO" : "PASSIVO"));
      dados.atributoNaoEditavel(colateral);
      dados.atributoNaoEditavel(codTipoIFCol);
   }

   public void entrada(GrupoDeGrupos arg0, Grupo arg1, Servicos arg2) throws Exception {
   }

   public Class obterDestino(Grupo arg0, Servicos arg1) throws Exception {
      return null;
   }

   public void validar(Grupo grupo, Servicos servicos) throws Exception {
   }

}
