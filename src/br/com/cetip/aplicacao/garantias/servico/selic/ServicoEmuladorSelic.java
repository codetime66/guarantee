package br.com.cetip.aplicacao.garantias.servico.selic;

import java.util.Date;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.mensageria.selic.MensagemSelicDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.tempo.DataHora;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico utilizado para emular a comunicacao via mensageria com a Selic
 * Este servico nao deve ser implantado em producao 
 * 
 * @author Daniela Pistelli Gomes
 *
 * @requisicao.method atributo="NumeroOperacao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 *                   
 * @requisicao.method atributo="CodigoContaSelic" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIDOR"
 *                   
 * @requisicao.method atributo="CodigoContaSelic" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIDO"
 *                   
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"  
 *                   
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="INSTRUMENTO_FINANCEIRO_VENCIMENTO"   
 *                   
 * @requisicao.method atributo="QuantidadeInteiraPositiva" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 */
public class ServicoEmuladorSelic extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      RequisicaoServicoEmuladorSelic req = (RequisicaoServicoEmuladorSelic) requisicao;
      ResultadoServicoEmuladorSelic response = new ResultadoServicoEmuladorSelic();

      NumeroOperacao numOperacao = req.obterINSTRUMENTO_FINANCEIRO_NumeroOperacao();
      CodigoContaSelic contaCedente = req.obterGARANTIDOR_CodigoContaSelic();
      CodigoContaSelic contaCessionario = req.obterGARANTIDO_CodigoContaSelic();

      CodigoIF codigoIF = req.obterINSTRUMENTO_FINANCEIRO_CodigoIF();
      Data dataVenc = req.obterINSTRUMENTO_FINANCEIRO_VENCIMENTO_Data();
      Quantidade quantidade = new Quantidade(req.obterINSTRUMENTO_FINANCEIRO_QuantidadeInteiraPositiva());

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug("Numero da Operacao: " + numOperacao);
         Logger.debug("Conta Selic do Garantidor: " + contaCedente);
         Logger.debug("Conta Selic do Garantido: " + contaCessionario);
         Logger.debug("CodigoIF: " + codigoIF);
         Logger.debug("Data Vencimento : " + dataVenc);
         Logger.debug("Data Vencimento formatada: " + dataVenc.formatarData("yyyyMMdd"));
         Logger.debug("Quantidade : " + quantidade);
      }

      //Obtem a movimentacao de garantia referente a operacao da mensagem recebida
      IGarantias gf = getFactory();
      IMovimentacoesGarantias mg = gf.getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO movExterna = mg.obterMovimentacaoParaGarantiaExterna(numOperacao, null,
            StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1611);

      //Nao encontrou movimentacao para a operacao 
      if (movExterna == null) {
         Logger.warn(this, "");
         return response;
      }

      ContaParticipanteDO contaCedenteDO = ContaParticipanteFactory.getInstance().obterContaParticipante(
            getNumIdContaSelic(contaCedente));
      ContaParticipanteDO contaCessionarioDO = ContaParticipanteFactory.getInstance().obterContaParticipante(
            getNumIdContaSelic(contaCessionario));
      if (contaCedenteDO.getIdContaParticipanteCetip() == null) {
         throw new Erro(CodigoErro.ERRO, "Conta do Garantidor nao possui conta cetip associada");
      }
      if (contaCessionarioDO.getIdContaParticipanteCetip() == null) {
         throw new Erro(CodigoErro.ERRO, "Conta do Garantido nao possui conta cetip associada");
      }

      MensagemSelicDO msgSelic = incluirMensagemFake(numOperacao, contaCedenteDO, contaCessionarioDO, codigoIF,
            dataVenc, quantidade);

      //valida movimentacao - efetuado no recebimento da SEL1611
      if (!validarMovimentacao(msgSelic, movExterna)) {
         Logger.warn(this, "Movimentacao: " + movExterna.getNumIdMovimentacaoGarantia()
               + " nao esta de acordo com os dados da mensagem recebida: " + msgSelic.getNumIdMensagemSelic());
         throw new Erro(CodigoErro.MMG_SELIC_MENSAGEM_NAO_CONFERE_MOVIMENTACAO);
      }

      //atualizar status 
      IGerenciadorPersistencia gp = getGp();
      movExterna.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.AGUARDANDO_SEL1021R1);
      movExterna.setDataMovimentacao(getDataHoraHoje());
      gp.update(movExterna);

      /*Emulador nao envia a mensagem SEL1021, processa automaticamente o retorno SEL1021R1*/
      IGarantiasSelic gSelic = gf.getInstanceGarantiasSelic();
      gSelic.registrarRespostaLancamentoTransferenciaCustodia(movExterna);

      return response;

   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   private MensagemSelicDO incluirMensagemFake(NumeroOperacao numeroOperacaoSelic, ContaParticipanteDO contaCedente,
         ContaParticipanteDO contaCessionario, CodigoIF codigoIF, Data dataVencimentoIF, Quantidade quantidadeIF)
         throws Exception {
      MensagemSelicDO msgDO = new MensagemSelicDO();
      msgDO.setNumIdContaCedente(contaCedente.getId());
      msgDO.setNumIdContaCessionaria(contaCessionario.getId());
      msgDO.setNumIF(getNumIF(codigoIF, dataVencimentoIF));
      msgDO.setValorUnitario(null);
      msgDO.setQuantidadeOperacao(quantidadeIF);

      msgDO.setTipoOperacao(null);
      msgDO.setValorFinanceiro(null);
      msgDO.setNumIdTipoMensagem(null);
      msgDO.setNumIdISPB(null);
      msgDO.setNumeroOperacao(numeroOperacaoSelic);
      msgDO.setNumeroControleSTR(null);
      msgDO.setSituacaoOperacao(null);
      msgDO.setNumIdCtxMsg(null);
      msgDO.setDataInclusao(new DataHora(new Date()));
      getGp().save(msgDO);
      return msgDO;
   }

   private Id getNumIdContaSelic(CodigoContaSelic conta) throws Exception {
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
      throw new IllegalStateException("Nao foi encontrada a conta selic: " + conta);
   }

   private Id getNumIF(CodigoIF codigoIF, Data dataVencimento) throws Exception {

      StringBuffer chave = new StringBuffer(14);
      chave.append(codigoIF.obterConteudo());
      if (codigoIF.obterConteudo().length() == 6) {
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
      throw new IllegalStateException("Nao foi encontrado o ativo selic: " + chave.toString());
   }

   //Validacao realizada no recebimento da mensagem SEL1611
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

}
