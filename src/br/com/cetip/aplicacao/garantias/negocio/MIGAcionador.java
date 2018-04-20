package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.com.cetip.aplicacao.administracao.apinegocio.ControleOperacionalFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.GarantiasFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.IBaseGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMIGAcionador;
import br.com.cetip.aplicacao.garantias.apinegocio.IMIGResultado;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.colateral.AutorizacaoPublicGarantiasFactory;
import br.com.cetip.aplicacao.garantias.negocio.Operacao895.Operacao895Parcial;
import br.com.cetip.aplicacao.garantias.negocio.Operacao895.Operacao895PenhorEmissor;
import br.com.cetip.aplicacao.garantias.negocio.OperacaoCestaDeCesta.Operacao990;
import br.com.cetip.aplicacao.garantias.negocio.OperacaoCestaDeCesta.Operacao991;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoExcluirCestaGarantias;
import br.com.cetip.dados.aplicacao.garantias.AutorizacaoPublicGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.SituacaoAutorizPublicGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.GerenciadorPersistenciaFactory;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.persistencia.NivelLock;
import br.com.cetip.infra.servico.contexto.ContextoAtivacao;
import br.com.cetip.infra.servico.roteador.Roteador;

public class MIGAcionador implements IMIGAcionador, IBaseGarantias {

   class HashMapPorId extends HashMap {

      public Object get(Object key) {
         return super.get(((Id) key).obterConteudo());
      }

      public Object put(Object key, Object value) {
         return super.put(((Id) key).obterConteudo(), value);
      }
   }

   private void mapearClasses() {
      classesOperacoes.put(IdTipoMovimentacaoGarantia.BLOQUEIO, Operacao889.class);
      classesOperacoes.put(IdTipoMovimentacaoGarantia.DESBLOQUEIO, Operacao890.class);
      classesOperacoes.put(IdTipoMovimentacaoGarantia.APORTE, Operacao893.class);
      classesOperacoes.put(IdTipoMovimentacaoGarantia.VINCULACAO, Operacao891.class);
      classesOperacoes.put(IdTipoMovimentacaoGarantia.RETIRADA, Operacao894.class);
      classesOperacoes.put(IdTipoMovimentacaoGarantia.TRANSFERENCIA, Operacao892.class);
      classesOperacoes.put(IdTipoMovimentacaoGarantia.LIBERACAO, Operacao895.class);
      classesOperacoes.put(IdTipoMovimentacaoGarantia.LIBERACAO_PARCIAL, Operacao895Parcial.class);
      classesOperacoes.put(IdTipoMovimentacaoGarantia.BLOQUEIO_EM_LASTRO, Operacao990.class);
      classesOperacoes.put(IdTipoMovimentacaoGarantia.RETIRADA_EM_LASTRO, Operacao991.class);
      classesOperacoes.put(IdTipoMovimentacaoGarantia.LIBERACAO_PENHOR_EMISSOR, Operacao895PenhorEmissor.class);
   }

   private Data dataOperacao;

   private IGarantias garantias;

   private Map classesOperacoes;

   public MIGAcionador() {
      classesOperacoes = new HashMapPorId();
      mapearClasses();
   }

   public void acionarOperacao(MovimentacaoGarantiaDO movimentacao, Booleano indBatch) {
      acionarOperacao(movimentacao, indBatch, false);
   }

   /**
    * 
    * @param movimentacao
    * @param indBatchParam
    * @param criaOperacaoFinalizada
    */
   private void acionarOperacao(MovimentacaoGarantiaDO movimentacao, Booleano indBatchParam,
         boolean criaOperacaoFinalizada) {

      Booleano indBatch = indBatchParam;
      if (indBatch == null) {
         indBatch = Booleano.FALSO;
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Inicio MIGAcionador");
      }

      Id codigoSistema = movimentacao.getInstrumentoFinanceiro().getSistema().getNumero();
      boolean ehPlataformaBaixa = codigoSistema.mesmoConteudo(SistemaDO.CETIP21)
            || codigoSistema.mesmoConteudo(SistemaDO.SELIC);

      // Recupera uma instância de um tratador para cada tipo de Operação
      MIGOperacao migOperacao = obterClasseMIGOperacao(movimentacao.getTipoMovimentacaoGarantia());

      IdStatusMovimentacaoGarantia migResultado;
      if (migOperacao == null) {
         migResultado = IdStatusMovimentacaoGarantia.ID_TIPO_MOVIMENTACAO_INVALIDO;
         Logger.error(this, "MIGAcionador -> Tipo de Movimentacao Invaldo: "
               + movimentacao.getTipoMovimentacaoGarantia());
      } else {
         // Executa a Operacao
         migResultado = executar(migOperacao, movimentacao, ehPlataformaBaixa, criaOperacaoFinalizada, indBatch
               .ehVerdadeiro());
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "MIGAcionador -> retornoResultado -> " + migResultado);
      }

      if (!criaOperacaoFinalizada) {
         IMIGResultado imigr = garantias.getInstanceMIGResultado();
         imigr.resultadoOperacao(movimentacao, migResultado, null);
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "FIM MIGAcionador");
      }
   }

   /**
    * 
    * @param operacao
    * @param movimentacao
    * @param b 
    * @param criaOperacaoFinalizada 
    * @param ehPlataformaBaixa 
    * @return
    */
   private IdStatusMovimentacaoGarantia executar(MIGOperacao operacao, MovimentacaoGarantiaDO movimentacao,
         boolean ehPlataformaBaixa, boolean criaOperacaoFinalizada, boolean batch) {
      operacao.setIndPlataformaBaixa(ehPlataformaBaixa);
      operacao.setIndBatch(batch);
      operacao.setGarantias(garantias);
      operacao.setDataOperacao(dataOperacao);

      if (operacao instanceof MIGOperacaoUnitaria) {
         ((MIGOperacaoUnitaria) operacao).setCriaOperacaoFinalizada(criaOperacaoFinalizada);
      }

      operacao.inicializar(movimentacao);

      // Valida a Operacao
      IdStatusMovimentacaoGarantia migResultado = null;

      if (operacao instanceof MIGOperacaoUnitaria && ((MIGOperacaoUnitaria) operacao).isOperacaoFinalizada()) {
         migResultado = IdStatusMovimentacaoGarantia.OK;
      } else {
         migResultado = operacao.validar();
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "MIGAcionador -> validar() -> " + migResultado);
      }

      if (migResultado.mesmoConteudo(IdStatusMovimentacaoGarantia.OK)) {
         migResultado = operacao.criarOperacao();
      }

      return migResultado;
   }

   /**
    * 
    * @param idTipoMovimentacaoGarantia
    * @param indPlataformaBaixa
    * @param criaOperacaoFinalizada
    * @param indBatch
    * @return
    */
   private MIGOperacao obterClasseMIGOperacao(TipoMovimentacaoGarantiaDO tipoMov) {
      Class operacaoClass = (Class) classesOperacoes.get(tipoMov.getNumIdTipoMovGarantia());
      if (operacaoClass == null) {
         return null;
      }

      MIGOperacao operacao = null;
      try {
         operacao = (MIGOperacao) operacaoClass.newInstance();
      } catch (InstantiationException e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, e.getMessage());
      } catch (IllegalAccessException e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, e.getMessage());
      }

      return operacao;
   }

   public void acionarInadimplencia(CestaGarantiasDO cesta) {
      Data data;
      try {
         data = ControleOperacionalFactory.getInstance().obterD0();
      } catch (Exception e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, e.getMessage());
      }

      cesta.setStatusCesta(StatusCestaDO.INADIMPLENTE, data);
      cesta.setIndInadimplencia(new Texto("S"));
      cesta.setDatInadimplencia(data);

      // Marca inadimplencia em todos os ativos vinculados
      Iterator i = cesta.getAtivosVinculados().iterator();
      while (i.hasNext()) {
         CestaGarantiasIFDO cestaIF = (CestaGarantiasIFDO) i.next();
         InstrumentoFinanceiroDO ativoVinculado = cestaIF.getInstrumentoFinanceiro();
         ativoVinculado.setIndInadimplencia(new Booleano(Booleano.VERDADEIRO));
      }
   }

   public void acionarDesbloqueioFinalizado(MovimentacaoGarantiaDO movimentacao) {
      if (!movimentacao.getTipoMovimentacaoGarantia().equals(TipoMovimentacaoGarantiaDO.DESBLOQUEIO)) {
         throw new IllegalArgumentException("Chamada somente para movimentacoes de desbloqueio");
      }

      acionarOperacaoFinalizada(movimentacao);
   }

   private void acionarOperacaoFinalizada(MovimentacaoGarantiaDO movimentacao) {
      acionarOperacao(movimentacao, Booleano.VERDADEIRO, true);
   }

   public void acionarRetiradaFinalizada(MovimentacaoGarantiaDO movimentacao) {
      if (!movimentacao.getTipoMovimentacaoGarantia().equals(TipoMovimentacaoGarantiaDO.RETIRADA)) {
         throw new IllegalArgumentException("Chamada somente para movimentacoes de desbloqueio");
      }

      acionarOperacaoFinalizada(movimentacao);
   }

   public void setDataOperacao(Data dataOperacao) {
      this.dataOperacao = dataOperacao;
   }

   public void acionarDesvinculacaoAtivo(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ativo) {
      IGarantias ig = GarantiasFactory.getInstance();
      IMovimentacoesGarantias movs = ig.getInstanceMovimentacoesGarantias();
      ICestaDeGarantias icg = ig.getInstanceCestaDeGarantias();
      IGerenciadorPersistencia gp = GerenciadorPersistenciaFactory.getGerenciadorPersistencia();

      // garante lock da cesta antes de contar quantos ativos vinculados existem
      // chamado 383,360
      gp.refresh(cesta, NivelLock.UPGRADE);
      int totalAtivosVinculados = cesta.getAtivosVinculados().size();

      if (totalAtivosVinculados > 1) {
         apenasDesvinculaAtivo(cesta, ativo, movs, icg);
      } else {
         desvinculaCesta(cesta);
      }
      //Torna a Autorizacao de Publicidade Inativa
      if (cesta != null) {
	       	AutorizacaoPublicGarantiasDO autDO = AutorizacaoPublicGarantiasFactory.getInstance().obterAutorizacaoPublicGarantias(cesta.getParametroPonta());
	        if (!Condicional.vazio(autDO)){
	             autDO.setSituacao(SituacaoAutorizPublicGarantiasDO.INATIVO);
	             gp = GerenciadorPersistenciaFactory.getGerenciadorPersistencia();
                 gp.saveOrUpdate(autDO);
	        }
	    }
      
      
   }

   private void desvinculaCesta(CestaGarantiasDO cesta) {
      RequisicaoServicoExcluirCestaGarantias excluirCesta = new RequisicaoServicoExcluirCestaGarantias();
      excluirCesta.atribuirOPERACAO_Data(dataOperacao);
      excluirCesta.atribuirGARANTIAS_CESTA_Id(cesta.getNumIdCestaGarantias());

      if (ContextoAtivacao.getContexto().getCanal().equals(Integer.valueOf("2"))) {
         excluirCesta.atribuirBATCH_Booleano(Booleano.VERDADEIRO);
         Roteador.executar(excluirCesta, ContextoAtivacao.getContexto());
      } else {
         excluirCesta.atribuirBATCH_Booleano(Booleano.FALSO);
         Roteador.executarAssincrono(excluirCesta, ContextoAtivacao.getContexto());
      }
   }

   private void apenasDesvinculaAtivo(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ativo,
         IMovimentacoesGarantias movs, ICestaDeGarantias icg) {
      MovimentacaoGarantiaDO mov = movs.incluirMovimentacaoDesvinculacao(cesta, ativo);
      mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);

      CestaGarantiasIFDO cgi = icg.obterIFDOVinculado(cesta, ativo);
      cesta.getAtivosVinculados().remove(cgi);
      GerenciadorPersistenciaFactory.getGerenciadorPersistencia().delete(cgi);

      GarantiasFactory.getInstance().getInstanceGarantidoCesta().excluirGarantidosSemCustodia(cesta);
   }

   public void acionarInadimplencia(InstrumentoFinanceiroDO ativo) {
      ICestaDeGarantias icg = GarantiasFactory.getInstance().getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = icg.obterCestaGarantindoIF(ativo);

      if (cesta != null) {
         acionarInadimplencia(cesta);
      }
   }

   public void setGarantias(IGarantias garantias) {
      this.garantias = garantias;
   }

   public void inicializar() {
      // TODO Auto-generated method stub
      
   }

}