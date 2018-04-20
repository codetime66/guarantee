package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.servicosinfra.apinegocio.arquivo.SolicitacaoGeracaoArquivoFactory;
import br.com.cetip.aplicacao.servicosinfra.apinegocio.servico.ServicoUtilFactory;
import br.com.cetip.dados.aplicacao.garantias.IfDetalheCestaVDO;
import br.com.cetip.dados.aplicacao.sap.MaloteDO;
import br.com.cetip.dados.aplicacao.servicosinfra.SolicitacaoGeracaoArquivoDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NomeMalote;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.SiglaUF;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.atributo.utilitario.DescricaoAtributo;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.persistencia.IResultadoPaginado;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

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
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="TIPO_IF"
 * 
 * @resultado.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="TIPO_IF"
 * 
 * @resultado.method atributo="CodIsin" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="INSTRUMENTO_FINANCEIRO_EMISSAO"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="INSTRUMENTO_FINANCEIRO_VENCIMENTO"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="INSTRUMENTO_FINANCEIRO_REGISTRO"
 * 
 * @resultado.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="INSTRUMENTO_FINANCEIRO_VALOR_UNITARIO_EMISSAO"
 * 
 * @resultado.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="VALOR_NOMINAL_ATUALIZADO"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="VALOR_NOMINAL_ATUALIZADO"
 * 
 * @resultado.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="INSTRUMENTO_FINANCEIRO_VALOR_UNITARIO_ATUALIZADO"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="INSTRUMENTO_FINANCEIRO_VALOR_UNITARIO_ATUALIZADO"
 * 
 * @resultado.method atributo="Preco" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="INSTRUMENTO_FINANCEIRO_PRECO_UNITARIO_JUROS"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="INSTRUMENTO_FINANCEIRO_PRECO_UNITARIO_JUROS"
 * 
 * @resultado.method atributo="Preco" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="INSTRUMENTO_FINANCEIRO_PRECO_UNITARIO_ATUALIZADO"
 * 
 * @resultado.method atributo="CodigoSituacaoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO_SITUACAO"
 * 
 * @resultado.method atributo="CodigoSituacaoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO_DESCRICAO_SITUACAO"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="INSTRUMENTO_FINANCEIRO_SITUACAO"
 * 
 * @resultado.method atributo="IdTipoFormaPagamento" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="FORMA_PAGAMENTO"
 * 
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="FORMA_PAGAMENTO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO_PERTENCE"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="SISTEMA"
 * 
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="DADOS_COMPLEMENTARES"
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="ADITAMENTO"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="ADITAMENTO"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="DATA_ULTIMO_JUROS"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="DATA_ULTIMA_AMORTIZACAO"
 * 
 * @resultado.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="VALOR_INCORPORADO"
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="INADIMPLENTE"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="INADIMPLENTE"
 * 
 * @resultado.method atributo="QuantidadeInteiraPositiva" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="QUANTIDADE_DEPOSITADA"
 * 
 * @resultado.method atributo="QuantidadeInteiraPositiva" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="QUANTIDADE_EMITIDA"
 * 
 * @resultado.method atributo="QuantidadeInteiraPositiva" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="QUANTIDADE_RESGATADA"
 * 
 * @resultado.method atributo="QuantidadeInteiraPositiva" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="QUANTIDADE_RETIRADA"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="CONTA_REGISTRADOR"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CONTA_REGISTRADOR"
 * 
 * @resultado.method atributo="NomeSimplificado" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="REGISTRADOR_NOME_SIMPLIFICADO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="CUSTODIANTE"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CUSTODIANTE"
 * 
 * @resultado.method atributo="NomeSimplificado" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CUSTODIANTE_NOME_SIMPLIFICADO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CONTA_AGENTE_PAGAMENTO"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CONTA_AGENTE_PAGAMENTO"
 * 
 * @resultado.method atributo="NomeSimplificado" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="AGENTE_PAGAMENTO_NOME_SIMPLIFICADO"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="EMITENTE"
 * 
 * @resultado.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="EMITENTE"
 * 
 * @resultado.method atributo="Natureza" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="EMITENTE"
 * 
 * @resultado.method atributo="CodigoCoobrigacao" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="COOBRIGACAO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="MUNICIPIO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="CREDOR_CONTA"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CREDOR_CONTA"
 * 
 * @resultado.method atributo="NomeSimplificado" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CREDOR"
 * 
 * @resultado.method atributo="ContratoCredito" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CONTRATO_CREDITO"
 * 
 * @resultado.method atributo="IdTipoOrigemCredito" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_CREDITO"
 * 
 * @resultado.method atributo="IdTipoOrigemCredito" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_CREDITO_DESCRICAO"
 * 
 * @resultado.method atributo="Percentual" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="PERCENTUAL_COOBRIGADO"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIDOR"
 * 
 * @resultado.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIDOR"
 * 
 * @resultado.method atributo="Natureza" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIDOR"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIA_1"
 * 
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="TIPO_GARANTIA_1"
 * 
 * @resultado.method atributo="IdTipoGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_GARANTIA_1"
 * 
 * @resultado.method atributo="ProprietarioGarantia" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="GARANTIA_1"
 * 
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIA_1"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIA_2"
 * 
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="TIPO_GARANTIA_2"
 * 
 * @resultado.method atributo="IdTipoGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_GARANTIA_2"
 * 
 * @resultado.method atributo="ProprietarioGarantia" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="GARANTIA_2"
 * 
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIA_2"
 * 
 * @resultado.method atributo="NomeAbreviadoIndice" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="INDICE_VALORIZACAO"
 * 
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="INDICE_VALORIZACAO_OUTROS"
 * 
 * @resultado.method atributo="NomeTipoIndicador" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="INDICE_VALORIZACAO_OUTROS"
 * 
 * @resultado.method atributo="NomeUnidadeTempo" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="TIPO_CORRECAO"
 * 
 * @resultado.method atributo="CodigoTipoPrazoJuros" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="PRO_RATA_CORRECAO"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="TIPO_CORRECAO"
 * 
 * @resultado.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="PERCENTUAL_TAXA_FLUTUANTE"
 * 
 * @resultado.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero" contexto="TAXA_SPREAD"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="CRITERIO_CALCULO_JUROS"
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="INCORPORA_JUROS"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="INCORPORA_JUROS"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="INCORPORA_JUROS"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="PERIODICIDADE_JUROS"
 * 
 * @resultado.method atributo="QuantidadeInteira" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="TIPO_UNIDADE_TEMPO_JUROS"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="TIPO_UNIDADE_TEMPO_JUROS"
 * 
 * @resultado.method atributo="CodigoTipoPrazoJuros" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_PRAZO_JUROS"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="JUROS_INICIO"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="TIPO_AMORTIZACAO"
 * 
 * @resultado.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="TAXA_AMORTIZACAO"
 * 
 * @resultado.method atributo="QuantidadeInteira" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="TIPO_UNIDADE_TEMPO_AMORTIZACAO"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="TIPO_UNIDADE_TEMPO_AMORTIZACAO"
 * 
 * @resultado.method atributo="CodigoTipoPrazoJuros" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_PRAZO_AMORTIZACAO"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="AMORTIZACAO_INICIO"
 * 
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="DESLOC_LIQUIDACAO"
 * 
 * @resultado.method atributo="NumeroInteiroLimitado" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="EVENTO_DATA_LIQUIDACAO"
 * 
 * @resultado.method atributo="Detalhe"
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="DETALHE_ADICIONAL"
 * 
 */
public class ServicoGeraArquivoCaracteristicasCesta extends BaseGarantias implements Servico {

   private ResultadoServicoGeraArquivoCaracteristicasCesta res = new ResultadoServicoGeraArquivoCaracteristicasCesta();

   private IGerenciadorPersistencia gp;

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {

      gp = getGp();
      Data dataOperacao = getDataHoje();

      Id idServicoCaracteristicas = ServicoUtilFactory.getInstance().obterIdServico(
            "br.com.cetip.aplicacao.garantias.servico.ServicoGeraArquivoCaracteristicasCesta");

      List listaSolicitacoes;
      try {
         listaSolicitacoes = SolicitacaoGeracaoArquivoFactory.getInstance().obterSolicitacaoGeracaoArquivoPendente(
               idServicoCaracteristicas, dataOperacao);
         Iterator it = listaSolicitacoes.iterator();
         NumeroCestaGarantia numeroCestaGarantia = null;
         Texto nomMalote = null;

         while (it.hasNext()) {
            SolicitacaoGeracaoArquivoDO solicitacao = (SolicitacaoGeracaoArquivoDO) it.next();
            List listaParametros = DescricaoAtributo.obterListaAtributos(solicitacao.getDesParametrosServico()
                  .toString());

            for (int i = 0; i < listaParametros.size(); i++) {
               if (i == 0) {
                  numeroCestaGarantia = (NumeroCestaGarantia) listaParametros.get(0);
                  if (Logger.estaHabilitadoDebug(this)) {
                     Logger.debug(this, "Id da Cesta: " + numeroCestaGarantia);
                  }
               } else if (i == 1) {
                  nomMalote = (Texto) listaParametros.get(1);
                  if (Logger.estaHabilitadoDebug(this)) {
                     Logger.debug(this, "Descricao do malote: " + nomMalote);
                  }
               }
            }

            if (nomMalote == null) {
               nomMalote = new Texto("");
            }

            MaloteDO malote = ContaParticipanteFactory.getInstance().obterMaloteAtivo(
                  new NomeMalote(nomMalote.toString()));
            SiglaUF uf = new SiglaUF(malote.getUf().getSigla().toString());
            IConsulta caracteristicasIF = obterCaracteristicasGarantias(numeroCestaGarantia);
            IResultadoPaginado rp = caracteristicasIF.scroll();

            if (!rp.next()) {
               res.atribuirARQUIVO_Data(dataOperacao);
            } else {
               do {
                  IfDetalheCestaVDO ifVDO = (IfDetalheCestaVDO) rp.get();
                  preencheResultado(ifVDO, numeroCestaGarantia, nomMalote,

                  uf, dataOperacao);
                  gp.evict(ifVDO);
               } while (rp.next());
            }

            SolicitacaoGeracaoArquivoFactory.getInstance().alteraStatusSolicitacaoGeracaoArquivo(solicitacao);

         }

      } catch (Exception e) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Nao existe solicitacao de arquivo pendente!");
         }
      }

      return res;
   }

   private IConsulta obterCaracteristicasGarantias(NumeroCestaGarantia numeroCestaGarantia) {
      IConsulta con = null;
      con = gp.criarConsulta("from " + IfDetalheCestaVDO.class.getName() + " if where if.numIdCestaGarantias = ? ");

      con.setAtributo(0, new Id(numeroCestaGarantia.obterConteudo().toString()));
      return con;
   }

   private void preencheResultado(IfDetalheCestaVDO ifVDO, NumeroCestaGarantia numeroCestaGarantia, Texto nomMalote,
         SiglaUF uf, Data dataArquivo) {

      res.novaLinha();

      res.atribuirCESTA_GARANTIA_NumeroCestaGarantia(numeroCestaGarantia);
      res.atribuirARQUIVO_Data(dataArquivo);
      res.atribuirUF_SiglaUF(uf);
      res.atribuirMALOTE_DESTINATARIO_Texto(nomMalote);
      res.atribuirINSTRUMENTO_FINANCEIRO_CodIsin(ifVDO.getCodigoIsin());
      res.atribuirINSTRUMENTO_FINANCEIRO_CodigoIF(new CodigoIF(ifVDO.getCodigoIF().toString()));
      res.atribuirINSTRUMENTO_FINANCEIRO_Id(ifVDO.getNumeroIF());
      res.atribuirINSTRUMENTO_FINANCEIRO_EMISSAO_Data(ifVDO.getDataEmissao());
      res.atribuirINSTRUMENTO_FINANCEIRO_REGISTRO_Data(ifVDO.getDataRegistro());
      res.atribuirINSTRUMENTO_FINANCEIRO_VALOR_UNITARIO_EMISSAO_ValorMonetario(ifVDO.getValorUnitarioEmissao());
      res.atribuirVALOR_NOMINAL_ATUALIZADO_ValorMonetario(ifVDO.getValorNominal());
      res.atribuirVALOR_NOMINAL_ATUALIZADO_Data(ifVDO.getDataValorNominal());
      res.atribuirINSTRUMENTO_FINANCEIRO_VALOR_UNITARIO_ATUALIZADO_ValorMonetario(ifVDO
            .getValorUnitarioEmissaoAtualizado());
      res.atribuirINSTRUMENTO_FINANCEIRO_VALOR_UNITARIO_ATUALIZADO_Data(ifVDO.getDataUnitarioEmissaoAtualizado());
      res.atribuirINSTRUMENTO_FINANCEIRO_PRECO_UNITARIO_JUROS_Preco(ifVDO.getPrecoUnitarioJuros());
      res.atribuirINSTRUMENTO_FINANCEIRO_PRECO_UNITARIO_JUROS_Data(ifVDO.getDataPrecoUnitarioJuros());
      res.atribuirINSTRUMENTO_FINANCEIRO_PRECO_UNITARIO_ATUALIZADO_Preco(ifVDO.getPrecoUnitarioAtualizado());
      res.atribuirTIPO_IF_Id(ifVDO.getNumeroTipoIF());
      res.atribuirINSTRUMENTO_FINANCEIRO_VENCIMENTO_Data(ifVDO.getDataVencimento());
      res.atribuirINSTRUMENTO_FINANCEIRO_SITUACAO_CodigoSituacaoIF(ifVDO.getIdSituacaoIf());
      res.atribuirFORMA_PAGAMENTO_IdTipoFormaPagamento(ifVDO.getCodigoFormaPagamento());
      res.atribuirINSTRUMENTO_FINANCEIRO_PERTENCE_Id(ifVDO.getCertificado());
      res.atribuirSISTEMA_Id(ifVDO.getNumeroSistema());
      res.atribuirINSTRUMENTO_FINANCEIRO_SITUACAO_Data(ifVDO.getDataSituacao());
      res.atribuirDADOS_COMPLEMENTARES_Descricao(ifVDO.getDescricaoGarantiaAdicional());

      Texto indAditamento = null;
      if ((Condicional.vazio(ifVDO.getIndAditamento())) || (ifVDO.getIndAditamento().mesmoConteudo(Booleano.FALSO))) {
         indAditamento = new Texto("NAO");
      } else if (ifVDO.getIndAditamento().mesmoConteudo(Booleano.VERDADEIRO)) {
         indAditamento = new Texto("SIM");
      }
      res.atribuirADITAMENTO_Texto(indAditamento);
      res.atribuirADITAMENTO_Booleano(ifVDO.getIndAditamento());
      res.atribuirDATA_ULTIMO_JUROS_Data(ifVDO.getDatUltimoJuros());
      res.atribuirDATA_ULTIMA_AMORTIZACAO_Data(ifVDO.getDatUltimaAmort());
      res.atribuirVALOR_INCORPORADO_ValorMonetario(ifVDO.getValorAposIncorporacao());

      Texto indInadimplente = null;
      if ((Condicional.vazio(ifVDO.getInadimplente())) || (ifVDO.getInadimplente().mesmoConteudo(Booleano.FALSO))) {
         indInadimplente = new Texto("NAO");
      } else if (ifVDO.getInadimplente().mesmoConteudo(Booleano.VERDADEIRO)) {
         indInadimplente = new Texto("SIM");
      }
      res.atribuirINADIMPLENTE_Texto(indInadimplente);
      res.atribuirINADIMPLENTE_Booleano(ifVDO.getInadimplente());
      res.atribuirQUANTIDADE_DEPOSITADA_QuantidadeInteiraPositiva(ifVDO.getQuantidadeDepositada());
      res.atribuirQUANTIDADE_EMITIDA_QuantidadeInteiraPositiva(ifVDO.getQuantidadeEmitida());
      res.atribuirQUANTIDADE_RESGATADA_QuantidadeInteiraPositiva(ifVDO.getQuantidadeResgatada());
      res.atribuirQUANTIDADE_RETIRADA_QuantidadeInteiraPositiva(ifVDO.getQuantidadeRetirada());
      res.atribuirCONTA_REGISTRADOR_Id(ifVDO.getIdContaRegistrador());
      res.atribuirCONTA_REGISTRADOR_CodigoContaCetip(ifVDO.getCodigoContaRegistrador());
      res.atribuirREGISTRADOR_NOME_SIMPLIFICADO_NomeSimplificado(ifVDO.getNomeSimplificadoRegistrador());
      res.atribuirCUSTODIANTE_Id(ifVDO.getIdContaCustodiante());
      res.atribuirCUSTODIANTE_CodigoContaCetip(ifVDO.getCodigoContaCustodiante());
      res.atribuirCUSTODIANTE_NOME_SIMPLIFICADO_NomeSimplificado(ifVDO.getNomeSimplificadoCustodiante());
      res.atribuirCONTA_AGENTE_PAGAMENTO_Id(ifVDO.getIdContaAgentePagamento());
      res.atribuirCONTA_AGENTE_PAGAMENTO_CodigoContaCetip(ifVDO.getCodigoContaAgentePagamento());
      res.atribuirAGENTE_PAGAMENTO_NOME_SIMPLIFICADO_NomeSimplificado(ifVDO.getNomeSimplificadoAgentePagamento());
      res.atribuirEMITENTE_Nome(ifVDO.getNomeEmitente());
      res.atribuirEMITENTE_CPFOuCNPJ(ifVDO.getCnpjEmitente());
      res.atribuirCOOBRIGACAO_CodigoCoobrigacao(ifVDO.getCodigoCoobrigacao());
      res.atribuirMUNICIPIO_Id(ifVDO.getMunicipioEmissao());
      res.atribuirCREDOR_CONTA_Id(ifVDO.getIdContaCredorOriginal());
      res.atribuirCREDOR_CONTA_CodigoContaCetip(ifVDO.getCodigoContaCredorOriginal());
      res.atribuirCREDOR_NomeSimplificado(ifVDO.getNomeSimplificadoCredorOriginal());
      res.atribuirCONTRATO_CREDITO_ContratoCredito(ifVDO.getNumeroContrato());
      res.atribuirTIPO_CREDITO_IdTipoOrigemCredito(ifVDO.getIdTipoOrigemCredito());
      res.atribuirEMITENTE_Natureza(ifVDO.getNaturezaEmitente());
      res.atribuirTIPO_IF_CodigoTipoIF(ifVDO.getCodigoTipoIF());
      res.atribuirFORMA_PAGAMENTO_Descricao(ifVDO.getFormaPagamento());
      res.atribuirTIPO_CREDITO_DESCRICAO_IdTipoOrigemCredito(ifVDO.getCodigoTipoCredito());
      res.atribuirINSTRUMENTO_FINANCEIRO_DESCRICAO_SITUACAO_CodigoSituacaoIF(ifVDO.getCodSituacaoIF());
      res.atribuirPERCENTUAL_COOBRIGADO_Percentual(ifVDO.getPercentualCoobrigacao());

      res.atribuirGARANTIDOR_Nome(ifVDO.getNomGarantidor());
      res.atribuirGARANTIDOR_CPFOuCNPJ(ifVDO.getCpfOuCnpjGarantidor());
      res.atribuirGARANTIDOR_Natureza(ifVDO.getIndNaturezaGarantidor());
      res.atribuirTIPO_GARANTIA_1_IdTipoGarantia(ifVDO.getIdTipoGarantia1());
      res.atribuirTIPO_GARANTIA_1_Descricao(ifVDO.getDesTipoGarantia1());
      res.atribuirGARANTIA_1_Id(ifVDO.getNumIdGarantia1());
      res.atribuirGARANTIA_1_Descricao(ifVDO.getDesGarantia1());
      res.atribuirGARANTIA_1_ProprietarioGarantia(ifVDO.getNomProprietarioGarantia1());
      res.atribuirTIPO_GARANTIA_2_IdTipoGarantia(ifVDO.getIdTipoGarantia2());
      res.atribuirTIPO_GARANTIA_2_Descricao(ifVDO.getDesTipoGarantia2());
      res.atribuirGARANTIA_2_Id(ifVDO.getNumIdGarantia2());
      res.atribuirGARANTIA_2_Descricao(ifVDO.getDesGarantia2());
      res.atribuirGARANTIA_2_ProprietarioGarantia(ifVDO.getNomProprietarioGarantia2());
      res.atribuirINDICE_VALORIZACAO_NomeAbreviadoIndice(ifVDO.getTipoIndiceValorizacao());
      res.atribuirINDICE_VALORIZACAO_OUTROS_Descricao(ifVDO.getDesIndiceOutros());
      res.atribuirINDICE_VALORIZACAO_OUTROS_NomeTipoIndicador(ifVDO.getTipoIndiceOutros());
      res.atribuirTIPO_CORRECAO_NomeUnidadeTempo(ifVDO.getNomTipoCorrecao());
      res.atribuirTIPO_CORRECAO_Texto(ifVDO.getTipoCorrecao());
      res.atribuirPRO_RATA_CORRECAO_CodigoTipoPrazoJuros(ifVDO.getProRataCorrecao());
      res.atribuirPERCENTUAL_TAXA_FLUTUANTE_ValorMonetario(ifVDO.getValPercentualTaxaFlutuante());
      res.atribuirTAXA_SPREAD_ValorMonetario(ifVDO.getValTaxaJurosSpread());
      res.atribuirCRITERIO_CALCULO_JUROS_Texto(ifVDO.getCriterioCalculoJuros());

      Texto indIncorporaJuros = null;
      if ((Condicional.vazio(ifVDO.getIndIncorporaJuros()))
            || (ifVDO.getIndIncorporaJuros().mesmoConteudo(Booleano.FALSO))) {
         indIncorporaJuros = new Texto("NAO");
      } else if (ifVDO.getIndIncorporaJuros().mesmoConteudo(Booleano.VERDADEIRO)) {
         indIncorporaJuros = new Texto("SIM");
      }
      res.atribuirINCORPORA_JUROS_Texto(indIncorporaJuros);
      res.atribuirINCORPORA_JUROS_Booleano(ifVDO.getIndIncorporaJuros());
      res.atribuirINCORPORA_JUROS_Data(ifVDO.getDataIncorporaJuros());
      res.atribuirPERIODICIDADE_JUROS_Nome(ifVDO.getPeriodicidadeJuros());
      res.atribuirTIPO_UNIDADE_TEMPO_JUROS_QuantidadeInteira(ifVDO.getQtdUnidTempoJuros());
      res.atribuirTIPO_UNIDADE_TEMPO_JUROS_Texto(ifVDO.getTipoUnidTempoJuros());
      res.atribuirTIPO_PRAZO_JUROS_CodigoTipoPrazoJuros(ifVDO.getTipoPrazoJuros());
      res.atribuirJUROS_INICIO_Data(ifVDO.getDatInicioPagtoJuros());
      res.atribuirTIPO_AMORTIZACAO_Texto(ifVDO.getTipoAmort());
      res.atribuirTAXA_AMORTIZACAO_ValorMonetario(ifVDO.getValTaxaAmortizacao());
      res.atribuirTIPO_UNIDADE_TEMPO_AMORTIZACAO_QuantidadeInteira(ifVDO.getQtdUnidTempoAmort());
      res.atribuirTIPO_UNIDADE_TEMPO_AMORTIZACAO_Texto(ifVDO.getTipoUnidTempoAmort());
      res.atribuirTIPO_PRAZO_AMORTIZACAO_CodigoTipoPrazoJuros(ifVDO.getCodTipoPrazoAmort());
      res.atribuirAMORTIZACAO_INICIO_Data(ifVDO.getDatInicioPagtoAmort());
      res.atribuirDESLOC_LIQUIDACAO_Descricao(ifVDO.getDesFormaDeslocLiquidacao());
      res.atribuirEVENTO_DATA_LIQUIDACAO_NumeroInteiroLimitado(ifVDO.getDiaUtilLiquidacaoEventos());
      res.atribuirDETALHE_ADICIONAL_Detalhe(ifVDO.getObs());

   }

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}
