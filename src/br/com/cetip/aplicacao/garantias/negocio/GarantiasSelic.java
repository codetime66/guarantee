package br.com.cetip.aplicacao.garantias.negocio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IAportarGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IConsultaCestasPorAtivo;
import br.com.cetip.aplicacao.garantias.apinegocio.IContaGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IDeletaCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IEventoSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IMensageriaGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.ITransferirCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularContratoCesta;
import br.com.cetip.aplicacao.sap.apinegocio.ConsultaContaFactory;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IConsultaConta;
import br.com.cetip.dados.aplicacao.custodia.PosicaoSelicDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.ContratoCestaGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.GarantidorCestaIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.NotificacaoEventoVO;
import br.com.cetip.dados.aplicacao.mensageria.selic.TransferenciaCustodiaVO;
import br.com.cetip.dados.aplicacao.operacao.DetalheCaucaoDO;
import br.com.cetip.dados.aplicacao.operacao.OperacaoDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sap.SituacaoContaDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.expressao.TipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.ValorMonetario;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.persistencia.NivelLock;
import br.com.cetip.infra.servico.contexto.ContextoAtivacao;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;

class GarantiasSelic extends BaseGarantias implements IGarantiasSelic {

   private static Date dateHoje = null;
   private static String dateHojeFormat = null;
   private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("ddMMyy");

   public List obterAtivosSelicMMG(CodigoTipoIF codigoTipoIF, CodigoIF codigoIF) {
      IGerenciadorPersistencia gp = getGp();

      StringBuffer hql = new StringBuffer("select i.id, t.codigoTipoIF, i.codigoIF, ");
      hql.append("sis.codSistema, s.indAtivo, s.tipoColateral, tp.codigoTipoIF, ");
      hql.append("s.dataExclusao");
      hql.append(" from ").append(GarantidorCestaIFDO.class.getName()).append(" s");
      hql.append("   right join s.instrumentoFinanceiro i");
      hql.append("   right join s.instrumentoFinanceiro.tipoIF t");
      hql.append("   right join s.instrumentoFinanceiro.sistema sis");
      hql.append("   left  join s.tipoIF tp");
      hql.append(" where i.tipoIF.codigoTipoIF = :codigoTipoIF");
      hql.append("       and i.dataHoraExclusao IS NULL and i.dataVencimento > :dataHoje");

      if (codigoIF != null && !codigoIF.vazio()) {
         hql.append(" and i.codigoIF = :codigoIF");
      }

      IConsulta consulta = gp.criarConsulta(hql.toString());
      consulta.setAtributo("codigoTipoIF", codigoTipoIF);

      if (codigoIF != null && !codigoIF.vazio()) {
         consulta.setAtributo("codigoIF", codigoIF);
      }

      consulta.setAtributo("dataHoje", getDataHoje());

      return consulta.list();
   }

   public GarantidorCestaIFDO obterAtivoSelicMMG(CodigoIF codigoIF) {
      StringBuffer hql = new StringBuffer("select s");
      hql.append(" from ").append(GarantidorCestaIFDO.class.getName()).append(" s");
      hql.append("   right join s.instrumentoFinanceiro i");
      hql.append("   right join s.instrumentoFinanceiro.tipoIF t");
      hql.append("   right join s.instrumentoFinanceiro.sistema sis");
      hql.append("   left  join s.tipoIF tp");
      hql.append(" where i.codigoIF = :codigoIF");
      hql.append("       and i.dataHoraExclusao IS NULL and i.dataVencimento > :dataHoje");
      hql.append("       and s.dataExclusao IS NULL");

      IConsulta consulta = getGp().criarConsulta(hql.toString());
      consulta.setAtributo("codigoIF", codigoIF);
      consulta.setAtributo("dataHoje", getDataHoje());

      List ativos = consulta.list();

      if (ativos == null || ativos.size() == 0) {
         throw new Erro(CodigoErro.MMG_SELIC_NAO_HABILITADO);
      } else if (ativos.size() > 1) {
         throw new Erro(CodigoErro.ENCONTRADO_MAIS_DE_UM_INSTRUMENTO_FINANCEIRO);
      } else {
         return (GarantidorCestaIFDO) ativos.get(0);
      }
   }

   public GarantidorCestaIFDO obterQualquerAtivoSelicMMG(CodigoIF codigoIF) {
      StringBuffer hql = new StringBuffer("select s");
      hql.append(" from ").append(GarantidorCestaIFDO.class.getName()).append(" s");
      hql.append("   right join s.instrumentoFinanceiro i");
      hql.append("   right join s.instrumentoFinanceiro.tipoIF t");
      hql.append("   right join s.instrumentoFinanceiro.sistema sis");
      hql.append("   left  join s.tipoIF tp");
      hql.append(" where i.codigoIF = :codigoIF");
      hql.append("       and i.dataHoraExclusao IS NULL and i.dataVencimento > :dataHoje");

      IConsulta consulta = getGp().criarConsulta(hql.toString());
      consulta.setAtributo("codigoIF", codigoIF);
      consulta.setAtributo("dataHoje", getDataHoje());

      List ativos = consulta.list();

      if (ativos == null || ativos.size() == 0) {
         return null;
      } else if (ativos.size() > 1) {
         throw new Erro(CodigoErro.ENCONTRADO_MAIS_DE_UM_INSTRUMENTO_FINANCEIRO);
      } else {
         return (GarantidorCestaIFDO) ativos.get(0);
      }
   }

   public InstrumentoFinanceiroDO obterAtivoSelic(CodigoIF codigoIF) {
      StringBuffer sql = new StringBuffer("from ").append(InstrumentoFinanceiroDO.class.getName()).append(
            " as if where if.codigoIF = :codigoIF").append(
            " and if.dataHoraExclusao is null and if.sistema = :sistemaIF");
      IConsulta consulta = getGp().criarConsulta(sql.toString());

      consulta.setAtributo("codigoIF", codigoIF);
      consulta.setAtributo("sistemaIF", SistemaDO.SELIC);
      List ativos = consulta.list();

      if (ativos == null || ativos.size() == 0) {
         throw new Erro(CodigoErro.INSTRUMENTO_FINANCEIRO_NAO_CADASTRADO);
      } else if (ativos.size() > 1) {
         throw new Erro(CodigoErro.ENCONTRADO_MAIS_DE_UM_INSTRUMENTO_FINANCEIRO);
      } else {
         return (InstrumentoFinanceiroDO) ativos.get(0);
      }
   }

   // Consulta Posição de Saldo de Selic
   public List obterPosicaoSaldo(CodigoIF codigoIF, CodigoContaSelic codigoContaSelic) {

      StringBuffer hql = new StringBuffer(300);
      hql.append("select ps from ").append(PosicaoSelicDO.class.getName()).append(" ps");
      hql.append(" ,").append(ContaParticipanteDO.class.getName()).append(" cp");
      hql.append(" ,").append(InstrumentoFinanceiroDO.class.getName()).append(" if");
      hql.append(" where ");
      hql.append(" cp.codContaParticipante = :codigoContaSelic ");
      hql.append(" and cp.id = ps.numIdContaParticipante ");
      hql.append(" and if.codigoIF = :codigoIF ");
      hql.append(" and if.dataHoraExclusao is null ");
      hql.append(" and if.id = ps.numIF ");
      hql.append(" and ps.dataResultado is not null");
      IConsulta consulta = getGp().criarConsulta(hql.toString());

      consulta.setAtributo("codigoIF", codigoIF);
      consulta.setAtributo("codigoContaSelic", codigoContaSelic);

      return consulta.list();
   }

   // Consulta Posição de Saldo de Selic
   public PosicaoSelicDO obterPosicaoSaldo(NumeroControleIF nroControle, Data data) {

      StringBuffer hql = new StringBuffer(300);
      hql.append("select ps from ").append(PosicaoSelicDO.class.getName()).append(" ps");
      hql.append(" where ");
      hql.append("     ps.numeroControleIF = :nroControleIF ");
      hql.append(" and trunc(ps.dataInclusao) = trunc(:data) ");
      IConsulta consulta = getGp().criarConsulta(hql.toString());

      consulta.setAtributo("nroControleIF", nroControle);
      consulta.setAtributo("data", data != null ? data : getDataHoje());

      List posicoes = consulta.list();
      if (posicoes == null || posicoes.size() == 0) {
         throw new Erro(CodigoErro.RESULTADO_INEXISTENTE);
      } else if (posicoes.size() > 1) {
         throw new Erro(CodigoErro.ENCONTRADO_MAIS_DE_UM_RESULTADO);
      } else {
         return (PosicaoSelicDO) posicoes.get(0);
      }
   }

   // Consulta Detalhes da Posição de Saldo de Selic
   public List obterDetalhesPosicaoSaldo(CodigoIF codigoIF) {
      ContextoAtivacaoVO contexto = ContextoAtivacao.getContexto();

      StringBuffer hql = new StringBuffer(500);
      hql.append(" from ").append(DetalheGarantiaDO.class.getName()).append(" dg");
      hql.append(" where dg.instrumentoFinanceiro.codigoIF = :codigoIF");
      hql.append("     and dg.cestaGarantias.statusCesta in (:statusList)");
      hql.append("     and dg.quantidadeGarantia > 0 ");
      if (!contexto.ehCETIP()) {
         hql.append("  and (dg.cestaGarantias.garantidor.codContaParticipante in (:contasList)");
         hql.append("       or dg.cestaGarantias.garantido.codContaParticipante in (:contasList))");
      }

      IConsulta consulta = getGp().criarConsulta(hql.toString());

      consulta.setAtributo("codigoIF", codigoIF);
      consulta.setParameterList("statusList", new Object[] { StatusCestaDO.VINCULADA, StatusCestaDO.INADIMPLENTE });
      if (!contexto.ehCETIP()) {
         consulta.setParameterList("contasList", contexto.getListaContasLiquidacao());
      }

      return consulta.list();
   }

   public boolean numeroOperacaoEhValido(NumeroOperacao numero) {
      // Valida tamanho
      if (numero == null || numero.obterConteudo() == null || numero.obterConteudo().length() != 6) {
         return false;
      }

      // O número informado não pode ser reutilizado e dessa forma, o sistema deve
      // verificar se existem movimentacoes realizadas naquela data com o numero operacao informado
      StringBuffer sql = new StringBuffer("select count(mg) from ").append(MovimentacaoGarantiaDO.class.getName())
            .append(" as mg where trunc(mg.dataMovimentacao) = trunc(:dataHoje)");
      sql.append(" and mg.numOperacao = :noOperacao");
      IConsulta consulta = getGp().criarConsulta(sql.toString());

      consulta.setAtributo("dataHoje", getDataHoje());
      consulta.setAtributo("noOperacao", numero);

      List l = consulta.list();
      Integer count = (Integer) l.get(0);

      return count.intValue() == 0;
   }

   public boolean temSelicNaCesta(CestaGarantiasDO cesta) {
      StringBuffer sql = new StringBuffer("select count(mg) from ").append(MovimentacaoGarantiaDO.class.getName())
            .append(" as mg where mg.cestaGarantias = :cesta");
      sql.append(" and mg.instrumentoFinanceiro.sistema.numero = :numSistema ");

      IConsulta consulta = getGp().criarConsulta(sql.toString());

      consulta.setAtributo("cesta", cesta);
      consulta.setAtributo("numSistema", SistemaDO.SELIC);

      List l = consulta.list();
      Integer count = (Integer) l.get(0);

      return count.intValue() > 0;
   }

   public boolean temSelicEmDetalhes(Id idCesta) {
      StringBuffer sql = new StringBuffer("select count(mg) from ").append(DetalheGarantiaDO.class.getName()).append(
            " as mg where mg.cestaGarantias.numIdCestaGarantias = :cesta");
      sql.append(" and mg.instrumentoFinanceiro.sistema.numero = :numSistema");
      sql.append(" and mg.quantidadeGarantia > 0");

      IConsulta consulta = getGp().criarConsulta(sql.toString());

      consulta.setAtributo("cesta", idCesta);
      consulta.setAtributo("numSistema", SistemaDO.SELIC);

      List l = consulta.list();
      Integer count = (Integer) l.get(0);

      return count.intValue() > 0;
   }

   // Consulta ativos selicados de uma cesta de garantias
   public List obterMovimentacaoAtivosSelicados(NumeroCestaGarantia numeroCesta) {
      StringBuffer hql = new StringBuffer(2000);
      hql.append("select m.numIdMovimentacaoGarantia,m.indDireitosGarantidor,");
      hql.append("m.qtdGarantia,tipoGarantia.desTipoGarantia,");
      hql.append("m.txtDescricao,m.codIfNCetipado,ativo.codigoIF,");
      hql.append("ativo.indInadimplencia,emissor.indInadimplencia,");
      hql.append("tipoIF.codigoTipoIF, m.indCetipado, ativo.id, m.numOperacao from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" m left join m.instrumentoFinanceiro ativo left join m.cestaGarantias.tipoGarantia tipoGarantia ");
      hql.append(" left join ativo.contaParticipante emissor left join ativo.tipoIF tipoIF ");
      hql.append(" where m.cestaGarantias.numIdCestaGarantias = ?");
      hql.append(" and ativo.sistema.numero = ?");
      hql.append(" and m.tipoMovimentacaoGarantia = ?");

      List movGarantias = getGp().find(
            hql.toString(),
            new Object[] { new Id(numeroCesta.obterConteudo().toString()), SistemaDO.SELIC,
                  TipoMovimentacaoGarantiaDO.BLOQUEIO });

      return movGarantias;
   }

   public void alterarNumerosOperacao(List idMovimentacoes, List numerosOperacao) {
      IGerenciadorPersistencia gp = getGp();

      for (int i = 0; i < idMovimentacoes.size(); i++) {
         Id idMov = (Id) idMovimentacoes.get(i);
         NumeroOperacao nuOp = (NumeroOperacao) numerosOperacao.get(i);

         MovimentacaoGarantiaDO movimentacao = (MovimentacaoGarantiaDO) gp.load(MovimentacaoGarantiaDO.class, idMov);
         movimentacao.setNumOperacao(nuOp);

         gp.update(movimentacao);
      }
   }

   private boolean validarMovimentacao(MensagemSelicDO msgSelic, MovimentacaoGarantiaDO movSelic) {

      if (!msgSelic.getNumIF().mesmoConteudo(movSelic.getInstrumentoFinanceiro().getId())
            || !msgSelic.getQuantidadeOperacao().mesmoConteudo(movSelic.getQtdGarantia())
            || !msgSelic.getNumeroOperacao().mesmoConteudo(movSelic.getNumOperacao())) {
         return false;
      }
      CestaGarantiasDO cesta = movSelic.getCestaGarantias();
      boolean cestaEmVinculacao = cesta.getStatusCesta().equals(StatusCestaDO.EM_VINCULACAO);
      boolean cestaVinculada = cesta.getStatusCesta().equals(StatusCestaDO.VINCULADA);

      Id tipoMov = movSelic.getTipoMovimentacaoGarantia().getNumIdTipoMovGarantia();
      boolean ehAporte = tipoMov.mesmoConteudo(IdTipoMovimentacaoGarantia.APORTE);
      boolean ehRetirada = tipoMov.mesmoConteudo(IdTipoMovimentacaoGarantia.RETIRADA);
      boolean ehLibParcial = tipoMov.mesmoConteudo(IdTipoMovimentacaoGarantia.LIBERACAO_PARCIAL);
      boolean ehControle = tipoMov.mesmoConteudo(IdTipoMovimentacaoGarantia.CONTROLE_FLUXO_SELIC);

      if (cestaVinculada && ehControle) {
         return true;
      }

      Id contaCedente;
      Id contaCessionario;
      try {
         contaCedente = ContaParticipanteFactory.getInstance().obterContaParticipante(msgSelic.getNumIdContaCedente())
               .getIdContaParticipanteCetip();
         contaCessionario = ContaParticipanteFactory.getInstance().obterContaParticipante(
               msgSelic.getNumIdContaCessionaria()).getIdContaParticipanteCetip();
      } catch (Exception e) {
         throw new Erro(CodigoErro.ERRO, e.getMessage());
      }

      if (cestaEmVinculacao || ehAporte) {
         return cesta.getGarantidor().getId().mesmoConteudo(contaCedente)
               && cesta.getConta60Garantido().getId().mesmoConteudo(contaCessionario);
      } else if (ehRetirada) {
         return cesta.getConta60Garantido().getId().mesmoConteudo(contaCedente)
               && cesta.getGarantidor().getId().mesmoConteudo(contaCessionario);
      } else if (ehLibParcial) {
         return cesta.getConta60Garantido().getId().mesmoConteudo(contaCedente)
               && cesta.getGarantido().getId().mesmoConteudo(contaCessionario);
      }
      return false;
   }

   public boolean registradoLancamentoTransferenciaCustodia(MovimentacaoGarantiaDO mov) {
      IMensageriaGarantiasSelic imsgSelic = getFactory().getInstanceMensageriaSelic();
      MensagemSelicDO msgSelic = imsgSelic.obterNotificacaoPendenciaContraParte(mov).getMensagemSelic();
      if (msgSelic != null) {
         registrarLancamentoTransferenciaCustodia(msgSelic, mov);
         return true;
      }
      return false;
   }

   public void registrarLancamentoTransferenciaCustodia(MensagemSelicDO msgSelic, MovimentacaoGarantiaDO movSelic) {
      if (!validarMovimentacao(msgSelic, movSelic)) {
         Logger.warn(this, "Movimentacao: " + movSelic.getNumIdMovimentacaoGarantia()
               + " nao esta de acordo com os dados da mensagem recebida: " + msgSelic.getNumIdMensagemSelic());
         throw new Erro(CodigoErro.MMG_SELIC_MENSAGEM_NAO_CONFERE_MOVIMENTACAO);
      }

      if (!Condicional.vazio(msgSelic.getValorFinanceiro()) || !Condicional.vazio(msgSelic.getValorUnitario())) {
         Logger.warn(this, "Mensagem de Transferencia de Custodia Invalida. NAO deve ser informado o valorFinanceiro: "
               + msgSelic.getValorFinanceiro() + " nem o PrecoUnitario: " + msgSelic.getValorUnitario());
         throw new Erro(CodigoErro.MMG_SELIC_MENSAGEM_MOVIMENTACAO_CUSTODIA_INVALIDA);
      }

      IMensageriaGarantiasSelic imgSelic = getFactory().getInstanceMensageriaSelic();
      CodigoContaSelic contaCedente = imgSelic.obterCodigoContaSelicCedente(msgSelic);
      CodigoContaSelic contaCessionario = imgSelic.obterCodigoContaSelicCessionario(msgSelic);

      IGerenciadorPersistencia gp = getGp();

      //Inclue as referencias a mensagem selic e corresponde movimentacao na tabela de controle
      MensagemSelicMovimentacaoGarantiaDO msgSelicMov = imgSelic.obterNotificacaoPendenciaContraParte(msgSelic
            .getNumeroOperacao());
      msgSelicMov.setMensagemSelic(msgSelic);
      msgSelicMov.setMovimentacaoGarantia(movSelic);
      gp.update(msgSelicMov);

      //atualizar status da movimentacao
      movSelic.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1021R1);
      movSelic.setDataMovimentacao(getDataHoraHoje());
      gp.update(movSelic);

      CestaGarantiasDO cesta = movSelic.getCestaGarantias();
      gp.lock(cesta, NivelLock.UPGRADE);
      gp.refresh(cesta);
      boolean cestaEmVinculacao = cesta.getStatusCesta().equals(StatusCestaDO.EM_VINCULACAO);

      if (cestaEmVinculacao) {
         IMovimentacoesGarantias mg = getFactory().getInstanceMovimentacoesGarantias();
         boolean aguardMovExt = mg.cestaAguardandoMovimentacaoExterna(cesta, movSelic.getTipoMovimentacaoGarantia(),
               new Object[] { StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611 }, SistemaDO.SELIC);

         if (!aguardMovExt) {
            // nao tem nenhuma movimentacao pendente envia TODAS as SEL1021

            //obtem todas as movimentacoes da cesta com status ja atualizado para AGUARDANDO_SEL1021R1
            List movsEnvioMsg = mg.listarMovimentacoes(cesta, new Object[] { movSelic.getTipoMovimentacaoGarantia() },
                  new Object[] { StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1021R1 });

            Iterator imsg = movsEnvioMsg.iterator();
            while (imsg.hasNext()) {
               confirmarLancamentoTransferenciaCustodia((MovimentacaoGarantiaDO) imsg.next(), contaCedente,
                     contaCessionario);
            }
         }
      } else {
         confirmarLancamentoTransferenciaCustodia(movSelic, contaCedente, contaCessionario);
      }
   }

   private void confirmarLancamentoTransferenciaCustodia(MovimentacaoGarantiaDO mov, CodigoContaSelic contaCedente,
         CodigoContaSelic contaCessionario) {

      IMensageriaGarantiasSelic imgSelic = getFactory().getInstanceMensageriaSelic();
      TipoOperacaoSelic tipoOperacaoSelic = imgSelic.obterTipoOperacaoSelic(mov);
      InstrumentoFinanceiroDO ifSelic = mov.getInstrumentoFinanceiro();
      TransferenciaCustodiaVO transferencia = new TransferenciaCustodiaVO(ifSelic.getCodigoIF(), mov.getNumOperacao(),
            contaCedente, contaCessionario, tipoOperacaoSelic, ifSelic.getDataVencimento(), mov.getQtdGarantia());
      imgSelic.enviarNotificacaoTransferenciaCustodia(transferencia);
   }

   public void registrarRespostaLancamentoTransferenciaCustodiaCreditoGarantido(MovimentacaoGarantiaDO movSelic) {

      IGerenciadorPersistencia gp = getGp();
      CestaGarantiasDO cesta = movSelic.getCestaGarantias();
      gp.lock(cesta, NivelLock.UPGRADE);
      gp.refresh(cesta);

      boolean cestaVinculada = cesta.getStatusCesta().equals(StatusCestaDO.VINCULADA);

      IGarantidoCesta gc = getFactory().getInstanceGarantidoCesta();
      IMovimentacoesGarantias mg = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO movTransf = mg.obterUltimaMovimentacao(cesta, TipoMovimentacaoGarantiaDO.TRANSFERENCIA,
            StatusMovimentacaoGarantiaDO.PENDENTE);

      if (cestaVinculada && movTransf != null) {
         movSelic.setDataMovimentacao(getDataHoraHoje());
         gp.update(movSelic);
         CodigoContaCetip contaCedente = obterContaGarantiaSelic(
         		gc.obterGarantidoCesta(movSelic.getCestaGarantias()).getCodContaParticipante()).getCodContaParticipante();
         CodigoContaCetip contaCessionario = obterContaGarantiaSelic(
               movSelic.getContaParticipante().getCodContaParticipante()).getCodContaParticipante();
         //Lanca a segunda ponta (CREDITO) para a transferencia do novo garantido 
         confirmarLancamentoTransferenciaCustodia(movSelic, new CodigoContaSelic(contaCedente), new CodigoContaSelic(
               contaCessionario));
      }
   }
   

   public void registrarRespostaLancamentoTransferenciaCustodia(MovimentacaoGarantiaDO movSelic) {

      //atualizar status 
      IGerenciadorPersistencia gp = getGp();
      movSelic.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);
      movSelic.setDataMovimentacao(getDataHoraHoje());
      gp.update(movSelic);

      IMovimentacoesGarantias mg = getFactory().getInstanceMovimentacoesGarantias();

      CestaGarantiasDO cesta = movSelic.getCestaGarantias();
      gp.lock(cesta, NivelLock.UPGRADE);
      gp.refresh(cesta);
      boolean cestaEmVinculacao = cesta.getStatusCesta().equals(StatusCestaDO.EM_VINCULACAO);
      boolean cestaVinculada = cesta.getStatusCesta().equals(StatusCestaDO.VINCULADA);

      TipoMovimentacaoGarantiaDO tipoMovExterna = movSelic.getTipoMovimentacaoGarantia();
      boolean ehAporte = tipoMovExterna.getNumIdTipoMovGarantia().mesmoConteudo(IdTipoMovimentacaoGarantia.APORTE);
      boolean ehRetirada = tipoMovExterna.getNumIdTipoMovGarantia().mesmoConteudo(IdTipoMovimentacaoGarantia.RETIRADA);
      boolean ehLibParcial = tipoMovExterna.getNumIdTipoMovGarantia().mesmoConteudo(
            IdTipoMovimentacaoGarantia.LIBERACAO_PARCIAL);

      if (cestaEmVinculacao) {

         boolean aguardMovExt = mg.cestaAguardandoMovimentacaoExterna(cesta, tipoMovExterna, new Object[] {
               StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611, StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1021R1 },
               SistemaDO.SELIC);

         if (!aguardMovExt) {
            executaVinculacao(cesta, movSelic);
         }
      } else if (ehRetirada || ehLibParcial) {
         //Retirada ou Liberacao Parcial 
         //(Liberacao/Desvinculacao da Cesta (Inadimplencia) - Gera Liberacao Parcial para os ativos selic
         ICestaDeGarantias cg = getFactory().getInstanceCestaDeGarantias();
         cg.acionaMIG(movSelic, Booleano.FALSO, null);
      } else if (ehAporte) {
         IAportarGarantia aporte = getFactory().getInstanceAportarGarantia(
               movSelic.getInstrumentoFinanceiro().getSistema());
         aporte.acionaAporte(movSelic);
      } else if (cestaVinculada) { //tranferencia por venda do ativo garantido

         MovimentacaoGarantiaDO movTransf = mg.obterUltimaMovimentacao(cesta, TipoMovimentacaoGarantiaDO.TRANSFERENCIA,
               StatusMovimentacaoGarantiaDO.PENDENTE);

         boolean aguardMovExt = mg.cestaAguardandoMovimentacaoExterna(cesta, tipoMovExterna,
               new Object[] { StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1021R1 }, SistemaDO.SELIC);

         if (movTransf != null && !aguardMovExt) {
            ITransferirCesta tc = getFactory().getInstanceTransferirCesta();
            tc.finalizarTransferencia(movTransf);
         }
      }
   }
   
   public void registrarRespostaLancamentoTransferenciaCustodiaExpirada(MovimentacaoGarantiaDO movSelic)  {
      //atualizar status 
      IGerenciadorPersistencia gp = getGp();
      movSelic.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.EXPIRADA);
      movSelic.setDataMovimentacao(getDataHoraHoje());
      gp.update(movSelic);

      IMovimentacoesGarantias mg = getFactory().getInstanceMovimentacoesGarantias();

      CestaGarantiasDO cesta = movSelic.getCestaGarantias();
      gp.lock(cesta, NivelLock.UPGRADE);
      gp.refresh(cesta);
      boolean cestaEmVinculacao = cesta.getStatusCesta().equals(StatusCestaDO.EM_VINCULACAO);
      boolean cestaGarantiasExpiradas = cesta.getStatusCesta().equals(StatusCestaDO.GRT_EXPIRADAS);
      
      if (cestaEmVinculacao || cestaGarantiasExpiradas) {
         IDeletaCesta dcesta = getFactory().getInstanceDeletaCesta(StatusCestaDO.EM_VINCULACAO);
         dcesta.deletaCestaGarantias(cesta, Booleano.FALSO, null);
      } 
      
   }
   
   public void lancarTransferenciaCustodia(MovimentacaoGarantiaDO movSelic, Funcao acesso) {

      if ( movSelic == null && !Condicional.vazio(acesso)){
    	  return;
      }
      
      Funcao garantido = ICestaDeGarantias.FUNCAO_GARANTIDO;
      Id tipoMov = movSelic.getTipoMovimentacaoGarantia().getNumIdTipoMovGarantia();
      boolean ehRetirada = tipoMov.mesmoConteudo(IdTipoMovimentacaoGarantia.RETIRADA);
      
      if ( ehRetirada && acesso.mesmoConteudo(garantido) ){
    	  IGerenciadorPersistencia gp = getGp();
    	  movSelic.setDataMovimentacao(getDataHoraHoje());
    	  movSelic.setNumOperacao(obterNumeroOperacaoCetip());
          
          IGarantidoCesta gc = getFactory().getInstanceGarantidoCesta();
          CodigoContaCetip contaCedente = obterContaGarantiaSelic(
         		gc.obterGarantidoCesta(movSelic.getCestaGarantias()).getCodContaParticipante()).getCodContaParticipante();
          CodigoContaCetip contaCessionario = obterContaGarantiaSelic(
               movSelic.getContaParticipante().getCodContaParticipante()).getCodContaParticipante();
          //Lanca primeiro a ponta do garantido para a retirada (DEBITO) 
          confirmarLancamentoTransferenciaCustodia(movSelic, new CodigoContaSelic(contaCedente), new CodigoContaSelic(
               contaCessionario));  
          movSelic.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1021R1);
          gp.update(movSelic);
      }
   }
   
   
   public List processarNotificacaoEvento(NotificacaoEventoVO notificacao, Id ativoSelic, Data dataMovimentacao) {

      List operacoes = criarOperacoes(ativoSelic, notificacao, dataMovimentacao);
      if ( operacoes == null || operacoes.isEmpty()){
    	  return null;
      }

      OperacaoDO operacao = null;
      Quantidade qtdTotalAtivoEmCestas = new Quantidade("0");

      ValorMonetario valorTotalOperacoes = new ValorMonetario("0");
      ValorMonetario maiorValorFinaceiroOperacoes = new ValorMonetario("0");
      int indexMVFOperacoes = 0;

      Iterator it = operacoes.iterator();
      while ( it.hasNext()){
    	  operacao = (OperacaoDO) it.next();
    	  qtdTotalAtivoEmCestas = qtdTotalAtivoEmCestas.somar(new Quantidade(operacao.getQtdOperacao()));
          valorTotalOperacoes = valorTotalOperacoes.somar(operacao.getValFinanceiro());
          if (operacao.getValFinanceiro().compareTo(maiorValorFinaceiroOperacoes) > 0) {
             maiorValorFinaceiroOperacoes = operacao.getValFinanceiro();
             indexMVFOperacoes = operacoes.indexOf(operacao);
          }
      }

      if (!operacoes.isEmpty()) {
    	  //diferenca entre o valorTotalOperacoes e o valorFinanceiro informado na msg 
         ValorMonetario diff = notificacao.getValorFinanceiro().subtrair(valorTotalOperacoes);

         if (diff.compareTo(new ValorMonetario("0")) != 0) {
            StringBuffer buffer = new StringBuffer(500);
            buffer
                  .append("\n********************************************************************************************");
            buffer
                  .append("\nValor Financeiro informado pela msg SEL1611, difere do valor total das operacoes geradas !!!");
            buffer.append("\nValor Financeiro: " + notificacao.getValorFinanceiro().obterConteudo());
            buffer.append("\nValor total das Operacoes geradas: " + valorTotalOperacoes.obterConteudo());
            buffer.append("\nQuantidade informada na msg: " + notificacao.getQuantidade().obterConteudo());
            buffer.append("\nQuantidade Total do ativo em cestas: " + qtdTotalAtivoEmCestas.obterConteudo());
            buffer.append("\nPreco unitario: " + notificacao.getPrecoUnitario().obterConteudo());
            buffer.append("\nMaior Valor Financeiro das Operacoes: " + maiorValorFinaceiroOperacoes.obterConteudo());
            buffer.append("\nDiferença : " + diff.obterConteudo());

            //Caso a diferenca seja positiva redistribui o que sobrou para a operacao com maior valorFinanceiro - indexMVFOperacoes
            if (diff.compareTo(new ValorMonetario("0")) > 0) {
               IGerenciadorPersistencia gp = getGp();
               OperacaoDO opCredito = (OperacaoDO) operacoes.get(indexMVFOperacoes);
               opCredito.setValFinanceiro(opCredito.getValFinanceiro().somar(diff));
               gp.saveOrUpdate(opCredito);
               buffer.append(" foi creditada para a operacao: ").append(opCredito.getCodOperacao());
               buffer
                     .append("\n********************************************************************************************");
               Logger.warn(buffer.toString());
            } else {//diferenca eh negativa - ERRO
               buffer
                     .append("\n********************************************************************************************");
               Logger.warn(buffer.toString());
               throw new Erro(CodigoErro.MMG_SELIC_MENSAGEM_NAO_CONFERE_MOVIMENTACAO);
            }
         }
      }
      return operacoes;
   }
   
   private List criarOperacoes(Id ativoSelic, NotificacaoEventoVO notificacao, Data dataMovimentacao){
	   IGarantias gf = getFactory();
	   IConsultaCestasPorAtivo consulta = gf.getInstanceConsultaCestasPorAtivo();
	   List cestasVinculadas = consulta.obterCaucaoCestasContendoGarantia(ativoSelic);
	   List cestasVinculacaoIncompleta = consulta.obterCestasVinculacaoImcompletaContendoGarantia(ativoSelic);
	   
	   List operacoes = new ArrayList(cestasVinculadas.size()+cestasVinculacaoIncompleta.size());
	   OperacaoDO operacao = null;
	   
	   IEventoSelic eventoSelic = gf.getInstanceEventoSelic(notificacao.getCodigoOperacaoSelic());
	   CodigoContaSelic contaCedente = notificacao.getContaCedente();
	   
	   Iterator it = cestasVinculadas.iterator();
	   while (it.hasNext()) {
         DetalheCaucaoDO det = (DetalheCaucaoDO) it.next();
         CodigoContaSelic conta60SelicGarantido = new CodigoContaSelic(obterContaGarantiaSelic(
               det.getNumContaParticipante().getCodContaParticipante()).getCodContaParticipante());
         //somente gera a operacao se o garantido for o mesmo que a conta cedente recebida na msg
         if (conta60SelicGarantido.mesmoConteudo(contaCedente)) {
            Quantidade quantCesta = new Quantidade(det.getQtdDetalheCaucao());
            //Cria a operacao referente ao evento
            operacao = eventoSelic.criarOperacao(det.getCestaGarantias(), notificacao.getCodigoOperacaoSelic(),
                  ativoSelic, det.getIndDireitoCaucionante(), quantCesta, notificacao.getPrecoUnitario(), notificacao
                        .getValorFinanceiro(), dataMovimentacao);
            operacoes.add(operacao);
            //processa o evento
            eventoSelic.processar(det);
            
         }
	   }
	  
	   it = cestasVinculacaoIncompleta.iterator();
	   while (it.hasNext()) {
         DetalheGarantiaDO det = (DetalheGarantiaDO) it.next();
         CestaGarantiasDO cesta = det.getCestaGarantias();
         CodigoContaSelic conta60SelicGarantido = new CodigoContaSelic(obterContaSelicDaContaCetip(
               cesta.getConta60Garantido()).getCodContaParticipante());
         //somente gera a operacao se o garantido for o mesmo que a conta cedente recebida na msg
         if (conta60SelicGarantido.mesmoConteudo(contaCedente)) {
            Quantidade quantCesta = det.getQuantidadeGarantia();
            //Cria a operacao referente ao evento informando direitosGarantidor = VERDADEIRO
            operacao = eventoSelic.criarOperacao(cesta.getNumIdCestaGarantias(), notificacao.getCodigoOperacaoSelic(),
                  ativoSelic, Booleano.VERDADEIRO, quantCesta, notificacao.getPrecoUnitario(), notificacao
                        .getValorFinanceiro(), dataMovimentacao);
            operacoes.add(operacao);
         }
	   }
	   return operacoes;
   }

   private void executaVinculacao(CestaGarantiasDO cesta, MovimentacaoGarantiaDO movExterna) {
      TipoMovimentacaoGarantiaDO tipoMovExterna = movExterna.getTipoMovimentacaoGarantia();

      IMovimentacoesGarantias mg = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO movVinculacao = mg.obterUltimaMovimentacao(cesta, TipoMovimentacaoGarantiaDO.VINCULACAO,
            StatusMovimentacaoGarantiaDO.PENDENTE);

      CodigoIF codigoIF = movVinculacao.getInstrumentoFinanceiro().getCodigoIF();

      IContratosCesta ics = getFactory().getInstanceContratosCesta();
      ContratoCestaGarantiaDO contrato = ics.obterVinculoContrato(codigoIF);

      if (contrato != null) {
         vincularContrato(contrato, cesta, tipoMovExterna);
      } else {
         IVincularCesta iVinc = getFactory().getInstanceVincularCesta();
         iVinc.vincularCesta(cesta);
      }
   }

   private void vincularContrato(ContratoCestaGarantiaDO contrato, CestaGarantiasDO cesta,
         TipoMovimentacaoGarantiaDO tipoMovExterna) {
      // verifica se o contrato tem outra cesta vinculada e em caso de ter SELIC, se
      // todas as mensagens retornaram para vincular.
      IMovimentacoesGarantias mg = getFactory().getInstanceMovimentacoesGarantias();
      boolean aguardMovExt = false;
      if (!Condicional.vazio(contrato.getCestaParte()) && !Condicional.vazio(contrato.getCestaContraparte())
            && contrato.getCestaParte().getNumIdCestaGarantias().mesmoConteudo(cesta.getNumIdCestaGarantias())) {
         aguardMovExt = mg.cestaAguardandoMovimentacaoExterna(contrato.getCestaContraparte(), tipoMovExterna,
               new Object[] { StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1021R1,
                     StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611 }, SistemaDO.SELIC);
      } else if (!Condicional.vazio(contrato.getCestaContraparte()) && !Condicional.vazio(contrato.getCestaParte())
            && contrato.getCestaContraparte().getNumIdCestaGarantias().mesmoConteudo(cesta.getNumIdCestaGarantias())) {
         aguardMovExt = mg.cestaAguardandoMovimentacaoExterna(contrato.getCestaParte(), tipoMovExterna, new Object[] {
               StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1021R1, StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611 },
               SistemaDO.SELIC);
      }

      if (!aguardMovExt) {
         IVincularContratoCesta iVinc = getFactory().getInstanceVincularContratoCesta();
         iVinc.vincularContrato(contrato);
      }
   }

   private List geraMovsControle(CestaGarantiasDO cesta) {
      IGarantiasSelic gs = getFactory().getInstanceGarantiasSelic();
      List det = gs.obterGarantiasSelic(cesta);

      Iterator iter = det.iterator();
      DetalheGarantiaDO detDO = null;
      List listMovControle = new ArrayList(det.size());
      while (iter.hasNext()) {
         detDO = (DetalheGarantiaDO) iter.next();
         boolean ehSelic = SistemaDO.SELIC.mesmoConteudo(detDO.getInstrumentoFinanceiro().getSistema().getNumero());
         int qtdeGarantia = detDO.getQuantidadeGarantia().obterConteudo().intValue();
         if (ehSelic && qtdeGarantia > 0) {
            MovimentacaoGarantiaDO movControle = getFactory().getInstanceMovimentacoesGarantias()
                  .incluirMovimentacaoControleSelic(detDO);
            listMovControle.add(movControle);
         }
      }
      return listMovControle;
   }

   public void geraMovsControleVinculacao(CestaGarantiasDO cesta) {
      List listMovControle = geraMovsControle(cesta);
      for (int i = 0; i < listMovControle.size(); i++) {
         MovimentacaoGarantiaDO movControle = (MovimentacaoGarantiaDO) listMovControle.get(i);
         MovimentacaoGarantiaDO movBloqueio = getFactory().getInstanceMovimentacoesGarantias()
               .obterMovimentacaoParaAtivo(cesta, movControle.getInstrumentoFinanceiro().getCodigoIF(),
                     TipoMovimentacaoGarantiaDO.BLOQUEIO, StatusMovimentacaoGarantiaDO.OK);
         movControle.setNumOperacao(movBloqueio.getNumOperacao());
         getGp().save(movControle);
         movBloqueio.setNumOperacao(null);
         getGp().save(movBloqueio);
      }
   }

   private List geraMovsControleTransferenciaCesta(CestaGarantiasDO cesta) {
      List listMovControle = geraMovsControle(cesta);
      MovimentacaoGarantiaDO movTransf = getFactory().getInstanceMovimentacoesGarantias().obterUltimaMovimentacao(
            cesta, TipoMovimentacaoGarantiaDO.TRANSFERENCIA, StatusMovimentacaoGarantiaDO.PENDENTE);
      for (int i = 0; i < listMovControle.size(); i++) {
         MovimentacaoGarantiaDO movControle = (MovimentacaoGarantiaDO) listMovControle.get(i);
         movControle.setNumOperacao(obterNumeroOperacaoCetip());
         movControle.setContaParticipante(movTransf.getContaParticipante());
         movControle.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1021R1);
         getGp().save(movControle);
      }
      return listMovControle;
   }

   private List geraMovsControleLiberacaoCesta(CestaGarantiasDO cesta) {
      List listMovControle = geraMovsControle(cesta);
      for (int i = 0; i < listMovControle.size(); i++) {
         MovimentacaoGarantiaDO movControle = (MovimentacaoGarantiaDO) listMovControle.get(i);
         movControle.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.LIBERACAO_PARCIAL);
         movControle.setNumOperacao(obterNumeroOperacaoCetip());
         getGp().save(movControle);
      }
      return listMovControle;
   }

   public NumeroOperacao obterNumeroOperacaoCetip() {
      Date date = dateHoje == null || dateHoje.before(getDataHoje().obterDate()) ? null : dateHoje;
      if (date == null) {
         dateHoje = getDataHoje().obterDate();
         synchronized (dateHoje) {
            dateHojeFormat = DATEFORMAT.format(dateHoje);
         }
      }
      NumeroOperacao nroOperacaoCetip = (NumeroOperacao) getGp().executarFunction(
            "CETIP.PKG_MMG_COD_SELIC.f_get_cod_op_selic (?)", new Object[] { new Texto(dateHojeFormat) },
            NumeroOperacao.class);
      //Teoricamente o numero obtido ja deveria ser valido
      if (numeroOperacaoEhValido(nroOperacaoCetip)) {
         return nroOperacaoCetip;
      }
      throw new Erro(CodigoErro.NUMERO_OPERACAO_INVALIDO, Condicional.vazio(nroOperacaoCetip) ? "nulo" : nroOperacaoCetip.obterConteudo());
   }

   public void transferirGarantias(CestaGarantiasDO cesta) {
      List movControleTransf = geraMovsControleTransferenciaCesta(cesta);
      IMensageriaGarantiasSelic msgSelic = getFactory().getInstanceMensageriaSelic();
      msgSelic.enviarNotificacaoTransferenciaCustodiaDebitoGarantido(movControleTransf);
   }

   public void liberarGarantias(CestaGarantiasDO cesta) {
      List movControleLib = geraMovsControleLiberacaoCesta(cesta);
      IMensageriaGarantiasSelic msgSelic = getFactory().getInstanceMensageriaSelic();
      msgSelic.enviarNotificacaoTransferenciaCustodiaDebitoGarantido(movControleLib);
   }

   public boolean ehSelicHabilitadoMMG(CodigoIF ativoSelic) {
      IGerenciadorPersistencia gp = getGp();

      List l = null;

      StringBuffer hql = new StringBuffer();
      hql.append("from ");
      hql.append(GarantidorCestaIFDO.class.getName());
      hql.append(" cg where cg.instrumentoFinanceiro.codigoIF = ?");
      hql.append("          and cg.indAtivo = ? and cg.dataExclusao is null");

      l = gp.find(hql.toString(), new Object[] { ativoSelic, new Booleano(Booleano.FALSO) });
      if (l.size() >= 1) {
         return true;
      }
      return false;
   }

   /**
    * Obtem a conta garantia selic (ativa) associada a conta de garantia cetip (60)
    * @param contaParte Codigo da conta propria cetip 
    * @return
    */
   public ContaParticipanteDO obterContaGarantiaSelic(CodigoContaCetip contaCetip) {
      IContaGarantia cg = getFactory().getInstanceContaGarantia();
      ContaParticipanteDO conta60 = cg.obterConta60(contaCetip);
      return obterContaSelicDaContaCetip(conta60);
   }

   /**
    * Obtem a conta selic (ativa) associada a conta propria cetip (00)
    * @param contaParte Codigo da conta propria cetip 
    * @return
    */
   public ContaParticipanteDO obterContaSelic(CodigoContaCetip contaCetip) {
      IConsultaConta iConsultaConta;
      ContaParticipanteDO contaParticipante;
      try {
         iConsultaConta = ConsultaContaFactory.getInstance();
         contaParticipante = iConsultaConta.obterContaParticipanteDO(contaCetip);
      } catch (Exception e) {
         Logger.error(this, e);
         if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
         }

         throw new RuntimeException(e);
      }
      return obterContaSelicDaContaCetip(contaParticipante);
   }

   //Obtem a conta selic associada a conta propria cetip 
   private ContaParticipanteDO obterContaSelicDaContaCetip(ContaParticipanteDO contaCetip) {

      Id contaCetipId = contaCetip.getId();

      if (contaCetip.getCodContaParticipante().ehContaCliente()) {
         try {
            IConsultaConta iConsultaConta = ConsultaContaFactory.getInstance();
            contaCetipId = iConsultaConta.obterContaPropriaAtivaMesmoRadicalOuPrincipal(
                  contaCetip.getCodContaParticipante()).getId();
         } catch (Exception e) {
            Logger.error(e);
            throw new Erro(CodigoErro.ERRO, "SAP Erro: " + e.getMessage());
         }
      }

      StringBuffer query = new StringBuffer();
      query.append("FROM ");
      query.append(ContaParticipanteDO.class.getName()).append(" cp1 ");
      query.append(" WHERE cp1.idContaParticipanteCetip = :contaCetipId ");
      query.append(" and cp1.situacaoConta = :situacao ");

      ContaParticipanteDO cpDO = null;
      IConsulta consulta = getGp().criarConsulta(query.toString());
      consulta.setAtributo("contaCetipId", contaCetipId);
      consulta.setAtributo("situacao", SituacaoContaDO.ATIVA);
      List cpList = consulta.list();
      if (cpList != null && cpList.size() > 0) {
         cpDO = (ContaParticipanteDO) cpList.get(0);
      }
      return cpDO;
   }

   public List obterGarantiasSelic(CestaGarantiasDO cesta) {
      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(500);
      hql.append("select d from ");
      hql.append(DetalheGarantiaDO.class.getName());
      hql.append(" d inner join d.instrumentoFinanceiro i ");
      hql.append(" where d.cestaGarantias = ?");
      hql.append(" and d.cestaGarantias.datExclusao is null");
      hql.append(" and d.quantidadeGarantia > 0");
      hql.append(" and i.sistema.numero = ?");

      return gp.find(hql.toString(), new Object[] { cesta, SistemaDO.SELIC });
   }
}
