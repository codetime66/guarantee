package br.com.cetip.aplicacao.garantias.negocio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import br.com.cetip.aplicacao.garantias.apinegocio.IAportarGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IAtualizaStatusCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IBaseGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.ICarteirasTipoDebito;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IConsultaCestasPorAtivo;
import br.com.cetip.aplicacao.garantias.apinegocio.IConsultaGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IContaGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IDeletaCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IEventoSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IExcluirGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IFiltroCestas;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasCDAWA;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasNaoCetip;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.ILiberacaoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.ILiberarCestaParaManutencao;
import br.com.cetip.aplicacao.garantias.apinegocio.ILiberarCestasInadimplentes;
import br.com.cetip.aplicacao.garantias.apinegocio.IMIGAcionador;
import br.com.cetip.aplicacao.garantias.apinegocio.IMIGResultado;
import br.com.cetip.aplicacao.garantias.apinegocio.IMapaAcoes;
import br.com.cetip.aplicacao.garantias.apinegocio.IMensageriaGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IPenhorNoEmissor;
import br.com.cetip.aplicacao.garantias.apinegocio.IRespostaTransferenciaCustodia;
import br.com.cetip.aplicacao.garantias.apinegocio.IRetirarGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IRetirarGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.ITipoGarantiaCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.ITransferirCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidaAcoes;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidaAtivoGarantidor;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoGarantidorNatEcon;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoRetirada;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoRetiradaGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoVinculacaoContrato;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoVinculacaoTitulo;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidarTipoIF;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularContrato;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularContratoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularTitulo;
import br.com.cetip.aplicacao.garantias.apinegocio.cobranca.IConsultaCobrancaAtivosGarantidos;
import br.com.cetip.aplicacao.garantias.apinegocio.colateral.IAutorizacaoPublicGarantias;
import br.com.cetip.aplicacao.garantias.negocio.acoes.IValidarAcao;
import br.com.cetip.aplicacao.garantias.negocio.acoes.ValidadoresFactory;
import br.com.cetip.aplicacao.garantias.negocio.cobranca.ConsultaAtivosGarantidos;
import br.com.cetip.aplicacao.garantias.negocio.cobranca.ConsultaContratosGarantidos;
import br.com.cetip.aplicacao.garantias.negocio.colateral.AutorizacaoPublicGarantias;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.SituacaoOperacaoSelic;
import br.com.cetip.infra.log.Logger;

public final class Garantias implements IGarantias {

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstance()
    */
   public ICestaDeGarantias getInstanceCestaDeGarantias() {
      return (ICestaDeGarantias) getInstance(CestaDeGarantias.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getConsultaGarantias()
    */
   public IConsultaGarantias getConsultaGarantias() {
      return (IConsultaGarantias) getInstance(ConsultaGarantias.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceAtualizaStatusCesta()
    */
   public IAtualizaStatusCesta getInstanceAtualizaStatusCesta() {
      return (IAtualizaStatusCesta) getInstance(AtualizaStatusCesta.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceLiberacaoCesta(br.com.cetip.infra.atributo.tipo.identificador.Id, br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF)
    */
   public ILiberacaoCesta getInstanceLiberacaoCesta(Id idSistema, CodigoTipoIF tipoIF) {
      TiposLiberacao f = (TiposLiberacao) getInstance(TiposLiberacao.class);

      ILiberacaoCesta o = f.obter(idSistema, tipoIF);

      if (o instanceof IBaseGarantias) {
         injeta((IBaseGarantias) o);
      }

      return o;
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getGarantiasCDAWA()
    */
   public IGarantiasCDAWA getGarantiasCDAWA() {
      return (IGarantiasCDAWA) getInstance(GarantiasCDAWA.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceContratoSwap()
    */
   public IContratosCesta getInstanceContratosCesta() {
      return (IContratosCesta) getInstance(ContratosCesta.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceContaGarantia()
    */
   public IContaGarantia getInstanceContaGarantia() {
      return (IContaGarantia) getInstance(ContaGarantia.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstancePenhorNoEmissor()
    */
   public IPenhorNoEmissor getInstancePenhorNoEmissor() {
      return (IPenhorNoEmissor) getInstance(PenhorNoEmissor.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceExcluirGarantia(br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF)
    */
   public IExcluirGarantia getInstanceExcluirGarantia(CodigoTipoIF tipoIF) {
      TiposExclusaoGarantia f = (TiposExclusaoGarantia) getInstance(TiposExclusaoGarantia.class);

      IExcluirGarantia o = f.obter(tipoIF);

      if (o instanceof IBaseGarantias) {
         injeta((IBaseGarantias) o);
      }

      return o;

   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceValidacaoRetirada(br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF)
    */
   public IValidacaoRetirada getInstanceValidacaoRetirada(CodigoTipoIF tipoIF) {
      TiposValidacaoRetirada f = (TiposValidacaoRetirada) getInstance(TiposValidacaoRetirada.class);

      IValidacaoRetirada o = f.obter(tipoIF);

      if (o instanceof IBaseGarantias) {
         injeta((IBaseGarantias) o);
      }

      return o;
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceMovimentacoesGarantias()
    */
   public IMovimentacoesGarantias getInstanceMovimentacoesGarantias() {
      return (IMovimentacoesGarantias) getInstance(MovimentacoesGarantias.class);
   }

   public IMapaAcoes getInstanceMapaAcoes() {
      return (IMapaAcoes) getInstance(MapaAcoes.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceValidaAcoes()
    */
   public IValidaAcoes getInstanceValidaAcoes() {
      ValidaAcoes mapa = new ValidaAcoes();
      injeta(mapa);
      return mapa;
   }

   protected void injeta(IBaseGarantias base) {
      base.setGarantias(this);
      base.inicializar();
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceDeletaCesta(br.com.cetip.dados.aplicacao.garantias.StatusCestaDO)
    */
   public IDeletaCesta getInstanceDeletaCesta(StatusCestaDO status) {
      TiposDelecaoCesta f = (TiposDelecaoCesta) getInstance(TiposDelecaoCesta.class);

      IDeletaCesta o = f.obter(status);

      if (o instanceof IBaseGarantias) {
         injeta((IBaseGarantias) o);
      }

      return o;
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceValidacaoVinculacao(br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF)
    */
   public IValidacaoVinculacaoTitulo getInstanceValidacaoVinculacao(CodigoTipoIF tipo) {
      TiposValidacaoVinculacaoTitulo f = (TiposValidacaoVinculacaoTitulo) getInstance(TiposValidacaoVinculacaoTitulo.class);
      IValidacaoVinculacaoTitulo o = f.obter(tipo);

      if (o instanceof IBaseGarantias) {
         injeta((IBaseGarantias) o);
      }

      return o;
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceConsultaCestasPorAtivo()
    */
   public IConsultaCestasPorAtivo getInstanceConsultaCestasPorAtivo() {
      return (IConsultaCestasPorAtivo) getInstance(ConsultaCestasPorAtivo.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceGarantidoCesta()
    */
   public IGarantidoCesta getInstanceGarantidoCesta() {
      return (IGarantidoCesta) getInstance(GarantidoCesta.class);
   }

   public int getPrazo() {
      return prazo;
   }

   private static int prazo = 2;

   private static int inadimplencia = 9999;

   private Map instances = new HashMap();

   public int getInadimplencia() {
      return inadimplencia;
   }

   static {
      Properties properties = new Properties();
      try {
         URL urlStream = Garantias.class.getResource("/garantias.properties");
         if (urlStream != null) {
            File file = new File(urlStream.getFile());
            if (file.exists()) {
               FileInputStream stream = new FileInputStream(file);
               properties.load(stream);
               stream.close();
            }
         } else {
            Logger.warn(Garantias.class.getName(), "Arquivo 'garantias.properties' NAO ENCONTRADO!");
         }
      } catch (IOException e) {
         Logger.warn(Garantias.class.getName(), "Problema ao abrir arquivo 'garantias.properties'.", e);
      }

      if (properties.isEmpty() == false) {
         String sPrazo = properties.getProperty("prazo");
         prazo = Integer.parseInt(sPrazo);
         String sInadimplencia = properties.getProperty("inadimplencia");
         inadimplencia = Integer.parseInt(sInadimplencia);
      } else {
         Logger.warn(Garantias.class.getName(), "Valores default serao utilizados. [prazo=" + prazo
               + ", inadimplencia=" + inadimplencia + "]");
      }
   }

   private Object getInstance(Class clazz) {
      if (instances.containsKey(clazz)) {
         return instances.get(clazz);
      }

      Object instance = null;
      try {
         instance = clazz.newInstance();
      } catch (Exception e) {
         e.printStackTrace();
         throw new Erro(CodigoErro.ERRO, e.getMessage());
      }

      if (instance instanceof IBaseGarantias) {
         IBaseGarantias base = (IBaseGarantias) instance;
         injeta(base);
      }

      instances.put(clazz, instance);

      return instance;
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceLiberarCestaParaManutencao()
    */
   public ILiberarCestaParaManutencao getInstanceLiberarCestaParaManutencao() {
      return (ILiberarCestaParaManutencao) getInstance(LiberarCestaParaManutencao.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceAportarGarantia()
    */
   public IAportarGarantia getInstanceAportarGarantia() {
      return (IAportarGarantia) getInstance(AportarGarantia.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceAportarGarantia()
    */
   public IAportarGarantia getInstanceAportarGarantia(SistemaDO sistema) {
      if (sistema != null && sistema.getNumero().mesmoConteudo(SistemaDO.SELIC)) {
         return (IAportarGarantia) getInstance(AportarGarantiaSelic.class);
      }

      return getInstanceAportarGarantia();
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceLiberarCestasInadimplentes()
    */
   public ILiberarCestasInadimplentes getInstanceLiberarCestasInadimplentes() {
      return (ILiberarCestasInadimplentes) getInstance(LiberarCestasInadimplentes.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceValidarTipoIF()
    */
   public IValidarTipoIF getInstanceValidarTipoIF() {
      return (IValidarTipoIF) getInstance(ValidarTipoIF.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceFiltroCestas()
    */
   public IFiltroCestas getInstanceFiltroCestas() {
      return (IFiltroCestas) getInstance(FiltroCestas.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceRetirarGarantias()
    */
   public IRetirarGarantias getInstanceRetirarGarantias() {
      return (IRetirarGarantias) getInstance(RetirarGarantias.class);
   }

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.IGarantiasFactory#getInstanceValidarAcao(br.com.cetip.infra.atributo.tipo.identificador.Funcao)
    */
   public IValidarAcao getInstanceValidarAcao(Funcao acao) {
      ValidadoresFactory factory = (ValidadoresFactory) getInstance(ValidadoresFactory.class);
      IValidarAcao instance = factory.getInstance(acao);

      if (instance != null && instance instanceof IBaseGarantias) {
         injeta((IBaseGarantias) instance);
      }

      return instance;
   }

   public ITipoGarantiaCesta getInstanceTipoGarantiaCesta() {
      return (ITipoGarantiaCesta) getInstance(TipoGarantiaCesta.class);
   }

   public IGarantiasSelic getInstanceGarantiasSelic() {
      return (IGarantiasSelic) getInstance(GarantiasSelic.class);
   }

   public IValidacaoGarantidorNatEcon getInstanceValidacaoGarantidorNatEcon(CodigoTipoIF tipoIF) {
      TiposValidacaoGarantidoresNatEcon f = (TiposValidacaoGarantidoresNatEcon) getInstance(TiposValidacaoGarantidoresNatEcon.class);
      IValidacaoGarantidorNatEcon o = f.obter(tipoIF);

      if (o instanceof IBaseGarantias) {
         injeta((IBaseGarantias) o);
      }

      return o;
   }

   public IVincularTitulo getInstanceVincularAtivo(Id sistema) {
      TiposVinculacaoTitulo t = (TiposVinculacaoTitulo) getInstance(TiposVinculacaoTitulo.class);
      IVincularTitulo ivt = t.obter(sistema);

      if (ivt != null && ivt instanceof IBaseGarantias) {
         injeta((IBaseGarantias) ivt);
      }

      return ivt;
   }

   public IVincularContrato getInstanceVincularContrato(CodigoTipoIF tipoIF) {
      TiposVinculacaoContrato t = (TiposVinculacaoContrato) getInstance(TiposVinculacaoContrato.class);
      IVincularContrato i = t.obter(tipoIF);

      if (i != null && i instanceof IBaseGarantias) {
         injeta((IBaseGarantias) i);
      }

      return i;
   }

   public IVincularContratoCesta getInstanceVincularContratoCesta() {
      return (IVincularContratoCesta) getInstance(VincularContratoCesta.class);
   }

   public IVincularCesta getInstanceVincularCesta() {
      return (IVincularCesta) getInstance(VincularCesta.class);
   }

   public ITransferirCesta getInstanceTransferirCesta() {
      return (ITransferirCesta) getInstance(TransferirCesta.class);
   }

   public ICarteirasTipoDebito getInstanceCarteirasTipoDebito() {
      return (ICarteirasTipoDebito) getInstance(MIGOperacao.class);
   }

   public IRetirarGarantia getInstanceRetirarGarantia(Id idSistema) {
      TiposRetiradaGarantia t = (TiposRetiradaGarantia) getInstance(TiposRetiradaGarantia.class);
      IRetirarGarantia instancia = t.obter(idSistema);

      if (instancia != null && instancia instanceof IBaseGarantias) {
         injeta((IBaseGarantias) instancia);
      }

      return instancia;
   }

   public IMensageriaGarantiasSelic getInstanceMensageriaSelic() {
      return (IMensageriaGarantiasSelic) getInstance(MensageriaGarantiasSelic.class);
   }

   public IValidacaoVinculacaoContrato getInstanceValidacaoVinculacaoContrato(CodigoTipoIF tipoIF) {
      TiposValidacaoVinculacaoContrato t = (TiposValidacaoVinculacaoContrato) getInstance(TiposValidacaoVinculacaoContrato.class);
      IValidacaoVinculacaoContrato instancia = t.obter(tipoIF);

      if (instancia != null && instancia instanceof IBaseGarantias) {
         injeta((IBaseGarantias) instancia);
      }

      return instancia;
   }

   public IValidacaoRetiradaGarantia getInstanceValidacaoRetiradaGarantia(Id idSistema) {
      if (SistemaDO.SELIC.mesmoConteudo(idSistema)) {
         return (IValidacaoRetiradaGarantia) getInstance(ValidacaoRetiradaGarantiaSelic.class);
      }
      return (IValidacaoRetiradaGarantia) getInstance(ValidacaoRetiradaGarantiaCetip21.class);
   }

   public IConsultaCobrancaAtivosGarantidos getInstanceConsultaCobrancaContratos() {
      return (IConsultaCobrancaAtivosGarantidos) getInstance(ConsultaAtivosGarantidos.class);
   }

   public IConsultaCobrancaAtivosGarantidos getInstanceConsultaCobrancaTitulos() {
      return (IConsultaCobrancaAtivosGarantidos) getInstance(ConsultaContratosGarantidos.class);
   }

   public IMIGAcionador getInstanceMIGAcionador() {
      return (IMIGAcionador) getInstance(MIGAcionador.class);
   }

   public IMIGResultado getInstanceMIGResultado() {
      return (IMIGResultado) getInstance(MIGResultado.class);
   }

   public IValidaAtivoGarantidor getInstanceValidaAtivoGarantidor(CodigoTipoIF codTipoIF) {
      if (codTipoIF.ehContrato()) {
         return (IValidaAtivoGarantidor) getInstanceValidacaoVinculacaoContrato(codTipoIF);
      }
      return (IValidaAtivoGarantidor) getInstanceValidacaoVinculacao(codTipoIF);
   }

   public IEventoSelic getInstanceEventoSelic(CodigoTipoOperacaoSelic tipoOperacao) {
      TiposEventoSelic f = (TiposEventoSelic) getInstance(TiposEventoSelic.class);

      IEventoSelic o = f.obter(tipoOperacao);

      if (o instanceof IBaseGarantias) {
         injeta((IBaseGarantias) o);
      }

      return o;
   }

   public IGarantiasNaoCetip getGarantiasNaoCetip() {
      return (IGarantiasNaoCetip) getInstance(GarantiasNaoCetip.class);
   }

   public IAutorizacaoPublicGarantias getInstanceAutorizacaoPublicGarantias() {
      return (IAutorizacaoPublicGarantias) getInstance(AutorizacaoPublicGarantias.class);
   }
   
   public IRespostaTransferenciaCustodia getInstanceRespostaTransferenciaCustodia(SituacaoOperacaoSelic situacao) {
      TiposRespostaTransferenciaCustodia t = (TiposRespostaTransferenciaCustodia) getInstance(TiposRespostaTransferenciaCustodia.class);
      IRespostaTransferenciaCustodia instancia = t.obter(situacao);

      if (instancia != null && instancia instanceof IBaseGarantias) {
         injeta((IBaseGarantias) instancia);
      }
      return instancia;
   }
}
