package br.com.cetip.aplicacao.garantias.batch.design;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.relatorios.AbstractJobRelatorio;
import br.com.cetip.infra.relatorios.CamposRelatorio;
import br.com.cetip.infra.relatorios.FonteDeDados;
import br.com.cetip.infra.relatorios.Scriptlet;
import br.com.cetip.infra.relatorios.datasource.DescricaoCampoArquivoRelatorio;
import br.com.cetip.infra.relatorios.datasource.DescricaoCampoRelatorio;
import br.com.cetip.infra.servico.batch.LeitorArquivo;

public class RelatorioCestaGarantias extends AbstractJobRelatorio {

   private static final String NOME_ARQUIVO = "/DCESTAGARANTIAS.txt";

   public void execute(Data data, Texto pathArquivo, Texto nomeRel) throws Exception {
      String pathJasper = "/br/com/cetip/aplicacao/garantias/batch/design/jasper/RCESTA_GARANTIAS.jasper";

      //Adicionando os parametros do relatorio 
      Map parameters = new HashMap();
      parameters.put("NOMERELATORIO", "RCESTAGARANTIAS");
      Scriptlet script = new Scriptlet();
      parameters.put("NOM_SCRIPTLET", script);
      parameters.put("DATAHOJE", data.toString());
      parameters.put("RELATORIO", "Relatório com os Itens Contidos nas Cestas de Garantias");

      List nomeRelatorio = obterNomeRelatorio(data, nomeRel);
      LeitorArquivo leitorArquivo = obterLeitorArquivo(data, pathArquivo);
      CamposRelatorio camposRelatorio = obterCamposRelatorio();
      // Criando uma fonte de dados informando esses valores
      FonteDeDados dados = new FonteDeDados(leitorArquivo, camposRelatorio, parameters, pathJasper, new Vector(
            nomeRelatorio));
      //Obtendo os filtros para quebra do relatorio e o nome
      List filtros = obtemQuebras();

      String diretorio = "";

      //Gera o relatorio
      dados.geraRelatorio(filtros, diretorio);
   }

   /**
    * Metodo responsavel por obter as quebras do Relatorio.
    */
   public static List obtemQuebras() {
      List filtros = new ArrayList(2);

      filtros.add("0"); //coluna MALOTE
      filtros.add("1"); //coluna UF

      return filtros;
   }

   /**
    * Metodo responsavel pela leitura do arquivo de origem
    * @param dataD0
    * @param pathArquivo
    * @return
    */
   private LeitorArquivo obterLeitorArquivo(Data dataD0, Texto pathArquivo) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      String dataFormatada = sdf.format(dataD0.obterDate());

      LeitorArquivo leitorArquivo = new LeitorArquivo(pathArquivo + dataFormatada + NOME_ARQUIVO);
      return leitorArquivo;
   }

   /*
     * Metodo responsavel preencher os campos do relatorio
     * @return camposRelatorio
     */
   private CamposRelatorio obterCamposRelatorio() {

      CamposRelatorio camposRelatorio = new CamposRelatorio();
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("NOM_MALOTE",
            DescricaoCampoRelatorio.TO_STRING, 0)); //NomeMalote(MALOTE)
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("SIG_UF", DescricaoCampoRelatorio.TO_STRING,
            1)); //SiglaUF(MALOTE)
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("COD_CONTA_PARTE",
            DescricaoCampoRelatorio.OBTER_CONTEUDO, 2)); //CodigoContaCetip(DESTINATARIO)
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("NOM_ENTIDADE_PARTE",
            DescricaoCampoRelatorio.TO_STRING, 3)); //NomeSimplificado(DESTINATARIO)
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("CESTA_GARANTIAS",
            DescricaoCampoRelatorio.TO_STRING, 4));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("SITUACAO_CESTA",
            DescricaoCampoRelatorio.TO_STRING, 5));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("QTD_ITENS",
            DescricaoCampoRelatorio.TO_STRING, 11));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("COD_CONTA_GARANTIDOR",
            DescricaoCampoRelatorio.OBTER_CONTEUDO, 6));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("NOME_GARANTIDOR",
            DescricaoCampoRelatorio.TO_STRING, 7));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("COD_CONTA_GARANTIDO",
            DescricaoCampoRelatorio.OBTER_CONTEUDO, 8));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("NOME_GARANTIDO",
            DescricaoCampoRelatorio.TO_STRING, 9));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("ATIVO_GARANTIDO",
            DescricaoCampoRelatorio.TO_STRING, 10));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("NUM_ITEM",
            DescricaoCampoRelatorio.TO_STRING, 12));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("NOM_TIPO_IF",
            DescricaoCampoRelatorio.TO_STRING, 13));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("CODIGO_IF",
            DescricaoCampoRelatorio.TO_STRING, 14));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("QUANTIDADE",
            DescricaoCampoRelatorio.OBTER_CONTEUDO, 15));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("TIPO_GARANTIA",
            DescricaoCampoRelatorio.TO_STRING, 16));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("EV_GARANTIDOR",
            DescricaoCampoRelatorio.TO_STRING, 17));
      camposRelatorio.addDescricaoCampo(new DescricaoCampoArquivoRelatorio("DESCRICAO_GARANTIA",
            DescricaoCampoRelatorio.TO_STRING, 18));
      camposRelatorio
            .addDescricaoCampo(new DescricaoCampoArquivoRelatorio("OBS", DescricaoCampoRelatorio.TO_STRING, 19));

      return camposRelatorio;
   }

   /**
    * Metodo responsavel por obter o nome do relatorio
    * 
    * @param data
    * @return
    */
   public List obterNomeRelatorio(Data data, Texto nomeRel) {
      SimpleDateFormat formatDate = new SimpleDateFormat("yyMMdd");
      List nomeRelatorio = new ArrayList();

      nomeRelatorio.add(QUEBRA1);
      nomeRelatorio.add(BARRA);
      nomeRelatorio.add(QUEBRA1);
      nomeRelatorio.add(QUEBRA2);
      nomeRelatorio.add(SEPARADOR);
      nomeRelatorio.add(formatDate.format(data.obterDate()));
      nomeRelatorio.add(SEPARADOR);
      nomeRelatorio.add(nomeRel.toString());
      nomeRelatorio.add(EXTENSAO);

      return nomeRelatorio;
   }

}