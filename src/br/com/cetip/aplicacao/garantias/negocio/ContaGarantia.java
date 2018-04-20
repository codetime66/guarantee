package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IContaGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.sap.apinegocio.ConsultaContaFactory;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IConsultaConta;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;

final class ContaGarantia extends BaseGarantias implements IContaGarantia {

   private IContaParticipante contaParticipanteDAO;

   private IConsultaConta iConsultaConta;

   public void inicializar() {
      try {
         contaParticipanteDAO = ContaParticipanteFactory.getInstance();
         iConsultaConta = ConsultaContaFactory.getInstance();
      } catch (Exception e) {
         Logger.error(this, e);
      }
   }

   public boolean possuiConta60(CodigoContaCetip garantido) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Dentro do possuiConta60: " + garantido);
      }

      ContaParticipanteDO conta = null;
      try {
         conta = contaParticipanteDAO.obterContaParticipanteDO(garantido);
      } catch (Exception e) {
         Logger.error(this, e);
      }

      ContaParticipanteDO conta60 = innerObterConta60(conta);

      return conta60 != null;
   }

   /**
    * Obtem a Conta 60 (Caucao) da conta principal
    * 
    * @param participante
    * @return
    */
   public ContaParticipanteDO obterConta60(ContaParticipanteDO contaParticipante) {
      ContaParticipanteDO conta60 = innerObterConta60(contaParticipante);

      if (conta60 == null) {
         throw new Erro(CodigoErro.CONTA_INEXISTENTE);
      }

      return conta60;
   }

   private ContaParticipanteDO innerObterConta60(ContaParticipanteDO contaParticipante) {
      if (contaParticipante == null) {
         return null;
      }

      ContaParticipanteDO conta60Garantido = null;
      try {
         conta60Garantido = iConsultaConta.obterContaGarantia(contaParticipante.getCodContaParticipante());
      } catch (Exception e) {
         Logger.error(this, e);
      }

      return conta60Garantido;
   }

   public ContaParticipanteDO obterConta60(CodigoContaCetip contaCetip) {
      ContaParticipanteDO participante = null;
      try {
         participante = contaParticipanteDAO.obterContaParticipanteDO(contaCetip);
      } catch (Exception e) {
         Logger.error(this, e);
      }

      return obterConta60(participante);
   }

   /**
    * Obtem a conta 60 referente a contaSelic especificada
    * @param contaSelic
    * @return
    */
   public ContaParticipanteDO obterConta60(CodigoContaSelic contaSelic) {
      ContaParticipanteDO participante;
      try {
         participante = contaParticipanteDAO.obterContaParticipanteDO(contaSelic);
      } catch (Exception e) {
         Logger.error(this, e);
         throw new Erro(CodigoErro.CONTA_INEXISTENTE);
      }

      return obterConta60(participante);
   }

   public ContaParticipanteDO obterConta60(CestaGarantiasDO cesta) {
      if (!Condicional.vazio(cesta.getConta60Garantido())) {
         return cesta.getConta60Garantido();
      }

      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
      return obterConta60(igc.obterGarantidoCesta(cesta));
   }

}
