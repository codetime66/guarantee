package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.ILiberacaoCesta;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

/**
 * <p>
 * Efetua a LIBERACAO de uma cesta vinculada a um instrumento financeiro do tipo SWAP.
 * </p>
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
final class LiberacaoCestaContrato extends LiberacaoCestaBaixaPlataforma {

   /**
    * @see ILiberacaoCesta#liberar(CestaGarantiasDO, Data)
    */
   public void liberar(CestaGarantiasDO cesta, Data data) {
      IContratosCesta ics = getFactory().getInstanceContratosCesta();
      ics.desvinculaPontaCesta(cesta);

      super.liberar(cesta, data);
   }

   public void registrar(TiposLiberacao f) {
      f.registrar(SistemaDO.CETIP21, CodigoTipoIF.SWAP, this);
      f.registrar(SistemaDO.OPCAO, CodigoTipoIF.BOX2, this);
      f.registrar(SistemaDO.OPCAO, CodigoTipoIF.OFVC, this);
      f.registrar(SistemaDO.OPCAO, CodigoTipoIF.OFCC, this);
      f.registrar(SistemaDO.TERMO, CodigoTipoIF.TMO, this);
      f.registrar(SistemaDO.TERMO, CodigoTipoIF.TIN, this);
      f.registrar(SistemaDO.TERMO, CodigoTipoIF.TCO, this);
   }

}
