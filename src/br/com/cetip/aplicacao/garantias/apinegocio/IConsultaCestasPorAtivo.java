package br.com.cetip.aplicacao.garantias.apinegocio;

import java.util.List;

import br.com.cetip.infra.atributo.tipo.identificador.Id;

public interface IConsultaCestasPorAtivo {

   public List obterCestasContendoGarantia(Id numIF);

   public List obterCaucaoCestasContendoGarantia(Id numIF);
   
   public List obterCestasVinculacaoImcompletaContendoGarantia(Id numIF);

}
