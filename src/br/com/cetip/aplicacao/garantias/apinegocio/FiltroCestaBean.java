package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

public class FiltroCestaBean {

   public NumeroCestaGarantia numero;
   public CodigoContaCetip garantidor;
   public CodigoContaCetip garantido;
   public Id status;
   public Booleano somenteComAtivoInadimplente;
   public Booleano somenteComEmissorInadimplente;
   public int tipoAcesso;
   public Texto reset;
   public CodigoTipoIF tipoIF;

}
