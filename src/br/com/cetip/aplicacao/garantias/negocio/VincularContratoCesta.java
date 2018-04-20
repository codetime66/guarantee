package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularContrato;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularContratoCesta;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.ContratoCestaGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaIFDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ComplementoContratoDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ParametroPontaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;

class VincularContratoCesta extends VincularCesta implements IVincularContratoCesta {

   public void vincularContrato(ContratoCestaGarantiaDO contrato) {
      CestaGarantiasDO cestaParte = contrato.getCestaParte();
      CestaGarantiasDO cestaContraparte = contrato.getCestaContraparte();

      if (!temCestaEmVinculacao(contrato)) {
         /*
          * Verifica se uma das cestas possui garantias externas
          * Caso positivo, a vinculacao do contrato soh continua
          * quando as garantias externas forem processadas
          * 
          * O servico de retorno vai executar esta classe novamente
          * e as cestas jah estarao EM_VINCULACAO, dando seguimento
          * na vinculacao do contrato, e das garantias
          */
         boolean segueVinculacao = verificaSeSegueVinculacao(cestaParte, cestaContraparte);
         if (!segueVinculacao) {
            return;
         }
      }

      // ASSOCIA PONTAS DO CONTRATO NAS CESTAS
      associarParte(contrato);
      associarContraparte(contrato);

      IVincularContrato ivc = getFactory().getInstanceVincularContrato(
            contrato.getContrato().getTipoIF().getCodigoTipoIF());
      ivc.vincularContrato(contrato);

      if (cestaParte != null) {
         vincularCesta(cestaParte);
      }

      if (cestaContraparte != null) {
         vincularCesta(cestaContraparte);
      }
   }

   private void associarContraparte(ContratoCestaGarantiaDO contratoCesta) {
      ComplementoContratoDO contrato = contratoCesta.getContrato();
      CestaGarantiasDO cestaContraparte = contratoCesta.getCestaContraparte();
      ContaParticipanteDO contraparte = contratoCesta.getContraparte();
      CPFOuCNPJ comitenteContraparte = contratoCesta.getComitenteContraparte();

      associarParametroPonta(contrato, cestaContraparte, contraparte, comitenteContraparte);
   }

   private void associarParte(ContratoCestaGarantiaDO contratoCesta) {
      ComplementoContratoDO contrato = contratoCesta.getContrato();
      CestaGarantiasDO cestaParte = contratoCesta.getCestaParte();
      ContaParticipanteDO parte = contratoCesta.getParte();
      CPFOuCNPJ comitenteParte = contratoCesta.getComitenteParte();

      associarParametroPonta(contrato, cestaParte, parte, comitenteParte);
   }

   private void associarParametroPonta(ComplementoContratoDO contrato, CestaGarantiasDO cesta,
         ContaParticipanteDO conta, CPFOuCNPJ cpfCnpj) {

      IContratosCesta icc = getFactory().getInstanceContratosCesta();
      ParametroPontaDO ponta = icc.obterPonta(contrato, conta, cpfCnpj);

      Booleano possuiCesta = cesta == null ? Booleano.FALSO : Booleano.VERDADEIRO;
      ponta.setPossuiCestaGarantia(possuiCesta);

      if (cesta != null) {
         cesta.setParametroPonta(ponta);
      }
   }

   private boolean temCestaEmVinculacao(ContratoCestaGarantiaDO contrato) {
      CestaGarantiasDO cestaParte = contrato.getCestaParte();
      CestaGarantiasDO cestaContraparte = contrato.getCestaContraparte();

      StatusCestaDO statusParte = cestaParte != null ? cestaParte.getStatusCesta() : null;
      StatusCestaDO statusContraparte = cestaContraparte != null ? cestaContraparte.getStatusCesta() : null;

      return StatusCestaDO.EM_VINCULACAO.equals(statusParte) || StatusCestaDO.EM_VINCULACAO.equals(statusContraparte);
   }

   private boolean verificaSeSegueVinculacao(CestaGarantiasDO cestaParte, CestaGarantiasDO cestaContraparte) {
      boolean segueVinculacaoParte = true;
      if (cestaParte != null) {
         segueVinculacaoParte = verificaELancaControleAtivosExternos(cestaParte);
      }

      boolean segueVinculacaoContraparte = true;
      if (cestaContraparte != null) {
         segueVinculacaoContraparte = verificaELancaControleAtivosExternos(cestaContraparte);
      }

      return segueVinculacaoParte && segueVinculacaoContraparte;
   }

   protected void vincularInstrumentoFinanceiro(MovimentacaoGarantiaDO movVinculacao, CestaGarantiasIFDO vinculo) {
      vinculo.setStatus(StatusCestaIFDO.VINCULADA_AO_ATIVO);
   }

}
