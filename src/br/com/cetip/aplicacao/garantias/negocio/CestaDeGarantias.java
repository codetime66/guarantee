package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiaVO;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IMIGAcionador;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.negocio.mainframe.WTCRegistraDesvinculacao;
import br.com.cetip.dados.aplicacao.custodia.TipoPosicaoCarteiraDO;
import br.com.cetip.dados.aplicacao.garantias.AcessoCestaDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.GarantidorCestaIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaIFDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ComplementoContratoDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.tempo.DataHora;
import br.com.cetip.infra.atributo.tipo.tempo.Hora;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.persistencia.NivelLock;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * Implementacao da interface de negocio do projeto Garantias. Implementa as operacoes de banco de dados necessarias.
 */
final class CestaDeGarantias extends BaseGarantias implements ICestaDeGarantias {

   private IConsulta consultaCestaGarantidoraAtivo;

   private IConsulta consAtivoGaranteCesta;

   private IConsulta consultaCestaGarantindoIF;

   /**
    * Cesta existe?
    * 
    * @param codigo
    * @return true se o codigo eh de uma cesta existente. false caso contrario
    */
   public boolean existeCestaDeGarantias(NumeroCestaGarantia codigo) {
      StringBuffer hql = new StringBuffer(500);
      hql.append("select count(*) from ");
      hql.append(CestaGarantiasDO.class.getName());
      hql.append(" cg where cg.numIdCestaGarantias = ?");

      List l = getGp().find(hql.toString(), new Id(codigo.toString()));
      Integer count = (Integer) l.get(0);
      return count.intValue() > 0;
   }

   public CestaGarantiasDO obterCestaDeGarantias(NumeroCestaGarantia codigo) {
      return obterCestaDeGarantias(codigo, null);
   }

   /**
    * Obtem a cesta de garantia para alteracao
    * 
    * @param codigo
    * @param tipoAcesso
    * @param inCancelada
    * @return
    */
   public CestaGarantiasDO obterCestaDeGarantias(NumeroCestaGarantia codigo, Funcao tipoAcesso) {
      if (Condicional.vazio(codigo)) {
         throw new Erro(CodigoErro.CESTA_INVALIDA);
      }

      ContextoAtivacaoVO ca = getContextoAtivacao();
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "OBTENDO CESTA DE NUMERO => " + codigo);
      }

      if (!Condicional.vazio(codigo) && !existeCestaDeGarantias(codigo)) {
         Erro erro = new Erro(CodigoErro.CESTA_X_INEXISTENTE);
         erro.parametroMensagem(codigo, 0);
         throw erro;
      }

      CestaGarantiasDO cesta = (CestaGarantiasDO) getGp().load(CestaGarantiasDO.class, new Id(codigo.toString()),
            NivelLock.UPGRADE);
      getGp().refresh(cesta);

      if (ca.ehCETIP()) {
         return cesta;
      }

      // Garantidor
      String garantidor = cesta.getGarantidor().getCodContaParticipante().toString();
      Set contas = new HashSet(ca.getListaContasLiquidacao());
      boolean ehGarantidor = contas.contains(garantidor);

      // Garantido
      Set contasGarantido = new HashSet(ca.getListaContasConsulta());
      boolean ehGarantido = false;
      if (cesta.getVisualizadores() != null && !cesta.getVisualizadores().isEmpty()) {
         Iterator i = cesta.getVisualizadores().iterator();

         while (i.hasNext()) {
            AcessoCestaDO acesso = (AcessoCestaDO) i.next();
            String garantido = acesso.getContaParticipante().getCodContaParticipante().toString();
            if (contasGarantido.contains(garantido)) {
               ehGarantido = true;
               break;
            }
         }
      }

      if (ehGarantido
            && (!ehGarantidor)
            && (cesta.getStatusCesta().equals(StatusCestaDO.EM_MANUTENCAO) || cesta.getStatusCesta().equals(
                  StatusCestaDO.EM_FINALIZACAO))) {
         throw new Erro(CodigoErro.CESTA_USUARIO_NAO_HABILITADO);
      }

      if ((Condicional.vazio(tipoAcesso) && (ehGarantidor || ehGarantido))
            || (ICestaDeGarantias.FUNCAO_GARANTIDO.mesmoConteudo(tipoAcesso) && ehGarantido)
            || (ICestaDeGarantias.FUNCAO_GARANTIDOR.mesmoConteudo(tipoAcesso) && ehGarantidor) || ehCanalMensageria()) {
         return cesta;
      }

      throw new Erro(CodigoErro.CESTA_USUARIO_NAO_HABILITADO);
   }

   public int contarGarantias(CestaGarantiasDO cesta) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Contagem de Garantias em DetalheGarantiaDO(qtd>0) para: " + cesta);
      }

      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(500);
      hql.append("select count(*) from ");
      hql.append(DetalheGarantiaDO.class.getName());
      hql.append(" d ");
      hql.append(" where d.cestaGarantias = ?");
      hql.append(" and d.quantidadeGarantia > 0");

      return ((Integer) gp.find(hql.toString(), cesta).get(0)).intValue();
   }

   public List listarGarantiasCesta(NumeroCestaGarantia numero) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Lista de Garantias em DetalheGarantiaDO para Cesta: " + numero);
      }

      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(500);
      hql.append("select d from ");
      hql.append(DetalheGarantiaDO.class.getName());
      hql.append(" d left join d.instrumentoFinanceiro ");
      hql.append(" where d.cestaGarantias.numIdCestaGarantias = ?");
      hql.append(" and d.cestaGarantias.datExclusao is null");
      hql.append(" and d.quantidadeGarantia > 0");

      return gp.find(hql.toString(), new Id(numero.obterConteudo().toString()));
   }

   /*
    * Metodo chamado pela maquina de estado da operacao 894 (RETIRADA) e pelo ServicoChamaMIGResultado (que eh acionado
    * pelo mainframe)
    */
   public void verificaNecessidadeDesvincularCesta(CestaGarantiasDO cesta) {
      getGp().lock(cesta, NivelLock.UPGRADE);
      getGp().refresh(cesta);
      StatusCestaDO status = cesta.getStatusCesta();

      if (status.equals(StatusCestaDO.EM_DESVINCULACAO) || status.equals(StatusCestaDO.EM_LIBERACAO)
            || status.equals(StatusCestaDO.INADIMPLENTE) || status.equals(StatusCestaDO.VINCULADA)) {
         int qtdGarantias = contarGarantias(cesta);
         if (qtdGarantias == 0) {
            if (Logger.estaHabilitadoDebug(this)) {
               Logger.debug(this, "Vai desvincular a cesta!");
            }

            desvinculaCestaSemGarantias(cesta);
         }
      }
   }

   public void desvinculaCestaSemGarantias(CestaGarantiasDO cesta) {

      Set ativosVinculados = new HashSet(cesta.getAtivosVinculados());
      Iterator i = ativosVinculados.iterator();
      IMovimentacoesGarantias movs = getFactory().getInstanceMovimentacoesGarantias();

      while (i.hasNext()) {
         CestaGarantiasIFDO vinculo = (CestaGarantiasIFDO) i.next();
         InstrumentoFinanceiroDO ifDO = vinculo.getInstrumentoFinanceiro();
         cesta.getAtivosVinculados().remove(vinculo);

         MovimentacaoGarantiaDO itemDO = movs.incluirMovimentacaoDesvinculacao(cesta, ifDO);
         itemDO.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);

         SistemaDO sistema = ifDO.getSistema();
         Id codSistema = sistema.getNumero();

         boolean ehContrato = (ifDO instanceof ComplementoContratoDO);
         boolean ehCETIP21 = codSistema.mesmoConteudo(SistemaDO.CETIP21);

         if (ehContrato) {
            IContratosCesta icc = getFactory().getInstanceContratosCesta();
            icc.desvinculaPontaCesta(cesta);
         }

         if (!ehContrato && !ehCETIP21) {
            if (Logger.estaHabilitadoDebug(this)) {
               Logger.debug(this, "*** DADOS PASSADOS AO MAINFRAME ***");
               Logger.debug(this, "CODIGO IF: " + ifDO.getCodigoIF());
               Logger.debug(this, "NR CESTA: " + cesta.getNumIdCestaGarantias());
            }

            WTCRegistraDesvinculacao wtc = new WTCRegistraDesvinculacao(cesta.getNumIdCestaGarantias(), ifDO
                  .getCodigoIF(), sistema.getNumSistemaNx());
            wtc.execute();
         }
      }

      if (new Texto("S").mesmoConteudo(cesta.getIndInadimplencia())) {
         cesta.setStatusCesta(StatusCestaDO.GRT_LIBERADAS);
      } else {
         cesta.setStatusCesta(StatusCestaDO.GRT_RETIRADAS);
      }
   }

   /**
    * Executa chamada ao MIGAcionador para a Movimentacao indicada
    * 
    * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
    * @param movimentacao
    */
   public void acionaMIG(MovimentacaoGarantiaDO movimentacao, Booleano indBatch, Data dataOperacao) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "AcionaMIG_Entramos no aciona MIG");
      }

      Booleano ehCetipado = movimentacao.getIndCetipado();
      if (ehCetipado.ehFalso()) {
         throw new IllegalArgumentException("MIGAcionador nao suporta IFs nao-cetipados!");
      }

      // MIG Acionador
      IMIGAcionador migAcionador = getFactory().getInstanceMIGAcionador();

      // Data da Operacao
      migAcionador.setDataOperacao(dataOperacao);

      // Chamada ao MIG
      migAcionador.acionarOperacao(movimentacao, indBatch);
   }

   public void incluirGarantiaExterna(MovimentacaoGarantiaDO mov) {
      IGerenciadorPersistencia gp = getGp();

      mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);
      mov.setDataMovimentacao(new DataHora(getDataHoje(), new Hora(new Date())));

      DetalheGarantiaDO garantia = new DetalheGarantiaDO();
      garantia.setCestaGarantias(mov.getCestaGarantias());
      garantia.setCodIfNCetipado(mov.getCodIfNCetipado());
      garantia.setInstrumentoFinanceiro(mov.getInstrumentoFinanceiro());
      garantia.setDataInclusao(new DataHora(new Date()));
      garantia.setIndCetipado(mov.getIndCetipado());
      garantia.setIndDireitosGarantidor(mov.getIndDireitosGarantidor());
      garantia.setQuantidadeGarantia(mov.getQtdGarantia());
      garantia.setTxtDescricao(mov.getTxtDescricao());

      gp.save(garantia);
   }

   public boolean possuiMovsBloqueioPendente(CestaGarantiasDO cesta) {
      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(500);
      hql.append("select count(*) from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" m where m.cestaGarantias = ? ");
      hql.append(" and m.statusMovimentacaoGarantia = ? ");
      hql.append(" and m.tipoMovimentacaoGarantia = ? ");

      List l = gp.find(hql.toString(), new Object[] { cesta, StatusMovimentacaoGarantiaDO.PENDENTE,
            TipoMovimentacaoGarantiaDO.BLOQUEIO });

      Integer count = (Integer) l.get(0);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "- Cesta: " + cesta.getNumIdCestaGarantias());
         Logger.debug(this, "- Movimentacoes de Bloqueio: " + l.size());
      }

      return count.intValue() > 0;
   }

   public boolean possuiMovsBloqueioSelic(CestaGarantiasDO cesta) {
      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(500);
      hql.append("select count(*) from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" m where m.cestaGarantias = :cesta ");
      hql.append(" and m.statusMovimentacaoGarantia in (:statusMov) ");
      hql.append(" and m.tipoMovimentacaoGarantia = :tipoMov ");
      hql.append(" and m.instrumentoFinanceiro.id in (select sel.instrumentoFinanceiro.id from ").append(
            GarantidorCestaIFDO.class.getName()).append(" sel");
      hql.append(" where sel.indAtivo = :indicador and sel.dataExclusao is null)");

      IConsulta consulta = gp.criarConsulta(hql.toString());
      consulta.setAtributo("cesta", cesta);
      consulta.setParameterList("statusMov", new Object[] { StatusMovimentacaoGarantiaDO.PENDENTE,
            StatusMovimentacaoGarantiaDO.OK });
      consulta.setAtributo("tipoMov", TipoMovimentacaoGarantiaDO.BLOQUEIO);
      consulta.setAtributo("indicador", new Booleano(Booleano.FALSO));

      List l = consulta.list();
      Integer count = (Integer) l.get(0);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "- Cesta: " + cesta.getNumIdCestaGarantias());
         Logger.debug(this, "- Movimentacoes de Bloqueio: " + l.size());
      }

      return count.intValue() > 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias#possuiMovsDefeituosas(br.com.cetip.dados.aplicacao
    * .garantias.CestaGarantiasDO)
    */
   public boolean possuiMovsDefeituosas(CestaGarantiasDO cesta) {
      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(500);
      hql.append("select count(*) from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" m where m.cestaGarantias = ? ");
      hql.append(" and m.statusMovimentacaoGarantia <> ? ");
      hql.append(" and m.statusMovimentacaoGarantia <> ? ");
      hql.append(" and m.statusMovimentacaoGarantia <> ? ");
      hql.append(" and m.tipoMovimentacaoGarantia = ? ");

      List l = gp.find(hql.toString(), new Object[] { cesta, StatusMovimentacaoGarantiaDO.PENDENTE,
            StatusMovimentacaoGarantiaDO.MOVIMENTACAO_CANCELADA, StatusMovimentacaoGarantiaDO.OK,
            TipoMovimentacaoGarantiaDO.BLOQUEIO });

      Integer count = (Integer) l.get(0);
      return count.intValue() > 0;
   }

   public boolean possuiMovimentacaoBloqueio(NumeroCestaGarantia numeroCesta) {
      IGerenciadorPersistencia gp = getGp();
      CestaGarantiasDO cesta = obterCestaDeGarantias(numeroCesta);

      StringBuffer hql = new StringBuffer(500);
      hql.append("select count(*) from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" m where m.cestaGarantias = ? ");
      hql.append(" and (m.statusMovimentacaoGarantia = ? ");
      hql.append(" or m.statusMovimentacaoGarantia = ? )");
      hql.append(" and m.tipoMovimentacaoGarantia = ? ");

      List l = gp.find(hql.toString(), new Object[] { cesta, StatusMovimentacaoGarantiaDO.PENDENTE,
            StatusMovimentacaoGarantiaDO.OK, TipoMovimentacaoGarantiaDO.BLOQUEIO });

      Integer count = (Integer) l.get(0);
      return count.intValue() > 0;
   }

   public List listarCestasVinculadasSemAtivos() {
      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(500);
      hql.append("select d.cestaGarantias.numIdCestaGarantias from ");
      hql.append(DetalheGarantiaDO.class.getName()).append(" d");
      hql.append(" where d.cestaGarantias.statusCesta = ? ");
      hql.append(" and not exists (");
      hql.append(" select 1 from ").append(CestaGarantiasIFDO.class.getName()).append(" cif");
      hql.append("        where d.cestaGarantias.numIdCestaGarantias = cif.cestaGarantia ");
      hql.append(" ) and d.cestaGarantias.datExclusao is null ");
      hql.append(" group by d.cestaGarantias.numIdCestaGarantias ");
      hql.append(" having sum(d.quantidadeGarantia) = 0 ");

      return gp.find(hql.toString(), new Object[] { StatusCestaDO.VINCULADA });
   }

   public boolean ehIFVinculado(Id identificadorIF) {
      IGerenciadorPersistencia gp = getGp();
      Id[] status = { StatusCestaIFDO.VINCULADA.getNumIdStatusCesta(),
            StatusCestaIFDO.INADIMPLENTE.getNumIdStatusCesta() };
      StringBuffer sql = new StringBuffer(500);

      sql.append("select count(*) from ").append(CestaGarantiasIFDO.class.getName()).append(
            " as cgi where cgi.instrumentoFinanceiro.id = :idIF").append(
            " and cgi.status.numIdStatusCesta in (:status)");

      IConsulta consulta = gp.criarConsulta(sql.toString());
      consulta.setAtributo("idIF", identificadorIF);
      consulta.setParameterList("status", status);
      List l = consulta.list();
      Integer count = (Integer) l.get(0);

      return (count.intValue() > 0);
   }

   public void cancelaMovimentacaoBloqueio(MovimentacaoGarantiaDO movDesbloq) {
      IGerenciadorPersistencia gp = getGp();
      List l = null;

      StringBuffer hql = new StringBuffer(500);
      hql.append("from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" cg where cg.cestaGarantias = ?");
      hql.append(" and cg.indDireitosGarantidor = ?");
      hql.append(" and cg.qtdGarantia = ?");

      if (movDesbloq.getIndCetipado().ehFalso()) {
         hql.append(" and cg.codIfNCetipado = ?");
         l = gp.find(hql.toString(), new Object[] { movDesbloq.getCestaGarantias(),
               movDesbloq.getIndDireitosGarantidor(), movDesbloq.getQtdGarantia(), movDesbloq.getCodIfNCetipado() });
      } else {
         hql.append(" and cg.instrumentoFinanceiro = ?");
         hql.append(" and cg.statusMovimentacaoGarantia.numIdStatusMovGarantia = 1");
         hql.append(" and cg.tipoMovimentacaoGarantia.numIdTipoMovGarantia = 1 ");
         l = gp.find(hql.toString(), new Object[] { movDesbloq.getCestaGarantias(),
               movDesbloq.getIndDireitosGarantidor(), movDesbloq.getQtdGarantia(),
               movDesbloq.getInstrumentoFinanceiro() });
      }

      if (l.size() > 0) {
         Iterator iter = l.iterator();
         MovimentacaoGarantiaDO movBloq = (MovimentacaoGarantiaDO) iter.next();
         movBloq.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.MOVIMENTACAO_CANCELADA);
      }
   }

   public Id getCestaDeGarantiaContendoIF(CodigoIF ifdo) {
      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(500);
      hql.append("select dg.cestaGarantias.numIdCestaGarantias from ");
      hql.append(DetalheGarantiaDO.class.getName());
      hql
            .append(" dg inner join fetch dg.cestaGarantias cesta inner join fetch cesta.statusCesta status inner join fetch dg.instrumentoFinanceiro ativo where");
      hql.append(" status in (:status)");
      hql.append(" and dg.quantidadeGarantia > 0");
      hql.append(" and ativo.codigoIF = :ativo");

      IConsulta c = gp.criarConsulta(hql.toString());
      c.setAtributo("ativo", ifdo);
      c.setParameterList("status", new Object[] { StatusCestaDO.VINCULADA, StatusCestaDO.INADIMPLENTE });

      List l = c.list();

      if (l.isEmpty()) {
         return null;
      }

      return (Id) l.get(0);
   }

   public CestaGarantiasDO obterCestaGarantindoIF(InstrumentoFinanceiroDO ifDO) {
      if (consultaCestaGarantindoIF == null) {
         StringBuffer hql = new StringBuffer(500);
         hql.append("select distinct c from ");
         hql.append(CestaGarantiasDO.class.getName()).append(" c, ");
         hql.append(CestaGarantiasIFDO.class.getName()).append(" cif ");
         hql.append(" where c.numIdCestaGarantias = cif.cestaGarantia");
         hql.append(" and c.statusCesta in (:statusCesta)");
         hql.append(" and cif.instrumentoFinanceiro = :ativo");

         consultaCestaGarantindoIF = getGp().criarConsulta(hql.toString());
         consultaCestaGarantindoIF.setParameterList("statusCesta", new Object[] { StatusCestaDO.VINCULADA,
               StatusCestaDO.INADIMPLENTE });
      }
      consultaCestaGarantindoIF.setAtributo("ativo", ifDO);

      List l = consultaCestaGarantindoIF.list();
      if (l.size() > 0) {
         return (CestaGarantiasDO) l.get(0);
      }

      return null;
   }

   public NumeroCestaGarantia obterCestaGarantindoIF(Id numIF, Id ponta) {
      IGerenciadorPersistencia gp = getGp();

      if (consultaCestaGarantidoraAtivo == null) {
         StringBuffer hql = new StringBuffer(500);
         hql.append("select distinct c.numIdCestaGarantias from ");
         hql.append(CestaGarantiasDO.class.getName()).append(" c, ");
         hql.append(CestaGarantiasIFDO.class.getName()).append(" cif ");
         hql.append(" where c.numIdCestaGarantias = cif.cestaGarantia");
         hql.append(" and c.statusCesta in (:statusCesta)");
         hql.append(" and cif.instrumentoFinanceiro.id = :numIF ");
         hql.append(" and c.parametroPonta.idParametroPonta = :ponta");

         consultaCestaGarantidoraAtivo = gp.criarConsulta(hql.toString());
         consultaCestaGarantidoraAtivo.setParameterList("statusCesta", new Object[] { StatusCestaDO.VINCULADA,
               StatusCestaDO.INADIMPLENTE });
      }

      consultaCestaGarantidoraAtivo.setAtributo("numIF", numIF);
      consultaCestaGarantidoraAtivo.setAtributo("ponta", ponta);

      List l = consultaCestaGarantidoraAtivo.list();
      NumeroCestaGarantia numero = null;
      if (l.size() == 1) {
         numero = new NumeroCestaGarantia(((Id) l.get(0)).obterConteudo());
      }

      return numero;
   }

   public NumeroCestaGarantia obterCestaGarantindoIF(Id numIf) {
      IGerenciadorPersistencia gp = getGp();

      if (consultaCestaGarantidoraAtivo == null) {
         StringBuffer hql = new StringBuffer(500);
         hql.append("select distinct c.numIdCestaGarantias from ");
         hql.append(CestaGarantiasDO.class.getName()).append(" c, ");
         hql.append(CestaGarantiasIFDO.class.getName()).append(" cif ");
         hql.append(" where c.numIdCestaGarantias = cif.cestaGarantia");
         hql.append(" and c.statusCesta in (:statusCesta)");
         hql.append(" and cif.instrumentoFinanceiro.id = :numIF");

         consultaCestaGarantidoraAtivo = gp.criarConsulta(hql.toString());
         consultaCestaGarantidoraAtivo.setParameterList("statusCesta", new Object[] { StatusCestaDO.VINCULADA,
               StatusCestaDO.INADIMPLENTE });
      }
      consultaCestaGarantidoraAtivo.setAtributo("numIF", numIf);

      List l = consultaCestaGarantidoraAtivo.list();
      NumeroCestaGarantia numero = null;
      if (l.size() == 1) {
         numero = new NumeroCestaGarantia(((Id) l.get(0)).obterConteudo());
      }

      return numero;
   }

   public boolean ativoGaranteCesta(Id numIf) {
      IGerenciadorPersistencia gp = getGp();

      if (consAtivoGaranteCesta == null) {
         StringBuffer hql = new StringBuffer(200);
         hql.append("select count(*) from ");
         hql.append(DetalheGarantiaDO.class.getName());
         hql.append(" dg where dg.instrumentoFinanceiro.id = :id");
         hql.append(" and ((dg.quantidadeGarantia > 0 and dg.cestaGarantias.statusCesta = :idVinc) or");
         hql.append("     (dg.quantidadeGarantia >= 0 and dg.cestaGarantias.statusCesta = :idInad))");

         consAtivoGaranteCesta = gp.criarConsulta(hql.toString());
         consAtivoGaranteCesta.setAtributo("idVinc", StatusCestaDO.VINCULADA);
         consAtivoGaranteCesta.setAtributo("idInad", StatusCestaDO.INADIMPLENTE);
         consAtivoGaranteCesta.setCacheable(true);
         consAtivoGaranteCesta.setCacheRegion("MMG");
      }

      consAtivoGaranteCesta.setAtributo("id", numIf);

      List l = consAtivoGaranteCesta.list();
      Integer count = (Integer) l.get(0);

      return count.intValue() > 0;
   }

   public List listarCestasSegundoNivel(CestaGarantiasDO cesta) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Listagem de cestas abaixo da cesta: " + cesta);
      }

      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(500);
      hql.append("select c from ");
      hql.append("   CestaGarantiasDO c,    ");
      hql.append("   CestaGarantiasIFDO cgi ");
      hql.append(" where (cgi.status = :statusCesta or cgi.status = :statusCesta2)");
      hql.append(" and c.numIdCestaGarantias = cgi.cestaGarantia and cgi.instrumentoFinanceiro.id in (");
      hql.append("    select d.instrumentoFinanceiro.id from DetalheGarantiaDO d ");
      hql.append("    where d.cestaGarantias.numIdCestaGarantias = :idCesta ");
      hql.append("      and d.quantidadeGarantia > 0 )");

      List l = gp.find(hql.toString(), new Object[] { StatusCestaDO.VINCULADA, StatusCestaDO.INADIMPLENTE,
            cesta.getNumIdCestaGarantias() });

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Encontrou " + l.size() + " cestas em segundo nivel");
      }

      return l;
   }

   public void retirarCestaDeCesta(CestaGarantiasDO cesta) {
      if (cesta.getIndSegundoNivel().ehFalso()) {
         return;
      }

      IMovimentacoesGarantias imovs = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO mov = imovs.incluirMovimentacaoRetiradaLastro(cesta);

      acionaMIG(mov, Booleano.VERDADEIRO, null);
   }

   public void bloquearCestaDeCesta(CestaGarantiasDO cesta) {
      if (cesta.getIndSegundoNivel().ehVerdadeiro()) {
         return;
      }

      IMovimentacoesGarantias imovs = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO mov = imovs.incluirMovimentacaoBloqueioLastro(cesta);

      acionaMIG(mov, Booleano.VERDADEIRO, null);
   }

   public CestaGarantiasIFDO obterIFDOVinculado(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ifDO) {
      Iterator i = cesta.getAtivosVinculados().iterator();
      while (i.hasNext()) {
         CestaGarantiasIFDO cgi = (CestaGarantiasIFDO) i.next();
         if (cgi.getInstrumentoFinanceiro().getId().mesmoConteudo(ifDO.getId())) {
            return cgi;
         }
      }

      return null;
   }

   public boolean possuiVinculacao(CestaGarantiasDO cesta) {
      Set s = cesta.getAtivosVinculados();

      if (s != null && s.size() > 0) {
         return true;
      }

      return false;
   }

   public NumeroInteiro obtemQtdAtivosVinculados(NumeroCestaGarantia numCesta) {
      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(500);
      hql.append("select count(*) from ");
      hql.append(CestaGarantiasIFDO.class.getName());
      hql.append(" cg where cg.cestaGarantia = ?");

      List l = gp.find(hql.toString(), new Id(numCesta.toString()));
      Integer count = (Integer) l.get(0);

      return new NumeroInteiro(count.toString());
   }

   public boolean ehCestaSegundoNivel(CestaGarantiasDO cesta) {
      if (!Condicional.vazio(cesta.getIndSegundoNivel())) {
         return cesta.getIndSegundoNivel().ehVerdadeiro();
      }
      boolean ativoVincGaranteOutraCesta = false;

      Iterator i = cesta.getAtivosVinculados().iterator();
      while (i.hasNext()) {
         InstrumentoFinanceiroDO ifDO = ((CestaGarantiasIFDO) i.next()).getInstrumentoFinanceiro();

         // Verifica se o IF vinculado esta dentro de alguma outra cesta e
         // esta por sua vez, esteja vinculada.
         if (ativoGaranteCesta(ifDO.getId())) {
            ativoVincGaranteOutraCesta = true;
            break;
         }
      }

      // S - segundo nivel; N - primeiro nivel
      return ativoVincGaranteOutraCesta;
   }

   public boolean cestaPossuiAtivoGarantidoComOperacaoPendente(CestaGarantiasDO cesta) {
      String hql = "select count(op) from OperacaoDO op, CestaGarantiasIFDO c where "
            + "op.instrumentoFinanceiro.id = c.instrumentoFinanceiro.id and c.cestaGarantia = ?"
            + " and op.situacaoOperacao.terminal = ? ";

      List l = getGp().find(hql, new Object[] { cesta.getNumIdCestaGarantias(), Booleano.FALSO });
      Integer count = (Integer) l.get(0);

      return count.intValue() > 0;
   }

   public boolean ativoGarantidoPossuiMovimentacoes(InstrumentoFinanceiroDO ativo) {
      CodigoIF codigoIF = ativo.getCodigoIF();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Verificando se ativo garantido " + codigoIF + " nao possui movimentacoes pendentes...");
      }

      AtributosColunados ac = new AtributosColunados();
      ac.atributo(codigoIF);

      boolean retorno = false;
      try {
         retorno = FabricaDeMotorDeRegra.getMotorDeRegra().avalia(
               ConstantesDeNomeDeRegras.existeOperacaoDeMovimentacaoDeCarteiraEmAberto, ac, false);
      } catch (Exception e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, "Regras Erro: " + e.getMessage());
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Ativo " + codigoIF + " possui movimentacoes pendentes: " + retorno);
      }

      return retorno;
   }

   public boolean verificaCustodiaAtivosCesta(CestaGarantiasDO cesta) {
      return verificaCustodiaAtivo(cesta, null);
   }

   public boolean verificaCustodiaAtivo(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ativo) {
      StringBuffer hql = new StringBuffer();
      hql.append("select carteira.contaParticipante, carteira.tipoPosicaoCarteira ");
      hql.append("  from CarteiraParticipanteDO carteira, ");
      hql.append(" CestaGarantiasIFDO vinculo inner join vinculo.instrumentoFinanceiro ativo");
      hql.append(" where vinculo.cestaGarantia = :idCesta");
      hql.append(" and ativo = carteira.instrumentoFinanceiro");
      hql.append(" and carteira.quantidade > 0 ");
      

      if (ativo != null) {
         hql.append("   and ativo.id = :idAtivo");
      }

      IConsulta c = getGp().criarConsulta(hql.toString());
      c.setAtributo("idCesta", cesta.getNumIdCestaGarantias());

      if (ativo != null) {
         c.setAtributo("idAtivo", ativo.getId());
      }

      List l = c.list();

      for (Iterator i = l.iterator(); i.hasNext();) {
         Object[] linha = (Object[]) i.next();
         ContaParticipanteDO participante = (ContaParticipanteDO) linha[0];
         TipoPosicaoCarteiraDO tipoPosCarteira = (TipoPosicaoCarteiraDO) linha[1];

         if (!participante.getId().mesmoConteudo(cesta.getGarantidor().getId())
               || !tipoPosCarteira.getCodigo().mesmoConteudo(TipoPosicaoCarteiraDO.PROPRIA_LIVRE)) {
            return false;
         }
      }

      return true;
   }

   public DetalheGarantiaDO obterGarantiaCesta(CestaGarantiasDO cesta, GarantiaVO vo) {
      Atributo codIF = vo.getCodGarantia();

      String query = "select dg from DetalheGarantiaDO dg left join dg.instrumentoFinanceiro i "
            + "where dg.cestaGarantias = ? and ((dg.instrumentoFinanceiro is not null and i.codigoIF = ?) "
            + "or (dg.instrumentoFinanceiro is null and dg.codIfNCetipado = ?))";

      List detalhesGarantia = getGp().find(query, new Object[] { cesta, codIF, codIF });

      DetalheGarantiaDO retorno = null;
      if (!detalhesGarantia.isEmpty()) {
         retorno = (DetalheGarantiaDO) detalhesGarantia.get(0);
      }

      return retorno;
   }

   public CodigoTipoIF obterTipoIFGarantidoCesta(CestaGarantiasDO cesta) {
      String hql = "select distinct(c.instrumentoFinanceiro.tipoIF.codigoTipoIF) from CestaGarantiasIFDO c where c.cestaGarantia = ?";
      List l = getGp().find(hql, cesta.getNumIdCestaGarantias());

      CodigoTipoIF codTipoIF = null;
      if (!l.isEmpty()) {
         codTipoIF = (CodigoTipoIF) l.get(0);
      }

      return codTipoIF;
   }

}
