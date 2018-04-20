package br.com.cetip.aplicacao.garantias.negocio;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import br.com.cetip.aplicacao.administracao.apinegocio.ControleOperacionalFactory;
import br.com.cetip.aplicacao.administracao.apinegocio.IControleOperacional;
import br.com.cetip.aplicacao.garantias.apinegocio.GarantiasFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.ICarteirasTipoDebito;
import br.com.cetip.aplicacao.garantias.apinegocio.IContaGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.sec.ComitenteFactory;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.sec.IComitente;
import br.com.cetip.dados.aplicacao.custodia.TipoDebitoDO;
import br.com.cetip.dados.aplicacao.custodia.TipoPosicaoCarteiraDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.ContratoCestaGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.operacao.OperacaoDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.GerenciadorPersistenciaFactory;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.contexto.ContextoAtivacao;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;
import br.com.cetip.infra.servico.nomeejb.NomeEJB;
import cetip.ejb.sic.ICtpSicIdentificacaoAutomaticaUCCHome;
import cetip.ejb.sic.ICtpSicIdentificacaoAutomaticaUCCRemote;
import cetip.global.util.CtpValidateException;
import cetip.sic.field.CtpSicComitentesVO;

/**
 * @author marco sergio
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
class MIGOperacao implements ICarteirasTipoDebito {

   private IGarantias garantias;

   private boolean initialized;
   
   private CodigoTipoOperacao codTipoOperacao;

   protected final IGarantias getGarantias() {
      init();
      return garantias;
   }

   private void init() {
      if (initialized) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "GarantiasFactory ja inicializado para este objeto: " + this.getClass().getName());
         }

         return;
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Inicializando GarantiasFactory para este objeto: " + this.getClass().getName());
      }

      garantias = GarantiasFactory.getInstance();
      initialized = true;

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "GarantiasFactory inicializado para objeto: " + this.getClass().getName());
      }
   }

   protected final ContextoAtivacaoVO getContextoAtivacao() {
      return ContextoAtivacao.getContexto();
   }

   protected final IControleOperacional getControleOperacional() {
      try {
         return ControleOperacionalFactory.getInstance();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   protected final IGerenciadorPersistencia getGerenciadorPersistencia() {
      return GerenciadorPersistenciaFactory.getGerenciadorPersistencia();
   }

   protected final IGerenciadorPersistencia getGp() {
      return getGerenciadorPersistencia();
   }

   protected final IControleOperacional getControle() {
      return getControleOperacional();
   }

   public void setGarantias(IGarantias garantias) {
      this.garantias = garantias;
      this.initialized = garantias != null;
   }

   private ContaParticipanteDO garantidor;

   private ICtpSicIdentificacaoAutomaticaUCCRemote sicEjb;

   private boolean identificacaoComitenteRequerida = false;

   private boolean indPlataformaBaixa = false;

   private boolean indBatch = false;

   private CestaGarantiasDO cesta;

   private Data dataOperacao;

   private ContaParticipanteDO garantido;

   private ContaParticipanteDO conta60garantido;

   private MovimentacaoGarantiaDO movimentacao;

   private Id idComitenteGarantidor;

   private Id idContaComitenteGarantidor;

   private boolean possuiComitenteGarantido;

   private Id idComitenteGarantido;

   private Id idContaComitenteGarantido;

   protected final ContaParticipanteDO getConta60Garantido() {
      return conta60garantido;
   }

   protected final CestaGarantiasDO getCesta() {
      return cesta;
   }

   public final void setDataOperacao(Data dataOperacao) {
      this.dataOperacao = dataOperacao;
   }

   protected final Data getDataOperacao() {
      if (this.dataOperacao == null) {
         try {
            return getControleOperacional().obterD0();
         } catch (Exception e) {
            Logger.error(e);
            throw new Erro(CodigoErro.ERRO, e.getMessage());
         }
      }

      return this.dataOperacao;
   }

   /*
    * 
    * 
    * @param cesta
    * 
    * @return Texto 'S' para cesta de segundo nivel
    */
   protected final boolean ehCestaSegundoNivel() {
      return cesta.getIndSegundoNivel().ehVerdadeiro();
   }

   protected final Texto tipoContaGarantidor() {
      CodigoContaCetip conta = garantidor.getCodContaParticipante();
      return tipoDebito(conta);
   }

   protected final Texto tipoContaGarantido() {
      CodigoContaCetip conta = garantido.getCodContaParticipante();
      return tipoDebito(conta);
   }

   /*
    * Retorna parametro para informar ao banco (functions e procedures) o tipo da conta do participante (garantidor e
    * garantido)
    * 
    * @param conta
    * 
    * @return Texto para o tipo da conta informada. 'P' para Propria, 'C1' para Cliente 1 e 'C2' para Cliente 2
    */
   protected final Texto tipoDebito(CodigoContaCetip conta) {
      return new Texto(!conta.ehContaCliente() ? "P" : conta.ehContaCliente1() ? "C1" : "C2");
   }

   /*
    * Obtem o CPF/CNPJ do Comitente da movimentacao sendo processada
    * 
    * Se a movimentacao nao possuir dados de comitente, tenta obter da mov. de vinculacao
    * 
    * @param idCesta
    * @return cpfCnpj do comitente
    */
   private final void carregaDadosComitente() {
      obtemDadosComitenteGarantidor();     
      obtemDadosComitenteGarantido();
   }

   /*
    * Dados do comitente garantido ficam na movimentacao de liberacao
    * ou em caso de vinculacao com contrato, fica na ContratoCestaGarantiaDO
    * 
    * Primeiro tenta pegar da movimentacao de liberacao
    * Senao, pega da cesta
    */
   private void obtemDadosComitenteGarantido() {
      // soh carrega dados de comitente garantido quando eh liberacao de garantias
      if (movimentacao.getTipoMovimentacaoGarantia().ehMovimentacaoLiberacao() == false) {
         return;
      }

      final Id idGarantido = movimentacao.getContaParticipante().getId();
      final CodigoContaCetip codContaGarantido = movimentacao.getContaParticipante().getCodContaParticipante();
      CPFOuCNPJ ccGarantido = movimentacao.getCpfOuCnpjComitente();

      boolean ehCestaComContrato = cesta.getParametroPonta() != null;
      if (ehCestaComContrato) {
         IContratosCesta icc = getGarantias().getInstanceContratosCesta();
         ContratoCestaGarantiaDO contratoCesta = icc.obterVinculoContrato(cesta);

        Id idCestaParte = new Id();
         if (!Condicional.vazio(contratoCesta.getCestaParte())){
           idCestaParte = contratoCesta.getCestaParte().getNumIdCestaGarantias();
         }
         
         final Id idCestaCorrente = cesta.getNumIdCestaGarantias();
         final boolean ehCestaParte = idCestaParte.mesmoConteudo(idCestaCorrente);
         if (ehCestaParte) {
            ccGarantido = contratoCesta.getComitenteContraparte();
         } else {
            ccGarantido = contratoCesta.getComitenteParte();
         }
      }
      
      // pegou dados? sim
      idComitenteGarantido = getIdComitente(ccGarantido, codContaGarantido);
      idContaComitenteGarantido = idGarantido;

      possuiComitenteGarantido = idComitenteGarantido != null && idContaComitenteGarantido != null;
   }

   /*
    * Dados do comitente garantidor ficam na movimentacao de vinculacao
    */
   private void obtemDadosComitenteGarantidor() {
      boolean ehMovVinculacao = movimentacao.getTipoMovimentacaoGarantia()
            .equals(TipoMovimentacaoGarantiaDO.VINCULACAO);

      idContaComitenteGarantidor = garantidor.getId();
      CodigoContaCetip codContaComitente = garantidor.getCodContaParticipante();

      Id idCesta = cesta.getNumIdCestaGarantias();
      CPFOuCNPJ ccComitente = null;

      if (ehMovVinculacao) {
         ccComitente = movimentacao.getCpfOuCnpjComitente();
      } else {
         // obtem CPF/CNPJ da primeira vinculacao da cesta
         StringBuffer hql = new StringBuffer("select cg.cpfOuCnpjComitente from MovimentacaoGarantiaDO cg");
         hql.append(" where cg.cestaGarantias.numIdCestaGarantias = :idCesta");
         hql.append(" and cg.tipoMovimentacaoGarantia.numIdTipoMovGarantia = 3 ");
         hql.append(" and cg.statusMovimentacaoGarantia.numIdStatusMovGarantia = 1 ");
         hql.append(" order by cg.numIdMovimentacaoGarantia asc ");

         IConsulta c = getGp().criarConsulta(hql.toString());
         c.setMaxResults(1);
         c.setAtributo("idCesta", idCesta);
         List l = c.list();

         ccComitente = (CPFOuCNPJ) l.get(0);
      }

      idComitenteGarantidor = getIdComitente(ccComitente, codContaComitente);
   }

   private Id getIdComitente(CPFOuCNPJ comitente, CodigoContaCetip conta) {
      Id idComitente = null;

      try {
         IComitente iComitente = ComitenteFactory.getInstance();
         if (!Condicional.vazio(comitente)){
            idComitente = iComitente.obterComitente(comitente, comitente.obterNatureza(), conta).getId();
         }
      } catch (Exception e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, "SIC: Erro ao consultar comitente.");
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, ">> Comitente encontrado: " + idComitente);
      }

      return idComitente;
   }

   private final void createEjb() {
      // Obtendo EJB do SIC
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Lookup do EJB do SIC... ");
      }

      String jndiName = ICtpSicIdentificacaoAutomaticaUCCHome.JNDI_NAME;
      ICtpSicIdentificacaoAutomaticaUCCHome home = null;
      try {
         home = (ICtpSicIdentificacaoAutomaticaUCCHome) NomeEJB.lookupHome(jndiName, NomeEJB.URL_NOME);
      } catch (NamingException e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, "SIC: erro ao localizar componente do NoMe");
      }

      try {
         sicEjb = home.create();
      } catch (Exception e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, "SIC: erro ao criar componente do NoMe");
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "EJB localizado e criado");
      }
   }

   /**
    * Efetua a identificacao das operacoes e quantidades indicadas
    * 
    * @param idOperacoes
    * @param quantidades
    */
   protected final void identificaOperacoes(Integer[] idOperacoes, BigDecimal[] quantidades) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Processo de identificacao de comitente...");
      }

      if (idOperacoes == null || idOperacoes.length == 0) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Nenhuma operacao para ser identificada.");
         }
         return;
      }

      int countOps = idOperacoes.length;
      int countQtd = quantidades.length;

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Identificacao no SIC para " + countOps + " operacoes (e " + countQtd + " valores)");
      }

      if (countOps != countQtd) {
         throw new IllegalArgumentException("Array de operacoes difere de quantidades");
      }

      createEjb();

      try {
         carregaDadosComitente();

         Integer intComitente = Integer.valueOf(this.idComitenteGarantidor.obterConteudo());
         Integer intContaComitente = Integer.valueOf(this.idContaComitenteGarantidor.obterConteudo());
         
            Logger.info(this, "Id CPF/CNPJ Garantidor: " + intComitente);
            Logger.info(this, "Id Conta Cliente Garantidor : " + intContaComitente);

         // Loop de operacoes
         for (int i = 0; i < idOperacoes.length; i++) {
            Integer id = idOperacoes[i];
            BigDecimal qtd = quantidades[i];

            if (verificaIdentContasOperacao(new Id(idOperacoes[i].toString()))){
            	
              if (codTipoOperacao.mesmoConteudo(CodigoTipoOperacao.COD_LIBERACAO_GARANTIA)){
                  Integer intComitenteGarantido = Integer.valueOf(this.idComitenteGarantido.obterConteudo());
                  Integer intContaComitenteGarantido = Integer.valueOf(this.idContaComitenteGarantido.obterConteudo());
            	  identificaComitente(id, qtd, intComitenteGarantido, intContaComitenteGarantido);
            	  Logger.info("comitente CPF/CNPJ garantido "+intComitenteGarantido);
                  Logger.info("comitente id conta garantido  "+intContaComitenteGarantido);
              } else {
            	  identificaComitente(id, qtd, intComitente, intContaComitente); //debita sempre do Garantidor            	  
              }
            }
         }
      } finally {
         // Remove o EJB mas deixa estourar a exception
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "EJB sera removido... ");
         }

         try {
            sicEjb.remove();
         } catch (Exception e) {
            Logger.error(e);
            throw new Erro(CodigoErro.ERRO, "SIC: erro ao fechar consulta de comitente.");
         }

         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "EJB removido");
         }
       }
   }

   /*
    * Identificacao Automatica de Comitente
    * 
    * @param idOperacao
    * @param idCesta
    */
   private final void identificaComitente(Integer idOperacao, BigDecimal quantidade, Integer intIdComitente,
         Integer idContaCliente) {
     // if (Logger.estaHabilitadoDebug(this)) {
         Logger.info(this, ">> Identificacao de comitente[" + intIdComitente + "] para Operacao[" + idOperacao
               + "], Conta Cliente[" + idContaCliente + "] e Quantidade[" + quantidade + "]");
     // }

      // Construcao do VO
      CtpSicComitentesVO comitente = new CtpSicComitentesVO();
      comitente.setIdComitente(intIdComitente);
      comitente.setQuantidade(quantidade);
      List listaComitentes = Arrays.asList(new Object[] { comitente });

      
      // verifica se a cesta possui ativo em carteira de comitente. Se sim, entao identifica todos
      StringBuffer hql = new StringBuffer();
      hql.append("from "+OperacaoDO.class.getName()+" o where o.id = "+idOperacao);
   	
      List l = getGp().find(hql.toString());
      OperacaoDO o = (OperacaoDO) l.get(0);
      
      Logger.info(o.getContaParticipanteP1().getCodContaParticipante());
      Logger.info(o.getContaParticipanteP2().getCodContaParticipante());
      
      Logger.info(o.getContaParticipanteP1().getId());
      Logger.info(o.getContaParticipanteP2().getId());      
      
      // Chamada do EJB
      try {
         sicEjb.identificaOperacaoGarantias(idOperacao, listaComitentes, idContaCliente);
    	  
      } catch (CtpValidateException e) {
         Logger.error(this, e);
         throw new Erro(CodigoErro.ERRO, "SIC: " + e.getMessage());
      } catch (Exception e) {
         Logger.error(this, e);
         throw new Erro(CodigoErro.ERRO, "SIC: erro ao identificar operação");
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Operacao identificada no SIC!");
      }
   }

   public IdStatusMovimentacaoGarantia validar() {
      return IdStatusMovimentacaoGarantia.OK;
   }

   /*
    * Conta do Garantidor da Cesta desta Operacao
    * 
    * @return Returns the contaGarantidorDO.
    */
   protected final ContaParticipanteDO getGarantidor() {
      return garantidor;
   }

   /**
    * Verifica se cesta informada possui pelo menos 1 ativo com especificacao de comitente.
    * 
    * @param idCesta codigo da cesta
    * @return true se cesta possui 1 ou mais ativos com especificacao de comitente. False caso contrario.
    */
   protected final boolean existeGarantiaIdentificadaPorComitente() {
      // verifica se a cesta possui ativo em carteira de comitente. Se sim, entao identifica todos
      StringBuffer hql = new StringBuffer();
      hql.append("select count(dg) from DetalheGarantiaDO dg ");
      hql.append(" inner join dg.instrumentoFinanceiro ativo, ");
      hql.append(" CarteiraComitenteDO cc where ativo.id = cc.numeroIF ");
      hql.append(" and dg.cestaGarantias.numIdCestaGarantias = ?");
      Id idCesta = cesta.getNumIdCestaGarantias();
      List l = getGp().find(hql.toString(), idCesta);
      Integer count = (Integer) l.get(0);

      if (count.intValue() > 0) {
         return true;
      }

      // caso nao tenha carteira, verifica a operacao atual, ou operacoes do lote
      // se na tipo_oper_objeto_serv eh identificacao obrigatoria
      hql.setLength(0);

      getGp().flush();

      hql.append("select count(*) ");
      hql.append(" from OperacaoDO o inner join o.tipoOperObjetoServ toos");
      hql.append(" where toos.indIdentificacaoObrigatoria = ?");
      hql.append("   and o.idCestaGarantias = ?");
      hql.append("   and o.numControleLancamentoOriginalP1 = ?"); // filtra pelo id da movimentacao corrente

      Id idCestaMov = getMovimentacao().getCestaGarantias().getNumIdCestaGarantias();
      Id idMov = getMovimentacao().getNumIdMovimentacaoGarantia();
      Object[] params = new Object[] { Booleano.VERDADEIRO, idCestaMov, idMov };
      l = getGp().find(hql.toString(), params);
      count = (Integer) l.get(0);

      if (count.intValue() > 0) {
         return true;
      }

      return false;
   }

   /*
    * Marca como passivel de identificacao a(s) operacao/operacoes corrente(s).
    * 
    * @param identifica sim ou nao
    */
   protected final void setComIdentificaComitente(boolean identifica) {
      this.identificacaoComitenteRequerida = identifica;
   }

   /**
    * Se o Garantidor da Cesta for Conta Cliente e a Cesta possuir ativos identificaveis, a identificacao de Comitente
    * eh obrigatoria
    * 
    * @return
    */
   protected final boolean deveIdentificarComitente() {
      boolean ehContaCliente = getGarantidor().getCodContaParticipante().ehContaCliente();
      boolean existeGarantiaIdentificada = existeGarantiaIdentificadaPorComitente();

      boolean deveIdentificar = identificacaoComitenteRequerida && ehContaCliente && existeGarantiaIdentificada;

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, deveIdentificar ? "SIC sera executado." : "SIC *NAO* sera executado.");
      }
      
      return deveIdentificar;
   }

   
   protected final boolean verificaIdentContasOperacao(Id idOperacao) {
	    
	   boolean deveIdentificar = false; 
	   StringBuffer hql = new StringBuffer();
	   hql.append("from "+OperacaoDO.class.getName()+" o where o.id = "+idOperacao);
	   
	   	
	   List l = getGp().find(hql.toString());
	   OperacaoDO opDO = (OperacaoDO) l.get(0);
	   
	   codTipoOperacao = opDO.getTipoOperObjetoServ().getTipoOperacao().getCodTipoOperacao();
	   CodigoContaCetip contaP1 = opDO.getContaParticipanteP1().getCodContaParticipante();
	   CodigoContaCetip contaP2 = opDO.getContaParticipanteP2().getCodContaParticipante();
	   
	   if (opDO.getContaParticipanteP1().getCodContaParticipante().ehContaCliente() ||
		   opDO.getContaParticipanteP2().getCodContaParticipante().ehContaCliente()) {
		   deveIdentificar = true;
	   }
	   
	      /*Logger.info(opDO.getContaParticipanteP1().getCodContaParticipante());
	      Logger.info(opDO.getContaParticipanteP2().getCodContaParticipante());
	      
	      Logger.info("Codigo Tipo Operacao ---------------------" +codTipoOperacao);
	      Logger.info("Conta P1 ---------------------------------"+contaP1);
	      Logger.info("Conta P2 ---------------------------------"+contaP2);*/
	      
	      return deveIdentificar;
	}
   
   public final boolean isIndBatch() {
      return indBatch;
   }

   public final void setIndBatch(boolean indBatch) {
      this.indBatch = indBatch;
   }

   public final boolean isIndPlataformaBaixa() {
      return indPlataformaBaixa;
   }

   public final void setIndPlataformaBaixa(boolean indPlataformaBaixa) {
      this.indPlataformaBaixa = indPlataformaBaixa;
   }

   protected void completarDadosOperacao() {
   }

   /**
    * Inicializa a Operacao de MMG
    * 
    * @param idMovimentacao
    */
   protected final void inicializar(MovimentacaoGarantiaDO mov) {
      this.movimentacao = mov;
      cesta = movimentacao.getCestaGarantias();
      garantidor = cesta.getGarantidor();

      IGarantidoCesta garantidoCesta = getGarantias().getInstanceGarantidoCesta();
      garantido = garantidoCesta.obterGarantidoCesta(cesta);

      IContaGarantia icg = garantias.getInstanceContaGarantia();
      if (garantido != null) {
         conta60garantido = icg.obterConta60(garantido);
      }
   }

   protected final MovimentacaoGarantiaDO getMovimentacao() {
      return movimentacao;
   }

   protected final InstrumentoFinanceiroDO getIfDO() {
      return movimentacao.getInstrumentoFinanceiro();
   }

   /**
    * Efetua o registro da operacao, ou caso seja uma operacao lote, executa
    * procedure/function no banco de dados.
    * 
    * @return
    */
   public IdStatusMovimentacaoGarantia criarOperacao() {
      return null;
   }

   /*
    * Primeiro indice: nivel Segundo indice: tipo da conta (propria / cliente 1 / cliente 2) Terceiro indice: tipo da
    * garantia (penhor / cessao fiduciaria)
    */
   private static final Id[][][] TIPOS_DEBITOS = new Id[][][] {
         {
               { TipoDebitoDO.CESTA_GRT_PENHOR, TipoDebitoDO.CESTA_GRT_CS_FID, TipoDebitoDO.CESTA_GRT_PENHOR_EMISSOR },
               { TipoDebitoDO.CESTA_GRT_PENHOR_CLIENTE1, TipoDebitoDO.CESTA_GRT_CS_FID_CLIENTE1,
                     TipoDebitoDO.CESTA_GRT_PENHOR_EMISSOR_CLIENTE1 },
               { TipoDebitoDO.CESTA_GRT_PENHOR_CLIENTE2, TipoDebitoDO.CESTA_GRT_CS_FID_CLIENTE2,
                     TipoDebitoDO.CESTA_GRT_PENHOR_EMISSOR_CLIENTE2 } },
         {
               { TipoDebitoDO.CESTA_GRT_PENHOR_AT_GRTDO_SEG_NIVEL, TipoDebitoDO.CESTA_GRT_CS_FID_AT_GRTDO_SEG_NIVEL,
                     TipoDebitoDO.CESTA_GRT_PENHOR_EMISSOR_SEG_NIVEL },
               { TipoDebitoDO.CESTA_GRT_PENHOR_CLIENTE1_AT_GRTDO_SEG_NIVEL,
                     TipoDebitoDO.CESTA_GRT_CS_FID_CLIENTE1_AT_GRTDO_SEG_NIVEL,
                     TipoDebitoDO.CESTA_GRT_PENHOR_EMISSOR_CLIENTE1_SEG_NIVEL },
               { TipoDebitoDO.CESTA_GRT_PENHOR_CLIENTE2_AT_GRTDO_SEG_NIVEL,
                     TipoDebitoDO.CESTA_GRT_CS_FID_CLIENTE2_AT_GRTDO_SEG_NIVEL,
                     TipoDebitoDO.CESTA_GRT_PENHOR_EMISSOR_CLIENTE2_SEG_NIVEL } }, };

   private static final Map TIPOS_CARTEIRAS_POR_DEBITO = new HashMap();
   static {
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_PENHOR, TipoPosicaoCarteiraDO.CESTA_GRT_PENHOR);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_CS_FID, TipoPosicaoCarteiraDO.CESTA_GRT_CS_FID);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_PENHOR_AT_GRTDO_SEG_NIVEL,
            TipoPosicaoCarteiraDO.CESTA_GRT_PENHOR_AT_GRTDO_SEG_NIVEL);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_CS_FID_AT_GRTDO_SEG_NIVEL,
            TipoPosicaoCarteiraDO.CESTA_GRT_CS_FID_AT_GRTDO_SEG_NIVEL);

      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_PENHOR_CLIENTE1,
            TipoPosicaoCarteiraDO.CESTA_GRT_PENHOR_CLIENTE1);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_CS_FID_CLIENTE1,
            TipoPosicaoCarteiraDO.CESTA_GRT_CS_FID_CLIENTE1);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_PENHOR_CLIENTE1_AT_GRTDO_SEG_NIVEL,
            TipoPosicaoCarteiraDO.CESTA_GRT_PENHOR_CLIENTE1_AT_GRTDO_SEG_NIVEL);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_CS_FID_CLIENTE1_AT_GRTDO_SEG_NIVEL,
            TipoPosicaoCarteiraDO.CESTA_GRT_CS_FID_CLIENTE1_AT_GRTDO_SEG_NIVEL);

      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_PENHOR_CLIENTE2,
            TipoPosicaoCarteiraDO.CESTA_GRT_PENHOR_CLIENTE2);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_CS_FID_CLIENTE2,
            TipoPosicaoCarteiraDO.CESTA_GRT_CS_FID_CLIENTE2);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_PENHOR_CLIENTE2_AT_GRTDO_SEG_NIVEL,
            TipoPosicaoCarteiraDO.CESTA_GRT_PENHOR_CLIENTE2_AT_GRTDO_SEG_NIVEL);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_CS_FID_CLIENTE2_AT_GRTDO_SEG_NIVEL,
            TipoPosicaoCarteiraDO.CESTA_GRT_CS_FID_CLIENTE2_AT_GRTDO_SEG_NIVEL);

      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_PENHOR_EMISSOR,
            TipoPosicaoCarteiraDO.CESTA_GRT_PENHOR_EMISSOR);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_PENHOR_EMISSOR_CLIENTE1,
            TipoPosicaoCarteiraDO.CESTA_GRT_PENHOR_EMISSOR_CLIENTE1);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_PENHOR_EMISSOR_CLIENTE2,
            TipoPosicaoCarteiraDO.CESTA_GRT_PENHOR_EMISSOR_CLIENTE2);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_PENHOR_EMISSOR_SEG_NIVEL,
            TipoPosicaoCarteiraDO.CESTA_GRT_PENHOR_EMISSOR_SEG_NIVEL);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_PENHOR_EMISSOR_CLIENTE1_SEG_NIVEL,
            TipoPosicaoCarteiraDO.CESTA_GRT_PENHOR_EMISSOR_CLIENTE1_SEG_NIVEL);
      TIPOS_CARTEIRAS_POR_DEBITO.put(TipoDebitoDO.CESTA_GRT_PENHOR_EMISSOR_CLIENTE2_SEG_NIVEL,
            TipoPosicaoCarteiraDO.CESTA_GRT_PENHOR_EMISSOR_CLIENTE2_SEG_NIVEL);
   }

   public final Id getTipoDebito(CodigoContaCetip conta, IdTipoGarantia tipoGarantia, boolean isNivel2) {
      int nivel = converteNivelIndice(isNivel2);
      int posConta = converteContaIndice(conta);
      int tipo = converteTipoGarantiaIndice(tipoGarantia);

      Id tipoDebito = TIPOS_DEBITOS[nivel][posConta][tipo];
      return tipoDebito;
   }

   private int converteTipoGarantiaIndice(IdTipoGarantia tipoGarantia) {
      return tipoGarantia.mesmoConteudo(IdTipoGarantia.REAL_PENHOR) ? 0 : (tipoGarantia
            .mesmoConteudo(IdTipoGarantia.CESSAO_FIDUCIARIA) ? 1 : 2);
   }

   private int converteContaIndice(CodigoContaCetip conta) {
      return conta.ehContaCliente() ? (conta.ehContaCliente1() ? 1 : 2) : 0;
   }

   private int converteNivelIndice(boolean isNivel2) {
      return isNivel2 ? 1 : 0;
   }

   public final IdTipoGarantia getTipoGarantia(Id tipoDebito) {
      for (int a = 0; a < 2; a++) {
         for (int b = 0; b < 3; b++) {
            for (int c = 0; c < 3; c++) {
               Id _tipo = TIPOS_DEBITOS[a][b][c];
               if (_tipo.mesmoConteudo(tipoDebito) && c == 0) {
                  return IdTipoGarantia.REAL_PENHOR;
               } else if (_tipo.mesmoConteudo(tipoDebito) && c == 1) {
                  return IdTipoGarantia.CESSAO_FIDUCIARIA;
               } else if (_tipo.mesmoConteudo(tipoDebito) && c == 2) {
                  return IdTipoGarantia.PENHOR_NO_EMISSOR;
               }
            }
         }
      }

      return null;
   }

   public final Id getTipoCarteira(Id tipoDebitoParam) {
      Id tipoDebito = new Id(tipoDebitoParam.obterConteudo());
      return (Id) TIPOS_CARTEIRAS_POR_DEBITO.get(tipoDebito);
   }

   protected final ContaParticipanteDO getGarantido() {
      return garantido;
   }

}