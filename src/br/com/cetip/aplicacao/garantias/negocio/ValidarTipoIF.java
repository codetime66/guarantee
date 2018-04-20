package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.IValidarTipoIF;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFSistemaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;

final class ValidarTipoIF extends BaseGarantias implements IValidarTipoIF {

   private Map consultas = new HashMap();

   private boolean validar(String tipo, Object tipoIF, Id idSistema) {
      String col = tipoIF instanceof CodigoTipoIF ? "codigoTipoIF" : "numTipoIF";

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Consulta viabilidade de movimentacao no MMG para o [tipo_if - sistema]: [" + tipoIF
               + " - " + idSistema + "]");
      }

      StringBuffer sql = new StringBuffer(200);
      sql.append("select count(tifs) from ").append(TipoIFSistemaDO.class.getName()).append(
            " as tifs where tifs.tipoIF.").append(col).append(" = :tipoIF ").append(
            " and tifs.sistema.numero = :numSistema ").append(" and tifs.").append(tipo).append(" = :indicador");

      String key = "1_" + col + "_" + tipo;
      IConsulta c = (IConsulta) consultas.get(key);

      if (c == null) {
         c = getGp().criarConsulta(sql.toString());
         c.setAtributo("indicador", new Booleano(Booleano.VERDADEIRO));
         c.setCacheable(true);
         c.setCacheRegion("MMG");
         consultas.put(key, c);
      }

      c.setAtributo("tipoIF", tipoIF);
      c.setAtributo("numSistema", idSistema);
      List l = c.list();
      Integer count = (Integer) l.get(0);

      return count.intValue() > 0;
   }

   public boolean validarGarantido(CodigoTipoIF tipoIF, Id idSistema) {
      return validar("indGarantido", tipoIF, idSistema);
   }

   public boolean validarGarantidor(CodigoTipoIF tipoIF, Id idSistema) {
      return validar("indGarantidor", tipoIF, idSistema);
   }

   public boolean validarGarantido(Id tipoIF, Id idSistema) {
      return validar("indGarantido", tipoIF, idSistema);
   }

   public boolean validarGarantidor(Id tipoIF, Id idSistema) {
      return validar("indGarantidor", tipoIF, idSistema);
   }

   public boolean validarTiposGarantidores(Set hashSet, Id idSistema) {
      return validarTipos("indGarantidor", hashSet, idSistema);
   }

   private boolean validarTipos(String tipo, Set hashSet, Id idSistema) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Consulta viabilidade de movimentacao no MMG para os tipos de IF " + hashSet.toString()
               + " para o sistema [" + idSistema + "]");
      }

      StringBuffer sql = new StringBuffer(200);
      sql.append("select count(*) from ").append(TipoIFSistemaDO.class.getName()).append(
            " tifs where tifs.tipoIF.codigoTipoIF in (:tipoIF) and tifs.sistema.numero = :numSistema ").append(
            " and tifs.").append(tipo).append(" = :indicador");

      String key = "2_" + tipo;
      IConsulta c = (IConsulta) consultas.get(key);

      if (c == null) {
         c = getGp().criarConsulta(sql.toString());
         c.setAtributo("indicador", Booleano.VERDADEIRO);
         c.setCacheable(true);
         c.setCacheRegion("MMG");
         consultas.put(key, c);
      }

      c.setParameterList("tipoIF", hashSet);
      c.setAtributo("numSistema", idSistema);
      List l = c.list();
      Integer count = (Integer) l.get(0);

      return count.intValue() == hashSet.size();
   }

   public boolean validarTiposGarantidos(Set hashSet, Id idSistema) {
      return validarTipos("indGarantido", hashSet, idSistema);
   }

   public Map obterMapaGarantidores() {
      return obterMapa("indGarantidor");
   }

   public Map obterMapaGarantidos() {
      return obterMapa("indGarantido");
   }

   private Map obterMapa(String flag) {
      String hql = "select t.sistema.numero, t.tipoIF.codigoTipoIF from " + TipoIFSistemaDO.class.getName()
            + " t inner join t.tipoIF inner join t.sistema where t." + flag + " = 'S'";

      if (consultas.containsKey(hql) == false) {
         IConsulta c = getGp().criarConsulta(hql);
         c.setCacheable(true);
         c.setCacheRegion("MMG");
         consultas.put(hql, c);
      }

      IConsulta c = (IConsulta) consultas.get(hql);
      List l = c.list();
      Iterator i = l.iterator();

      Map mapa = new HashMap();
      while (i.hasNext()) {
         Object[] row = (Object[]) i.next();

         Id numSistema = (Id) row[0];

         if (mapa.containsKey(numSistema) == false) {
            mapa.put(numSistema, new HashSet());
         }

         Set tiposIF = (Set) mapa.get(numSistema);
         tiposIF.add(row[1]);
      }

      return mapa;
   }

}
