package br.com.cetip.aplicacao.garantias.apinegocio;

public class FactoryGenerica {

   private Class clazz;

   protected FactoryGenerica(String className) {
      try {
         this.clazz = Class.forName(className);
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
   }

   protected Object newInstance() {
      try {
         return clazz.newInstance();
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }

      return null;
   }
}
