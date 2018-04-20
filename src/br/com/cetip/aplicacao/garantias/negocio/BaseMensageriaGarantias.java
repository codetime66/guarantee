package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.cetip.aplicacao.mensageria.apinegocio.garantias.MensageriaSelicFactory;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.mensageria.Mensageria21VDO;
import br.com.cetip.dados.aplicacao.mensageria.TipoMensagemDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.Notificacao;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sap.ParticISPBDO;
import br.com.cetip.dados.aplicacao.sap.SituacaoContaDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.tipo.expressao.TipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoMensagem;
import br.com.cetip.infra.atributo.tipo.identificador.ISPB;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NuOp;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleSTR;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.identificador.SituacaoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.tempo.DataHora;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

class BaseMensageriaGarantias extends BaseGarantias {

   //	Mapea os Tipo de Movimentacoes como Movimentacoes de Credito ou Debito
   private static final Map TIPOS_MOVIMENTACOES = new HashMap(7);

   static {
      //Envio da SEL1021 atraves da mov de controle
      TIPOS_MOVIMENTACOES.put(TipoMovimentacaoGarantiaDO.CONTROLE_FLUXO_SELIC, TipoOperacaoSelic.CREDITO);
      TIPOS_MOVIMENTACOES.put(TipoMovimentacaoGarantiaDO.VINCULACAO, TipoOperacaoSelic.CREDITO);
      TIPOS_MOVIMENTACOES.put(TipoMovimentacaoGarantiaDO.APORTE, TipoOperacaoSelic.CREDITO);

      //Envia somente a mensagem de Transferencia para o NOVO GARANTIDO, por isto Credito
      TIPOS_MOVIMENTACOES.put(TipoMovimentacaoGarantiaDO.TRANSFERENCIA, TipoOperacaoSelic.CREDITO);

      TIPOS_MOVIMENTACOES.put(TipoMovimentacaoGarantiaDO.RETIRADA, TipoOperacaoSelic.DEBITO);
      TIPOS_MOVIMENTACOES.put(TipoMovimentacaoGarantiaDO.LIBERACAO_PARCIAL, TipoOperacaoSelic.DEBITO);
      TIPOS_MOVIMENTACOES.put(TipoMovimentacaoGarantiaDO.LIBERACAO, TipoOperacaoSelic.DEBITO);
   }

   protected final TipoOperacaoSelic getTipoOperacaoSelic(TipoMovimentacaoGarantiaDO tipo) {
      return (TipoOperacaoSelic) TIPOS_MOVIMENTACOES.get(tipo);
   }

   //Mapea os Tipo de Mensagens e seus respectivos Id
   private Map mapIdTipoMensagem = new HashMap(9);

   protected MensagemSelicDO criaMensagemSelic(Notificacao notificacao) {
      MensagemSelicDO msgDO = criaMensagemSelic(notificacao.getCodigoTipoMensagem(), notificacao.getISPBIF(),
            notificacao.getNumeroOperacao(), notificacao.getNumeroControleSTR(), notificacao.getSituacaoOperacao());
      msgDO.setCodigoOperacao(notificacao.getCodigoOperacaoSelic());
      msgDO.setNumIdContaCedente(getNumIdContaSelic(notificacao.getContaCedente()));
      msgDO.setNumIdContaCessionaria(getNumIdContaSelic(notificacao.getContaCessionario()));
      msgDO.setTipoOperacao(notificacao.getTipoOperacaoSelic());
      msgDO.setNumIF(getNumIF(notificacao.getCodigoIF(), notificacao.getDataVencimentoIF()));
      msgDO.setValorUnitario(notificacao.getPrecoUnitario());
      msgDO.setQuantidadeOperacao(notificacao.getQuantidade());
      msgDO.setValorFinanceiro(notificacao.getValorFinanceiro());
      return msgDO;
   }

   protected MensagemSelicDO criaMensagemSelic(CodigoTipoMensagem codTipoMsg, ISPB ISPBIF,
         NumeroOperacao numeroOperacaoSelic, NumeroControleSTR nroControleSTR, SituacaoOperacaoSelic sitOperacao) {
      MensagemSelicDO msgDO = new MensagemSelicDO();
      msgDO.setNumIdTipoMensagem(getNumIdTipoMensagem(codTipoMsg.obterConteudo()));
      msgDO.setNumIdISPB(getNumIdISPB(new Id(ISPBIF.obterConteudo())));
      msgDO.setNumeroOperacao(numeroOperacaoSelic);
      msgDO.setNumeroControleSTR(nroControleSTR);
      msgDO.setSituacaoOperacao(sitOperacao);
      msgDO.setNumIdCtxMsg(getNumIdCtxMsg());
      msgDO.setDataInclusao(new DataHora(new Date()));
      return msgDO;
   }

   protected MensagemSelicMovimentacaoGarantiaDO obterMensagemMovimentacaoGarantia(MovimentacaoGarantiaDO movimentacao,
         MensagemSelicDO mensagemSelic) {

      Id idMovimentacao = null;
      Id idMensagemSelic = null;

      StringBuffer hql = new StringBuffer(250);
      hql.append("from " + MensagemSelicMovimentacaoGarantiaDO.class.getName());
      hql.append(" msg where 1 = 1 ");
      if (movimentacao != null) {
         hql.append(" and msg.movimentacaoGarantia = :mov");
      }
      if (mensagemSelic != null) {
         hql.append(" and msg.mensagemSelic = :msg");
      }

      IGerenciadorPersistencia gp = getGp();
      IConsulta consulta = gp.criarConsulta(hql.toString());
      if (movimentacao != null) {
         consulta.setAtributo("mov", movimentacao);
         idMovimentacao = movimentacao.getNumIdMovimentacaoGarantia();
      }
      if (mensagemSelic != null) {
         consulta.setAtributo("msg", mensagemSelic);
         idMensagemSelic = mensagemSelic.getNumIdMensagemSelic();
      }
      List l = consulta.list();
      if (l.size() == 1) {
         return (MensagemSelicMovimentacaoGarantiaDO) l.get(0);
      }
      if (l.size() < 1) {
         MensagemSelicMovimentacaoGarantiaDO msgDo = new MensagemSelicMovimentacaoGarantiaDO();
         msgDo.setMensagemSelic(mensagemSelic);
         msgDo.setMovimentacaoGarantia(movimentacao);
         gp.save(msgDo);
         return msgDo;
      }
      if (l.size() > 1) {
         throw new IllegalStateException("Encontrada mais de uma movimentacao: " + idMovimentacao
               + " referente a mensagem selic: " + idMensagemSelic);
      }
      return null;
   }

   //Metodos que obtem os Id(Cetip) das informacoes recebidas na mensagem Selic
   //para armazenamento na tabela de controle de mensageria

   protected Id getNumIdContaSelic(CodigoContaSelic conta) {
      StringBuffer hql = new StringBuffer(100);
      hql.append("select cp.id from ");
      hql.append(ContaParticipanteDO.class.getName());
      hql.append(" cp where cp.codContaParticipante = :codigoConta");

      IGerenciadorPersistencia gp = getGp();
      IConsulta consulta = gp.criarConsulta(hql.toString());
      consulta.setAtributo("codigoConta", conta);
      List l = consulta.list();
      if (l.size() == 1) {
         return (Id) l.get(0);
      }

      lancaException(l, "CodigoContaSelic", conta.obterConteudo());

      return null;
   }

   protected CodigoContaSelic getCodigoContaSelic(Id idContaParticipante) {
      StringBuffer hql = new StringBuffer(100);
      hql.append("select cp.codContaParticipante from ");
      hql.append(ContaParticipanteDO.class.getName());
      hql.append(" cp where cp.id = :idContaParticipante");
      hql.append(" and cp.situacaoConta = :situacaoConta");

      IGerenciadorPersistencia gp = getGp();
      IConsulta consulta = gp.criarConsulta(hql.toString());
      consulta.setAtributo("idContaParticipante", idContaParticipante);
      consulta.setAtributo("situacaoConta", SituacaoContaDO.ATIVA);
      List l = consulta.list();

      if (l.size() == 1) {
         CodigoContaCetip codConta = (CodigoContaCetip) l.get(0);
         return new CodigoContaSelic(codConta);
      }

      lancaException(l, "IdContaParticipante", idContaParticipante.obterConteudo());

      return null;
   }

   protected Id getNumIF(CodigoIF codigoIF, Data dataVencimento) {
      StringBuffer chave = new StringBuffer(14);
      chave.append(codigoIF.obterConteudo());
      if (dataVencimento != null) {
         chave.append(dataVencimento.formatarData("yyyyMMdd").toString());
      }

      StringBuffer hql = new StringBuffer(100);
      hql.append("select if.id from ");
      hql.append(InstrumentoFinanceiroDO.class.getName());
      hql.append(" if where if.codigoIF = :codigo");
      hql.append(" and if.sistema = :sistema");
      hql.append(" and if.dataHoraExclusao is null");

      IGerenciadorPersistencia gp = getGp();
      IConsulta consulta = gp.criarConsulta(hql.toString());
      consulta.setAtributo("codigo", new CodigoIF(chave.toString()));
      consulta.setAtributo("sistema", SistemaDO.SELIC);

      List l = consulta.list();
      if (l.size() == 1) {
         return (Id) l.get(0);
      }

      lancaException(l, "InstrumentoFinanceiro", chave.toString());

      return null;
   }

   protected Id getNumIdISPB(Id codParticipanteISPB) {
      StringBuffer hql = new StringBuffer(100);
      hql.append("select p.numIdEntidade from ");
      hql.append(ParticISPBDO.class.getName());
      hql.append(" p where p.codParticISPB = :codigo");

      IGerenciadorPersistencia gp = getGp();
      IConsulta consulta = gp.criarConsulta(hql.toString());
      consulta.setAtributo("codigo", codParticipanteISPB);

      List l = consulta.list();
      if (l.size() == 1) {
         return (Id) l.get(0);
      } else if (l.size() > 1) {
         Id idISPB = (Id) l.get(0);
         for (int i = 1; i < l.size(); i++) {
            Id id = (Id) l.get(i);
            if (!id.mesmoConteudo(idISPB)) {
               lancaException(l, "ISPB", codParticipanteISPB.obterConteudo());
            }
         }
         return idISPB;
      } else {
         lancaException(l, "ISPB", codParticipanteISPB.obterConteudo());
      }
      return null;
   }

   protected Id getNumIdCtxMsg() {
      Mensageria21VDO mensagem = MensageriaSelicFactory.getInstance().obterMensagem(
            new NuOp(getContextoAtivacao().getNuop()));
      if (!Condicional.vazio(mensagem)) {
         return mensagem.getNumIdCtxMsg();
      }
      throw new IllegalStateException("Contexto da mensagem não encontrado para o NuOp: "
            + getContextoAtivacao().getNuop());
   }

   protected Id getNumIdTipoMensagem(String codigoMensagem) {
      if (codigoMensagem == null) {
         throw new IllegalStateException("Codigo da Mensagem invalido (nulo) " + codigoMensagem);
      }

      if (mapIdTipoMensagem == null) {
         inicializaTiposMensagens();
      }

      Id idTipo = (Id) mapIdTipoMensagem.get(codigoMensagem);
      if (idTipo == null) {
         IGerenciadorPersistencia gp = getGp();
         List list = gp.find("select t from " + TipoMensagemDO.class.getName() + " t where t.codTipoMensagem = '"
               + codigoMensagem + "'");
         if (list.size() == 1) {
            idTipo = ((TipoMensagemDO) list.get(0)).getNumIdTipoMensagem();
         } else {
            lancaException(list, "Tipo de Mensagem", codigoMensagem);
         }
      }
      return idTipo;
   }

   /**
    * Obtem a mensagemSelic referente a operacao realizada no dia da consulta (dataHoje).
    * O numero de operacao pode ser reutilizado em dias distintos.
    * @param codTipoMsg Codigo do Tipo da Mensagem (SEL1611, SEL1021, SEL1021R1, SEL10801, SEL1081R1)
    * @param numeroOperacao Numero que identifica a operacao na Selic
    * @return
    */
   protected MensagemSelicDO obterMensagemSelic(CodigoTipoMensagem codTipoMsg, NumeroOperacao numeroOperacao) {
      StringBuffer hql = new StringBuffer(250);
      hql.append(" select msg");
      hql.append(" from " + MensagemSelicDO.class.getName());
      hql.append(" msg where msg.numeroOperacao = :numOperacao");
      hql.append(" and trunc(msg.dataInclusao) = trunc(:dataHoje)");
      hql.append(" and msg.dataExclusao is null");
      hql.append(" and msg.numIdTipoMensagem = :idTipoMsg");

      IGerenciadorPersistencia gp = getGp();
      IConsulta consulta = gp.criarConsulta(hql.toString());
      consulta.setAtributo("numOperacao", numeroOperacao);
      consulta.setAtributo("dataHoje", getDataHoje());
      consulta.setAtributo("idTipoMsg", getNumIdTipoMensagem(codTipoMsg.obterConteudo()));

      List l = consulta.list();
      if (l.size() == 1) {
         return (MensagemSelicDO) l.get(0);
      } else if (l.size() > 1) {
         throw new IllegalStateException("Encontrada mais de uma mensagem " + codTipoMsg + " com numero operacao: "
               + numeroOperacao);
      }
      return null;
   }

   private void inicializaTiposMensagens() {
      Object[] listaCodigoMsg = new Object[] { "SEL1611", "SEL1021", "SEL1021R1", "SEL1081", "SEL1081R1" };

      IGerenciadorPersistencia gp = getGp();
      String hql = "select t from " + TipoMensagemDO.class.getName() + " t where t.codTipoMensagem in (:listaCodMsg)";
      IConsulta consulta = gp.criarConsulta(hql);
      consulta.setParameterList("listaCodMsg", listaCodigoMsg);

      List list = consulta.list();
      for (int i = 0; i < list.size(); i++) {
         TipoMensagemDO tipoMsg = (TipoMensagemDO) list.get(i);
         mapIdTipoMensagem.put(tipoMsg.getCodTipoMensagem(), tipoMsg.getNumIdTipoMensagem());
      }
   }

   private void lancaException(List l, String objeto, String conteudo) {
      if (l.size() > 1) {
         throw new IllegalStateException("Encontrado mais de um : " + objeto + ": " + conteudo);
      } else if (l.size() < 1) {
         throw new IllegalStateException(objeto + " não encontrado " + conteudo);
      }
   }

}
