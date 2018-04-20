package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
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
 * @resultado.class
 * 
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CESTA_GARANTIA"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="SOLICITANTE"
 * 
 * @resultado.method atributo="NomeSimplificado" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="SOLICITANTE"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="DESTINATARIO"
 * 
 * @resultado.method atributo="NomeSimplificado" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="DESTINATARIO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="MALOTE_DESTINATARIO"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="MALOTE_DESTINATARIO"
 * 
 */

public class ServicoObterDadosAdicionaisSolicitacaoArquivoCaracteristicasCesta extends BaseGarantias implements Servico {

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {

      RequisicaoServicoObterDadosAdicionaisSolicitacaoArquivoCaracteristicasCesta req = (RequisicaoServicoObterDadosAdicionaisSolicitacaoArquivoCaracteristicasCesta) requisicao;
      ResultadoServicoObterDadosAdicionaisSolicitacaoArquivoCaracteristicasCesta res = new ResultadoServicoObterDadosAdicionaisSolicitacaoArquivoCaracteristicasCesta();

      NumeroCestaGarantia numeroCestaGarantia = req.obterCESTA_GARANTIA_NumeroCestaGarantia();
      res.atribuirCESTA_GARANTIA_NumeroCestaGarantia(numeroCestaGarantia);

      CodigoContaCetip codigoContaSolicitante = req.obterSOLICITANTE_CodigoContaCetip();
      res.atribuirSOLICITANTE_CodigoContaCetip(codigoContaSolicitante);

      IContaParticipante contaPartDao = ContaParticipanteFactory.getInstance();
      ContaParticipanteDO contaSolicitante = contaPartDao.obterContaParticipanteDO(codigoContaSolicitante);
      NomeSimplificado nomeSimplificadoSolicitante = contaSolicitante.getParticipante().getNomSimplificadoEntidade();
      res.atribuirSOLICITANTE_NomeSimplificado(nomeSimplificadoSolicitante);

      CodigoContaCetip codigoContaDestinatario = req.obterDESTINATARIO_CodigoContaCetip();
      res.atribuirDESTINATARIO_CodigoContaCetip(codigoContaDestinatario);

      ContaParticipanteDO contaDestinatario = contaPartDao.obterContaParticipanteDO(codigoContaDestinatario);
      NomeSimplificado nomeSimplificadoDestinatario = contaDestinatario.getParticipante().getNomSimplificadoEntidade();
      res.atribuirDESTINATARIO_NomeSimplificado(nomeSimplificadoDestinatario);

      Texto nomMalote = contaDestinatario.getMalote().getNomMalote();
      res.atribuirMALOTE_DESTINATARIO_Texto(nomMalote);

      Id idMalote = contaDestinatario.getMalote().getNumIdMalote();
      res.atribuirMALOTE_DESTINATARIO_Id(idMalote);

      return res;
   }

   public Resultado executar(Requisicao arg0) throws Exception {
      return null;
   }

}
