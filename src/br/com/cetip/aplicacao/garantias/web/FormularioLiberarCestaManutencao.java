package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoLiberarCestaParaManutencao;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.TextoLimitado;

/**
 * Formulario de confirmacao para "Liberar Cesta para Manutencao"
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class FormularioLiberarCestaManutencao extends AbstractFormularioGarantias {

   public boolean confirmacao(Grupo parametros, Servicos servicos) throws Exception {
      return true;
   }

   public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      NumeroCestaGarantia numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      layout.contexto(Contexto.GARANTIAS_DADOS);
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);

      GrupoDeGrupos grupoCadastro = principal.grupoDeGrupos(1);
      grupoCadastro.contexto(Contexto.GARANTIAS_DADOS);

      GrupoDeAtributos grupo = grupoCadastro.grupoDeAtributos(2);
      grupo.atributoNaoEditavel(numero);
   }

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {

      NumeroCestaGarantia numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      RequisicaoServicoLiberarCestaParaManutencao req;
      req = new RequisicaoServicoLiberarCestaParaManutencao();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);

      servicos.executarServico(req);
      // marreta para exibir num cesta com 8 posicoes
      TextoLimitado codCesta = new TextoLimitado(numero.obterConteudo().toString().length() < 8 ? "0"
            + numero.obterConteudo().toString() : numero.obterConteudo().toString());

      Notificacao not = new Notificacao("LiberarCestaManutencao.sucesso");
      not.parametroMensagem(codCesta, 0);

      return not;
   }

}
