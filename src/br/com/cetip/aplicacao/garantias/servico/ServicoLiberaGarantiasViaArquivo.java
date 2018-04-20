package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.roteador.Roteador;

/**
 * Servico para RETIRAR garantias de uma determinada Cesta via Arquivo
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vincius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @since Abril/2006
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_QUANTIDADE"
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="SISTEMA"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIAS_CESTA"
 * 
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_IF"
 * 
 * @requisicao.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_QUANTIDADE"
 * 
 * @requisicao.method atributo="IdTipoGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 *                    
 * @requisicao.method atributo="NumeroOperacao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="OPERACAO"             
 *                          
 * @requisicao.method atributo="CodigoTipoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_TIPO"
 *                    
 * @requisicao.method atributo="NomeSimplificado" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GERADOR_ARQUIVO"                                      
 */
public class ServicoLiberaGarantiasViaArquivo extends ServicoLiberarGarantias {

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Liberacao de Itens na Cesta de Garantias Via Arquivo ***** ");
      }

      RequisicaoServicoLiberaGarantiasViaArquivo req = (RequisicaoServicoLiberaGarantiasViaArquivo) requisicao;

      Id idCesta = req.obterGARANTIAS_CODIGO_Id();
      IdTipoGarantia idTipoGarantia = req.obterGARANTIAS_CODIGO_IdTipoGarantia();
      CodigoIF codigoIF = req.obterGARANTIAS_CODIGO_IF_CodigoIF();
      CodigoContaCetip parte = req.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
      CodigoContaCetip contraParte = req.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();
      Quantidade quantidade = req.obterGARANTIAS_QUANTIDADE_Quantidade();
      NumeroOperacao numOperacao = req.obterOPERACAO_NumeroOperacao();
      CodigoTipoIF tipoIF = req.obterGARANTIAS_CODIGO_TIPO_CodigoTipoIF();

      try {
         RequisicaoServicoValidaLiberarCestaGarantias reqV = new RequisicaoServicoValidaLiberarCestaGarantias();
         reqV.atribuirGARANTIAS_CESTA_Booleano(req.obterGARANTIAS_CESTA_Booleano());
         reqV.atribuirGARANTIAS_CODIGO_Booleano(req.obterGARANTIAS_CODIGO_Booleano());
         reqV.atribuirGARANTIAS_CODIGO_IdTipoGarantia(idTipoGarantia);
         reqV.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);
         reqV.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(new NumeroCestaGarantia(idCesta.obterConteudo()));
         reqV.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(req.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip());
         reqV.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(req.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip());
         reqV.atribuirGARANTIAS_QUANTIDADE_Quantidade(req.obterGARANTIAS_QUANTIDADE_Quantidade());
         reqV.atribuirOPERACAO_NumeroOperacao(numOperacao);
         reqV.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(tipoIF);
         reqV.atribuirGERADOR_ARQUIVO_NomeSimplificado(req.obterGERADOR_ARQUIVO_NomeSimplificado());

         ResultadoServicoValidaLiberarCestaGarantias resV = (ResultadoServicoValidaLiberarCestaGarantias) Roteador
               .executar(reqV, getContextoAtivacao());

         RequisicaoServicoLiberarGarantias reqItem = new RequisicaoServicoLiberarGarantias();
         reqItem.atribuirGARANTIAS_LIBERACAO_Id(new Id(resV.obterGARANTIAS_CODIGO_Id().obterConteudo()));
         reqItem.atribuirGARANTIAS_LIBERAR_QUANTIDADE_Quantidade(req.obterGARANTIAS_QUANTIDADE_Quantidade());
         reqItem.atribuirOPERACAO_NumeroOperacao(numOperacao);
         reqItem.atribuirGARANTIAS_TIPO_ACESSO_Funcao(resV.obterGARANTIAS_TIPO_ACESSO_Funcao());

         Roteador.executar(reqItem, getContextoAtivacao());

         return new ResultadoServicoLiberaGarantiasViaArquivo();
      } catch (Exception e) {
         Logger.error(this, "Cesta: " + idCesta);
         Logger.error(this, "Tipo Garantia: " + idTipoGarantia);
         Logger.error(this, "Codigo Tipo IF: " + tipoIF);
         Logger.error(this, "Codigo IF: " + codigoIF);
         Logger.error(this, "Parte: " + parte);
         Logger.error(this, "ContraParte: " + contraParte);
         Logger.error(this, "Quantidade: " + quantidade);
         Logger.error(this, "NumeroOperacao(Selic): " + numOperacao);
         throw e;
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
