package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.IMIGAcionador;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.class
 * 
 * @author brunob
 *
 */
public class ServicoAcionaInadimplenciaCesta extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      IMIGAcionador mig = getFactory().getInstanceMIGAcionador();
      Id idCesta = ((RequisicaoServicoAcionaInadimplenciaCesta) req).obterGARANTIAS_CODIGO_Id();
      CestaGarantiasDO cesta = getFactory().getInstanceCestaDeGarantias().obterCestaDeGarantias(
            new NumeroCestaGarantia(idCesta.obterConteudo()));
      mig.acionarInadimplencia(cesta);

      return new ResultadoServicoAcionaInadimplenciaCesta();
   }

   public Resultado executarConsulta(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}
