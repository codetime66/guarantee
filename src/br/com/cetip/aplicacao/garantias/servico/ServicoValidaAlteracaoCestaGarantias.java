package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.IContaGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.motorderegra.nucleo.motor.MotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.method atributo="IdTipoGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIAS_CESTA"
 * 
 * @author <a href="bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ServicoValidaAlteracaoCestaGarantias extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws ExcecaoServico {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao req) throws Exception {
      // Chama regras de avaliacao
      MotorDeRegra motorRegra = FabricaDeMotorDeRegra.getMotorDeRegra();

      RequisicaoServicoValidaAlteracaoCestaGarantias requisicao = null;
      requisicao = (RequisicaoServicoValidaAlteracaoCestaGarantias) req;

      NumeroCestaGarantia codCesta = requisicao.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      IdTipoGarantia idTipoGarantia = requisicao.obterGARANTIAS_CODIGO_IdTipoGarantia();

      IGarantias factory = getFactory();

      CestaGarantiasDO cesta = factory.getInstanceCestaDeGarantias().obterCestaDeGarantias(codCesta);

      CodigoContaCetip garantidor = cesta.getGarantidor().getCodContaParticipante();

      garantidor.atribuirContexto(Contexto.GARANTIAS_PARTICIPANTE);

      CodigoContaCetip garantido = requisicao.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();

      if (!Condicional.vazio(idTipoGarantia) && !cesta.getStatusCesta().equals(StatusCestaDO.EM_MANUTENCAO)) {
         throw new Erro(CodigoErro.ERRO, "Cesta deve estar EM MANUTENCAO para alterar Tipo de Garantia");
      }

      if (Condicional.vazio(idTipoGarantia) && Condicional.vazio(garantido)) {
         throw new Erro(CodigoErro.ERRO, "Nenhum campo alterado");
      }

      AtributosColunados ac = new AtributosColunados();
      ac.atributo(codCesta);
      ac.atributo(garantidor);
      ac.atributo(garantido);
      ac.atributo(idTipoGarantia);
      motorRegra.avalia(ConstantesDeNomeDeRegras.podeAlterarCestaGarantias, ac, true);

      IContaGarantia icg = factory.getInstanceContaGarantia();
      Logger.debug(this, "Chamada ao possuiConta60: " + garantido);
      if (!icg.possuiConta60(garantido)) {
         throw new Erro(CodigoErro.GARANTIAS_NAO_POSSUI_CONTA60);
      }

      Booleano b = new Booleano(true);
      ResultadoServicoValidaAlteracaoCestaGarantias res;
      res = new ResultadoServicoValidaAlteracaoCestaGarantias();
      res.atribuirGARANTIAS_CESTA_Booleano(b);

      return res;
   }
}