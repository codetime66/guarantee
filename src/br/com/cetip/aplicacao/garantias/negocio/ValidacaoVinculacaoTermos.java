package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.VinculacaoContratoVO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

/**
 * @author brunob
 */
class ValidacaoVinculacaoTermos extends ValidacaoVinculacaoContrato {

   public void registrar(TiposValidacaoVinculacaoContrato f) {
      f.registrar(CodigoTipoIF.TMO, this);
      f.registrar(CodigoTipoIF.TCO, this);
      f.registrar(CodigoTipoIF.TIN, this);
   }

   protected void validarAtivo(VinculacaoContratoVO vc) {
      Integer numIF = Integer.valueOf(vc.ativo.getId().obterConteudo());

      // Parte
      Integer participante = Integer.valueOf(vc.contaParte.getId().obterConteudo());

      String cestaParte = null;
      if (vc.cestaParte != null) {
         cestaParte = vc.cestaParte.getNumIdCestaGarantias().obterConteudo();
      }
      String comitenteParte = null;
      if (vc.comitenteParte != null) {
         vc.comitenteParte.obterConteudo();
      }

      // Contra Parte
      Integer contraparte = Integer.valueOf(vc.contaContraparte.getId().obterConteudo());

      String cestaContraParte = null;
      if (vc.cestaContraparte != null) {
         cestaContraParte = vc.cestaContraparte.getNumIdCestaGarantias().obterConteudo();
      }

      String comitenteContraParte = null;
      if (vc.comitenteContraParte != null) {
         comitenteContraParte = vc.comitenteContraParte.obterConteudo();
      }

      String regra = vc.regraLiberacao.obterConteudo();

      CtpTermoGarantia ctpTermo = null;
      try {
         ctpTermo = new CtpTermoGarantia();
         ctpTermo.podeVincularCesta(numIF, participante, comitenteParte, cestaParte, contraparte, comitenteContraParte,
               cestaContraParte, regra);
      } finally {
         if (ctpTermo != null) {
            ctpTermo.remove();
         }
      }
   }

}
