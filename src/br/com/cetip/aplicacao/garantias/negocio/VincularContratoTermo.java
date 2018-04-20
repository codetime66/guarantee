package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IVincularContrato;
import br.com.cetip.dados.aplicacao.garantias.ContratoCestaGarantiaDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

class VincularContratoTermo extends BaseGarantias implements IVincularContrato {

   public void registrar(TiposVinculacaoContrato t) {
      t.registrar(CodigoTipoIF.TCO, this);
      t.registrar(CodigoTipoIF.TIN, this);
      t.registrar(CodigoTipoIF.TMO, this);
   }

   public void vincularContrato(ContratoCestaGarantiaDO contrato) {
      CtpTermoGarantia termo = new CtpTermoGarantia();

      // Contrato
      Integer numIF = new Integer(contrato.getContrato().getId().obterConteudo());

      // Parte
      Integer numContaParte = new Integer(contrato.getParte().getId().obterConteudo());

      String cestaParte = null;
      if (contrato.getCestaParte() != null) {
         cestaParte = contrato.getCestaParte().getNumIdCestaGarantias().obterConteudo();
      }

      // Comitente Parte
      String cpfCnpjComitenteParte = null;
      if (contrato.getComitenteParte() != null) {
         cpfCnpjComitenteParte = contrato.getComitenteParte().obterConteudo();
      }

      // Contra Parte
      Integer numContaContraparte = new Integer(contrato.getContraparte().getId().obterConteudo());

      String cestaContraParte = null;
      if (contrato.getCestaContraparte() != null) {
         cestaContraParte = contrato.getCestaContraparte().getNumIdCestaGarantias().obterConteudo();
      }

      // Comitente ContraParte
      String cpfCnpjComitenteContraparte = null;
      if (contrato.getComitenteContraparte() != null) {
         cpfCnpjComitenteContraparte = contrato.getComitenteContraparte().obterConteudo();
      }

      String regraLiberacao = contrato.getIndRegraLiberacao().obterConteudo();

      termo.vinculacaoCestaGarantia(numIF, numContaParte, cpfCnpjComitenteParte, cestaParte, numContaContraparte,
            cpfCnpjComitenteContraparte, cestaContraParte, regraLiberacao);
   }
}
