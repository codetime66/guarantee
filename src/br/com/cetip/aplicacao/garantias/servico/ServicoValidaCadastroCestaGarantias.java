package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
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
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.method atributo="IdTipoGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CODIGO"
 */
public class ServicoValidaCadastroCestaGarantias extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      return null;
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoValidaCadastroCestaGarantias req = (RequisicaoServicoValidaCadastroCestaGarantias) requisicao;

      CodigoContaCetip participante = req.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();
      CodigoContaCetip contraparte = req.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
      IdTipoGarantia tipoGarantia = req.obterGARANTIAS_CODIGO_IdTipoGarantia();

      AtributosColunados ac = new AtributosColunados();
      ac.atributo(participante);
      ac.atributo(contraparte);
      ac.atributo(tipoGarantia);

      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeRegistrarCadastroCestaGarantias, ac);

      return new ResultadoServicoValidaCadastroCestaGarantias();
   }

}
