package br.com.cetip.aplicacao.garantias.servico.selic;

import br.com.cetip.aplicacao.garantias.apinegocio.IMensageriaGarantiasSelic;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.mensageria.selic.ConsultaSaldoVO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoContaSelic"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTA"
 *                    
 * @requisicao.method atributo="CodigoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CODIGO_IF"
 * 
 * @requisicao.method atributo="Data"
 *                    pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                    contexto="DATA_VENCIMENTO"
 *                    
 * @requisicao.method atributo="Data"
 *                    pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                    contexto="DATA_REFERENCIA"
 *                    
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CODIGO_IF"
 * 
 */

public class ServicoConsultaPosicaoSelic extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {

      RequisicaoServicoConsultaPosicaoSelic req = (RequisicaoServicoConsultaPosicaoSelic) requisicao;
      CodigoIF codigoIF = req.obterCODIGO_IF_CodigoIF();
      CodigoContaSelic codigoContaSelic = req.obterCONTA_CodigoContaSelic();
      Data datVenc = req.obterDATA_VENCIMENTO_Data();
      Data datRef = req.obterDATA_REFERENCIA_Data();

      IMensageriaGarantiasSelic imsgSelic = getFactory().getInstanceMensageriaSelic();
      ConsultaSaldoVO consulta = new ConsultaSaldoVO(codigoIF, codigoContaSelic, datVenc, datRef);
      imsgSelic.enviarConsultaDeSaldo(consulta);

      ResultadoServicoConsultaPosicaoSelic res = new ResultadoServicoConsultaPosicaoSelic();
      res.atribuirCODIGO_IF_CodigoIF(codigoIF);

      return res;
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      throw new ExcecaoServico(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

}
