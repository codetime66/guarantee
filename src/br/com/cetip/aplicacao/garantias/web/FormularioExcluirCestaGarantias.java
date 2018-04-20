package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoExcluirCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaAcaoCesta;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaExcluirCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoValidaExcluirCestaGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.TextoLimitado;

/**
 * <p>
 * Tela de registro de Itens da Cesta de Garantias
 * </p>
 */
public class FormularioExcluirCestaGarantias extends AbstractFormularioGarantias {

   public void validar(Grupo dados, Servicos servico) throws Exception {
      NumeroCestaGarantia numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      // Valida acao de excluir cesta
      RequisicaoServicoValidaAcaoCesta reqAcao = new RequisicaoServicoValidaAcaoCesta();
      reqAcao.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
      reqAcao.atribuirACAO_Funcao(ICestaDeGarantias.EXCLUIR_CESTA);
      reqAcao.atribuirGARANTIAS_TIPO_ACESSO_Funcao(ICestaDeGarantias.FUNCAO_GARANTIDOR);
      servico.chamarServico(reqAcao);
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.base.web.acao.Formulario#confirmacao(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    *      br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public boolean confirmacao(Grupo dados, Servicos servico) throws Exception {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.base.web.acao.Formulario#confirmacao(br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos,
    *      br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos servico) throws Exception {
      NumeroCestaGarantia numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      RequisicaoServicoValidaExcluirCestaGarantias req = new RequisicaoServicoValidaExcluirCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);

      ResultadoServicoValidaExcluirCestaGarantias res = (ResultadoServicoValidaExcluirCestaGarantias) servico
            .executarServico(req);

      NumeroCestaGarantia numeroCesta = res.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      CodigoContaCetip conta = res.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();
      Data data = res.obterGARANTIAS_DATA_CRIACAO_Data();

      layout.contexto(Contexto.GARANTIAS_DADOS);
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);

      GrupoDeGrupos grupoCadastro = principal.grupoDeGrupos(1);
      grupoCadastro.contexto(Contexto.GARANTIAS_DADOS);

      GrupoDeAtributos grupoIF = grupoCadastro.grupoDeAtributos(2);
      grupoIF.atributoNaoEditavel(numeroCesta);
      grupoIF.atributoNaoEditavel(conta);
      grupoIF.atributoNaoEditavel(data);
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.base.web.acao.Formulario#chamarServico(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    *      br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public Notificacao chamarServico(Grupo dados, Servicos servico) throws Exception {
      NumeroCestaGarantia numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);
      Id idCesta = numero.copiarParaId();
      idCesta.atribuirContexto(Contexto.GARANTIAS_CESTA);

      RequisicaoServicoExcluirCestaGarantias requisicao = new RequisicaoServicoExcluirCestaGarantias();
      requisicao.atribuirGARANTIAS_CESTA_Id(idCesta);
      servico.chamarServicoAssincrono(requisicao);

      Notificacao not = new Notificacao("ExcluirCestaGarantias.Sucesso");
      TextoLimitado codCesta = new TextoLimitado(numero.obterConteudo().toString().length() < 8 ? "0"
            + numero.obterConteudo().toString() : numero.obterConteudo().toString());

      not.parametroMensagem(codCesta, 0);

      return not;
   }

}