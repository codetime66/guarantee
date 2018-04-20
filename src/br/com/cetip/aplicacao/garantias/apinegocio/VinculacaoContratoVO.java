package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ComplementoContratoDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.texto.Texto;

public class VinculacaoContratoVO {

   public ComplementoContratoDO ativo;
   public ContaParticipanteDO contaParte;
   public ContaParticipanteDO contaContraparte;
   public CestaGarantiasDO cestaParte;
   public CestaGarantiasDO cestaContraparte;
   public CPFOuCNPJ comitenteParte;
   public CPFOuCNPJ comitenteContraParte;
   public Texto regraLiberacao;

}
