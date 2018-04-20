package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoGarantiaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * <p>
 * Alteracao de Cesta de Garantias:
 * </p>
 * 
 * <p>
 * Altera apenas o visualizador
 * </p>
 * 
 * @requisicao.class
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 *                    
 * @requisicao.method atributo="IdTipoGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 *                    
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.class
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ServicoAlteraCestaGarantias extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      RequisicaoServicoAlteraCestaGarantias req;
      req = (RequisicaoServicoAlteraCestaGarantias) requisicao;

      NumeroCestaGarantia numCesta = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      CodigoContaCetip contraParte = req.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
      IdTipoGarantia idTipoGarantia = req.obterGARANTIAS_CODIGO_IdTipoGarantia();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Alteracao de Cesta");
         Logger.debug(this, "Numero: " + numCesta);
         Logger.debug(this, "ContraParte: " + contraParte);
         Logger.debug(this, "Tipo Garantia: " + idTipoGarantia);
      }

      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = icg.obterCestaDeGarantias(numCesta);

      IContaParticipante icp = ContaParticipanteFactory.getInstance();
      ContaParticipanteDO conta = icp.obterContaParticipanteDO(contraParte);

      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
      igc.associarGarantidoNaCesta(cesta, conta);

      if (!Condicional.vazio(idTipoGarantia)) {
         TipoGarantiaDO tipo = new TipoGarantiaDO(idTipoGarantia);
         cesta.setTipoGarantia(tipo);
         getGp().update(cesta);
      }

      return new ResultadoServicoAlteraCestaGarantias();
   }

   public Resultado executarConsulta(Requisicao req) throws Exception {
      return null;
   }

}