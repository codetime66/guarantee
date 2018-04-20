package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Iterator;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.ILiberacaoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.negocio.mainframe.WTCRegistraOperacao;
import br.com.cetip.dados.aplicacao.financeiro.ModalidadeLiquidacaoDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.dados.aplicacao.sca.UsuarioDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleLancamento;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;

/**
 * Avisa o ativo da alta para se desvincular desta cesta
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
class LiberacaoCestaAltaPlataforma extends BaseGarantias implements ILiberacaoCesta {

   /**
    * @see ILiberacaoCesta#liberar(CestaGarantiasDO, MovimentacaoGarantiaDO)
    */
   public void liberar(CestaGarantiasDO cesta, Data data) {
      IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO movimentacao = imov.obterUltimaMovimentacao(cesta, TipoMovimentacaoGarantiaDO.VINCULACAO,
            StatusMovimentacaoGarantiaDO.OK);

      Data hoje = Condicional.vazio(data) ? getDataHoje() : data;

      Id mod = null;
      NumeroControleLancamento numero = new NumeroControleLancamento("");
      Quantidade qt = new Quantidade("");
      Quantidade pu = new Quantidade("");
      if (movimentacao.getIndDepositado().ehFalso()) {
         ModalidadeLiquidacaoDO modalidade = new ModalidadeLiquidacaoDO();
         modalidade.setId(movimentacao.getModalidade().getId());
         modalidade = (ModalidadeLiquidacaoDO) getGp().load(ModalidadeLiquidacaoDO.class, modalidade.getId());
         mod = modalidade.getGrupoModalidadeLiquidacao().getCodigo();
         qt = movimentacao.getQtdOperacao();
         pu = movimentacao.getPuOperacao();
         numero = movimentacao.getNumControleLancamento();
      }

      CodigoContaCetip contaGarantido = null;
      ContextoAtivacaoVO ca = getContextoAtivacao();

      // Obtem o Garantido
      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
      ContaParticipanteDO garantido = igc.obterGarantidoCesta(cesta);
      if (garantido != null) {
         contaGarantido = garantido.getCodContaParticipante();
      }

      CodigoContaCetip idConta2 = new CodigoContaCetip(ca.getCodContaTitularFamilia());
      UsuarioDO userLogin = (UsuarioDO) getGp().load(UsuarioDO.class, new Id(ca.getIdUsuario().toString()));
      String nome2 = userLogin.getNomSimplificadoEntidade().toString();

      // ALTA PLATAFORMA
      // Comunicacao com a alta WTC
      WTCRegistraOperacao wtc = new WTCRegistraOperacao();

      Iterator i = cesta.getAtivosVinculados().iterator();
      InstrumentoFinanceiroDO ifDO = ((CestaGarantiasIFDO) i.next()).getInstrumentoFinanceiro();

      CodigoContaCetip parte = cesta.getGarantidor().getCodContaParticipante();
      wtc.setCodigoIF(ifDO.getCodigoIF());
      wtc.setIndAcao("DSVC001");
      wtc.setNumeroCesta(cesta.getNumIdCestaGarantias());
      wtc.setDepositado(movimentacao.getIndDepositado().obterRepresentacaoBD());
      wtc.setContaParte(parte.obterConteudoSemMascara());
      if (contaGarantido != null) {
         wtc.setContaContraParte(contaGarantido.obterConteudoSemMascara());
      }
      wtc.setNumeroControle(numero);
      wtc.setQtd(qt);
      wtc.setPu(pu);
      wtc.setModalidade(mod);
      // Auditoria
      wtc.setIdContaParte(idConta2.obterConteudoSemMascara());
      wtc.setNomeLoginParte(nome2);
      wtc.setIdContraParte(idConta2.obterConteudoSemMascara());
      wtc.setNomeLoginContraParte(nome2);

      wtc.setDataOperacao(hoje.obterDataFormatadaParaMF());
      wtc.setSequencia(new NumeroInteiro("002"));

      wtc.execute();

      // Fim da comunicacao WTC
   }

   public final void registrar(TiposLiberacao f) {
      f.registrar(SistemaDO.SNA, CodigoTipoIF.CDB, this);
      f.registrar(SistemaDO.SNA, CodigoTipoIF.DI, this);
      f.registrar(SistemaDO.SNA, CodigoTipoIF.DIM, this);
   }

}
