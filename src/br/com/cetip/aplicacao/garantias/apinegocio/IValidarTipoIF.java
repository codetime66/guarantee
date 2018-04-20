package br.com.cetip.aplicacao.garantias.apinegocio;

import java.util.Map;
import java.util.Set;

import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;

public interface IValidarTipoIF {

   public boolean validarGarantidor(CodigoTipoIF tipoIF, Id sistema);

   public boolean validarGarantido(CodigoTipoIF tipoIF, Id sistema);

   public boolean validarGarantidor(Id tipoIF, Id sistema);

   public boolean validarGarantido(Id tipoIF, Id sistema);

   /*
    * <p>Valida se todos os CodigoTipoIF informados no hashSet sao garantidores do sistema <code>sistema</code></p>
    * 
    * @param hashSet
    * 
    * @param sistema
    * 
    * @return
    */
   public boolean validarTiposGarantidores(Set hashSet, Id sistema);

   /*
    * <p>Valida se todos os CodigoTipoIF informados no hashSet sao garantidos do sistema <code>sistema</code></p>
    * 
    * @param hashSet
    * 
    * @param sistema
    * 
    * @return
    */
   public boolean validarTiposGarantidos(Set hashSet, Id sistema);

   /**
    * Retorna um mapa de Id (sistema) - Collection(CodigoTipoIF) dos tipos garantidores
    * 
    * @return
    */
   public Map obterMapaGarantidores();

   public Map obterMapaGarantidos();

}