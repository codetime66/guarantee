package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IVincularContrato;
import br.com.cetip.dados.aplicacao.garantias.ContratoCestaGarantiaDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;

class VincularContratoOpcao extends BaseGarantias implements IVincularContrato {

   public void registrar(TiposVinculacaoContrato t) {
      t.registrar(CodigoTipoIF.OFCC, this);
      t.registrar(CodigoTipoIF.OFVC, this);
   }

   public final void vincularContrato(ContratoCestaGarantiaDO contrato) {
      CtpBoxGarantia ctpBox = new CtpBoxGarantia();

      try {
         boolean bCestaParte = contrato.getCestaParte() != null;

         Integer numIF = Integer.valueOf(contrato.getContrato().getId().obterConteudo());
         Integer participante = Integer.valueOf(contrato.getParte().getId().obterConteudo());
         Integer contraparte = Integer.valueOf(contrato.getContraparte().getId().obterConteudo());
         Integer ponta = bCestaParte ? participante : contraparte;

         ctpBox.vinculacaoCestaGarantia(numIF, ponta, contrato.getIndRegraLiberacao().obterConteudo());
      } finally {
         ctpBox.remove();
      }
   }

}
