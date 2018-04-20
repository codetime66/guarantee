package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * Servico para validacao dos dados inseridos pelo usuario na interface grafica de compra/venda de cda/wa.
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vincius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @since Abril/2006
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_PARTICIPANTE"
 */
public class ServicoValidaExcluirCestaGarantias extends BaseGarantias implements Servico {

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   /*
    * Realiza a validacao dos dados digitados via motor de regras.
    * 
    * @param requisicao requisicao que contem os dados para validacao
    * 
    * @return o Resultado contendo se a validacao foi aceita ou nao
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoValidaExcluirCestaGarantias req = (RequisicaoServicoValidaExcluirCestaGarantias) requisicao;
      ResultadoServicoValidaExcluirCestaGarantias res = new ResultadoServicoValidaExcluirCestaGarantias();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "*** Executar Validacao de Exclusao de Cesta de Garantias ***");
      }

      NumeroCestaGarantia numeroCesta = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();

      IGarantias factory = getFactory();

      ICestaDeGarantias cg = factory.getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = cg.obterCestaDeGarantias(numeroCesta);

      CodigoContaCetip participante = cesta.getGarantidor().getCodContaParticipante();
      Id status = cesta.getStatusCesta().getNumIdStatusCesta();
      Data data = cesta.getDatCriacao();

      AtributosColunados ac = new AtributosColunados();
      ac.atributo(participante);
      ac.atributo(status);
      ac.atributo(numeroCesta);

      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeExcluirCestaGarantias, ac, true);
      res.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(participante);
      res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numeroCesta);
      res.atribuirGARANTIAS_DATA_CRIACAO_Data(data);
      return res;
   }

}
