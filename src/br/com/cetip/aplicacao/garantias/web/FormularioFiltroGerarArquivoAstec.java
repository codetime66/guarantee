package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;

/**
 * Formulario de filtro para Geracao de Arquivos para ASTEC
 * 
 * @author <a href="mailto:fernando@summa-tech.com">Fernando Henrique Martins</a>
 * @since Dezembro/2006
 */
public class FormularioFiltroGerarArquivoAstec extends AbstractFormularioGarantias {

   /**
    * Metodo que permite desenhar a tela de entrada. Recebe o layout a ser definido, os dados da requisicao e a
    * interface que permite chamar um servico. Retorna um booleano indicando se o formulario tem ou nao tem tela de
    * entrada.
    * 
    * @param layout
    *           grupo que contem o layout da interface grafica
    * @param grupo
    *           grupo que contem os atributos da interface grafica
    * @param servicos
    *           Classe responsavel pelo encapsulamento do servico.
    * @throws Exception
    *            caso algum erro aconteca na montagem da interface grafica.
    * @see br.com.cetip.base.web.acao.Formulario#entrada(br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos,
    *      br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeGrupos grupoCadastro = principal.grupoDeGrupos(1);

      GrupoDeAtributos grupoIF = grupoCadastro.grupoDeAtributos(1);
      grupoIF.atributoObrigatorio(new CodigoContaCetip(Contexto.SOLICITANTE));
      grupoIF.atributoObrigatorio(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));
      grupoIF.atributoObrigatorio(new CodigoContaCetip(Contexto.DESTINO));
   }

   /**
    * Indica se esse formulario possui janela de confirmacao
    * 
    * @param grupo
    *           Representa os dados informados pelo usuario na tela.
    * @param servicos
    *           Classe responsavel pelo encapsulamento do servico.
    * @throws Exception
    *            caso algum problema aconteca na obtencao da confirmacao.
    * @see br.com.cetip.base.web.acao.Formulario#confirmacao(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    *      br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public boolean confirmacao(Grupo grupo, Servicos servicos) throws Exception {
      return true;
   }

   /**
    * Metodo que permite chamar um servico. Recebe os dados da requisicao e a interface que permite chamar um servico.
    * Retorna uma mensagem de notificacao ao usuario, que pode ser nula.
    * 
    * @param grupo
    *           grupo que contem os atributos da interface grafica
    * @param servicos
    *           Classe responsavel pelo encapsulamento do servico.
    * @throws Exception
    *            caso algum erro aconteca na execucao de um servico.
    * @see br.com.cetip.base.web.acao.Formulario#chamarServico(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    *      br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      String notificacao = "SolicitacaoArquivoCaracteristicasAtivo.Sucesso";
      Notificacao not = new Notificacao(notificacao);
      return not;
   }

}
