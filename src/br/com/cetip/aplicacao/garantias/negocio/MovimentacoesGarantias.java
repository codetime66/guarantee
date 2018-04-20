package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiaVO;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

final class MovimentacoesGarantias extends BaseGarantias implements IMovimentacoesGarantias {

   private IConsulta consultaUltimaMov;
   private IConsulta consultaMovimentacaoAtivo;
   private IConsulta existeMovimentacaoAtivo;

   public MovimentacaoGarantiaDO incluirMovimentacaoDesbloqueio(DetalheGarantiaDO garantia) {
      final boolean ehCetipado = garantia.getIndCetipado().ehVerdadeiro();
      boolean ehSelic = false;

      if (ehCetipado) {
         ehSelic = garantia.getInstrumentoFinanceiro().getSistema().getNumero().mesmoConteudo(SistemaDO.SELIC);
      }

      MovimentacaoGarantiaDO movimentacao = new MovimentacaoGarantiaDO();
      movimentacao.setCestaGarantias(garantia.getCestaGarantias());
      movimentacao.setCodIfNCetipado(garantia.getCodIfNCetipado());
      movimentacao.setContaParticipante(garantia.getCestaGarantias().getGarantidor());
      movimentacao.setDataMovimentacao(getDataHoje());
      movimentacao.setIndCetipado(garantia.getIndCetipado());
      movimentacao.setIndDireitosGarantidor(garantia.getIndDireitosGarantidor());
      movimentacao.setInstrumentoFinanceiro(garantia.getInstrumentoFinanceiro());
      movimentacao.setQtdGarantia(garantia.getQuantidadeGarantia());
      movimentacao.setStatusMovimentacaoGarantia(ehSelic || !ehCetipado ? StatusMovimentacaoGarantiaDO.OK
            : StatusMovimentacaoGarantiaDO.PENDENTE);
      movimentacao.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.DESBLOQUEIO);
      movimentacao.setTxtDescricao(garantia.getTxtDescricao());

      garantia.getCestaGarantias().getMovimentacoes().add(movimentacao);
      getGp().save(movimentacao);

      return movimentacao;
   }

   public MovimentacaoGarantiaDO incluirMovimentacaoExclusao(CestaGarantiasDO cesta) {
      MovimentacaoGarantiaDO mov = new MovimentacaoGarantiaDO();
      mov.setQtdGarantia(new Quantidade("0"));
      mov.setIndCetipado(Booleano.FALSO);
      mov.setIndDireitosGarantidor(Booleano.VERDADEIRO);
      mov.setCestaGarantias(cesta);
      mov.setDataMovimentacao(getDataHoje());
      mov.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.EXCLUSAO);
      mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);

      cesta.getMovimentacoes().add(mov);
      getGp().save(mov);

      return mov;
   }

   public MovimentacaoGarantiaDO incluirMovimentacaoDesvinculacao(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ifDO) {
      MovimentacaoGarantiaDO itemDO = new MovimentacaoGarantiaDO();
      itemDO.setInstrumentoFinanceiro(ifDO);
      itemDO.setIndCetipado(Booleano.VERDADEIRO);
      itemDO.setQtdGarantia(new Quantidade("0"));
      itemDO.setIndDireitosGarantidor(Booleano.VERDADEIRO);
      itemDO.setCestaGarantias(cesta);
      itemDO.setDataMovimentacao(getDataHoje());
      itemDO.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.DESVINCULACAO);
      itemDO.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);

      // salva movimentacao de desvinculacao como pendente
      cesta.getMovimentacoes().add(itemDO);
      getGp().save(itemDO);

      return itemDO;
   }

   public MovimentacaoGarantiaDO incluirMovimentacaoTransferencia(CestaGarantiasDO cesta,
         ContaParticipanteDO contaParticipante, InstrumentoFinanceiroDO ifDO) {
      MovimentacaoGarantiaDO mov = new MovimentacaoGarantiaDO();
      mov.setCestaGarantias(cesta);
      mov.setContaParticipante(contaParticipante);
      mov.setDataMovimentacao(getDataHoje());
      mov.setQtdGarantia(new Quantidade("0"));
      mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);
      mov.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.TRANSFERENCIA);
      mov.setInstrumentoFinanceiro(ifDO);
      mov.setIndCetipado(Booleano.VERDADEIRO);

      cesta.getMovimentacoes().add(mov);
      getGp().save(mov);

      return mov;
   }

   public MovimentacaoGarantiaDO incluirMovimentacaoRetiradaLastro(CestaGarantiasDO cesta) {
      Iterator i = cesta.getAtivosVinculados().iterator();
      InstrumentoFinanceiroDO ifDO = ((CestaGarantiasIFDO) i.next()).getInstrumentoFinanceiro();

      MovimentacaoGarantiaDO mov = new MovimentacaoGarantiaDO();
      mov.setCestaGarantias(cesta);
      mov.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.RETIRADA_EM_LASTRO);
      mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);
      mov.setIndDireitosGarantidor(Booleano.FALSO);
      mov.setQtdGarantia(new Quantidade("0"));
      mov.setDataMovimentacao(getDataHoje());
      mov.setTxtDescricao(new Texto(""));
      mov.setInstrumentoFinanceiro(ifDO);
      mov.setIndCetipado(Booleano.VERDADEIRO);

      cesta.getMovimentacoes().add(mov);
      getGp().save(mov);

      return mov;
   }

   public MovimentacaoGarantiaDO incluirMovimentacaoBloqueioLastro(CestaGarantiasDO cesta) {
      Iterator i = cesta.getAtivosVinculados().iterator();
      InstrumentoFinanceiroDO ifDO = ((CestaGarantiasIFDO) i.next()).getInstrumentoFinanceiro();

      MovimentacaoGarantiaDO mov = new MovimentacaoGarantiaDO();
      mov.setCestaGarantias(cesta);
      mov.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.BLOQUEIO_EM_LASTRO);
      mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);
      mov.setTxtDescricao(new Texto(""));
      mov.setIndDireitosGarantidor(Booleano.FALSO);
      mov.setQtdGarantia(new Quantidade("0"));
      mov.setDataMovimentacao(getDataHoje());
      mov.setInstrumentoFinanceiro(ifDO);
      mov.setIndCetipado(Booleano.VERDADEIRO);

      cesta.getMovimentacoes().add(mov);
      getGp().save(mov);

      return mov;
   }

   public MovimentacaoGarantiaDO incluirMovimentacaoLiberacao(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ifDO) {
      MovimentacaoGarantiaDO itemDO = new MovimentacaoGarantiaDO();
      itemDO.setQtdGarantia(new Quantidade("0"));
      itemDO.setIndDireitosGarantidor(Booleano.VERDADEIRO);
      itemDO.setCestaGarantias(cesta);
      itemDO.setDataMovimentacao(getDataHoje());
      itemDO.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.LIBERACAO);
      itemDO.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);
      itemDO.setInstrumentoFinanceiro(ifDO);
      itemDO.setCodIfNCetipado(null);
      itemDO.setIndCetipado(Booleano.VERDADEIRO);
      itemDO.setContaParticipante(cesta.getGarantido());

      // salva movimentacao de desvinculacao como pendente
      cesta.getMovimentacoes().add(itemDO);
      getGp().save(itemDO);

      return itemDO;
   }

   public MovimentacaoGarantiaDO incluirMovimentacaoLiberacaoParcial(DetalheGarantiaDO garantia, Quantidade quantidade,
         CodigoContaCetip destino, NumeroOperacao numeroOperacao) {
      IGerenciadorPersistencia gp = getGp();

      MovimentacaoGarantiaDO libParcial = new MovimentacaoGarantiaDO();
      libParcial.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.liberacaoParcialPorTipo(garantia
            .getCestaGarantias().getTipoGarantia()));

      if (garantia.ehGarantiaCetipada() == false) {
         libParcial.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);
      } else if (garantia.getInstrumentoFinanceiro().getSistema().getNumero().mesmoConteudo(SistemaDO.SELIC)) {
         libParcial.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611);
      } else {
         libParcial.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);
      }

      libParcial.setInstrumentoFinanceiro(garantia.getInstrumentoFinanceiro());
      libParcial.setCodIfNCetipado(garantia.getCodIfNCetipado());
      libParcial.setIndDireitosGarantidor(garantia.getIndDireitosGarantidor());
      libParcial.setIndCetipado(garantia.getIndCetipado());
      libParcial.setDataMovimentacao(getDataHoje());
      libParcial.setQtdGarantia(quantidade);
      libParcial.setCestaGarantias(garantia.getCestaGarantias());
      libParcial.setNumOperacao(numeroOperacao);

      IContaParticipante icp = ContaParticipanteFactory.getInstance();
      ContaParticipanteDO participante = icp.obterContaParticipanteDO(destino);

      libParcial.setContaParticipante(participante);

      gp.save(libParcial);

      return libParcial;
   }

   public MovimentacaoGarantiaDO incluirMovimentacaoVinculacao(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ifDO) {
      MovimentacaoGarantiaDO mov = new MovimentacaoGarantiaDO();
      mov.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.VINCULACAO);
      mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);
      mov.setCestaGarantias(cesta);
      mov.setContaParticipante(cesta.getGarantidor());
      mov.setQtdGarantia(new Quantidade("0"));
      mov.setIndDireitosGarantidor(Booleano.VERDADEIRO);
      mov.setDataMovimentacao(getDataHoje());
      mov.setIndDepositado(Booleano.VERDADEIRO);
      mov.setInstrumentoFinanceiro(ifDO);
      mov.setIndCetipado(Booleano.VERDADEIRO);

      cesta.getMovimentacoes().add(mov);
      getGp().save(mov);

      return mov;
   }

   public MovimentacaoGarantiaDO obterUltimaMovimentacao(CestaGarantiasDO cesta, TipoMovimentacaoGarantiaDO tipo,
         StatusMovimentacaoGarantiaDO status) {
      if (consultaUltimaMov == null) {
         StringBuffer hql = new StringBuffer();
         hql.append(" select m from ");
         hql.append(MovimentacaoGarantiaDO.class.getName());
         hql.append(" m inner join m.cestaGarantias c ");
         hql.append(" inner join m.tipoMovimentacaoGarantia t ");
         hql.append(" inner join m.statusMovimentacaoGarantia s ");
         hql.append(" where t = :tipo and c = :cesta and s = :status order by m.numIdMovimentacaoGarantia desc");

         consultaUltimaMov = getGp().criarConsulta(hql.toString());
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Criterio para pesquisa: ");
         Logger.debug(this, "Cesta: " + cesta);
         Logger.debug(this, "Tipo: " + tipo);
         Logger.debug(this, "Status: " + status);
      }

      consultaUltimaMov.setAtributo("cesta", cesta);
      consultaUltimaMov.setAtributo("tipo", tipo);
      consultaUltimaMov.setAtributo("status", status);
      List l = consultaUltimaMov.list();

      if (l.size() > 1) {
         StringBuffer erro = new StringBuffer(200);
         erro.append("Mais de uma Movimentacao encontrada para o criterio: [");
         erro.append("Cesta= " + cesta + ", ");
         erro.append("Tipo= " + tipo + ", ");
         erro.append("Status= " + status + "]");
         Logger.error(this, erro.toString());
         throw new Erro(CodigoErro.REGISTRO_INVALIDO);
      }

      if (l.size() == 1) {
         return (MovimentacaoGarantiaDO) l.get(0);
      }

      return null;
   }

   public MovimentacaoGarantiaDO criarMovimentacaoBloqueio(GarantiaVO garantia) {
      MovimentacaoGarantiaDO movimentacao = new MovimentacaoGarantiaDO();
      movimentacao.setQtdGarantia(garantia.quantidade);
      movimentacao.setIndDireitosGarantidor(garantia.indDireitoGarantidor);
      movimentacao.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.BLOQUEIO);
      movimentacao.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);
      movimentacao.setDataMovimentacao(getDataHoje());
      movimentacao.setTxtDescricao(garantia.descricao);
      movimentacao.setNumOperacao(garantia.numeroOperacao);

      garantia.associarGarantia(movimentacao);

      return movimentacao;
   }

   public MovimentacaoGarantiaDO incluirMovimentacaoBloqueio(CestaGarantiasDO cesta, GarantiaVO garantia) {
      MovimentacaoGarantiaDO movimentacao = criarMovimentacaoBloqueio(garantia);

      // salva a movimentacao dentro da cesta
      if (cesta.getMovimentacoes() == null) {
         cesta.setMovimentacoes(new HashSet());
      }

      movimentacao.setCestaGarantias(cesta);
      cesta.getMovimentacoes().add(movimentacao);

      IGerenciadorPersistencia gp = getGp();
      gp.save(movimentacao);
      gp.update(cesta);

      return movimentacao;
   }

   public MovimentacaoGarantiaDO incluirMovimentacaoAporte(CestaGarantiasDO cesta, GarantiaVO garantia) {
      MovimentacaoGarantiaDO movimentacao = incluirMovimentacaoBloqueio(cesta, garantia);
      movimentacao.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.APORTE);
      movimentacao.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);
      getGp().update(movimentacao);
      return movimentacao;
   }

   public MovimentacaoGarantiaDO obterMovimentacaoParaAtivo(CestaGarantiasDO cesta, Atributo codGarantia,
         TipoMovimentacaoGarantiaDO tipo, StatusMovimentacaoGarantiaDO status) {

      Logger.debug("Obtendo movimentacao para ativo");

      if (consultaMovimentacaoAtivo == null) {
         StringBuffer hql = new StringBuffer(500);
         hql.append("select cg from ");
         hql.append(MovimentacaoGarantiaDO.class.getName());
         hql.append(" cg left join cg.instrumentoFinanceiro ativoCetipado");
         hql.append(" where cg.cestaGarantias = :cesta");
         hql.append(" and :ativo in (ativoCetipado.codigoIF, cg.codIfNCetipado) ");
         hql.append(" and cg.statusMovimentacaoGarantia = :statusMov");
         hql.append(" and cg.tipoMovimentacaoGarantia = :tipoMov");

         consultaMovimentacaoAtivo = getGp().criarConsulta(hql.toString());
      }

      consultaMovimentacaoAtivo.setAtributo("cesta", cesta);
      consultaMovimentacaoAtivo.setAtributo("ativo", codGarantia);
      consultaMovimentacaoAtivo.setAtributo("statusMov", status);
      consultaMovimentacaoAtivo.setAtributo("tipoMov", tipo);

      List l = consultaMovimentacaoAtivo.list();

      if (l.size() == 1) {
         return (MovimentacaoGarantiaDO) l.get(0);
      } else if (l.size() > 1) {
         throw new IllegalStateException("Cesta possui mais de uma movimentacao com mesmos valores/tipo/status: "
               + cesta.getNumIdCestaGarantias() + "/" + tipo + "/" + status + " para a mesma garantia: " + codGarantia);
      }

      return null;
   }

   public boolean existeMovimentacaoParaAtivo(CestaGarantiasDO cesta, CodigoIF codigoIFNaoCetipado,
         TipoMovimentacaoGarantiaDO tipo, StatusMovimentacaoGarantiaDO status) {
      IGerenciadorPersistencia gp = getGp();

      if (existeMovimentacaoAtivo == null) {
         StringBuffer hql = new StringBuffer(500);
         hql.append("select count(cg) from ");
         hql.append(MovimentacaoGarantiaDO.class.getName());
         hql.append(" cg left join cg.instrumentoFinanceiro ativoCetipado where cg.cestaGarantias = :cesta");
         hql.append(" and (ativoCetipado.codigoIF = :ativo or cg.codIfNCetipado = :codNCetipado)");
         hql.append(" and cg.statusMovimentacaoGarantia = :statusMov");
         hql.append(" and cg.tipoMovimentacaoGarantia = :tipoMov");

         existeMovimentacaoAtivo = gp.criarConsulta(hql.toString());
      }

      existeMovimentacaoAtivo.setAtributo("cesta", cesta);
      existeMovimentacaoAtivo.setAtributo("ativo", codigoIFNaoCetipado);
      existeMovimentacaoAtivo.setAtributo("codNCetipado", codigoIFNaoCetipado);
      existeMovimentacaoAtivo.setAtributo("statusMov", status);
      existeMovimentacaoAtivo.setAtributo("tipoMov", tipo);

      List l = existeMovimentacaoAtivo.list();
      Integer count = (Integer) l.get(0);
      return count.intValue() > 0;
   }

   public List listarMovimentacoes(CestaGarantiasDO cesta, Object[] tipo, Object[] status) {
      StringBuffer b = new StringBuffer(300);
      b.append("select m from ").append(MovimentacaoGarantiaDO.class.getName());
      b.append(" m where ");
      b.append(" m.cestaGarantias = :cesta ");

      boolean temTipo = tipo != null && tipo.length > 0;
      if (temTipo) {
         b.append(" and m.tipoMovimentacaoGarantia in (:tipo) ");
      }

      boolean temStatus = status != null && status.length > 0;
      if (temStatus) {
         b.append(" and m.statusMovimentacaoGarantia in (:status)");
      }

      IConsulta consultaListaMovimentacoes = getGp().criarConsulta(b.toString());
      consultaListaMovimentacoes.setAtributo("cesta", cesta);

      if (temTipo) {
         consultaListaMovimentacoes.setParameterList("tipo", tipo);
      }

      if (temStatus) {
         consultaListaMovimentacoes.setParameterList("status", status);
      }

      List l = consultaListaMovimentacoes.list();
      if (l == null) {
         return Collections.EMPTY_LIST;
      }

      return l;
   }

   public MovimentacaoGarantiaDO incluirMovimentacaoControleSelic(DetalheGarantiaDO garantia) {
      MovimentacaoGarantiaDO movimentacao = new MovimentacaoGarantiaDO();
      movimentacao.setCestaGarantias(garantia.getCestaGarantias());
      movimentacao.setCodIfNCetipado(garantia.getCodIfNCetipado());
      movimentacao.setInstrumentoFinanceiro(garantia.getInstrumentoFinanceiro());
      movimentacao.setContaParticipante(garantia.getCestaGarantias().getGarantidor());
      movimentacao.setDataMovimentacao(getDataHoje());
      movimentacao.setIndCetipado(garantia.getIndCetipado());
      movimentacao.setIndDireitosGarantidor(garantia.getIndDireitosGarantidor());
      movimentacao.setQtdGarantia(garantia.getQuantidadeGarantia());
      movimentacao.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611);
      movimentacao.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.CONTROLE_FLUXO_SELIC);
      movimentacao.setTxtDescricao(garantia.getTxtDescricao());

      garantia.getCestaGarantias().getMovimentacoes().add(movimentacao);
      return movimentacao;
   }

   public MovimentacaoGarantiaDO obterMovimentacaoParaGarantiaExterna(NumeroOperacao codigoOperacaoExterna,
         TipoMovimentacaoGarantiaDO tipo, StatusMovimentacaoGarantiaDO status) {

      TipoMovimentacaoGarantiaDO tipoParaFiltro = tipo;
      IGerenciadorPersistencia gp = getGp();

      StringBuffer hql = new StringBuffer(500);
      hql.append("select mg from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" mg where mg.numOperacao = :numOpMov");
      hql.append(" and trunc(mg.dataMovimentacao) = trunc(:dataMov)");

      if (tipo != null) {
         hql.append(" and mg.tipoMovimentacaoGarantia = :tipoMov");
      } else {
         hql.append(" and mg.tipoMovimentacaoGarantia != :tipoMov");
         tipoParaFiltro = TipoMovimentacaoGarantiaDO.BLOQUEIO;
      }

      if (status != null) {
         hql.append(" and mg.statusMovimentacaoGarantia = :statusMov");
      }

      IConsulta consultaMovimentacaoExterna = gp.criarConsulta(hql.toString());
      consultaMovimentacaoExterna.setAtributo("numOpMov", codigoOperacaoExterna);
      consultaMovimentacaoExterna.setAtributo("dataMov", getDataHoje());
      consultaMovimentacaoExterna.setAtributo("tipoMov", tipoParaFiltro);

      if (status != null) {
         consultaMovimentacaoExterna.setAtributo("statusMov", status);
      }

      List l = consultaMovimentacaoExterna.list();

      if (l.size() == 1) {
         return (MovimentacaoGarantiaDO) l.get(0);
      } else if (l.size() > 1) {
         throw new IllegalStateException(
               "Encontrada mais de uma movimentacao com mesmos valores/tipo/status/operacao_externa: " + "/" + tipo
                     + "/" + status + "/" + codigoOperacaoExterna);
      }

      return null;
   }

   /**
    * Verifica se existem movimentacoes do sistema especificado pelo parametro sistema 
    * do tipo CONTROLE_FLUXO_SELIC e com o status especificado na cesta
    * @param cesta
    * @param status
    * @param sistema
    * @return
    */
   public boolean cestaAguardandoMovimentacaoExterna(CestaGarantiasDO cesta, TipoMovimentacaoGarantiaDO tipo,
         Object[] status, Id sistema) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Verificando se existem movimentacoes externas na cesta com status " + status);
      }

      IGerenciadorPersistencia gp = getGp();

      StringBuffer hql = new StringBuffer(500);

      hql.append("select count(cg) from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" cg left join cg.instrumentoFinanceiro ativoCetipado where ");
      hql.append(" ativoCetipado.sistema = :sistema");
      hql.append(" and cg.statusMovimentacaoGarantia  in (:statusMovimentacao)");
      hql.append(" and cg.tipoMovimentacaoGarantia = :tipoMovimentacao");
      hql.append(" and cg.cestaGarantias = :cesta ");

      IConsulta consulta = gp.criarConsulta(hql.toString());

      consulta.setParameterList("statusMovimentacao", status);
      consulta.setAtributo("tipoMovimentacao", tipo);
      consulta.setAtributo("sistema", sistema);
      consulta.setAtributo("cesta", cesta);

      List l = consulta.list();

      Integer count = (Integer) l.get(0);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Total de Movimentacoes externas do tipo: " + tipo + " e status: " + status + " ="
               + count.intValue());
      }
      return count.intValue() > 0;

   }

   public MovimentacaoGarantiaDO incluirMovimentacaoRetirada(DetalheGarantiaDO garantia, Quantidade quantidadeGarantia) {
      IGerenciadorPersistencia gp = getGp();
      CestaGarantiasDO cesta = garantia.getCestaGarantias();

      MovimentacaoGarantiaDO mov = new MovimentacaoGarantiaDO();
      mov.setCestaGarantias(cesta);
      mov.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.RETIRADA);
      mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);
      mov.setIndCetipado(garantia.getIndCetipado());
      mov.setTxtDescricao(garantia.getTxtDescricao());
      mov.setIndDireitosGarantidor(garantia.getIndDireitosGarantidor());
      mov.setQtdGarantia(quantidadeGarantia);
      mov.setCodIfNCetipado(garantia.getCodIfNCetipado());
      mov.setDataMovimentacao(getDataHoje());
      mov.setInstrumentoFinanceiro(garantia.getInstrumentoFinanceiro());
      mov.setContaParticipante(cesta.getGarantidor());

      cesta.getMovimentacoes().add(cesta);
      gp.save(mov);

      return mov;
   }

}
