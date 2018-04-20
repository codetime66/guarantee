package br.com.cetip.aplicacao.garantias.servico;

import java.util.ArrayList;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.AdministracaoParticipantesFactory;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.servicosinfra.apinegocio.arquivo.SolicitacaoGeracaoArquivoFactory;
import br.com.cetip.aplicacao.servicosinfra.apinegocio.servico.ServicoUtilFactory;
import br.com.cetip.dados.aplicacao.sap.EntidadeDO;
import br.com.cetip.dados.aplicacao.sap.MaloteDO;
import br.com.cetip.dados.aplicacao.sca.UsuarioDO;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NomeMalote;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Descricao;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.DescricaoAtributo;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CESTA_GARANTIA"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="SOLICITANTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="DESTINATARIO"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="MALOTE_DESTINATARIO"
 * 
 * @requisicao.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="MALOTE_DESTINATARIO"
 * 
 * @resultado.class
 * 
 */

public class ServicoRegistraSolicitacaoArquivoCaracteristicasCesta extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Entrei no servico que registra solicitacao de geracao de arquivo...");
      }
      RequisicaoServicoRegistraSolicitacaoArquivoCaracteristicasCesta req = (RequisicaoServicoRegistraSolicitacaoArquivoCaracteristicasCesta) requisicao;
      ResultadoServicoRegistraSolicitacaoArquivoCaracteristicasCesta res = new ResultadoServicoRegistraSolicitacaoArquivoCaracteristicasCesta();

      Data d0 = getDataHoje();

      Id idUsuario = new Id(getContextoAtivacao().getIdUsuario().toString());

      UsuarioDO usuario = (UsuarioDO) getGp().load(UsuarioDO.class, idUsuario);

      NumeroCestaGarantia numeroCestaGarantia = req.obterCESTA_GARANTIA_NumeroCestaGarantia();

      CodigoContaCetip codigoContaSolicitante = req.obterSOLICITANTE_CodigoContaCetip();
      EntidadeDO entidadeSolicitante = AdministracaoParticipantesFactory.getInstance().obterParticipante(
            codigoContaSolicitante);
      Texto nomMalote = req.obterMALOTE_DESTINATARIO_Texto();

      MaloteDO malote = ContaParticipanteFactory.getInstance().obterMaloteAtivo(new NomeMalote(nomMalote.toString()));

      List lista = new ArrayList();

      lista.add(new NumeroCestaGarantia(Contexto.CESTA_GARANTIA, numeroCestaGarantia.toString()));

      lista.add(new Texto(Contexto.MALOTE_DESTINATARIO, malote.getNomMalote().toString()));

      lista.add(new Id(Contexto.MALOTE_DESTINATARIO, malote.getNumIdMalote().toString()));

      Descricao desParametrosServico = new Descricao(DescricaoAtributo.obterDescricaoListaAtributos(lista));

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Vou gerar arquivos de eventos de CCB...");
      }

      Id idServicoCaracteristicas = ServicoUtilFactory.getInstance().obterIdServico(
            "br.com.cetip.aplicacao.garantias.servico.ServicoGeraArquivoCaracteristicasCesta");

      SolicitacaoGeracaoArquivoFactory.getInstance().insereSolicitacaoGeracaoArquivo(usuario, entidadeSolicitante,
            malote, d0, d0, idServicoCaracteristicas, desParametrosServico, Booleano.VERDADEIRO);

      Id idServicoeventos = ServicoUtilFactory.getInstance().obterIdServico(
            "br.com.cetip.aplicacao.garantias.servico.ServicoGeraArquivoEventosCesta");

      SolicitacaoGeracaoArquivoFactory.getInstance().insereSolicitacaoGeracaoArquivo(usuario, entidadeSolicitante,
            malote, d0, d0, idServicoeventos, desParametrosServico, Booleano.VERDADEIRO);

      return res;
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      return null;
   }

}
