package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.ILiberarCestaParaManutencao;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico que permite o Garantido liberar ao Garantidor, acesso para manutencao da Cesta
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * 
 * @requisicao.class
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.class
 */
public class ServicoLiberarCestaParaManutencao extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      RequisicaoServicoLiberarCestaParaManutencao req;
      req = (RequisicaoServicoLiberarCestaParaManutencao) requisicao;

      NumeroCestaGarantia numero = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();

      IGarantias factory = getFactory();

      ILiberarCestaParaManutencao liberar = factory.getInstanceLiberarCestaParaManutencao();
      liberar.liberarCestaParaManutencao(numero);

      return new ResultadoServicoLiberarCestaParaManutencao();
   }

   public Resultado executarConsulta(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}