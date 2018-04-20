package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IContaGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IEventoSelic;
import br.com.cetip.aplicacao.operacao.apinegocio.CalculosFactory;
import br.com.cetip.aplicacao.operacao.apinegocio.EstimuloVO;
import br.com.cetip.aplicacao.operacao.apinegocio.ICalculos;
import br.com.cetip.aplicacao.operacao.apinegocio.IControleOperacao;
import br.com.cetip.aplicacao.operacao.apinegocio.OperacaoVO;
import br.com.cetip.aplicacao.operacao.apinegocio.TipoOperacaoFactory;
import br.com.cetip.aplicacao.sap.apinegocio.AdministracaoParticipantesFactory;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IAdministracaoParticipantes;
import br.com.cetip.dados.aplicacao.custodia.TipoDebitoDO;
import br.com.cetip.dados.aplicacao.financeiro.ModalidadeLiquidacaoDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.operacao.DetalheCaucaoDO;
import br.com.cetip.dados.aplicacao.operacao.OperacaoDO;
import br.com.cetip.dados.aplicacao.operacao.SituacaoOperacaoDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sap.IdentificacaoISPBDO;
import br.com.cetip.dados.aplicacao.sca.ObjetoServicoDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoObjetoServico;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoOperacaoSelic;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.numero.Preco;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.tipo.numero.ValorMonetario;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

class EventoSelic extends BaseGarantias implements IEventoSelic {

   //Conta transitoria utilizada como debito da operacao
   private CodigoContaCetip contaDebito = new CodigoContaCetip("99998.00-6");

   public void processar(DetalheCaucaoDO caucao) {
   }

   public OperacaoDO criarOperacao(Id idCesta, CodigoTipoOperacaoSelic codOperacao, Id numIF,
         Booleano direitosGarantidor, Quantidade quantidade, ValorMonetario precoUnitario,
         ValorMonetario valorFinanceiro, Data dataMovimentacao) {

      IGerenciadorPersistencia gp = getGp();
      CestaGarantiasDO cesta = (CestaGarantiasDO) gp.load(CestaGarantiasDO.class, idCesta);

      //@TODO ESPECIFICAR ContaP1
      ContaParticipanteDO contaP1;
      try {
         contaP1 = ContaParticipanteFactory.getInstance().obterContaParticipanteDO(contaDebito);
      } catch (Exception e) {
         Logger.error(this, e);
         if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
         }

         throw new RuntimeException(e);
      }
      IContaGarantia cg = getFactory().getInstanceContaGarantia();
      ContaParticipanteDO contaP2 = direitosGarantidor.ehVerdadeiro() ? cesta.getGarantidor() : cg.obterConta60(cesta
            .getGarantido());

      //obtem os bancos liquidante
      IAdministracaoParticipantes apf;
      try {
         apf = AdministracaoParticipantesFactory.getInstance();
      } catch (Exception e) {
         Logger.error(this, e);
         if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
         }

         throw new RuntimeException(e);
      }
      //@TODO VALIDAR liquidantes
      Id idLiquidanteP1;
      Id idLiquidanteP2;
      try {
         idLiquidanteP1 = apf.obterBancoLiquidantePrincipal(contaP1.getCodContaParticipante()).getIdLiquidante();
         idLiquidanteP2 = apf.obterBancoLiquidantePrincipal(contaP2.getCodContaParticipante()).getIdLiquidante();
      } catch (Exception e) {
         Logger.error(this, e);
         if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
         }
         throw new RuntimeException(e);
      }
      IdentificacaoISPBDO liquidanteP1 = (IdentificacaoISPBDO) gp.load(IdentificacaoISPBDO.class, idLiquidanteP1);
      IdentificacaoISPBDO liquidanteP2 = (IdentificacaoISPBDO) gp.load(IdentificacaoISPBDO.class, idLiquidanteP2);

      OperacaoVO opVO = new OperacaoVO();

      SituacaoOperacaoDO situacaoPendenteLiq = (SituacaoOperacaoDO) gp.load(SituacaoOperacaoDO.class,
            SituacaoOperacaoDO.PEND_ATUALIZA);
      opVO.setSituacaoOperacao(situacaoPendenteLiq);
      opVO.setCodObjetoServico(CodigoObjetoServico.SISTEMA_SELIC);
      opVO.setContaParticipanteP1(contaP1);
      opVO.setContaParticipanteP2(contaP2);
      opVO.setDataOperacao(getDataHoje());
      opVO.setIndLancadoP1(Booleano.VERDADEIRO);
      opVO.setIndLancadoP2(Booleano.VERDADEIRO);
      opVO.setInstrumentoFinanceiro((InstrumentoFinanceiroDO) gp.load(InstrumentoFinanceiroDO.class, numIF));
      opVO.setLiquidanteP1(liquidanteP1);
      opVO.setLiquidanteP2(liquidanteP2);
      ModalidadeLiquidacaoDO modalSelic = (ModalidadeLiquidacaoDO) gp.load(ModalidadeLiquidacaoDO.class,
            ModalidadeLiquidacaoDO.EVENTOS_SELIC);
      opVO.setModalidadeLiquidacao(modalSelic);
      opVO.setDataFinanceiro(dataMovimentacao);
      opVO.setTipoDebitoP1((TipoDebitoDO) gp.load(TipoDebitoDO.class, TipoDebitoDO.NAO_APLICAVEL));
      opVO.setTipoDebitoP2((TipoDebitoDO) gp.load(TipoDebitoDO.class, TipoDebitoDO.NAO_APLICAVEL));
      opVO.setQtdOperacao(new QuantidadeInteiraPositiva(quantidade));
      Preco preco = new Preco(precoUnitario);
      ValorMonetario valorOperacao = getValorFinanceiro(quantidade, preco);
      opVO.setValPrecoUnitario(preco);
      opVO.setValFinanceiro(valorOperacao);
      opVO.setIdEvento(null);

      IControleOperacao cof = getControleOperacao();
      OperacaoDO op;
      try {
         opVO.setTipoOperacao(TipoOperacaoFactory.getInstance().obterTipoOperacaoDO(codOperacao,
               ObjetoServicoDO.SISTEMA_SELIC));
         op = cof.criaOperacao(opVO);
         cof.proximoEstado(op, EstimuloVO.CONFIRMAR_OPERACAO);
      } catch (Exception e) {
         Logger.error(this, e);
         if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
         }
         throw new RuntimeException(e);
      }

      return op;
   }

   private ValorMonetario getValorFinanceiro(Quantidade quantidade, Preco precoUnitario) {
      try {
         ICalculos calc = CalculosFactory.getInstance();
         return calc.calcularValorFinanceiro(quantidade, precoUnitario);
      } catch (Exception e) {
         Logger.error(this, e);
         if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
         }
         throw new RuntimeException(e);
      }
   }

   public void registrar(TiposEventoSelic i) {
      i.registrar(null, this);
   }

}
