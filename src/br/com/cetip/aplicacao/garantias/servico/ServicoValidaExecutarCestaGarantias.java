package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiasFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.ContratoCestaGarantiaDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
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
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="CONTRATO"
 *                   
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @resultado.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_PARTICIPANTE"
 *                    
 * @requisicao.method atributo="CPFOuCNPJ"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 */
public class ServicoValidaExecutarCestaGarantias extends BaseGarantias implements Servico {

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      final RequisicaoServicoValidaExecutarCestaGarantias req = (RequisicaoServicoValidaExecutarCestaGarantias) requisicao;
      final NumeroCestaGarantia numeroCesta = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      CPFOuCNPJ cpfCnpj = req.obterGARANTIAS_CONTRAPARTE_CPFOuCNPJ();
      CodigoContaCetip contraParte = req.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();

      final ICestaDeGarantias cg = getFactory().getInstanceCestaDeGarantias();
      final CestaGarantiasDO cesta = cg.obterCestaDeGarantias(numeroCesta);
      IGarantias garantias = GarantiasFactory.getInstance(); 
      final Data dataCriacao = validaData(cesta);

      //Se cesta possui garantias selic,
      //elas devem ser liberadas antes do comando de liberacao total
      validaSelic(cesta);

      // se cesta ta vinculada a contrato, informa pra regra o cod do contrato pra validar a ponta do comitente
      // em caso de conta cliente
      CodigoIF codIFContrato = new CodigoIF();
      IContratosCesta icc = getFactory().getInstanceContratosCesta();
      ContratoCestaGarantiaDO vinculoContrato = icc.obterVinculoContrato(cesta);
      Booleano ehContrato = Booleano.FALSO;
      contraParte = cesta.getGarantido().getCodContaParticipante();

      if (vinculoContrato != null) {
         codIFContrato = vinculoContrato.getContrato().getCodigoIF();         
         ehContrato = Booleano.VERDADEIRO;
      }

      if (Condicional.vazio(cpfCnpj) && !Condicional.vazio(codIFContrato) && contraParte.ehContaCliente()){
    	  cpfCnpj = vinculoContrato.getComitenteContraparte();
      }
      
      AtributosColunados ac = new AtributosColunados();
      ac.atributo(contraParte);
      ac.atributo(numeroCesta);
      ac.atributo(cpfCnpj);
      ac.atributo(cpfCnpj != null ? cpfCnpj.obterNatureza() : null);
      ac.atributo(codIFContrato);

      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeExecutarCestaGarantias, ac, true);

      ResultadoServicoValidaExecutarCestaGarantias res = new ResultadoServicoValidaExecutarCestaGarantias();
      res.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contraParte);
      res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numeroCesta);
      res.atribuirGARANTIAS_DATA_CRIACAO_Data(dataCriacao);
      res.atribuirCONTRATO_Booleano(ehContrato);
      
      if (!Condicional.vazio(cpfCnpj)){
        res.atribuirGARANTIAS_CONTRAPARTE_CPFOuCNPJ(cpfCnpj);
      }

      return res;
   }

   private void validaSelic(final CestaGarantiasDO cesta) {
      final IGarantiasSelic igs = getFactory().getInstanceGarantiasSelic();
      if (igs.temSelicEmDetalhes(cesta.getNumIdCestaGarantias())) {
         throw new Erro(CodigoErro.MMG_SELIC_NAO_PODE_LIBERAR_GARANTIAS);
      }
   }

   private Data validaData(final CestaGarantiasDO cesta) {
      final Data dataCriacao = cesta.getDatCriacao();
      final Data dataInadimplencia = cesta.getDatInadimplencia();
      final Data d0 = getDataHoje();

      int prazo = getFactory().getInadimplencia();
      int comparacaoData = dataInadimplencia.somarDiasCorridos(prazo).comparar(d0);
      if (!(comparacaoData >= 0)) {
         throw new Erro(CodigoErro.DT_INADIMPLENCIA);
      }

      return dataCriacao;
   }

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new UnsupportedOperationException();
   }

}
