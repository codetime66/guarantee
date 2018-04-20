package br.com.cetip.aplicacao.garantias.servico;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
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
 *
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_IF"
 * 
 * @requisicao.method atributo="IdTipoGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_QUANTIDADE"
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="DescricaoLimitada" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                    contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="ACAO"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_ACESSO"
 *                    
 * @requisicao.method atributo="NumeroOperacao"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="OPERACAO"
 */
public class ServicoManutencaoItensCestaGarantiasViaArquivo extends ServicoRegistraItensCestaGarantias {

   private static final Map MAPA_ACOES = new HashMap();

   static {
      MAPA_ACOES.put("INCLUSAO", ICestaDeGarantias.INCLUIR_GARANTIA);
      MAPA_ACOES.put("APORTE", ICestaDeGarantias.APORTAR_GARANTIA);
      MAPA_ACOES.put("EXCLUSAO", ICestaDeGarantias.EXCLUIR_GARANTIA);
      MAPA_ACOES.put("", new Funcao(" "));
   }

   private Map idAtivos;

   private Funcao funcaoItem;

   private Funcao tipoAcesso;

   private IdTipoGarantia idTipoGarantia;

   private Data data;

   private DescricaoLimitada descricao;

   private Booleano indEventosGarantidor;

   private Quantidade quantidade;

   private CodigoIF codigoIF;

   private CodigoTipoIF codigoTipoIF;

   private CodigoContaCetip contraParte;

   private CodigoContaCetip parte;

   private Id codigoIdCesta;

   private NumeroCestaGarantia numeroCestaGarantia;

   private NumeroOperacao numOperacao;

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Manutencao de Itens na Cesta de Garantias Via Arquivo ***** ");
      }

      idAtivos = new HashMap();

      Servico validacao = new ServicoValidaCadastroItensCestaGarantias();
      Servico inclusaoAporte = new ServicoRegistraItensCestaGarantias();
      Servico delecao = new ServicoDelecaoItensCestaGarantias();

      // Primeiro valida todas as linhas
      AtributosColunados ac = requisicao.obterAtributosColunados();
      while (ac.avancarLinha()) {
         populaValores(ac);
         if (!funcaoItem.mesmoConteudo(ICestaDeGarantias.EXCLUIR_GARANTIA)) {
            RequisicaoServicoValidaCadastroItensCestaGarantias reqV = new RequisicaoServicoValidaCadastroItensCestaGarantias();
            reqV.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(parte);
            reqV.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contraParte);
            reqV.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(codigoTipoIF);
            reqV.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);
            reqV.atribuirGARANTIAS_QUANTIDADE_Quantidade(quantidade);
            reqV.atribuirGARANTIAS_ITENS_Booleano(indEventosGarantidor);
            reqV.atribuirGARANTIAS_ITENS_DescricaoLimitada(descricao);
            reqV.atribuirGARANTIAS_DATA_CRIACAO_Data(data);
            reqV.atribuirGARANTIAS_CODIGO_IdTipoGarantia(idTipoGarantia);
            reqV.atribuirGARANTIAS_TIPO_ACESSO_Funcao(tipoAcesso);
            reqV.atribuirACAO_Funcao(funcaoItem);
            reqV.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numeroCestaGarantia);
            reqV.atribuirOPERACAO_NumeroOperacao(numOperacao);

            // Valida a linha
            validacao.executarConsulta(reqV);
         }

      }

      ac.reiniciarLinha();
      while (ac.avancarLinha()) {
         populaValores(ac);

         // Insere ou Aporta / Deleta
         if (funcaoItem.mesmoConteudo(ICestaDeGarantias.INCLUIR_GARANTIA)
               || funcaoItem.mesmoConteudo(ICestaDeGarantias.APORTAR_GARANTIA)) {
            RequisicaoServicoRegistraItensCestaGarantias reqItem = new RequisicaoServicoRegistraItensCestaGarantias();

            reqItem.atribuirACAO_Funcao(funcaoItem);
            reqItem.atribuirGARANTIAS_CODIGO_Id(codigoIdCesta);
            reqItem.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);
            reqItem.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(parte);
            reqItem.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contraParte);
            reqItem.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(codigoTipoIF);
            reqItem.atribuirGARANTIAS_CODIGO_IdTipoGarantia(idTipoGarantia);
            reqItem.atribuirGARANTIAS_QUANTIDADE_Quantidade(quantidade);
            reqItem.atribuirGARANTIAS_ITENS_Booleano(indEventosGarantidor);
            reqItem.atribuirGARANTIAS_ITENS_DescricaoLimitada(descricao);
            reqItem.atribuirGARANTIAS_TIPO_ACESSO_Funcao(tipoAcesso);
            reqItem.atribuirOPERACAO_NumeroOperacao(numOperacao);

            inclusaoAporte.executar(reqItem);

         } else if (funcaoItem.mesmoConteudo(ICestaDeGarantias.EXCLUIR_GARANTIA)) {
            RequisicaoServicoDelecaoItensCestaGarantias reqItemDelecao = new RequisicaoServicoDelecaoItensCestaGarantias();
            reqItemDelecao.atribuirGARANTIAS_CODIGO_Id(codigoIdCesta);
            reqItemDelecao.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numeroCestaGarantia);
            reqItemDelecao.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);
            reqItemDelecao.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(parte);
            reqItemDelecao.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contraParte);
            reqItemDelecao.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(codigoTipoIF);
            reqItemDelecao.atribuirGARANTIAS_CODIGO_IdTipoGarantia(idTipoGarantia);
            reqItemDelecao.atribuirGARANTIAS_QUANTIDADE_Quantidade(quantidade);
            reqItemDelecao.atribuirGARANTIAS_ITENS_Booleano(indEventosGarantidor);
            reqItemDelecao.atribuirOPERACAO_NumeroOperacao(numOperacao);
            reqItemDelecao.atribuirGARANTIAS_ITENS_DescricaoLimitada(descricao);

            delecao.executar(reqItemDelecao);
         }
      }

      return new ResultadoServicoManutencaoItensCestaGarantiasViaArquivo();
   }

   private void populaValores(AtributosColunados ac) {
      parte = (CodigoContaCetip) (ac.obterAtributo(CodigoContaCetip.class, Contexto.GARANTIAS_PARTICIPANTE));
      contraParte = (CodigoContaCetip) (ac.obterAtributo(CodigoContaCetip.class, Contexto.GARANTIAS_CONTRAPARTE));
      codigoTipoIF = (CodigoTipoIF) (ac.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CODIGO_TIPO));
      codigoIF = (CodigoIF) (ac.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF));
      quantidade = (Quantidade) (ac.obterAtributo(Quantidade.class, Contexto.GARANTIAS_QUANTIDADE));
      indEventosGarantidor = (Booleano) (ac.obterAtributo(Booleano.class, Contexto.GARANTIAS_ITENS));
      descricao = (DescricaoLimitada) (ac.obterAtributo(DescricaoLimitada.class, Contexto.GARANTIAS_ITENS));
      data = (Data) (ac.obterAtributo(Data.class, Contexto.GARANTIAS_DATA_CRIACAO));
      idTipoGarantia = (IdTipoGarantia) (ac.obterAtributo(IdTipoGarantia.class, Contexto.GARANTIAS_CODIGO));
      tipoAcesso = (Funcao) (ac.obterAtributo(Funcao.class, Contexto.GARANTIAS_TIPO_ACESSO));

      codigoIdCesta = (Id) ac.obterAtributo(Id.class, Contexto.GARANTIAS_CODIGO);
      numeroCestaGarantia = (NumeroCestaGarantia) ac
            .obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);

      String stringFuncaoItem = ac.obterAtributo(Funcao.class, Contexto.ACAO).toString().trim();
      funcaoItem = (Funcao) MAPA_ACOES.get(stringFuncaoItem);
      numOperacao = (NumeroOperacao) (ac.obterAtributo(NumeroOperacao.class, Contexto.OPERACAO));

      Logger.info("CODIGO IF [" + codigoIF + "] TIPO IF [" + codigoTipoIF + "]");

      if (!codigoTipoIF.ehNAO_CETIPADO()) {
         if (idAtivos.get(codigoIF.obterConteudo()) == null) {
            InstrumentoFinanceiroDO ifDO;
            try {
               ifDO = InstrumentoFinanceiroFactory.getInstance().obterInstrumentoFinanceiro(codigoIF);
            } catch (Exception e) {
               e.printStackTrace();
               throw new RuntimeException(e);
            }
            idAtivos.put(codigoIF.obterConteudo(), new Object[] { ifDO.getId(), ifDO.getSistema().getNumero() });
         }
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
      return null;
   }

}
