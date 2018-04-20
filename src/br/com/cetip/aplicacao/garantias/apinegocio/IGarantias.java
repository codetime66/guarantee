package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.aplicacao.garantias.apinegocio.cobranca.IConsultaCobrancaAtivosGarantidos;
import br.com.cetip.aplicacao.garantias.negocio.acoes.IValidarAcao;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.SituacaoOperacaoSelic;

public interface IGarantias {

   public ICestaDeGarantias getInstanceCestaDeGarantias();

   public IConsultaGarantias getConsultaGarantias();

   public IAtualizaStatusCesta getInstanceAtualizaStatusCesta();

   public ILiberacaoCesta getInstanceLiberacaoCesta(Id idSistema, CodigoTipoIF tipoIF);

   public IGarantiasCDAWA getGarantiasCDAWA();

   public IContratosCesta getInstanceContratosCesta();

   public IContaGarantia getInstanceContaGarantia();

   public IPenhorNoEmissor getInstancePenhorNoEmissor();

   public IExcluirGarantia getInstanceExcluirGarantia(CodigoTipoIF tipoIF);

   public IValidacaoRetirada getInstanceValidacaoRetirada(CodigoTipoIF tipoIF);

   public IMovimentacoesGarantias getInstanceMovimentacoesGarantias();

   public IValidaAcoes getInstanceValidaAcoes();

   public IDeletaCesta getInstanceDeletaCesta(StatusCestaDO status);

   public IValidacaoVinculacaoTitulo getInstanceValidacaoVinculacao(CodigoTipoIF tipo);

   public IConsultaCestasPorAtivo getInstanceConsultaCestasPorAtivo();

   public IGarantidoCesta getInstanceGarantidoCesta();

   public ILiberarCestaParaManutencao getInstanceLiberarCestaParaManutencao();

   public IAportarGarantia getInstanceAportarGarantia();

   public IAportarGarantia getInstanceAportarGarantia(SistemaDO sistema);

   public ILiberarCestasInadimplentes getInstanceLiberarCestasInadimplentes();

   public IValidarTipoIF getInstanceValidarTipoIF();

   public IFiltroCestas getInstanceFiltroCestas();

   public IRetirarGarantias getInstanceRetirarGarantias();

   public IRetirarGarantia getInstanceRetirarGarantia(Id idSistema);

   public IValidarAcao getInstanceValidarAcao(Funcao acao);

   public IMapaAcoes getInstanceMapaAcoes();

   public int getInadimplencia();

   public int getPrazo();

   public ITipoGarantiaCesta getInstanceTipoGarantiaCesta();

   public IGarantiasSelic getInstanceGarantiasSelic();

   public IValidacaoGarantidorNatEcon getInstanceValidacaoGarantidorNatEcon(CodigoTipoIF tipoIF);

   public IVincularTitulo getInstanceVincularAtivo(Id sistema);

   public IVincularContrato getInstanceVincularContrato(CodigoTipoIF tipoIF);

   public IVincularContratoCesta getInstanceVincularContratoCesta();

   public IVincularCesta getInstanceVincularCesta();

   public ITransferirCesta getInstanceTransferirCesta();

   public ICarteirasTipoDebito getInstanceCarteirasTipoDebito();

   public IMensageriaGarantiasSelic getInstanceMensageriaSelic();

   public IValidacaoVinculacaoContrato getInstanceValidacaoVinculacaoContrato(CodigoTipoIF tipoIF);

   public IValidacaoRetiradaGarantia getInstanceValidacaoRetiradaGarantia(Id idSistema);

   public IMIGAcionador getInstanceMIGAcionador();

   public IMIGResultado getInstanceMIGResultado();

   public IConsultaCobrancaAtivosGarantidos getInstanceConsultaCobrancaTitulos();

   public IConsultaCobrancaAtivosGarantidos getInstanceConsultaCobrancaContratos();

   public IValidaAtivoGarantidor getInstanceValidaAtivoGarantidor(CodigoTipoIF tipoIF);

   public IEventoSelic getInstanceEventoSelic(CodigoTipoOperacaoSelic tipoOperacao);

   public IGarantiasNaoCetip getGarantiasNaoCetip();
   
   public IRespostaTransferenciaCustodia getInstanceRespostaTransferenciaCustodia(SituacaoOperacaoSelic situacao);
}
