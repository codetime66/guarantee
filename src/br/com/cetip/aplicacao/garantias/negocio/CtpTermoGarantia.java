package br.com.cetip.aplicacao.garantias.negocio;

import java.rmi.RemoteException;

import br.com.cetip.infra.log.Logger;
import cetip.global.util.CtpValidateException;
import cetip.termo.ejb.QTermoGarantiaContrato;
import cetip.termo.ejb.QTermoGarantiaContratoHome;

class CtpTermoGarantia extends AbstractCtpGarantia {

   private QTermoGarantiaContrato ejbBean = null;

   public CtpTermoGarantia() {
      super("TERMO");

      QTermoGarantiaContratoHome home = (QTermoGarantiaContratoHome) initHome(QTermoGarantiaContratoHome.JNDI_NAME);

      try {
         ejbBean = home.create();
      } catch (Exception e) {
         Logger.error(this, e);
         throw trataErro(e);
      }
   }

   /**
    * @param numIF  
    * @param participante 
    * @param regraLiberacao 
    */
   public void podeVincularCesta(Integer numIF, Integer participante, String cpfCnpjComitenteParte,
         String cestaGarantiaParte, Integer contraParte, String cpfCnpjComitenteContraparte,
         String cestaGarantiaContraparte, String regraLiberacao) {
      try {
         ejbBean.validaVinculacaoCesta(numIF, participante, cpfCnpjComitenteParte, cestaGarantiaParte, contraParte,
               cpfCnpjComitenteContraparte, cestaGarantiaContraparte, regraLiberacao);
      } catch (RemoteException e) {
         Logger.error(this, e);
         throw criaErro(e.getMessage());
      } catch (CtpValidateException e) {
         Logger.error(this, e);
         throw trataErro(e);
      }
   }

   public void remove() {
      internalRemove(ejbBean);
   }

   public void vinculacaoCestaGarantia(Integer numIF, Integer participante, String cpfCnpjComitenteParte,
         String cestaGarantiaParte, Integer contraParte, String cpfCnpjComitenteContraparte,
         String cestaGarantiaContraparte, String regraLiberacao) {
      try {
         ejbBean.vinculacaoCestaGarantia(numIF, participante, cpfCnpjComitenteParte, cestaGarantiaParte, contraParte,
               cpfCnpjComitenteContraparte, cestaGarantiaContraparte, regraLiberacao);
      } catch (Exception e) {
         Logger.error(this, e);
         throw trataErro(e);
      }
   }
}
