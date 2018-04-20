package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.infra.log.Logger;
import cetip.ejb.box.ICtpBoxGarantiaContratoOpcaoUCCHome;
import cetip.ejb.box.ICtpBoxGarantiaContratoOpcaoUCCRemote;
import cetip.global.util.CtpValidateException;

class CtpBoxGarantia extends AbstractCtpGarantia {

   private ICtpBoxGarantiaContratoOpcaoUCCRemote ejbBean = null;

   public CtpBoxGarantia() {
      super("OPÇÕES");

      ICtpBoxGarantiaContratoOpcaoUCCHome home = (ICtpBoxGarantiaContratoOpcaoUCCHome) initHome(ICtpBoxGarantiaContratoOpcaoUCCHome.JNDI_NAME);

      try {
         ejbBean = home.create();
      } catch (Exception e) {
         Logger.error(this, e);
         throw trataErro(e);
      }
   }

   public boolean podeVincularCesta(Integer numIF, Integer participante, String regraLiberacao) {
      try {
         return ejbBean.podeVincularCesta(numIF, participante, regraLiberacao);
      } catch (CtpValidateException e) {
         Logger.error(this, e);
         throw criaErro(e.getMessage());
      } catch (Exception e) {
         Logger.error(this, e);
         throw trataErro(e);
      }
   }

   public void vinculacaoCestaGarantia(Integer numIF, Integer participante, String regraLiberacao) {
      try {
         ejbBean.vinculacaoCestaGarantia(numIF, participante, regraLiberacao);
      } catch (Exception e) {
         Logger.error(this, e);
         throw trataErro(e);
      }
   }

   public void remove() {
      internalRemove(ejbBean);
   }

}
