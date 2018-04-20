package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IPenhorNoEmissor;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico para consulta de garantias penhor no emissor ja liberadas
 * 
 * @author <a href="mailto:cabreva@summa-tech.com">Daniel A. "Cabreva" Alfenas</a>
 * @since maio/2008
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIDO"
 * 
 * @resultado.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_IF"    
 *
 * @resultado.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_QUANTIDADE"                
 * 
 * 
 */
public class ServicoListarGrtiaPenhorEmissorLiberacaoCadastrada extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {

      RequisicaoServicoListarGrtiaPenhorEmissorLiberacaoCadastrada req = (RequisicaoServicoListarGrtiaPenhorEmissorLiberacaoCadastrada) requisicao;
      ResultadoServicoListarGrtiaPenhorEmissorLiberacaoCadastrada res = new ResultadoServicoListarGrtiaPenhorEmissorLiberacaoCadastrada();

      NumeroCestaGarantia numeroCesta = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();

      IGarantias factory = getFactory();

      IPenhorNoEmissor pe = factory.getInstancePenhorNoEmissor();
      Iterator it = pe.obterListaGarantiasPenhorEmissorLiberadas(numeroCesta).iterator();

      while (it.hasNext()) {
         MovimentacaoGarantiaDO lib = (MovimentacaoGarantiaDO) it.next();

         res.novaLinha();
         res.atribuirGARANTIAS_CODIGO_IF_CodigoIF(lib.getInstrumentoFinanceiro().getCodigoIF());
         res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numeroCesta);
         res.atribuirGARANTIAS_QUANTIDADE_Quantidade(lib.getQtdGarantia());
         res.atribuirGARANTIDO_CodigoContaCetip(lib.getContaParticipante().getCodContaParticipante());
      }

      return res;
   }

}
