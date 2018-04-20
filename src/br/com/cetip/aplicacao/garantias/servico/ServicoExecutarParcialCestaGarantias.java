package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiaVO;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasCDAWA;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoCDA;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoWA;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico para LIBERACAO PARCIAL dos ativos de Cesta de Garantia
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vincius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @since Abril/2006
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="NumeroCestaGarantia"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="CodigoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_IF"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="CodigoContaCetip"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_GARANTIDO"
 * 
 * @requisicao.method atributo="Quantidade"
 *                    pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_QUANTIDADE"
 * 
 * @requisicao.method atributo="CPFOuCNPJ"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.method atributo="CodigoTipoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @requisicao.method atributo="CodigoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_IF"
 * 
 * @requisicao.method atributo="NumeroOperacao"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="OPERACAO"
 *                    
 */
public class ServicoExecutarParcialCestaGarantias extends BaseGarantias implements Servico {

   private boolean removeuCDAWA = false;

   /*
    * Este servico/metodo executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao req) throws Exception {
      RequisicaoServicoExecutarParcialCestaGarantias requisicao = (RequisicaoServicoExecutarParcialCestaGarantias) req;
      ResultadoServicoExecutarParcialCestaGarantias res = new ResultadoServicoExecutarParcialCestaGarantias();

      NumeroCestaGarantia nrCesta = requisicao.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      CodigoTipoIF codigoTipoIF = requisicao.obterGARANTIAS_CODIGO_TIPO_CodigoTipoIF();
      CodigoIF codIF = requisicao.obterGARANTIAS_CODIGO_IF_CodigoIF();
      CPFOuCNPJ comitente = requisicao.obterGARANTIAS_CONTRAPARTE_CPFOuCNPJ();
      CodigoContaCetip contaGarantido = requisicao.obterGARANTIAS_GARANTIDO_CodigoContaCetip();
      Quantidade quantidade = requisicao.obterGARANTIAS_QUANTIDADE_Quantidade();
      NumeroOperacao nuOp = requisicao.obterOPERACAO_NumeroOperacao();

      log(nrCesta, codigoTipoIF, codIF, contaGarantido, quantidade);

      ContextoAtivacaoVO ca = getContextoAtivacao();

      IGarantias factory = getFactory();

      ICestaDeGarantias dao = factory.getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = dao.obterCestaDeGarantias(nrCesta);

      contaGarantido = validaEouTrocaGarantido(contaGarantido, cesta);

      String conta = contaGarantido.toString();
      StatusCestaDO stCesta = cesta.getStatusCesta();
      Data dataInadimplencia = cesta.getDatInadimplencia();

      if ((ca.getListaContasConsulta().contains(conta) || ca.ehCETIP()) && (dataInadimplencia != null)
            && (stCesta.equals(StatusCestaDO.INADIMPLENTE))) {

         GarantiaVO garantiaVO = new GarantiaVO();
         garantiaVO.atribuirCodIFComTipoIF(codIF, codigoTipoIF);

         DetalheGarantiaDO garantia = dao.obterGarantiaCesta(cesta, garantiaVO);

         if (garantia == null) {
            throw new Erro(CodigoErro.CESTA_ITEM_NAO_EXISTE);
         }

         IMovimentacoesGarantias img = factory.getInstanceMovimentacoesGarantias();
         MovimentacaoGarantiaDO libParcial = img.incluirMovimentacaoLiberacaoParcial(garantia, quantidade,
               contaGarantido, nuOp);

         libParcial.setCpfOuCnpjComitente(comitente);

         getGp().flush();

         res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(nrCesta);
         res.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codIF);

         // Se for SELIC E não foi recebida msg com liberacao,
         // não acionaMIG agora , espera notificacao do selic SEL1611
         if (garantia.ehGarantiaCetipada()
               && garantia.getInstrumentoFinanceiro().getSistema().getNumero().mesmoConteudo(SistemaDO.SELIC)
               && !realizadoLancamentoTransferenciaCustodiaSelic(libParcial)) {
            return res;
         }

         if (garantia.ehGarantiaCetipada()) {
            dao.acionaMIG(libParcial, Booleano.FALSO, null);
            executaCDAWA(requisicao, codigoTipoIF, codIF, factory, cesta);
         } else {
            garantia.setQuantidadeGarantia(garantia.getQuantidadeGarantia().subtrair(quantidade));

            dao.verificaNecessidadeDesvincularCesta(cesta);
         }

         removeuCDAWA = false;
      } else {
         throw new Erro(CodigoErro.CESTA_INCOMPATIVEL);
      }

      return res;
   }

   /**
    * Caso a conta do garantido foi informada, entao eh pq a cesta eh penhor no emissor e foi permitido
    * Caso contrario, deve usar a conta do garantido da cesta
    * 
    * @param contaGarantido
    * @param cesta
    * @return
    */
   private CodigoContaCetip validaEouTrocaGarantido(CodigoContaCetip contaGarantido, CestaGarantiasDO cesta) {
      // se vazio nao eh Penhor no Emissor
      if (Condicional.vazio(contaGarantido)) {
         IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
         ContaParticipanteDO garantido = igc.obterGarantidoCesta(cesta);
         return garantido.getCodContaParticipante();
      } else if (cesta.getTipoGarantia().getNumIdTipoGarantia().mesmoConteudo(IdTipoGarantia.CESSAO_FIDUCIARIA)) {
         throw new Erro(CodigoErro.ERRO, "Cesta é cessão mas participante informou garantido. Operação não permitida");
      }

      return contaGarantido;
   }

   private void log(NumeroCestaGarantia nrCesta, CodigoTipoIF codigoTipoIF, CodigoIF codIF, CodigoContaCetip usuario,
         Quantidade quantidade) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Cesta: " + nrCesta);
         Logger.debug(this, "Ativo a liberar: " + codIF);
         Logger.debug(this, "Tipo Ativo: " + codigoTipoIF);
         Logger.debug(this, "Quantidade: " + quantidade);
         Logger.debug(this, "Conta: " + usuario);
      }
   }

   private void executaCDAWA(RequisicaoServicoExecutarParcialCestaGarantias requisicao, CodigoTipoIF codigoTipoIF,
         CodigoIF codIF, IGarantias factory, CestaGarantiasDO cesta) throws Exception {
      IGarantiasCDAWA valCdaWa = factory.getGarantiasCDAWA();

      /*
       * Tarefa exclusiva para CDA/WA
       */
      if (codigoTipoIF.mesmoConteudo(CodigoTipoIF.CDA) && !removeuCDAWA) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "CDA Liberado Parcialmente da Cesta de Garantias");
         }

         boolean cestaTemWA = valCdaWa.encontrarWA(codIF, cesta);
         if (cestaTemWA) {
            if (Logger.estaHabilitadoDebug(this)) {
               Logger.debug(this, "Cesta tem o WA correspondente. Liberando automaticamente...");
            }

            removeuCDAWA = true;

            CodigoCDA codigoCDA = new CodigoCDA(codIF.obterConteudo());
            CodigoIF codigoWA = new CodigoIF(codigoCDA.obterRespectivoWA().obterConteudo());
            RequisicaoServicoExecutarParcialCestaGarantias reqNova = copiaRequisicao(requisicao, CodigoTipoIF.WA,
                  codigoWA);
            executar(reqNova);
         }
      } else if (codigoTipoIF.mesmoConteudo(CodigoTipoIF.WA) && !removeuCDAWA) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "WA Liberado Parcialmente da Cesta de Garantias");
         }

         boolean cestaTemCDA = valCdaWa.encontrarCDA(codIF, cesta);
         if (cestaTemCDA) {
            if (Logger.estaHabilitadoDebug(this)) {
               Logger.debug(this, "Cesta tem o CDA correspondente. Liberando automaticamente.");
            }

            removeuCDAWA = true;

            CodigoWA codigoWA = new CodigoWA(codIF.obterConteudo());
            CodigoIF codigoCDA = new CodigoIF(codigoWA.obterRespectivoCDA().obterConteudo());
            RequisicaoServicoExecutarParcialCestaGarantias reqNova = copiaRequisicao(requisicao, CodigoTipoIF.CDA,
                  codigoCDA);
            executar(reqNova);
         }
      }
   }

   private RequisicaoServicoExecutarParcialCestaGarantias copiaRequisicao(
         RequisicaoServicoExecutarParcialCestaGarantias reqVelha, CodigoTipoIF codigoTipoIF, CodigoIF codigoIF) {

      CodigoIF novoCodigoIF = new CodigoIF(Contexto.GARANTIAS_CODIGO_IF, codigoIF.obterConteudo());

      RequisicaoServicoExecutarParcialCestaGarantias reqNova = new RequisicaoServicoExecutarParcialCestaGarantias();
      reqNova.atribuirGARANTIAS_CODIGO_IF_CodigoIF(novoCodigoIF);
      reqNova.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(reqVelha.obterGARANTIAS_CODIGO_NumeroCestaGarantia());
      reqNova.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(codigoTipoIF);
      reqNova.atribuirGARANTIAS_GARANTIDO_CodigoContaCetip(reqVelha.obterGARANTIAS_GARANTIDO_CodigoContaCetip());
      reqNova.atribuirGARANTIAS_QUANTIDADE_Quantidade(reqVelha.obterGARANTIAS_QUANTIDADE_Quantidade());

      return reqNova;
   }

   private boolean realizadoLancamentoTransferenciaCustodiaSelic(MovimentacaoGarantiaDO mov) {
      //Verifica se a msg SEL1611 de liberacao ja foi recebida, se sim efetua o registroLancamentoTransferenciaCustodia
      IGarantiasSelic gSelic = getFactory().getInstanceGarantiasSelic();
      return gSelic.registradoLancamentoTransferenciaCustodia(mov);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      return null;
   }

}