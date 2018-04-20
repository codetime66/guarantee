package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidaAcoes;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * Servico para validacao do cadastro da liberacao das garantias tipo penhor emissor de uma cesta de garantias
 * 
 * @author <a href="mailto:cabreva@summa-tech.com">Daniel A. "Cabreva" Alfenas</a>
 * @since maio/2008
 * 
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_GARANTIDO"
 * 
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_IF"
 * 
 * @requisicao.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_QUANTIDADE"
 * 
 * @requisicao.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @resultado.class
 */
public class ServicoValidarCadastroLiberacaoPenhorEmissor extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      return null;
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      RequisicaoServicoValidarCadastroLiberacaoPenhorEmissor req = (RequisicaoServicoValidarCadastroLiberacaoPenhorEmissor) arg0;

      ICestaDeGarantias dados = getFactory().getInstanceCestaDeGarantias();
      IValidaAcoes ima = getFactory().getInstanceValidaAcoes();
      NumeroCestaGarantia nrCesta = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      CestaGarantiasDO cesta = dados.obterCestaDeGarantias(nrCesta);

      if (ima.podeExecutarAcao(ICestaDeGarantias.LIBERAR_PENHOR_EMISSOR, null, cesta)) {
         AtributosColunados ac = new AtributosColunados();
         ac.atributo(req.obterGARANTIAS_CODIGO_NumeroCestaGarantia());
         ac.atributo(req.obterGARANTIAS_GARANTIDO_CodigoContaCetip());
         ac.atributo(req.obterGARANTIAS_CODIGO_IF_CodigoIF());
         ac.atributo(req.obterGARANTIAS_CODIGO_TIPO_CodigoTipoIF());
         ac.atributo(req.obterGARANTIAS_QUANTIDADE_Quantidade());

         FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeLiberarGarantiaPenhorEmissor, ac,
               true);
      } else {
         throw new Erro(CodigoErro.ACAO_INVALIDA_CESTA);
      }

      return new ResultadoServicoValidarCadastroLiberacaoPenhorEmissor();
   }

}
