package br.com.cetip.aplicacao.garantias.servico;

import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoGarantiaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="IdTipoGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="QuantidadeInteiraPositiva" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_CESTA"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="NumeroInteiro" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @author <a href="bruno.borges@summa-tech.com">Bruno Borges</a>
 * @author <a href="vinicius@summa-tech.com">Vinicius Fernandes</a>
 */
public class ServicoConsultaCestaGarantias extends BaseGarantias implements Servico {

   private IGerenciadorPersistencia gp;

   public Resultado executar(Requisicao req) throws ExcecaoServico {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      gp = getGp();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, ">> Executando o servico ServicoConsultaCestaGarantias");
      }

      RequisicaoServicoConsultaCestaGarantias req = null;
      req = (RequisicaoServicoConsultaCestaGarantias) requisicao;

      ResultadoServicoConsultaCestaGarantias res = null;
      NumeroCestaGarantia codCesta = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();

      IGarantias gf = getFactory();
      ICestaDeGarantias dao = gf.getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = dao.obterCestaDeGarantias(codCesta);
      Integer qtItens = obterQtItensCesta(cesta);

      res = new ResultadoServicoConsultaCestaGarantias();
      res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(new NumeroCestaGarantia(cesta.getNumIdCestaGarantias()));
      res.atribuirGARANTIAS_CODIGO_Id(cesta.getNumIdCestaGarantias());
      res.atribuirGARANTIAS_DATA_CRIACAO_Data(cesta.getDatCriacao());
      res.atribuirGARANTIAS_CODIGO_NumeroInteiro(new NumeroInteiro(Contexto.GARANTIAS_CODIGO, qtItens.toString()));

      // Garantidor
      ContaParticipanteDO parte = cesta.getGarantidor();

      res.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(parte.getCodContaParticipante());
      res.atribuirGARANTIAS_PARTICIPANTE_Nome(parte.getNomeContaParticipante());

      // Garantido
      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
      ContaParticipanteDO garantido = igc.obterGarantidoCesta(cesta);

      if (garantido != null) {
         res.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(garantido.getCodContaParticipante());
         res.atribuirGARANTIAS_CONTRAPARTE_Nome(garantido.getNomeContaParticipante());
      }

      TipoGarantiaDO tipoGarantia = cesta.getTipoGarantia();
      IdTipoGarantia idTipoGarantia = new IdTipoGarantia(tipoGarantia.getDesTipoGarantia().obterConteudo(),
            tipoGarantia.getNumIdTipoGarantia().obterConteudo());
      res.atribuirGARANTIAS_CODIGO_IdTipoGarantia(idTipoGarantia);

      return res;
   }

   private Integer obterQtItensCesta(CestaGarantiasDO cesta) throws Exception {
      String hql = "select size(c.detalhes) from CestaGarantiasDO c where c = ?";
      List l = gp.find(hql, cesta);

      if (l.size() == 1) {
         return (Integer) l.get(0);
      }

      return Integer.valueOf("0");
   }

}
