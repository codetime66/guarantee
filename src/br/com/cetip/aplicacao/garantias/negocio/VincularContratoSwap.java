package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularContrato;
import br.com.cetip.aplicacao.garantias.apinegocio.VinculacaoContratoVO;
import br.com.cetip.dados.aplicacao.garantias.ContratoCestaGarantiaDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.roteador.Roteador;

class VincularContratoSwap extends BaseGarantias implements IVincularContrato {

   public void registrar(TiposVinculacaoContrato t) {
      t.registrar(CodigoTipoIF.SWAP, this);
   }

   public final void vincularContrato(ContratoCestaGarantiaDO contrato) {
      VinculacaoContratoVO vc = new VinculacaoContratoVO();
      vc.ativo = contrato.getContrato();
      vc.cestaContraparte = contrato.getCestaContraparte();
      vc.cestaParte = contrato.getCestaParte();
      vc.contaParte = contrato.getParte();
      vc.contaContraparte = contrato.getContraparte();
      vc.comitenteParte = contrato.getComitenteParte();
      vc.comitenteContraParte = contrato.getComitenteContraparte();
      vc.regraLiberacao = contrato.getIndRegraLiberacao();

      IContratosCesta icc = getFactory().getInstanceContratosCesta();
      Requisicao r = icc.construirRequisicaoSwap(vc);
      Roteador.executar(r, getContextoAtivacao());
   }

}
