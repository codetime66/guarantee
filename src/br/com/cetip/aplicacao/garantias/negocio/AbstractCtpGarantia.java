package br.com.cetip.aplicacao.garantias.negocio;

import java.rmi.RemoteException;

import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.ejb.RemoveException;

import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.nomeejb.NomeEJB;
import cetip.global.util.CtpValidateException;

abstract class AbstractCtpGarantia {

   private String sistema;

   public AbstractCtpGarantia(String sistema) {
      this.sistema = sistema;
   }

   protected final EJBHome initHome(String jndiName) {
      try {
         return NomeEJB.lookupHome(jndiName, NomeEJB.URL_NOME);
      } catch (Exception e) {
         Logger.error(this, e);
         throw trataErro(e);
      }
   }

   protected final void internalRemove(EJBObject ejbBean2) {
      try {
         ejbBean2.remove();
      } catch (RemoteException e) {
         Logger.error(this, e);
         throw criaErro(e.getMessage());
      } catch (RemoveException e) {
         Logger.error(this, e);
         throw criaErro(e.getMessage());
      }
   }

   protected final Erro criaErro(String mensagem) {
      Erro erro = new Erro(CodigoErro.ERRO);
      erro.parametroMensagem(sistema + ": " + mensagem, 0);
      return erro;
   }

   protected final Erro trataErro(Exception e) {
      if (e instanceof CtpValidateException) {
         return criaErro(e.getMessage());
      }

      Throwable cause = e;
      while (cause != null && cause.getClass().equals(CtpValidateException.class) == false) {
         cause = cause.getCause();
      }

      if (cause == null) {
         cause = e;
      }

      return criaErro(cause.getMessage());
   }

}
