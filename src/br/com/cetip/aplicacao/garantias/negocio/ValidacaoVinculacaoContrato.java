package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoVinculacaoContrato;
import br.com.cetip.aplicacao.garantias.apinegocio.VinculacaoContratoVO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ParametroPontaDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

abstract class ValidacaoVinculacaoContrato extends ValidacaoVinculacao implements IValidacaoVinculacaoContrato {

   private void validarCesta(CestaGarantiasDO cesta) {
      validarTiposGarantidoresValidos(cesta);
      validaNaturezaGarantido(cesta);
   }

   public final void validar(VinculacaoContratoVO vinculacaoContrato) {
      // Valida se Tipo IF do IF possui IND_GARANTIDO = 'S'
      if (!validaTipoInstrumentoFinanceiroGarantido(vinculacaoContrato.ativo)) {
         throw new Erro(CodigoErro.CESTA_TIPO_IF_INVALIDO);
      }

      validaRegra(vinculacaoContrato);

      validarPontas(vinculacaoContrato);

      // Valida a cesta da parte
      if (vinculacaoContrato.cestaParte != null) {
         validarCesta(vinculacaoContrato.cestaParte);
      }

      // Valida a cesta da contraparte
      if (vinculacaoContrato.cestaContraparte != null) {
         validarCesta(vinculacaoContrato.cestaContraparte);
      }

      validarAtivo(vinculacaoContrato);
   }

   private void validarPontas(VinculacaoContratoVO vo) {
      IContratosCesta icc = getFactory().getInstanceContratosCesta();

      ParametroPontaDO pontaP1 = icc.obterPonta(vo.ativo, vo.contaParte, vo.comitenteParte);
      ParametroPontaDO pontaP2 = icc.obterPonta(vo.ativo, vo.contaContraparte, vo.comitenteContraParte);

      CestaGarantiasDO cestaP1 = icc.obterCestaPorPonta(pontaP1);
      CestaGarantiasDO cestaP2 = icc.obterCestaPorPonta(pontaP2);

      if (cestaP1 != null && vo.cestaParte != null) {
         throw new Erro(CodigoErro.ERRO, "Ponta '" + pontaP1.getContaParticipante().getCodContaParticipante()
               + "' já vinculou uma cesta a este contrato.");
      }

      if (cestaP2 != null && vo.cestaContraparte != null) {
         throw new Erro(CodigoErro.ERRO, "Ponta '" + pontaP2.getContaParticipante().getCodContaParticipante()
               + "' já vinculou uma cesta a este contrato.");
      }

      // Se o contrato está pendente , verifica indicacao de cesta
      if (!vo.ativo.getSituacaoContratoDO().getIdSituacaoContrato().mesmoConteudo(new Id("17")) &&
    	  !vo.ativo.getSituacaoContratoDO().getIdSituacaoContrato().mesmoConteudo(new Id("19")) &&
    	  !vo.ativo.getSituacaoContratoDO().getIdSituacaoContrato().mesmoConteudo(new Id("22") )) {
         boolean exigeCestaParte = pontaP1.getPossuiCestaGarantia().ehVerdadeiro();
         boolean exigeCestaContraParte = pontaP2.getPossuiCestaGarantia().ehVerdadeiro();

         boolean indicouCestaParte = vo.cestaParte != null;
         boolean indicouCestaContraParte = vo.cestaContraparte != null;
         
         if (exigeCestaParte && !indicouCestaParte) {
            throw new Erro(CodigoErro.ERRO, "Contrato exige indicar cesta da parte.");
         } else if (exigeCestaContraParte && !indicouCestaContraParte) {
            throw new Erro(CodigoErro.ERRO, "Contrato exige indicar cesta da contra parte.");
         }
      }
   }

   private void validaRegra(VinculacaoContratoVO vinculacaoContrato) {
      AtributosColunados ac = criaAtributosColunadosRegra(vinculacaoContrato);

      // Avalia entrada de parametros de parte e contra-parte (match de duplo comando)
      try {
         FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeVincularContratoCesta, ac, true);
      } catch (Exception e) {
         Logger.error(this, e);

         if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
         }

         throw new RuntimeException(e);
      }
   }

   private AtributosColunados criaAtributosColunadosRegra(VinculacaoContratoVO vinculacaoContrato) {
      Id idCestaParte = vinculacaoContrato.cestaParte == null ? null : vinculacaoContrato.cestaParte
            .getNumIdCestaGarantias();
      Id idCestaContraparte = vinculacaoContrato.cestaContraparte == null ? null : vinculacaoContrato.cestaContraparte
            .getNumIdCestaGarantias();

      AtributosColunados ac = new AtributosColunados();
      ac.atributo(vinculacaoContrato.contaParte.getCodContaParticipante());
      ac.atributo(vinculacaoContrato.contaContraparte.getCodContaParticipante());
      ac.atributo(idCestaParte);
      ac.atributo(idCestaContraparte);
      ac.atributo(vinculacaoContrato.ativo.getCodigoIF());

      Id statusIdParte = null;
      if (vinculacaoContrato.cestaParte != null) {
         statusIdParte = vinculacaoContrato.cestaParte.getStatusCesta().getNumIdStatusCesta();
      }
      ac.atributo(statusIdParte);

      Id statusIdContraparte = null;
      if (vinculacaoContrato.cestaContraparte != null) {
         statusIdContraparte = vinculacaoContrato.cestaContraparte.getStatusCesta().getNumIdStatusCesta();
      }
      ac.atributo(statusIdContraparte);

      ac.atributo(vinculacaoContrato.regraLiberacao);

      Natureza naturezaParte = null;
      if (!Condicional.vazio(vinculacaoContrato.comitenteParte)) {
         naturezaParte = vinculacaoContrato.comitenteParte.obterNatureza();
      }

      Natureza naturezaContraParte = null;
      if (!Condicional.vazio(vinculacaoContrato.comitenteContraParte)) {
         naturezaContraParte = vinculacaoContrato.comitenteContraParte.obterNatureza();
      }

      ac.atributo(vinculacaoContrato.comitenteParte);
      ac.atributo(naturezaParte);
      ac.atributo(vinculacaoContrato.comitenteContraParte);
      ac.atributo(naturezaContraParte);
      return ac;
   }

   protected abstract void validarAtivo(VinculacaoContratoVO vinculacaoContrato);

}
