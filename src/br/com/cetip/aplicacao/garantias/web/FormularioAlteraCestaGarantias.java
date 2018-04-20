package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoAlteraCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoConsultaCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaAlteracaoCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoConsultaCestaGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.log.Logger;

/**
 * Formulario de especificacao de compra e venda final de um WA.
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vinicius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @since Abril/2006
 */
public class FormularioAlteraCestaGarantias extends AbstractFormularioGarantias {

   private NumeroCestaGarantia numero = null;

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
   public void entrada(GrupoDeGrupos layout, Grupo parametros, Servicos servicos) throws Exception {
      layout.contexto(Contexto.GARANTIAS_CESTA);
      // Monta layout
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeGrupos grupoDados = principal.grupoDeGrupos(1);
      GrupoDeGrupos grupoEdicao = principal.grupoDeGrupos(1);
      grupoDados.contexto(Contexto.GARANTIAS_DADOS);
      grupoEdicao.contexto(Contexto.GARANTIAS_CONTRAPARTE);

      // Obtem numero da cesta
      numero = (NumeroCestaGarantia) parametros.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);

      // Consulta a Cesta e obtem os dados
      RequisicaoServicoConsultaCestaGarantias reqConsulta;
      reqConsulta = new RequisicaoServicoConsultaCestaGarantias();
      reqConsulta.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);

      ResultadoServicoConsultaCestaGarantias resConsulta;
      resConsulta = (ResultadoServicoConsultaCestaGarantias) servicos.executarServico(reqConsulta);

      // Preenche os campos da tela
      GrupoDeAtributos dados = grupoDados.grupoDeAtributos(2);

      dados.atributoNaoEditavel(resConsulta.obterGARANTIAS_CODIGO_NumeroCestaGarantia());
      dados.atributoNaoEditavel(new Id(Contexto.GARANTIAS_CODIGO, resConsulta.obterGARANTIAS_CODIGO_IdTipoGarantia()
            .obterRepresentacao()));
      dados.atributoNaoEditavel(resConsulta.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip());
      dados.atributoNaoEditavel(resConsulta.obterGARANTIAS_PARTICIPANTE_Nome());

      if (resConsulta.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip() != null) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Cesta " + numero + " possui garantido");
         }

         CodigoContaCetip nc = (CodigoContaCetip) resConsulta.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip()
               .criarCopia();
         nc.atribuirContexto(Contexto.GARANTIAS_GARANTIDO);
         dados.atributoNaoEditavel(nc);
         dados.atributoNaoEditavel(resConsulta.obterGARANTIAS_CONTRAPARTE_Nome());
      } else if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Cesta " + numero + " NAO possui garantido");
      }

      dados.atributoNaoEditavel(resConsulta.obterGARANTIAS_DATA_CRIACAO_Data());

      GrupoDeAtributos edicao = grupoEdicao.grupoDeAtributos(1);
      edicao.atributo(new CodigoContaCetip(Contexto.GARANTIAS_CONTRAPARTE));
      // edicao.atributo(getTipoGarantia(servicos));
   }

   /**
    * Metodo que permite validar os dados do formulario. Recebe os dados da requisicao e a interface que permite chamar
    * um servico.
    * 
    * @param grupo
    *           Representa os dados informados pelo usuario na tela.
    * @param servicos
    *           Classe responsavel pelo encapsulamento do servico.
    * @see br.com.cetip.base.web.acao.suporte.InterfaceFiltro#validar(br.com.cetip.base.web.layout.manager.grupo.Grupo)
    */
   public void validar(Grupo dados, Servicos servicos) throws Exception {
      CodigoContaCetip conta = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_CONTRAPARTE);
      /*IdTipoGarantia idTipoGarantia = (IdTipoGarantia) dados.obterAtributo(IdTipoGarantia.class,
            Contexto.GARANTIAS_CODIGO);*/

      RequisicaoServicoValidaAlteracaoCestaGarantias req;
      req = new RequisicaoServicoValidaAlteracaoCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(conta);
      //req.atribuirGARANTIAS_CODIGO_IdTipoGarantia(idTipoGarantia);

      servicos.chamarServico(req);
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
      CodigoContaCetip contraparte = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.GARANTIAS_CONTRAPARTE);

      /*
      IdTipoGarantia idTipoGarantia = (IdTipoGarantia) dados.obterAtributo(IdTipoGarantia.class,
            Contexto.GARANTIAS_CODIGO);
            */

      RequisicaoServicoAlteraCestaGarantias req = new RequisicaoServicoAlteraCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contraparte);
      // req.atribuirGARANTIAS_CODIGO_IdTipoGarantia(idTipoGarantia);
      servicos.executarServico(req);

      Notificacao not = new Notificacao("AlteracaoCestaGarantias.Sucesso");
      not.parametroMensagem(req.obterGARANTIAS_CODIGO_NumeroCestaGarantia(), 0);
      not.parametroMensagem(req.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip(), 1);

      return not;
   }

}
