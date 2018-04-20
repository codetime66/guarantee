package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMIGAcionador;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.garantias.negocio.acoes.IValidarAcao;
import br.com.cetip.aplicacao.operacao.apinegocio.ConsultaOperacaoFactory;
import br.com.cetip.aplicacao.operacao.apinegocio.IConsultaOperacao;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ComplementoContratoDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.operacao.OperacaoDO;
import br.com.cetip.dados.aplicacao.operacao.SituacaoOperacaoDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoGrade;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * Servico para validacao dos dados inseridos pelo usuario na interface grafica de Vinculacao de Cesta de Garantias
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * @since Agosto/2006
 * 
 * @resultado.class
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CESTA_GARANTIA"
 *
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="INSTRUMENTO_FINANCEIRO"
 *                    
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="INSTRUMENTO_FINANCEIRO"
 *                    
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="SOMENTE_VALIDACAO"
 *                    
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_ACESSO"
 * 
 */
public class ServicoRegistraDesvinculacaoCestaGarantias extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      RequisicaoServicoRegistraDesvinculacaoCestaGarantias requisicao = (RequisicaoServicoRegistraDesvinculacaoCestaGarantias) req;
      ResultadoServicoRegistraDesvinculacaoCestaGarantias res = new ResultadoServicoRegistraDesvinculacaoCestaGarantias();

      IGarantias factory = getFactory();

      IMIGAcionador mig = factory.getInstanceMIGAcionador();
      ICestaDeGarantias cestaDao = factory.getInstanceCestaDeGarantias();

      InstrumentoFinanceiroDO ativo = null;
      Id idCesta = requisicao.obterCESTA_GARANTIA_Id();
      Id idAtivo = requisicao.obterINSTRUMENTO_FINANCEIRO_Id();
      CodigoIF codIF = requisicao.obterINSTRUMENTO_FINANCEIRO_CodigoIF();
      if (!Condicional.vazio(idAtivo)) {
         ativo = (InstrumentoFinanceiroDO) getGp().load(InstrumentoFinanceiroDO.class, idAtivo);
      } else if (!Condicional.vazio(codIF)) {
         String hql = "select v.instrumentoFinanceiro from CestaGarantiasIFDO v where v.cestaGarantia = ? and v.instrumentoFinanceiro.codigoIF = ?";
         List l = getGp().find(hql, new Object[] { idCesta, codIF });

         if (l.isEmpty()) {
            throw new Erro(CodigoErro.INSTRUMENTO_FINANCEIRO_INEXISTENTE);
         }

         ativo = (InstrumentoFinanceiroDO) l.get(0);
      }

      CestaGarantiasDO cesta = cestaDao.obterCestaDeGarantias(new NumeroCestaGarantia(idCesta.obterConteudo()));

      // soh chama a validacao se foi comandado por tela pelo participante
      Funcao tipoAcesso = requisicao.obterGARANTIAS_TIPO_ACESSO_Funcao();
      if (!Condicional.vazio(tipoAcesso)) {
         validarDesvinculacao(cesta, ativo);
      }

      // desvincula se nao foi comandado pelo participante, ou se nao era apenas para validar
      Booleano sohValidar = requisicao.obterSOMENTE_VALIDACAO_Booleano();
      if (Condicional.vazio(tipoAcesso) || Condicional.vazio(sohValidar)
            || (!Condicional.vazio(sohValidar) && sohValidar.ehFalso())) {
         mig.acionarDesvinculacaoAtivo(cesta, ativo);
      }

      return res;
   }

   private void validarDesvinculacao(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ativo) {
      IValidarAcao iva = getFactory().getInstanceValidarAcao(ICestaDeGarantias.DESVINCULAR_GARANTIDO);
      iva.validarAcao(ICestaDeGarantias.FUNCAO_GARANTIDOR, cesta);

      // valida a grade CTP11
      AtributosColunados ac = new AtributosColunados();
      ac.atributo(new CodigoGrade("CTP11"));

      try {
         FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.gradeEstaAbertaParaOpCestaGarantia,
               ac, true);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

      if (ativo instanceof ComplementoContratoDO) {
         validarDesvinculacaoContrato((ComplementoContratoDO) ativo);
      } else {
         validarDesvinculacaoTitulo(cesta, ativo);
      }
   }

   private void validarDesvinculacaoTitulo(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ativo) {
      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      boolean ativoPossuiMovs = icg.ativoGarantidoPossuiMovimentacoes(ativo);
      if (ativoPossuiMovs) {
         throw new Erro(CodigoErro.ERRO, "Ativo Garantido possui operações pendentes");
      }

      // se nao for no dia do resgate, valida a custodia
      if (ativo.getDataVencimento().mesmoConteudo(getDataHoje()) == false) {
         if (!icg.verificaCustodiaAtivo(cesta, ativo)) {
            throw new Erro(CodigoErro.ERRO, "Ativo Garantido deve estar todo depositado na posição livre do Garantidor");
         }
      } else {
         // se for, valida se nao tem operacao de pgto de juros pendente
         List listaOperacoes = null;
         try {
            IConsultaOperacao icop = ConsultaOperacaoFactory.getInstance();
            listaOperacoes = icop.obterOperacoes(ativo.getCodigoIF(), getDataHoje());
         } catch (Exception e) {
            throw new Erro(CodigoErro.ERRO, "Operações Erro: " + e.getMessage());
         }

         Iterator iOperacoes = listaOperacoes.iterator();
         while (iOperacoes.hasNext()) {
            OperacaoDO operacao = (OperacaoDO) iOperacoes.next();
            SituacaoOperacaoDO situacaoOperacao = operacao.getSituacaoOperacao();

            if (situacaoOperacao.getTerminal().ehFalso()) {
               throw new Erro(CodigoErro.ERRO, "Ativo Garantido possui operações pendentes");
            }
         }
      }
   }

   private void validarDesvinculacaoContrato(ComplementoContratoDO ativo) {
      if (ativo.getDataHoraExclusao() == null) {
         throw new Erro(CodigoErro.ERRO, "Contrato não pode ser desvinculado pois ainda não foi excluído.");
      }
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      return null;
   }

}