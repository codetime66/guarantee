package br.com.cetip.aplicacao.garantias.servico.selic;

import java.util.ArrayList;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

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

public class ServicoValidarConsultaPosicaoSelic extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new ExcecaoServico(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoValidarConsultaPosicaoSelic req = (RequisicaoServicoValidarConsultaPosicaoSelic) requisicao;

      CodigoContaSelic contaSelic = req.obterCONTA_CodigoContaSelic();
      CodigoIF codigoIF = req.obterCODIGO_IF_CodigoIF();
      Data datVenc = req.obterDATA_VENCIMENTO_Data();
      Data datRef = req.obterDATA_REFERENCIA_Data();
      IContaParticipante contaParticipante = ContaParticipanteFactory.getInstance();
      ContaParticipanteDO contaSelicDO = contaParticipante.obterContaParticipanteDO(contaSelic);
      ContaParticipanteDO contaPrincipalDO = contaSelicDO == null ? null : contaParticipante
            .obterContaPrincipal(contaSelicDO.getParticipante().getId());

      if (contaSelicDO == null || contaPrincipalDO == null) {
         Erro erro = new Erro(CodigoErro.PARAMETRO_INVALIDO);
         erro.parametroMensagem(contaSelic, 0);
         throw erro;
      }
      CodigoContaCetip contaPrincipal = contaPrincipalDO.getCodContaParticipante();
      Logger.info(this, "Conta Principal: " + contaPrincipal);

      Data datVencAtivo = InstrumentoFinanceiroFactory.getInstance().obterInstrumentoFinanceiro(codigoIF)
            .getDataVencimento();

      List ac = new ArrayList(15);
      ac.add(contaSelic);
      ac.add(codigoIF);
      ac.add(datVenc);
      ac.add(datRef);
      ac.add(getDataHoje());
      ac.add(contaPrincipal);
      ac.add(datVencAtivo);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Parametros da regra podeConsultarPosicaoSelic " + ac.toString());
      }

      // chama uma regra para validar os campos
      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeConsultarPosicaoSelic, ac, true);

      return new ResultadoServicoValidarConsultaPosicaoSelic();
   }

}
