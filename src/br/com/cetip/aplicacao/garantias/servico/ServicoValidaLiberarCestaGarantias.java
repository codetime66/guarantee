package br.com.cetip.aplicacao.garantias.servico;

import java.math.BigDecimal;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiaVO;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidaAcoes;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.sap.ParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico para validacao dos dados inseridos pelo usuario na interface grafica de compra/venda de cda/wa.
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
 * @resultado.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_ACESSO"                  
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
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="SISTEMA"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIAS_CESTA"
 * 
 * @requisicao.method atributo="CodigoTipoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_TIPO"
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
 * @requisicao.method atributo="NomeSimplificado" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GERADOR_ARQUIVO"                                        
 *                    
 */
public class ServicoValidaLiberarCestaGarantias extends BaseGarantias implements Servico {

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      return null;
   }

   /*
    * Realiza a validacao dos dados digitados via motor de regras.
    * 
    * @param requisicao requisicao que contem os dados para validacao
    * @return o Resultado contendo se a validacao foi aceita ou nao
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoValidaLiberarCestaGarantias req = (RequisicaoServicoValidaLiberarCestaGarantias) requisicao;
      ResultadoServicoValidaLiberarCestaGarantias res = new ResultadoServicoValidaLiberarCestaGarantias();

      CodigoContaCetip garantido = req.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();

      ICestaDeGarantias dados = getFactory().getInstanceCestaDeGarantias();
      IValidaAcoes ima = getFactory().getInstanceValidaAcoes();
      NumeroCestaGarantia nrCesta = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      CestaGarantiasDO cesta = dados.obterCestaDeGarantias(nrCesta);
      Quantidade quantidadeARetirar = req.obterGARANTIAS_QUANTIDADE_Quantidade();

      Funcao tipoAcesso = obterTipoAcesso(cesta, req.obterGERADOR_ARQUIVO_NomeSimplificado());

      if (!ima.podeExecutarAcao(ICestaDeGarantias.RETIRAR_GARANTIAS, tipoAcesso, cesta)) {
         Logger.error(this, "Acao de Retirar Garantias nao permitida para cesta: " + nrCesta);
         throw new Erro(CodigoErro.CESTA_INCOMPATIVEL);
      }

      final CodigoIF codIF = req.obterGARANTIAS_CODIGO_IF_CodigoIF();
      final boolean indCetipado = req.obterGARANTIAS_CODIGO_Booleano().ehVerdadeiro();

      GarantiaVO vo = new GarantiaVO();
      if (indCetipado) {
         vo.codIF = codIF;
      } else {
         vo.codIfNCetipado = codIF;
      }

      DetalheGarantiaDO garantia = dados.obterGarantiaCesta(cesta, vo);

      Booleano indDirGarantidor = req.obterGARANTIAS_CESTA_Booleano();
      if (garantia == null || !garantia.getIndDireitosGarantidor().mesmoConteudo(indDirGarantidor)) {
         throw new Erro(CodigoErro.CESTA_ITEM_NAO_EXISTE);
      }

      BigDecimal qtde = garantia.getQuantidadeGarantia().obterConteudo();
      int compara = qtde.compareTo(quantidadeARetirar.obterConteudo());

      if (compara >= 0 && garantia.getQuantidadeGarantia().ehPositivo()) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "ID DETALHE: " + garantia.getNumIdDetalheGarantia());
         }

         res.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(garantido);
         res.atribuirGARANTIAS_CODIGO_Id(garantia.getNumIdDetalheGarantia());
         res.atribuirGARANTIAS_QUANTIDADE_Quantidade(quantidadeARetirar);
         res.atribuirGARANTIAS_TIPO_ACESSO_Funcao(tipoAcesso);
      } else {
         Logger.error(this, "Quantidade invalida: " + quantidadeARetirar);
         throw new Erro(CodigoErro.QUANTIDADE_INVALIDA);
      }

      return res;
   }

   //obtem o tipo de Acesso FUNCAO_GARANTIDOR ou FUNCAO_GARANTIDO
   //o acesso default eh FUNCAO_GARANTIDO
   private Funcao obterTipoAcesso(CestaGarantiasDO cesta, NomeSimplificado nomeParticipante) {
      ParticipanteDO garantidor = cesta.getGarantidor().getParticipante();

      ContextoAtivacaoVO ca = getContextoAtivacao();
      if (ca.ehCETIP()) {
         return nomeParticipante.mesmoConteudo(garantidor.getNomSimplificadoEntidade()) ? ICestaDeGarantias.FUNCAO_GARANTIDOR
               : ICestaDeGarantias.FUNCAO_GARANTIDO;
      }

      CodigoContaCetip contaParticipante = new CodigoContaCetip(ca.getCodContaTitularFamilia());
      return contaParticipante.mesmoConteudo(garantidor.getContaParticipantePrincipal().getCodContaParticipante()) ? ICestaDeGarantias.FUNCAO_GARANTIDOR
            : ICestaDeGarantias.FUNCAO_GARANTIDO;
   }

}
