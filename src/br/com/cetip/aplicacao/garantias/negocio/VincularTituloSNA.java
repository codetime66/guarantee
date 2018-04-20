package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularTitulo;
import br.com.cetip.aplicacao.garantias.negocio.mainframe.WTCRegistraOperacao;
import br.com.cetip.dados.aplicacao.financeiro.ModalidadeLiquidacaoDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaIFDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.dados.aplicacao.sca.UsuarioDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;

class VincularTituloSNA extends BaseGarantias implements IVincularTitulo {

   public void vincularTitulo(MovimentacaoGarantiaDO movVinculacao, CestaGarantiasIFDO vinculo) {
      Data dataHoje = getDataHoje();
      ContextoAtivacaoVO ca = getContextoAtivacao();
      IGerenciadorPersistencia gp = getGp();

      CestaGarantiasDO cesta = movVinculacao.getCestaGarantias();
      InstrumentoFinanceiroDO titulo = movVinculacao.getInstrumentoFinanceiro();

      CodigoContaCetip parte = cesta.getGarantidor().getCodContaParticipante();
      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
      ContaParticipanteDO garantido = igc.obterGarantidoCesta(cesta);
      CodigoContaCetip contraParte = garantido.getCodContaParticipante();

      Id codGrupoMod = null;

      if (movVinculacao.getIndDepositado().ehFalso()) {
         ModalidadeLiquidacaoDO modalidade = (ModalidadeLiquidacaoDO) gp.load(ModalidadeLiquidacaoDO.class,
               movVinculacao.getModalidade().getId());
         codGrupoMod = modalidade.getGrupoModalidadeLiquidacao().getCodigo();
      }

      CodigoContaCetip idConta1 = movVinculacao.getContaParticipante().getCodContaParticipante();
      CodigoContaCetip idConta2 = new CodigoContaCetip(ca.getCodContaTitularFamilia());

      UsuarioDO primeiro = movVinculacao.getUsuario();
      NomeSimplificado nomePrimeiro = primeiro != null ? primeiro.getNomSimplificadoEntidade() : null;
      String nome1 = nomePrimeiro != null ? nomePrimeiro.toString() : "";

      UsuarioDO userLogin = (UsuarioDO) gp.load(UsuarioDO.class, new Id(ca.getIdUsuario().toString()));
      String nome2 = userLogin.getNomSimplificadoEntidade().toString();

      // Comunicacao com a alta WTC
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "*** DADOS PASSADOS AO MAINFRAME ***");
         Logger.debug(this, "CODIGO IF: " + titulo.getCodigoIF());
         Logger.debug(this, "NR CESTA: " + cesta.getNumIdCestaGarantias());
         Logger.debug(this, "DEPOSITADO: " + movVinculacao.getIndDepositado().obterRepresentacaoBD());
         Logger.debug(this, "PARTE: " + parte.obterConteudoSemMascara());
         Logger.debug(this, "CONTRAPARTE: " + contraParte.obterConteudoSemMascara());
         Logger.debug(this, "NR CONTROLE: " + movVinculacao.getNumControleLancamento());
         Logger.debug(this, "QT OPERACAO: " + movVinculacao.getQtdOperacao());
         Logger.debug(this, "PU QUANTIDADE: " + movVinculacao.getPuOperacao());
         Logger.debug(this, "MODALIDADE: " + codGrupoMod);
         Logger.debug(this, "ID CONTA PRIMEIRO: " + idConta1.obterConteudoSemMascara());
         Logger.debug(this, "ID CONTA SEGUNDO: " + idConta2.obterConteudoSemMascara());
         Logger.debug(this, "NOME SIMPL. PRIMEIRO: " + nome1);
         Logger.debug(this, "NOME SIMPL. SEGUNDO: " + nome2);
         Logger.debug(this, "DT OPERACAO: " + dataHoje.obterDataFormatadaParaMF());
      }

      WTCRegistraOperacao wtc = new WTCRegistraOperacao();

      wtc.setCodigoIF(titulo.getCodigoIF());
      wtc.setIndAcao(movVinculacao.getIndDepositado().ehFalso() ? "VINC001" : "VINC002");

      wtc.setNumeroCesta(cesta.getNumIdCestaGarantias());
      wtc.setDepositado(movVinculacao.getIndDepositado().obterRepresentacaoBD());
      wtc.setContaParte(parte.obterConteudoSemMascara());
      wtc.setContaContraParte(contraParte.obterConteudoSemMascara());
      wtc.setNumeroControle(movVinculacao.getNumControleLancamento());
      wtc.setQtd(movVinculacao.getQtdOperacao());
      wtc.setPu(movVinculacao.getPuOperacao());
      wtc.setModalidade(codGrupoMod);

      if (cesta.getStatusCesta().equals(StatusCestaDO.VNC_PEND_GRTDO)) {
         wtc.setIdContaParte(idConta1.obterConteudoSemMascara());
         wtc.setIdContraParte(idConta2.obterConteudoSemMascara());
         wtc.setNomeLoginParte(nome1);
         wtc.setNomeLoginContraParte(nome2);
      } else {
         wtc.setIdContaParte(idConta2.obterConteudoSemMascara());
         wtc.setIdContraParte(idConta1.obterConteudoSemMascara());
         wtc.setNomeLoginParte(nome2);
         wtc.setNomeLoginContraParte(nome1);
      }

      wtc.setDataOperacao(dataHoje.obterDataFormatadaParaMF());
      wtc.setSequencia(new NumeroInteiro("001"));

      String controle = System.getProperty("desativawtc");
      if (controle != null && controle.equals("true")) {
         vinculo.setStatus(StatusCestaIFDO.VINCULADA_AO_ATIVO);
         vinculo.setDatAlteracao(dataHoje);
      } else {
         wtc.execute();
      }
   }

   public void registrar(TiposVinculacaoTitulo tipos) {
      tipos.registrar(SistemaDO.SNA, this);
   }

}
