package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.IExcluirGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

/**
 * 
 * @author brunob
 */
final class TiposExclusaoGarantia {

   public TiposExclusaoGarantia() {
      new ExcluirGarantia().registrar(this);
      new ExcluirGarantiaCDAWA().registrar(this);
      new ExcluirGarantiaSelic().registrar(this);
      new ExcluirGarantiaNaoCetip().registrar(this);
   }

   private Map mapa = new HashMap();

   public void registrar(CodigoTipoIF tipoIF, IExcluirGarantia instance) {
      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      mapa.put(key, instance);
   }

   public IExcluirGarantia obter(CodigoTipoIF tipoIF) {
      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      Object o = mapa.get(key);
      if (o == null) {
         o = mapa.get(null);
      }

      return (IExcluirGarantia) o;
   }

}