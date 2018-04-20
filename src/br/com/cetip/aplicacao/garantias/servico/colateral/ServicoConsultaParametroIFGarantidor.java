package br.com.cetip.aplicacao.garantias.servico.colateral;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.dados.aplicacao.garantias.HabilitaIFGarantidorDO;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.GerenciadorPersistenciaFactory;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
* @requisicao.method
 *     atributo="CodigoSistema"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="GARANTIAS_SISTEMA"
 *     
 *  @requisicao.method 
 *    atributo="CodigoTipoIF"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="GARANTIAS_TIPO_IF"
 * 
 * @resultado.class
 * 
 *  @resultado.method 
 *   atributo="Id"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIAS_PARAM_IF_GARANTIDOR"
 *          
 *  @resultado.method 
 *   atributo="CodigoSistema"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIAS_SISTEMA"
 *     
 *  @resultado.method 
 *   atributo="CodigoTipoIF"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIAS_TIPO_IF"
 *     
 *  @resultado.method 
 *   atributo="CodigoIF"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIAS_CODIGO_IF"
 * 
 *  @resultado.method 
 * 	 atributo="Funcao"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIAS_PARAM_IF_GARANTIDOR"
 *       
 * 
 */
public class ServicoConsultaParametroIFGarantidor implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new UnsupportedOperationException();
   }

   private CodigoIF codigoIF = new CodigoIF();
   private CodigoTipoIF codTipoIF = new CodigoTipoIF();
   private CodigoSistema codSistema = new CodigoSistema();
   private Id idHabilitaIFGarantidor = new Id();
   private StringBuffer hql = new StringBuffer();
   private List hqlParams = new ArrayList();

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoConsultaParametroIFGarantidor req = (RequisicaoServicoConsultaParametroIFGarantidor) requisicao;
      ResultadoServicoConsultaParametroIFGarantidor resultado = new ResultadoServicoConsultaParametroIFGarantidor();

      codSistema = req.obterGARANTIAS_SISTEMA_CodigoSistema();
      codTipoIF = req.obterGARANTIAS_TIPO_IF_CodigoTipoIF();

      HabilitaIFGarantidorDO habIFGarantDO = null;
      List listParamIF = new ArrayList();

      hql.append(" from HabilitaIFGarantidorDO h");
      param(codTipoIF, "where h.tipoIFSistema.tipoIF.codigoTipoIF = ?");
      param(codSistema, "and  h.tipoIFSistema.sistema.codSistema = ?");

      listParamIF = GerenciadorPersistenciaFactory.getGerenciadorPersistencia().find(hql.toString(),
            hqlParams.toArray());

      if (listParamIF.isEmpty()) {
         throw new Erro(CodigoErro.GARANTIAS_LISTA_VAZIA);
      }

      Iterator itParamIF = listParamIF.iterator();

      while (itParamIF.hasNext()) {
         habIFGarantDO = (HabilitaIFGarantidorDO) itParamIF.next();
         idHabilitaIFGarantidor = habIFGarantDO.getId();
         codSistema = habIFGarantDO.getTipoIFSistema().getSistema().getCodSistema();
         codTipoIF = habIFGarantDO.getTipoIFSistema().getTipoIF().getCodigoTipoIF();
         if (!Condicional.vazio(habIFGarantDO.getInstrumentoFinanceiro())) {
            codigoIF = habIFGarantDO.getInstrumentoFinanceiro().getCodigoIF();
         }
         preencheResultado(resultado);
      }

      return resultado;
   }

   private void param(Atributo param, String s) {
      if (!Condicional.vazio(param)) {
         hql.append(' ').append(s);
         hqlParams.add(param);
      }
   }

   private void preencheResultado(ResultadoServicoConsultaParametroIFGarantidor resultado) {
      resultado.atribuirGARANTIAS_PARAM_IF_GARANTIDOR_Id(idHabilitaIFGarantidor);
      resultado.atribuirGARANTIAS_SISTEMA_CodigoSistema(codSistema);
      resultado.atribuirGARANTIAS_TIPO_IF_CodigoTipoIF(codTipoIF);
      resultado.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);

      Funcao acoes = new Funcao(Contexto.GARANTIAS_PARAM_IF_GARANTIDOR);
      acoes.getDomain().add(new Funcao());
      acoes.getDomain().add(Funcao.EXCLUIR);
      resultado.atribuirGARANTIAS_PARAM_IF_GARANTIDOR_Funcao(acoes);
      resultado.novaLinha();

   }

}