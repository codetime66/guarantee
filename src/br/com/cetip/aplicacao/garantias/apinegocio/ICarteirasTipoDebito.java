package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;

public interface ICarteirasTipoDebito {

   public Id getTipoCarteira(Id tipoDebito);

   public IdTipoGarantia getTipoGarantia(Id tipoDebito);

   public Id getTipoDebito(CodigoContaCetip conta, IdTipoGarantia tipoGarantia, boolean isNivel2);

}
