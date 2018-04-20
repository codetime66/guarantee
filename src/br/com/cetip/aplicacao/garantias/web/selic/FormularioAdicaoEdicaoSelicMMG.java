package br.com.cetip.aplicacao.garantias.web.selic;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoAdicaoEdicaoSelicMMG;
import br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoValidarAdicaoEdicaoSelicMMG;
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
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

public class FormularioAdicaoEdicaoSelicMMG extends Formulario {

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      servicos.executarServico(new RequisicaoServicoAdicaoEdicaoSelicMMG(), dados);

      Funcao f = (Funcao) dados.obterAtributo(Funcao.class, Contexto.OPERACAO);
      Notificacao not = null;
      if (f.mesmoConteudo(RelacaoAtivosSelicMMG.ADICIONAR)) {
         not = new Notificacao("AdicaoSelicMMG.Sucesso");
      } else {
         not = new Notificacao("EdicaoSelicMMG.Sucesso");
      }
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
   }

   public void entrada(GrupoDeGrupos layout, Grupo grupo, Servicos servicos) throws Exception {
      GrupoDeAtributos dados = layout.grupoDeAtributos(1);
      dados.posicaoTitulos(GrupoDeAtributos.TITULOS_ACIMA);

      Funcao f = new Funcao(Contexto.OPERACAO, "" + grupo.obterAtributo(Funcao.class, Contexto.OPERACAO));
      CodigoTipoIF codigoTipoIF = new CodigoTipoIF(Contexto.CODIGO_TIPO_IF, ""
            + grupo.obterAtributo(CodigoTipoIF.class, Contexto.CODIGO_TIPO_IF));
      CodigoIF codigoIF = new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO, ""
            + grupo.obterAtributo(CodigoIF.class, Contexto.INSTRUMENTO_FINANCEIRO));
      CodigoSistema sistema = new CodigoSistema(Contexto.SISTEMA, ""
            + grupo.obterAtributo(CodigoSistema.class, Contexto.SISTEMA));

      Id qualificador = null;
      Booleano indAtivo = (Booleano) grupo.obterAtributo(Booleano.class, Contexto.ATIVO_GARANTIA);
      if (indAtivo == null) {
         // Adicionando um novo selic na lista de habilitados para cesta
         qualificador = new Id(Contexto.ACESSO);
      } else {
         // Editando um novo selic na lista de habilitados para cesta
         qualificador = new Id(Contexto.ACESSO, indAtivo.ehVerdadeiro() ? "ATIVO" : "PASSIVO");
      }
      qualificador.getDomain().add(new Id(Contexto.ACESSO, ""));
      qualificador.getDomain().add(new Id(Contexto.ACESSO, "ATIVO"));
      qualificador.getDomain().add(new Id(Contexto.ACESSO, "PASSIVO"));

      Id colateralRelacao = (Id) grupo.obterAtributo(Id.class, Contexto.TIPO_COLATERAL);
      Id colateral = new Id(Contexto.TIPO_COLATERAL, colateralRelacao == null ? "" : colateralRelacao.obterConteudo());
      colateral.getDomain().add(new Id(Contexto.TIPO_COLATERAL, ""));
      colateral.getDomain().add(new Id(Contexto.TIPO_COLATERAL, "CETIP"));
      colateral.getDomain().add(new Id(Contexto.TIPO_COLATERAL, "SELIC"));

      RequisicaoServicoObterCombosTipoInstrumentoFinanceiro reqObterCombo = new RequisicaoServicoObterCombosTipoInstrumentoFinanceiro();
      reqObterCombo.atribuirTIPO_INSTRUMENTO_FINANCEIRO_Texto(new Texto("TITULO"));
      ResultadoServicoObterCombosTipoInstrumentoFinanceiro resTipoIf = (ResultadoServicoObterCombosTipoInstrumentoFinanceiro) servicos
            .executarServico(reqObterCombo);
      CodigoTipoIF codigoTipoIf = resTipoIf.obterTIPO_IF_GARANTIDOR_CodigoTipoIF();
      codigoTipoIf.atribuirContexto(Contexto.GARANTIAS_CODIGO_TIPO);

      dados.atributoNaoEditavel(codigoTipoIF);
      dados.atributoNaoEditavel(codigoIF);
      dados.atributoNaoEditavel(sistema);
      dados.atributoObrigatorio(qualificador);
      dados.atributoObrigatorio(colateral);
      dados.atributo(codigoTipoIf);

      dados.atributoOculto(f);
   }

   public Class obterDestino(Grupo arg0, Servicos arg1) throws Exception {
      return null;
   }

   public void validar(Grupo grupo, Servicos servicos) throws Exception {
      servicos.chamarServico(new RequisicaoServicoValidarAdicaoEdicaoSelicMMG(), grupo);
   }

}
