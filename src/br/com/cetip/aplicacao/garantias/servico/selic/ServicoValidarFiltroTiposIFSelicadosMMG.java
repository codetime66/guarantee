package br.com.cetip.aplicacao.garantias.servico.selic;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.IInstrumentoFinanceiro;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.ITipoIF;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.TipoIFFactory;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * @requisicao.method atributo="CodigoTipoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CODIGO_TIPO_IF"
 * 
 * @requisicao.method atributo="CodigoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CODIGO_IF"
 * 
 * @resultado.class
 * 
 */
public class ServicoValidarFiltroTiposIFSelicadosMMG extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {

      RequisicaoServicoValidarFiltroTiposIFSelicadosMMG req = (RequisicaoServicoValidarFiltroTiposIFSelicadosMMG) requisicao;

      ITipoIF tipoIF = TipoIFFactory.getInstance();
      if (!tipoIF.ehTipoIFSelicado(req.obterCODIGO_TIPO_IF_CodigoTipoIF())) {
         throw new Erro(CodigoErro.CESTA_TIPO_IF_INVALIDO);
      }

      if (req.obterCODIGO_IF_CodigoIF() != null && !req.obterCODIGO_IF_CodigoIF().vazio()) {
         IInstrumentoFinanceiro instrumentoFinanceiro = InstrumentoFinanceiroFactory.getInstance();
         InstrumentoFinanceiroDO instrFinanceiro = instrumentoFinanceiro.obterInstrumentoFinanceiro(req
               .obterCODIGO_IF_CodigoIF());

         if (instrFinanceiro.getTipoIF().getCodigoTipoIF() != null
               && !req.obterCODIGO_TIPO_IF_CodigoTipoIF().toString().equalsIgnoreCase(
                     instrFinanceiro.getTipoIF().getCodigoTipoIF().toString())) {
            throw new Erro(CodigoErro.TIPO_IF_INCOMPATIVEL_CODIGO_IF);
         }
      }

      return new ResultadoServicoValidarFiltroTiposIFSelicadosMMG();
   }
}
