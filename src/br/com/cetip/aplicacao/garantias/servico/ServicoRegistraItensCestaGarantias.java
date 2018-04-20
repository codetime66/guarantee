package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiaVO;
import br.com.cetip.aplicacao.garantias.apinegocio.IAportarGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasCDAWA;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.IInstrumentoFinanceiro;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoGarantiaDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoCDA;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoWA;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico que registra um cadastro da cesta de garantias
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vinicius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @since Abril/2006
 * @resultado.class
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @resultado.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_IF"
 * 
 * @resultado.method atributo="IdTipoGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_QUANTIDADE"
 * 
 * @resultado.method atributo="DescricaoLimitada" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_ITENS"
 *                   
 * @resultado.method atributo="NumeroOperacao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="OPERACAO"                  
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_IF"
 * 
 * @requisicao.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_QUANTIDADE"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="DescricaoLimitada" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                    contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_ACESSO"
 *                    
 * @requisicao.method atributo="NumeroOperacao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="OPERACAO"
 *                   
 * @requisicao.method atributo="IdTipoGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="ACAO"
 */
public class ServicoRegistraItensCestaGarantias extends BaseGarantias implements Servico {

   private CestaGarantiasDO cesta;

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Chama o servico de inclusao de Itens na Cesta de Garantias ***** ");
      }

      RequisicaoServicoRegistraItensCestaGarantias req = (RequisicaoServicoRegistraItensCestaGarantias) requisicao;
      ResultadoServicoRegistraItensCestaGarantias res = new ResultadoServicoRegistraItensCestaGarantias();

      Id codigo = req.obterGARANTIAS_CODIGO_Id();

      if (codigo == null) {
         return new ResultadoServicoRegistraItensCestaGarantias();
      }

      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      cesta = icg.obterCestaDeGarantias(new NumeroCestaGarantia(codigo.obterConteudo()));

      criaEntradaItensCesta(req);

      res.atribuirGARANTIAS_CODIGO_Id(codigo);

      return res;
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   private void criaEntradaItensCesta(RequisicaoServicoRegistraItensCestaGarantias req) {
      // Criacao dos Itens da Cesta de Garantias
      Quantidade quantidade = req.obterGARANTIAS_QUANTIDADE_Quantidade();
      CodigoTipoIF codigoTipoIF = req.obterGARANTIAS_CODIGO_TIPO_CodigoTipoIF();
      CodigoIF codigoIF = req.obterGARANTIAS_CODIGO_IF_CodigoIF();
      Booleano indDireitosGarantidor = req.obterGARANTIAS_ITENS_Booleano();
      DescricaoLimitada descricao = req.obterGARANTIAS_ITENS_DescricaoLimitada();
      Funcao tipoAcesso = req.obterGARANTIAS_TIPO_ACESSO_Funcao();
      NumeroOperacao numeroOperacao = req.obterOPERACAO_NumeroOperacao();
      IdTipoGarantia idTipoGarantia = req.obterGARANTIAS_CODIGO_IdTipoGarantia();

      if (quantidade.toString().length() == 0
            || (quantidade.obterParteDecimal().obterInt() != 0 && codigoTipoIF.mesmoConteudo(CodigoTipoIF.CCB))) {
         throw new Erro(CodigoErro.CESTA_QT_INVALIDA);
      }

      final boolean ehFuncaoAporte = req.obterACAO_Funcao().mesmoConteudo(ICestaDeGarantias.APORTAR_GARANTIA);
      if (!cesta.getStatusCesta().isVinculada() && ehFuncaoAporte) {
         throw new Erro(CodigoErro.ACAO_INVALIDA_CESTA);
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "CESTA [" + cesta.getNumCestaGarantias() + "] STATUS [" + cesta.getStatusCesta()
               + "] FUNCAO [" + req.obterACAO_Funcao() + "]");
      }

      SistemaDO sistemaAtivo = null;

      GarantiaVO garantia = new GarantiaVO();
      garantia.indDireitoGarantidor = indDireitosGarantidor;
      garantia.quantidade = quantidade;
      garantia.descricao = descricao;
      garantia.numeroOperacao = numeroOperacao;

      final boolean ehAtivoCetipado = !codigoTipoIF.ehNAO_CETIPADO();
      if (ehAtivoCetipado) {
         IInstrumentoFinanceiro iif = InstrumentoFinanceiroFactory.getInstance();
         InstrumentoFinanceiroDO ativo = iif.obterInstrumentoFinanceiro(codigoIF);
         sistemaAtivo = ativo.getSistema();
         garantia.ativoCetipado = ativo;
      } else {
         garantia.codIfNCetipado = codigoIF;
      }

      if (cesta.getStatusCesta().isVinculada()) {
         IAportarGarantia aporte = getFactory().getInstanceAportarGarantia(sistemaAtivo);
         aporte.aportarItem(cesta, garantia, tipoAcesso);
      } else {
         IMovimentacoesGarantias imovs = getFactory().getInstanceMovimentacoesGarantias();
         imovs.incluirMovimentacaoBloqueio(cesta, garantia);
      }

      if (!Condicional.vazio(idTipoGarantia)) {
         TipoGarantiaDO tipoGarantia = new TipoGarantiaDO();
         tipoGarantia.setNumIdTipoGarantia(idTipoGarantia);
         cesta.setTipoGarantia(tipoGarantia);
      }

      IGarantiasCDAWA valCdaWa = getFactory().getGarantiasCDAWA();

      /*
       * Tarefa exclusiva para CDA/WA
       */
      if (!adicionouCDAWA
            && (codigoTipoIF.mesmoConteudo(CodigoTipoIF.CDA) || codigoTipoIF.mesmoConteudo(CodigoTipoIF.WA))) {
         if (codigoTipoIF.mesmoConteudo(CodigoTipoIF.CDA)) {
            adicionouCDAWA = true;
            boolean cestaTemWA = valCdaWa.encontrarWA(codigoIF, cesta);
            if (!cestaTemWA) {
               CodigoCDA codigoCDA = new CodigoCDA(codigoIF.obterConteudo());
               final CodigoWA respectivoWA = codigoCDA.obterRespectivoWA();
               RequisicaoServicoRegistraItensCestaGarantias reqNova = copiaRequisicao(req, CodigoTipoIF.WA,
                     respectivoWA);
               criaEntradaItensCesta(reqNova);
            }
         } else if (codigoTipoIF.mesmoConteudo(CodigoTipoIF.WA)) {
            adicionouCDAWA = true;
            boolean cestaTemCDA = valCdaWa.encontrarCDA(codigoIF, cesta);
            if (!cestaTemCDA) {
               CodigoWA codigoWA = new CodigoWA(codigoIF.obterConteudo());
               final CodigoCDA respectivoCDA = codigoWA.obterRespectivoCDA();
               RequisicaoServicoRegistraItensCestaGarantias reqNova = copiaRequisicao(req, CodigoTipoIF.CDA,
                     respectivoCDA);
               criaEntradaItensCesta(reqNova);
            }
         }
      }
   }

   private boolean adicionouCDAWA = false;

   private RequisicaoServicoRegistraItensCestaGarantias copiaRequisicao(
         RequisicaoServicoRegistraItensCestaGarantias reqVelha, CodigoTipoIF codigoTipoIF, CodigoIF codigoIF) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Fazendo copia de Requisicao para CDA/WA");
      }

      RequisicaoServicoRegistraItensCestaGarantias reqNova = new RequisicaoServicoRegistraItensCestaGarantias();

      reqNova.atribuirGARANTIAS_CODIGO_Id(reqVelha.obterGARANTIAS_CODIGO_Id());
      reqNova.atribuirGARANTIAS_CODIGO_IdTipoGarantia(reqVelha.obterGARANTIAS_CODIGO_IdTipoGarantia());
      reqNova.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(reqVelha.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip());
      reqNova.atribuirGARANTIAS_ITENS_Booleano(reqVelha.obterGARANTIAS_ITENS_Booleano());
      reqNova.atribuirGARANTIAS_ITENS_DescricaoLimitada(reqVelha.obterGARANTIAS_ITENS_DescricaoLimitada());
      reqNova.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(reqVelha.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip());
      reqNova.atribuirGARANTIAS_QUANTIDADE_Quantidade(reqVelha.obterGARANTIAS_QUANTIDADE_Quantidade());
      reqNova.atribuirGARANTIAS_TIPO_ACESSO_Funcao(reqVelha.obterGARANTIAS_TIPO_ACESSO_Funcao());
      reqNova.atribuirACAO_Funcao(reqVelha.obterACAO_Funcao());

      reqNova.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(codigoTipoIF);
      reqNova
            .atribuirGARANTIAS_CODIGO_IF_CodigoIF(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF, codigoIF.obterConteudo()));

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Requisicao nova para CDA/WA");
         Logger.debug(this, reqNova.toString());
      }

      return reqNova;
   }

}
