package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.ContratoCestaGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ComplementoContratoDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ParametroPontaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.servico.interfaces.Requisicao;

public interface IContratosCesta {

   public static final Funcao AJUSTES_EVENTO_VENC = new Funcao(Contexto.RESET, "AE");

   public static final Funcao AJUSTES_VENCIMENTO = new Funcao(Contexto.RESET, "AV");

   public static final Funcao EVENTOS_VENCIMENTO = new Funcao(Contexto.RESET, "EV");

   public static final Funcao VENCIMENTO = new Funcao(Contexto.RESET, "VE");

   public static final Funcao EXERCICIO = new Funcao(Contexto.RESET, "EX");

   public ContratoCestaGarantiaDO obterVinculoContrato(CestaGarantiasDO cesta);

   public ContratoCestaGarantiaDO obterVinculoContrato(CodigoIF contrato);

   public void desvinculaPontaCesta(CestaGarantiasDO cesta);

   public CestaGarantiasDO obterCestaPorPonta(Id idParametroPonta);

   public ContratoCestaGarantiaDO obterVinculoContrato(ComplementoContratoDO contrato);

   public CestaGarantiasDO obterCestaPorPonta(ParametroPontaDO ponta);

   public ParametroPontaDO[] obterPontas(ComplementoContratoDO contrato);

   public ParametroPontaDO obterPonta(ComplementoContratoDO contrato, ContaParticipanteDO conta, CPFOuCNPJ cpfCnpj);

   public ComplementoContratoDO obterContrato(CodigoIF codigoIF);

   public Requisicao construirRequisicaoSwap(VinculacaoContratoVO vc);

   public CestaGarantiasDO obterCestaVinculadaPonta(Id idPonta);

}
