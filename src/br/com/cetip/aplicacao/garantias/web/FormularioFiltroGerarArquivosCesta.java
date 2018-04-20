package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoObterDadosAdicionaisSolicitacaoArquivoCaracteristicasCesta;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoRegistraSolicitacaoArquivoCaracteristicasCesta;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaSolicitacaoArquivoCaracteristicasCesta;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoObterDadosAdicionaisSolicitacaoArquivoCaracteristicasCesta;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

/**
 * Formulario de filtro para Geracao de Arquivos
 * 
 * @author <a href="mailto:fernando@summa-tech.com">Fernando Henrique Martins</a>
 * @since Janeiro/2007
 */

public class FormularioFiltroGerarArquivosCesta extends AbstractFormularioGarantias {

   private Texto nomMalote;

   private Id idMalote;

   public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servico) throws Exception {
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      GrupoDeGrupos grupoCadastro = principal.grupoDeGrupos(1);

      GrupoDeAtributos grupoIF = grupoCadastro.grupoDeAtributos(1);
      grupoIF.atributoObrigatorio(new NumeroCestaGarantia(Contexto.CESTA_GARANTIA));
      grupoIF.atributoObrigatorio(new CodigoContaCetip(Contexto.SOLICITANTE));
      grupoIF.atributoObrigatorio(new CodigoContaCetip(Contexto.DESTINATARIO));
      grupoIF.atributoOculto(new Texto(Contexto.MALOTE_DESTINATARIO));
   }

   public void validar(Grupo dados, Servicos servico) throws Exception {
      RequisicaoServicoValidaSolicitacaoArquivoCaracteristicasCesta req = new RequisicaoServicoValidaSolicitacaoArquivoCaracteristicasCesta();

      req.atribuirCESTA_GARANTIA_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(
            NumeroCestaGarantia.class, Contexto.CESTA_GARANTIA));
      req.atribuirSOLICITANTE_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.SOLICITANTE));
      req.atribuirDESTINATARIO_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.DESTINATARIO));

      servico.executarServico(req);
   }

   public boolean confirmacao(Grupo arg0, Servicos arg1) throws Exception {
      return true;
   }

   public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos servico) throws Exception {

      NumeroCestaGarantia numeroCestaGarantia = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.CESTA_GARANTIA);
      CodigoContaCetip codigoContaSolicitante = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.SOLICITANTE);
      CodigoContaCetip codigoContaDestinatario = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.DESTINATARIO);

      RequisicaoServicoObterDadosAdicionaisSolicitacaoArquivoCaracteristicasCesta reqObterDadosAdcionais = new RequisicaoServicoObterDadosAdicionaisSolicitacaoArquivoCaracteristicasCesta();

      reqObterDadosAdcionais.atribuirCESTA_GARANTIA_NumeroCestaGarantia(numeroCestaGarantia);
      reqObterDadosAdcionais.atribuirSOLICITANTE_CodigoContaCetip(codigoContaSolicitante);
      reqObterDadosAdcionais.atribuirDESTINATARIO_CodigoContaCetip(codigoContaDestinatario);
      ResultadoServicoObterDadosAdicionaisSolicitacaoArquivoCaracteristicasCesta resObterDadosAdcionais = (ResultadoServicoObterDadosAdicionaisSolicitacaoArquivoCaracteristicasCesta) servico
            .executarServico(reqObterDadosAdcionais);

      GrupoDeAtributos col = layout.grupoDeAtributos(2);
      col.posicaoTitulos(GrupoDeAtributos.TITULOS_A_ESQUERDA);

      col.atributoNaoEditavel(resObterDadosAdcionais.obterCESTA_GARANTIA_NumeroCestaGarantia());
      col.atributoNaoEditavel(null);
      col.atributoNaoEditavel(resObterDadosAdcionais.obterSOLICITANTE_CodigoContaCetip());
      col.atributoNaoEditavel(resObterDadosAdcionais.obterSOLICITANTE_NomeSimplificado());
      col.atributoNaoEditavel(resObterDadosAdcionais.obterDESTINATARIO_CodigoContaCetip());
      col.atributoNaoEditavel(resObterDadosAdcionais.obterDESTINATARIO_NomeSimplificado());
      col.atributoOculto(resObterDadosAdcionais.obterMALOTE_DESTINATARIO_Id());

      nomMalote = resObterDadosAdcionais.obterMALOTE_DESTINATARIO_Texto();
      col.atributoNaoEditavel(nomMalote);

      idMalote = resObterDadosAdcionais.obterMALOTE_DESTINATARIO_Id();
   }

   public Notificacao chamarServico(Grupo dados, Servicos servico) throws Exception {

      RequisicaoServicoRegistraSolicitacaoArquivoCaracteristicasCesta req = new RequisicaoServicoRegistraSolicitacaoArquivoCaracteristicasCesta();

      // Cesta
      NumeroCestaGarantia cesta = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class,
            Contexto.CESTA_GARANTIA);
      req.atribuirCESTA_GARANTIA_NumeroCestaGarantia(cesta);

      // Solicitante
      CodigoContaCetip solicitante = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.SOLICITANTE);
      req.atribuirSOLICITANTE_CodigoContaCetip(solicitante);

      // Destinatario
      CodigoContaCetip destinatario = (CodigoContaCetip) dados.obterAtributo(CodigoContaCetip.class,
            Contexto.DESTINATARIO);
      req.atribuirDESTINATARIO_CodigoContaCetip(destinatario);

      // Id Malote
      req.atribuirMALOTE_DESTINATARIO_Id(idMalote);

      // Nome Malote
      req.atribuirMALOTE_DESTINATARIO_Texto(nomMalote);

      servico.executarServico(req);

      Notificacao notificacao = new Notificacao("SolicitacaoArquivoCaracteristicasCesta.Sucesso");
      return notificacao;
   }

}
