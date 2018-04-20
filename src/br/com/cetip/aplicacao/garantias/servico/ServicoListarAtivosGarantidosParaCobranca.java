package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.administracao.apinegocio.ControleOperacionalFactory;
import br.com.cetip.aplicacao.administracao.apinegocio.DiaUtilFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.cobranca.ConsultaAtivosGarantidosFactory;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.tipo.numero.ValorMonetario;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.tempo.Prazo;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Data"
 *                   pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="ARQUIVO"
 * 
 * @resultado.method atributo="QuantidadeInteiraPositiva"
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="DIAS_UTEIS"
 * 
 * @resultado.method atributo="Data"
 *                   pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="DIA"
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="DETENTOR"
 * 
 * @resultado.method atributo="CodigoTipoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @resultado.method atributo="CodigoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @resultado.method atributo="ValorMonetario"
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="VALOR_NOMINAL_ATUALIZADO"
 *                   
 * @resultado.method atributo="Id"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CESTA_GARANTIA"
 *                   
 * @resultado.method atributo="Texto"
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_STATUS"                 
 *                   
 * @resultado.method atributo="QuantidadeInteiraPositiva"
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="NATUREZA_SELIC"
 */
public class ServicoListarAtivosGarantidosParaCobranca implements Servico {

   private ResultadoServicoListarAtivosGarantidosParaCobranca resultado;

   private Data dataBatch;

   private static final int COL_DETENTOR = 0;
   private static final int COL_CODIGO_TIPO_IF = 1;
   private static final int COL_CODIGO_IF = 2;
   private static final int COL_ID_CESTA_GARANTIA = 3;
   private static final int COL_STATUS_CESTA = 4;
   private static final int COL_VALOR_ATUALIZADO = 5;
   private static final int COL_COUNT_SELIC = 6;

   public Resultado executar(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      resultado = new ResultadoServicoListarAtivosGarantidosParaCobranca();

      List ativos = ConsultaAtivosGarantidosFactory.getInstanceTitulos().obterAtivosGarantidos();
      Iterator listaDeAtivos = ativos.iterator();

      List contratos = ConsultaAtivosGarantidosFactory.getInstanceContratos().obterAtivosGarantidos();
      Iterator listaDeContratos = contratos.iterator();

      dataBatch = ControleOperacionalFactory.getInstance().obterDataBatchCetip21(new NumeroInteiro(0));

      if (!listaDeAtivos.hasNext() && !listaDeContratos.hasNext()) {
         resultado.atribuirARQUIVO_Data(dataBatch);
         resultado.atribuirDIAS_UTEIS_QuantidadeInteiraPositiva(new QuantidadeInteiraPositiva(calculaDiasUteis(
               dataBatch).toString()));
      } else {
         obterRegistros(listaDeAtivos);
         obterRegistros(listaDeContratos);
      }

      return resultado;
   }

   private void obterRegistros(Iterator listaDeGarantidos) {
      while (listaDeGarantidos.hasNext()) {
         Object[] linha = (Object[]) listaDeGarantidos.next();
         popularResultado(linha);
      }
   }

   /**
    * 
    * Método para preencher o resultado com os ativos garantidos
    * 
    * @param linha
    * @param dataParam
    */
   private void popularResultado(Object[] linha) {
      resultado.novaLinha();
      resultado.atribuirARQUIVO_Data(dataBatch);
      resultado.atribuirDIAS_UTEIS_QuantidadeInteiraPositiva(new QuantidadeInteiraPositiva(calculaDiasUteis(dataBatch)
            .toString()));
      resultado.atribuirDIA_Data(dataBatch);
      resultado.atribuirDETENTOR_CodigoContaCetip((CodigoContaCetip) linha[COL_DETENTOR]);
      resultado.atribuirINSTRUMENTO_FINANCEIRO_CodigoTipoIF((CodigoTipoIF) linha[COL_CODIGO_TIPO_IF]);
      resultado.atribuirINSTRUMENTO_FINANCEIRO_CodigoIF((CodigoIF) linha[COL_CODIGO_IF]);
      resultado.atribuirVALOR_NOMINAL_ATUALIZADO_ValorMonetario((ValorMonetario) linha[COL_VALOR_ATUALIZADO]);
      resultado.atribuirCESTA_GARANTIA_Id((Id) linha[COL_ID_CESTA_GARANTIA]);
      resultado.atribuirGARANTIAS_STATUS_Texto(new Texto(linha[COL_STATUS_CESTA].toString()));
      resultado.atribuirNATUREZA_SELIC_QuantidadeInteiraPositiva((QuantidadeInteiraPositiva) linha[COL_COUNT_SELIC]);
   }

   /**
    * Método responsável por calcular a quantidade de dias úteis a partir de
    * uma data
    * 
    * @return
    */
   private Prazo calculaDiasUteis(Data dataControleOperacional) {
      Data dtPrimeiroDiaMes = dataControleOperacional.obterPrimeiroDiaDoMes();
      Data dataAtual = dataControleOperacional;

      Prazo diasUteis;
      try {
         diasUteis = DiaUtilFactory.getInstance().diferenca(dtPrimeiroDiaMes,
               DiaUtilFactory.getInstance().adiciona(dataAtual, new Prazo(1)));
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
      return diasUteis;

   }
}
