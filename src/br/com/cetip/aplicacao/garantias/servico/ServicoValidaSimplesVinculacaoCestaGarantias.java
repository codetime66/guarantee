package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * Servico para validacao dos dados inseridos pelo usuario na interface grafica de Vinculacao de Cesta de Garantias
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * @since Julho/2006
 * 
 * @resultado.class
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_ACESSO"
 */
public class ServicoValidaSimplesVinculacaoCestaGarantias extends BaseGarantias implements Servico {

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
    * @return o Resultado contendo se a validacao foi aceita ou nao
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      Logger.debug(this, "*** Validando dados da interface Vincular Cesta de Garantias ***");

      RequisicaoServicoValidaSimplesVinculacaoCestaGarantias req = (RequisicaoServicoValidaSimplesVinculacaoCestaGarantias) requisicao;
      ResultadoServicoValidaSimplesVinculacaoCestaGarantias res = new ResultadoServicoValidaSimplesVinculacaoCestaGarantias();

      NumeroCestaGarantia numero = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      Funcao tipoAcesso = req.obterGARANTIAS_TIPO_ACESSO_Funcao();

      ICestaDeGarantias objCesta = getFactory().getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = objCesta.obterCestaDeGarantias(numero);

      CodigoContaCetip parte = cesta.getGarantidor().getCodContaParticipante();
      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();

      ContaParticipanteDO contaGarantido = igc.obterGarantidoCesta(cesta);
      if (contaGarantido == null) {
         throw new Erro(CodigoErro.GARANTIAS_NAO_POSSUI_GARANTIDO);
      }

      CodigoContaCetip contraParte = contaGarantido.getCodContaParticipante();

      Id stCesta = cesta.getStatusCesta().getNumIdStatusCesta();
      stCesta.atribuirContexto(Contexto.GARANTIAS_STATUS);
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Cesta: " + numero);
         Logger.debug(this, "Parte: " + parte);
         Logger.debug(this, "ContraParte: " + contraParte);
      }

      AtributosColunados ac = new AtributosColunados();
      ac.atributo(parte);
      ac.atributo(contraParte);
      ac.atributo(numero);
      ac.atributo(tipoAcesso);
      ac.atributo(stCesta);

      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeVincularCestaGarantias, ac, true);

      return res;
   }

}
