package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;

public interface IContaGarantia {

   public ContaParticipanteDO obterConta60(CestaGarantiasDO cesta);

   public ContaParticipanteDO obterConta60(ContaParticipanteDO contaParticipante);

   public ContaParticipanteDO obterConta60(CodigoContaCetip contaCetip);

   public boolean possuiConta60(CodigoContaCetip garantido);

   public ContaParticipanteDO obterConta60(CodigoContaSelic contaSelic);

}
