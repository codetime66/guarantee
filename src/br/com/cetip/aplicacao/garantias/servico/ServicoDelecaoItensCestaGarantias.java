package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IExcluirGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico para delecao de garantias
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
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="GARANTIAS_DATA_CRIACAO"
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
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CODIGO"
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
 * @requisicao.method atributo="NumeroOperacao"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="OPERACAO"
 *                    
 * @requisicao.method atributo="DescricaoLimitada" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                    contexto="GARANTIAS_ITENS"
 *                    
 */
public class ServicoDelecaoItensCestaGarantias extends BaseGarantias implements Servico {

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.interfaces.Servico#executar(br.com.cetip.infra.servico.interfaces.Requisicao)
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      RequisicaoServicoDelecaoItensCestaGarantias req = (RequisicaoServicoDelecaoItensCestaGarantias) requisicao;
      ResultadoServicoDelecaoItensCestaGarantias res = new ResultadoServicoDelecaoItensCestaGarantias();

      Id numero = req.obterGARANTIAS_CODIGO_Id();
      Booleano eventos = req.obterGARANTIAS_ITENS_Booleano();
      CodigoIF codigoIF = req.obterGARANTIAS_CODIGO_IF_CodigoIF();
      IdTipoGarantia tipoGarantia = req.obterGARANTIAS_CODIGO_IdTipoGarantia();
      Quantidade quantidade = req.obterGARANTIAS_QUANTIDADE_Quantidade();
      NumeroOperacao nuOp = req.obterOPERACAO_NumeroOperacao();

      // Carrega a cesta
      IGarantias factory = getFactory();
      ICestaDeGarantias icg = factory.getInstanceCestaDeGarantias();
      CestaGarantiasDO cestaGarantias = icg.obterCestaDeGarantias(new NumeroCestaGarantia(numero.toString()));

      CodigoTipoIF codigoTipoIF = req.obterGARANTIAS_CODIGO_TIPO_CodigoTipoIF();
      // Cria a mov 'espelho' para a exclusao
      MovimentacaoGarantiaDO itemDO = new MovimentacaoGarantiaDO();

      if(!codigoTipoIF.ehNAO_CETIPADO()){
          itemDO.setIndCetipado(Booleano.VERDADEIRO);
          itemDO.setInstrumentoFinanceiro(InstrumentoFinanceiroFactory.getInstance().obterInstrumentoFinanceiro(codigoIF));
      } else {
          itemDO.setIndCetipado(Booleano.FALSO);
          itemDO.setCodIfNCetipado(codigoIF);
      }
      
      itemDO.setQtdGarantia(quantidade);
      itemDO.setIndDireitosGarantidor(eventos);
      itemDO.setCestaGarantias(cestaGarantias);
      itemDO.setNumOperacao(nuOp);

      if (!tipoGarantia.mesmoConteudo(cestaGarantias.getTipoGarantia().getNumIdTipoGarantia())) {
          throw new Erro(CodigoErro.CESTA_OPERACAO_RETIRADA_NAO_CONFERE, "Tipo de Garantia nao confere.");
       }

       if (cestaGarantias.getStatusCesta().equals(StatusCestaDO.EM_MANUTENCAO)
             || cestaGarantias.getStatusCesta().equals(StatusCestaDO.EM_EDICAO)
             || cestaGarantias.getStatusCesta().equals(StatusCestaDO.INCOMPLETA)) {


          IExcluirGarantia excluirGarantia = factory.getInstanceExcluirGarantia(codigoTipoIF);
          excluirGarantia.excluirItemEspelho(itemDO);
       } else {
          throw new Erro(CodigoErro.CESTA_JA_FECHADA);
       }
      

      return res;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * br.com.cetip.infra.servico.interfaces.Servico#executarConsulta(br.com.cetip.infra.servico.interfaces.Requisicao)
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}
