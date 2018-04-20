package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.VinculacaoContratoVO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

/**
 * @author brunob
 */
class ValidacaoVinculacaoOpcoes extends ValidacaoVinculacaoContrato {

   public void registrar(TiposValidacaoVinculacaoContrato f) {
      f.registrar(CodigoTipoIF.OFCC, this);
      f.registrar(CodigoTipoIF.OFVC, this);
   }

   protected void validarAtivo(VinculacaoContratoVO vc) {
      boolean bCestaParte = vc.cestaParte != null;

      Integer numIF = Integer.valueOf(vc.ativo.getId().obterConteudo());
      Integer participante = Integer.valueOf(vc.contaParte.getId().obterConteudo());
      Integer contraparte = Integer.valueOf(vc.contaContraparte.getId().obterConteudo());
      Integer ponta = bCestaParte ? participante : contraparte;

      CtpBoxGarantia ctpBox = null;
      try {
         ctpBox = new CtpBoxGarantia();

         boolean podeVincular = ctpBox.podeVincularCesta(numIF, ponta, vc.regraLiberacao.obterConteudo());

         if (podeVincular == false) {
            throw new Erro(CodigoErro.CESTA_VINC_INCOMPATIVEL);
         }
      } finally {
         if (ctpBox != null) {
            ctpBox.remove();
         }
      }
   }
}
