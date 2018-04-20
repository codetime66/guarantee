package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.servicosinfra.apinegocio.arquivo.ISolicitacaoGeracaoArquivo;
import br.com.cetip.aplicacao.servicosinfra.apinegocio.arquivo.SolicitacaoGeracaoArquivoFactory;
import br.com.cetip.aplicacao.servicosinfra.apinegocio.servico.ServicoUtilFactory;
import br.com.cetip.dados.aplicacao.garantias.EventosDetalheCestaVDO;
import br.com.cetip.dados.aplicacao.sap.MaloteDO;
import br.com.cetip.dados.aplicacao.servicosinfra.SolicitacaoGeracaoArquivoDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NomeMalote;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.SiglaUF;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.DescricaoAtributo;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="ARQUIVO"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="MALOTE_DESTINATARIO"
 * 
 * @resultado.method atributo="SiglaUF" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="UF"
 * 
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CESTA_GARANTIA"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @resultado.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO_PERTENCE"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="INSTRUMENTO_FINANCEIRO_EMISSAO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="TIPO_IF"
 * 
 * @resultado.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="TIPO_IF"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="SISTEMA"
 * 
 * @resultado.method atributo="CodigoSistema" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="SISTEMA"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="EVENTO"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="EVENTO_DATA_ORIGINAL"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="EVENTO_DATA_OCORRENCIA"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="EVENTO_DATA_LIQUIDACAO"
 * 
 * @resultado.method atributo="Percentual" pacote="br.com.cetip.infra.atributo.tipo.numero" contexto="TAXA_EVENTO"
 * 
 * @resultado.method atributo="Preco" pacote="br.com.cetip.infra.atributo.tipo.numero" contexto="PU_EVENTO"
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="INCORPORA_JUROS"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="TIPO_EVENTO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CODIGO_TIPO_EVENTO_LEGADO"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="TIPO_EVENTO"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="OBSERVACAO"
 * 
 * @resultado.method atributo="NomeSimplificado" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="REGISTRADOR_NOME_SIMPLIFICADO"
 * 
 * @resultado.method atributo="NomeSimplificado" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="AGENTE_PAGAMENTO_NOME_SIMPLIFICADO"
 *                   
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="CARACTERISTICA_CESTA"                   
 * 
 */
public class ServicoGeraArquivoEventosCesta extends BaseGarantias implements Servico {

   private ResultadoServicoGeraArquivoEventosCesta res = new ResultadoServicoGeraArquivoEventosCesta();

   private IGerenciadorPersistencia gp;

   private IConsulta consultaEventos;

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      Data dataOperacao = getDataHoje();

      gp = getGp();

      Id idServicoCaracteristicas = ServicoUtilFactory.getInstance().obterIdServico(
            "br.com.cetip.aplicacao.garantias.servico.ServicoGeraArquivoEventosCesta");

      ISolicitacaoGeracaoArquivo iSol = SolicitacaoGeracaoArquivoFactory.getInstance();

      List listaSolicitacoes = iSol.obterSolicitacaoGeracaoArquivoPendente(idServicoCaracteristicas, dataOperacao);
      Iterator it = listaSolicitacoes.iterator();

      while (it.hasNext()) {
         SolicitacaoGeracaoArquivoDO solicitacao = (SolicitacaoGeracaoArquivoDO) it.next();
         List listaParametros = DescricaoAtributo.obterListaAtributos(solicitacao.getDesParametrosServico().toString());

         NumeroCestaGarantia numeroCestaGarantia = (NumeroCestaGarantia) listaParametros.get(0);
         Id idCesta = numeroCestaGarantia.copiarParaId();
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Id da Cesta: " + numeroCestaGarantia);
         }

         Texto nomMalote = (Texto) listaParametros.get(1);
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Descricao do malote: " + nomMalote);
         }

         MaloteDO malote = ContaParticipanteFactory.getInstance()
               .obterMaloteAtivo(new NomeMalote(nomMalote.toString()));
         SiglaUF uf = new SiglaUF(malote.getUf().getSigla().toString());
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "UF: " + uf);
         }

         Iterator eventosIF = obterEventosCesta(idCesta);
         if (!eventosIF.hasNext()) {
            res.atribuirARQUIVO_Data(dataOperacao);
         } else {
            while (eventosIF.hasNext()) {
               EventosDetalheCestaVDO eventosVDO = (EventosDetalheCestaVDO) eventosIF.next();
               preencheResultado(eventosVDO, numeroCestaGarantia, nomMalote, uf, dataOperacao);
            }
         }

         iSol.alteraStatusSolicitacaoGeracaoArquivo(solicitacao);

         gp.flush();
         gp.clear();
      }

      return res;
   }

   private Iterator obterEventosCesta(Id numeroCestaGarantia) {
      if (consultaEventos == null) {
         consultaEventos = gp.criarConsulta("from " + EventosDetalheCestaVDO.class.getName()
               + " if where if.numIdCestaGarantias = ? ");
      }

      consultaEventos.setAtributo(0, numeroCestaGarantia);

      return consultaEventos.list().iterator();
   }

   private void preencheResultado(EventosDetalheCestaVDO eventosVDO, NumeroCestaGarantia numeroCestaGarantia,
         Texto nomMalote, SiglaUF uf, Data dataArquivo) {

      res.novaLinha();

      res.atribuirCESTA_GARANTIA_NumeroCestaGarantia(numeroCestaGarantia);
      res.atribuirARQUIVO_Data(dataArquivo);
      res.atribuirUF_SiglaUF(uf);
      res.atribuirMALOTE_DESTINATARIO_Texto(nomMalote);
      res.atribuirINSTRUMENTO_FINANCEIRO_Id(eventosVDO.getNumIf());
      res.atribuirINSTRUMENTO_FINANCEIRO_CodigoIF(new CodigoIF(eventosVDO.getCodigoIf().toString()));
      res.atribuirINSTRUMENTO_FINANCEIRO_PERTENCE_Id(eventosVDO.getNumIfPertence());
      res.atribuirINSTRUMENTO_FINANCEIRO_EMISSAO_Data(eventosVDO.getDataEmissao());
      res.atribuirTIPO_IF_Id(eventosVDO.getNumTipoIf());
      res.atribuirTIPO_IF_CodigoTipoIF(eventosVDO.getCodigoTipoIf());
      res.atribuirSISTEMA_Id(eventosVDO.getNumSistema());
      res.atribuirSISTEMA_CodigoSistema(eventosVDO.getCodSistema());
      res.atribuirEVENTO_Id(eventosVDO.getNumEvento());
      res.atribuirEVENTO_DATA_ORIGINAL_Data(eventosVDO.getDataOriginalEvento());
      res.atribuirEVENTO_DATA_OCORRENCIA_Data(eventosVDO.getDataOcorrenciaEvento());
      res.atribuirEVENTO_DATA_LIQUIDACAO_Data(eventosVDO.getDataLiquidacao());
      res.atribuirTAXA_EVENTO_Percentual(eventosVDO.getValTaxaEvento());
      res.atribuirPU_EVENTO_Preco(eventosVDO.getValPuEvento());
      res.atribuirINCORPORA_JUROS_Booleano(eventosVDO.getIndIncorpora());
      res.atribuirTIPO_EVENTO_Id(eventosVDO.getNumTipoEventoLegado());
      res.atribuirCODIGO_TIPO_EVENTO_LEGADO_Id(eventosVDO.getCodTipoEventoLegado());
      res.atribuirTIPO_EVENTO_Nome(eventosVDO.getNomTipoEventoLegado());
      res.atribuirOBSERVACAO_Texto(eventosVDO.getTxtObservacao());
      res.atribuirREGISTRADOR_NOME_SIMPLIFICADO_NomeSimplificado(eventosVDO.getNomeRegistrador());
      res.atribuirAGENTE_PAGAMENTO_NOME_SIMPLIFICADO_NomeSimplificado(eventosVDO.getNomeAgentePgto());
      res.atribuirCARACTERISTICA_CESTA_Texto(eventosVDO.getObsCesta());

   }

   public Resultado executar(Requisicao arg0) throws Exception {
      return null;
   }

}
