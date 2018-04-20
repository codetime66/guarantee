package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
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
 * @resultado.class
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIAS_CESTA"
 * 
 * @author <a href="bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ServicoValidaFechamentoCestaGarantias extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws ExcecaoServico {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao req) throws Exception {
      // Chama regras de avaliacao
      AtributosColunados ac = new AtributosColunados();

      RequisicaoServicoValidaFechamentoCestaGarantias requisicao = null;
      requisicao = (RequisicaoServicoValidaFechamentoCestaGarantias) req;

      NumeroCestaGarantia codCesta = requisicao.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      ac.atributo(codCesta);

      ICestaDeGarantias dao = getFactory().getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = dao.obterCestaDeGarantias(codCesta);

      StatusCestaDO status = cesta.getStatusCesta();

      boolean emManutencao = status.equals(StatusCestaDO.EM_MANUTENCAO);
      boolean emEdicao = status.equals(StatusCestaDO.EM_EDICAO);
      boolean incompleta = status.equals(StatusCestaDO.INCOMPLETA);
      boolean possuiGarantias = dao.contarGarantias(cesta) > 0;
      boolean possuiMovsBloqueioPendente = dao.possuiMovsBloqueioPendente(cesta);
      boolean possuiMovsBloqueioSelic = dao.possuiMovsBloqueioSelic(cesta);
      boolean possuiMovsDefeituosas = dao.possuiMovsDefeituosas(cesta);
      possuiGarantias = possuiGarantias || possuiMovsBloqueioPendente || possuiMovsBloqueioSelic;

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "- Cesta: " + cesta.getNumIdCestaGarantias());
         Logger.debug(this, "- Status: " + status.getCodStatusCesta());
         Logger.debug(this, "- Possui Movs Bloqueio Pendente: " + possuiMovsBloqueioPendente);
         Logger.debug(this, "- Possui Movs Defeituosas: " + possuiMovsDefeituosas);
         Logger.debug(this, "- Possui Garantias: " + possuiGarantias);
      }

      if ((emManutencao || emEdicao || (incompleta && !possuiMovsDefeituosas)) && !possuiGarantias) {
         throw new Erro(CodigoErro.CESTA_NAO_POSSUI_GARANTIAS);
      }

      if (possuiMovsDefeituosas) {
         throw new Erro(CodigoErro.CESTA_POSSUI_ITENS_INVALIDOS);
      }

      MotorDeRegra motorRegra = FabricaDeMotorDeRegra.getMotorDeRegra();
      motorRegra.avalia(ConstantesDeNomeDeRegras.eCestaPossivelFechamento, ac, true);

      CodigoContaCetip garantidor = cesta.getGarantidor().getCodContaParticipante();
      garantidor.atribuirContexto(Contexto.GARANTIAS_PARTICIPANTE);

      CodigoContaCetip garantido = requisicao.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
      if (Condicional.vazio(garantido)) {
         IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
         ContaParticipanteDO contaGarantido = igc.obterGarantidoCesta(cesta);

         if (contaGarantido != null) {
            garantido = contaGarantido.getCodContaParticipante();
         }
      } else {
         AtributosColunados aca = new AtributosColunados();
         aca.atributo(codCesta);
         aca.atributo(garantidor);
         aca.atributo(garantido);
         aca.atributo(cesta.getTipoGarantia().getNumIdTipoGarantia());
         motorRegra.avalia(ConstantesDeNomeDeRegras.podeAlterarCestaGarantias, aca, true);
      }

      if (garantido == null) {
         garantido = new CodigoContaCetip();
      }
      garantido.atribuirContexto(Contexto.GARANTIAS_CONTRAPARTE);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Garantido -> " + garantido);
      }

      if (possuiMovsBloqueioSelic && garantidor.mesmoConteudo(garantido)) {
         throw new Erro(CodigoErro.FECHAMENTO_MMG_MESMOS_PARTICIPANTES);
      }

      ac = new AtributosColunados();
      ac.atributo(codCesta);
      ac.atributo(garantidor);
      ac.atributo(garantido);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "ServicoValidaFechamentoCestaGarantias_codCesta = " + codCesta);
         Logger.debug(this, "ServicoValidaFechamentoCestaGarantias_garantidor = " + garantidor);
         Logger.debug(this, "ServicoValidaFechamentoCestaGarantias_garantido = " + garantido);
      }

      motorRegra.avalia(ConstantesDeNomeDeRegras.podeFecharCestaGarantias, ac);
      Booleano b = new Booleano(true);
      ResultadoServicoValidaFechamentoCestaGarantias res;
      res = new ResultadoServicoValidaFechamentoCestaGarantias();
      res.atribuirGARANTIAS_CESTA_Booleano(b);

      return res;
   }

}