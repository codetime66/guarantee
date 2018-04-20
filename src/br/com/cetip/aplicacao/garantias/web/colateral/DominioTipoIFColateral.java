package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.data.element.DataElement;

public class DominioTipoIFColateral {

   public CodigoTipoIF obterDominioParamIFGarantidor() {
      CodigoTipoIF codigoTipoIF = new CodigoTipoIF(Contexto.GARANTIAS_TIPO_IF);
      DataElement.Domain domain = codigoTipoIF.getDomain();
      domain.add(new CodigoTipoIF());
      domain.add(CodigoTipoIF.CDB);
      domain.add(CodigoTipoIF.DEB);
      domain.add(CodigoTipoIF.CDB);
      domain.add(CodigoTipoIF.DEB);
      domain.add(CodigoTipoIF.LFT);
      domain.add(CodigoTipoIF.LFTA);
      domain.add(CodigoTipoIF.LFTB);
      domain.add(CodigoTipoIF.LTN);
      domain.add(CodigoTipoIF.NTNA);
      domain.add(CodigoTipoIF.NTNB);
      domain.add(CodigoTipoIF.NTNC);
      domain.add(CodigoTipoIF.NTND);
      domain.add(CodigoTipoIF.NTNE);
      domain.add(CodigoTipoIF.NTNF);
      domain.add(CodigoTipoIF.NTNH);
      domain.add(CodigoTipoIF.NTNI);
      domain.add(CodigoTipoIF.NTNL);
      domain.add(CodigoTipoIF.NTNM);
      domain.add(CodigoTipoIF.NTNP);
      domain.add(CodigoTipoIF.NTNR);
      domain.add(CodigoTipoIF.NTNS);
      domain.add(CodigoTipoIF.NTNU);
      domain.add(CodigoTipoIF.CSEC);
      domain.add(CodigoTipoIF.TDA);
      domain.add(CodigoTipoIF.TDE);

      return codigoTipoIF;
   }

   public CodigoSistema obterModulos() {
      CodigoSistema modulos = new CodigoSistema(Contexto.GARANTIAS_SISTEMA);
      modulos.getDomain().clear();
      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA, ""));
      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA, "SNA"));
      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA, "SND"));
      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA, "CETIP21"));
      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA, "SELIC"));
      modulos.getDomain().add(new CodigoSistema(Contexto.GARANTIAS_SISTEMA, "MOP"));

      return modulos;
   }
}
