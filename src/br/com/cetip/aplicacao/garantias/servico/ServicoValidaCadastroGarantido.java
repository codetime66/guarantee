package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * Servico para validacao dos dados inseridos pelo usuario na interface grafica de Cadastro Garantido.
 * 
 * @author <a href="mailto:fernando@summa-tech.com">Fernando Henrique</a>
 * @since Maio/2008
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIDO"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIDO"
 *                   
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="GARANTIDO_VISUALIZADOR"                  
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIDO"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIDO"
 * 
 */
public class ServicoValidaCadastroGarantido extends BaseGarantias implements Servico {

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      return null;
   }

   /*
    * Realiza a validacao dos dados digitados via motor de regras.
    * 
    * @param requisicao requisicao que contem os dados para validacao
    * 
    * @return o Resultado contendo se a validacao foi aceita ou nao
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoValidaCadastroGarantido req = (RequisicaoServicoValidaCadastroGarantido) requisicao;
      ResultadoServicoValidaCadastroGarantido res = new ResultadoServicoValidaCadastroGarantido();

      NumeroCestaGarantia cesta = req.obterGARANTIDO_NumeroCestaGarantia();

      CodigoContaCetip vizualizador = req.obterGARANTIDO_CodigoContaCetip();

      AtributosColunados ac = new AtributosColunados();
      ac.atributo(cesta);
      ac.atributo(vizualizador);

      // chama uma regra criada pela cetip para validacao

      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeCadastrarGarantido, ac, true);

      res.atribuirGARANTIDO_NumeroCestaGarantia(cesta);
      res.atribuirGARANTIDO_CodigoContaCetip(vizualizador);

      return res;
   }

}
