package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.financeiro.ModalidadeLiquidacaoDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sca.UsuarioDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico para validacao dos dados inseridos pelo usuario na interface grafica de Vinculacao de Cesta de Garantias
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vincius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * @since Abril/2006
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_ACESSO"
 * 
 * @requisicao.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_IF"
 * 
 * @requisicao.method atributo="CodigoSistema" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_SISTEMA"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="GARANTIAS_DEPOSITADO"
 * 
 * @requisicao.method atributo="NumeroControleLancamento" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_QT_OPERACAO"
 * 
 * @requisicao.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_QT_OPERACAO"
 * 
 * @requisicao.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero" contexto="GARANTIAS_PU"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="MODALIDADE_LIQUIDACAO"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_STATUS"
 * 
 * @requisicao.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_PARTICIPANTE"
 *
 */
public class ServicoRegistraVinculacaoCestaGarantias extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Servico que registra vinculacao de Cesta de Garantias ***** ");
      }

      ContextoAtivacaoVO ca = getContextoAtivacao();
      IGerenciadorPersistencia gp = getGp();
      IGarantias factory = getFactory();

      ICestaDeGarantias cdgDao = factory.getInstanceCestaDeGarantias();
      IMovimentacoesGarantias iMov = factory.getInstanceMovimentacoesGarantias();
      Data dataHoje = getDataHoje();

      RequisicaoServicoRegistraVinculacaoCestaGarantias requisicao = (RequisicaoServicoRegistraVinculacaoCestaGarantias) req;
      ResultadoServicoRegistraVinculacaoCestaGarantias res = new ResultadoServicoRegistraVinculacaoCestaGarantias();

      CodigoIF codigoIF = requisicao.obterGARANTIAS_CODIGO_IF_CodigoIF();
      Funcao tipoAcesso = requisicao.obterGARANTIAS_TIPO_ACESSO_Funcao();
      InstrumentoFinanceiroDO instrFinanc = InstrumentoFinanceiroFactory.getInstance().obterInstrumentoFinanceiro(
            codigoIF);
      IContaParticipante contPartDao = ContaParticipanteFactory.getInstance();

      UsuarioDO usuario = new UsuarioDO();
      usuario.setId(new Id(ca.getIdUsuario().toString()));

      CestaGarantiasDO cesta = cdgDao.obterCestaDeGarantias(requisicao.obterGARANTIAS_CODIGO_NumeroCestaGarantia());

      MovimentacaoGarantiaDO movVinc = new MovimentacaoGarantiaDO();
      movVinc.setInstrumentoFinanceiro(instrFinanc);
      movVinc.setQtdGarantia(new Quantidade("0"));
      movVinc.setIndCetipado(Booleano.VERDADEIRO);
      movVinc.setIndDireitosGarantidor(Booleano.VERDADEIRO);
      movVinc.setCestaGarantias(cesta);
      movVinc.setDataMovimentacao(dataHoje);
      movVinc.setContaParticipante(cesta.getGarantidor());
      movVinc.setUsuario(usuario);

      Booleano indDepositado = requisicao.obterGARANTIAS_DEPOSITADO_Booleano();
      if (indDepositado.ehFalso()) {
         ModalidadeLiquidacaoDO modalidade = (ModalidadeLiquidacaoDO) gp.load(ModalidadeLiquidacaoDO.class, requisicao
               .obterMODALIDADE_LIQUIDACAO_Id());

         movVinc.setModalidade(modalidade);
         movVinc.setNumControleLancamento(requisicao.obterGARANTIAS_QT_OPERACAO_NumeroControleLancamento());
         movVinc.setQtdOperacao(requisicao.obterGARANTIAS_QT_OPERACAO_Quantidade());
         movVinc.setPuOperacao(requisicao.obterGARANTIAS_PU_Quantidade());
      }

      movVinc.setIndDepositado(indDepositado);
      movVinc.setTipoMovimentacaoGarantia(TipoMovimentacaoGarantiaDO.VINCULACAO);
      movVinc.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);
      movVinc.setCpfOuCnpjComitente(requisicao.obterGARANTIAS_PARTICIPANTE_CPFOuCNPJ());

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "STATUS CESTA: " + cesta.getStatusCesta().getNumIdStatusCesta().obterConteudo());
         Logger.debug(this, "LANCADOR: " + tipoAcesso);
      }

      // Pre movimentacao de vinculacao
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "ServicoRegistraVinculacaoCestaGarantias_Antes da pre  movimentacao de vinculacao");
      }

      MovimentacaoGarantiaDO preMovVinc = iMov.obterUltimaMovimentacao(cesta, TipoMovimentacaoGarantiaDO.VINCULACAO,
            StatusMovimentacaoGarantiaDO.PENDENTE);

      CodigoContaCetip parte = requisicao.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();
      CodigoContaCetip contraParte = requisicao.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
      IContaParticipante iCP = contPartDao;
      boolean mesmoParticipante = parte.mesmoConteudo(contraParte);

      if (!mesmoParticipante) {
         mesmoParticipante = iCP.eMesmoParticipanteRegra(parte, contraParte);
      }

      if (preMovVinc != null || mesmoParticipante) {
         // Validacao
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "ServicoRegistraVinculacaoCestaGarantias_Prestes a validar pre vinculacao");
         }

         if (!mesmoParticipante) {
            validaPreVinculacao(cesta.getStatusCesta(), tipoAcesso, preMovVinc, movVinc);
         } else if (tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDO)) {
            throw new Erro(CodigoErro.VINC_MMG_MESMO_PARTICIPANTE_LANCAMENTO_GARANTIDO);
         } else {
            gp.saveOrUpdate(movVinc);
         }

         // Tudo OK, inicia a vinculacao
         IVincularCesta vincularCesta = factory.getInstanceVincularCesta();
         vincularCesta.vincularCesta(cesta);
      } else {
         StatusCestaDO status = tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDOR) ? StatusCestaDO.VNC_PEND_GRTDO
               : StatusCestaDO.VNC_PEND_GRTDOR;
         cesta.setStatusCesta(status, dataHoje);
         movVinc.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.PENDENTE);

         gp.save(movVinc);
      }

      return res;
   }

   private void validaPreVinculacao(StatusCestaDO statusCesta, Funcao tipoAcesso, MovimentacaoGarantiaDO preMovVinc,
         MovimentacaoGarantiaDO movVinc) {
      boolean ladoGarantido = statusCesta.equals(StatusCestaDO.VNC_PEND_GRTDO)
            && tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDO);
      boolean ladoGarantidor = statusCesta.equals(StatusCestaDO.VNC_PEND_GRTDOR)
            && tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDOR);
      boolean mesmoIF = movVinc.getInstrumentoFinanceiro().equals(preMovVinc.getInstrumentoFinanceiro());
      boolean mesmoIndDepositado = preMovVinc.getIndDepositado().mesmoConteudo(movVinc.getIndDepositado());

      // Valida dados informados no segundo lancamento
      if (!(ladoGarantido || ladoGarantidor)
            || !mesmoIF
            || !mesmoIndDepositado
            || (movVinc.getIndDepositado().ehFalso() && (!movVinc.getNumControleLancamento().mesmoConteudo(
                  preMovVinc.getNumControleLancamento())
                  || !movVinc.getQtdOperacao().mesmoConteudo(preMovVinc.getQtdOperacao())
                  || !movVinc.getPuOperacao().mesmoConteudo(preMovVinc.getPuOperacao()) || !movVinc.getModalidade()
                  .getId().mesmoConteudo(preMovVinc.getModalidade().getId())))) {
         throw new Erro(CodigoErro.CESTA_VINC_INCOMPATIVEL);
      }
   }

   /*
    * Realiza a validacao dos dados digitados via motor de regras.
    * 
    * @param requisicao requisicao que contem os dados para validacao
    * 
    * @return o Resultado contendo se a validacao foi aceita ou nao
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}
