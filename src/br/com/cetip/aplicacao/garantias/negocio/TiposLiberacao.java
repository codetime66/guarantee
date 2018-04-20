package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.ILiberacaoCesta;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;

final class TiposLiberacao {

   public TiposLiberacao() {
      new LiberacaoCestaAltaPlataforma().registrar(this);
      new LiberacaoCestaContrato().registrar(this);
      new LiberacaoCestaBaixaPlataforma().registrar(this);
   }

   private Map mapa = new HashMap();

   public void registrar(Id idSistema, CodigoTipoIF tipoIF, ILiberacaoCesta instance) {
      if (idSistema == null) {
         throw new IllegalArgumentException("idSistema nao pode ser nulo.");
      }

      Map tipos = getMapa(mapa, idSistema);

      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      tipos.put(key, instance);
   }

   private Map getMapa(Map mapaParam, Id idSistema) {
      Object key = idSistema.obterConteudo();
      Map mapaInterno = (Map) mapaParam.get(key);

      if (mapaInterno == null) {
         mapaInterno = new HashMap();
         mapaParam.put(key, mapaInterno);
      }

      return mapaInterno;
   }

   public ILiberacaoCesta obter(Id idSistema, CodigoTipoIF tipoIF) {
      Map tipos = getMapa(mapa, idSistema);

      Object key = null;
      if (tipoIF != null) {
         key = tipoIF.obterConteudo();
      }

      Object o = tipos.get(key);
      if (o == null) {
         o = tipos.get(null);
      }

      return (ILiberacaoCesta) o;
   }

}
