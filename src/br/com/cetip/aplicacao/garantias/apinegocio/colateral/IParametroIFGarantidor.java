package br.com.cetip.aplicacao.garantias.apinegocio.colateral;

import br.com.cetip.dados.aplicacao.garantias.HabilitaIFGarantidorDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;

public interface IParametroIFGarantidor {

   public void incluirParamIFGarantidor(CodigoSistema codSistema, CodigoTipoIF codTipoIF, CodigoIF codIF);

   public Booleano existeCodigoIFJaHabilitado(CodigoSistema codSistema, CodigoTipoIF codTipoIF, CodigoIF codigoIF);

   public HabilitaIFGarantidorDO obtemParamIFGarantidorPorModuloSistema(CodigoSistema codigoSistema,
         CodigoTipoIF codTipoIF);
   public Booleano ehHabilitadoPorModuloSistema(CodigoSistema codSistema, CodigoTipoIF codTipoIF);
   
   public void excluiParamIFGarantidor(CodigoSistema codSistema, CodigoTipoIF codTipoIF);
   
   public Booleano ehAtivoCetipComoAgenteCalculo(Id numIF );
}
