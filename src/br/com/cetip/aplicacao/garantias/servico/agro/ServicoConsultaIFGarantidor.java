package br.com.cetip.aplicacao.garantias.servico.agro;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.agro.IFGarantidorFactory;
import br.com.cetip.dados.aplicacao.garantias.HabilitaGarantidorDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.expressao.TipoHabilitacao;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.tempo.DataHora;
import br.com.cetip.infra.atributo.tipo.texto.NomeAbreviado;
import br.com.cetip.infra.atributo.tipo.texto.NomeUsuario;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CESTA"
 *                   
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CESTA"    
 * 
 * @resultado.class
 * 
 *  @resultado.method 
 *   atributo="Id"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIAS_CESTA"
 *          
 *  @resultado.method 
 *   atributo="CodigoSistema"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIAS_CESTA"
 *     
 *  @resultado.method 
 *   atributo="CodigoTipoIF"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIAS_CESTA"
 *     
 *  @resultado.method 
 *   atributo="CodigoIF"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIAS_CESTA"
 * 
 *  @resultado.method 
 * 	 atributo="Funcao"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIAS_CESTA"
 *   
 *  @resultado.method 
 * 	 atributo="Data"
 *   pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *   contexto="GARANTIAS_CESTA"
 *   
 *  @resultado.method 
 * 	 atributo="NomeAbreviado"
 *   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *   contexto="GARANTIAS_CESTA"
 *   
 *  @resultado.method 
 * 	 atributo="Booleano"
 *   pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *   contexto="GARANTIAS_CESTA"            
 *   
 *  @resultado.method 
 * 	 atributo="TipoHabilitacao"
 *   pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *   contexto="GARANTIAS_CESTA"  
 *   
 *  @resultado.method 
 * 	 atributo="NomeUsuario"
 *   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *   contexto="GARANTIAS_CESTA"      
 *   
 *  @resultado.method 
 * 	 atributo="Nome"
 *   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *   contexto="GARANTIAS_CESTA"   
 *
 *       
 *       
 *       
 *       
 *       
 * 
 */
public class ServicoConsultaIFGarantidor implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new UnsupportedOperationException();
   }


   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoConsultaIFGarantidor req = (RequisicaoServicoConsultaIFGarantidor) requisicao;
      ResultadoServicoConsultaIFGarantidor resultado = new ResultadoServicoConsultaIFGarantidor();

      CodigoTipoIF codigoTipoIF = req.obterGARANTIAS_CESTA_CodigoTipoIF();
      CodigoIF codigoIF = req.obterGARANTIAS_CESTA_CodigoIF();

      List ifGarantidorDo = IFGarantidorFactory.getInstance()
      												.obtemListaIFGarantidorHabilitado(codigoTipoIF, codigoIF);

      if (Condicional.vazio( ifGarantidorDo )) {
         throw new Erro(CodigoErro.GARANTIAS_LISTA_VAZIA);
      }

      Iterator itParamIF = ifGarantidorDo.iterator();

      while (itParamIF.hasNext()) {
   	     
    	 HabilitaGarantidorDO dao = (HabilitaGarantidorDO) itParamIF.next();
    	 
         Id idHabilitaIFGarantidor = dao.getId();         
         CodigoTipoIF codTipoIF = dao.getInstrumentoFinanceiro().getTipoIF().getCodigoTipoIF();         
         CodigoIF codIF = dao.getInstrumentoFinanceiro().getCodigoIF();
         DataHora datInclusao = dao.getDataInclusao();
         NomeAbreviado destinoGarantia = dao.getDestinoGarantia();
         NomeUsuario nomeUsuario = new NomeUsuario(dao.getEntidadeAtualiza().getUsuarioAtualizEntidade().getNomeEntidade().toString());
         Booleano indAtivo = null;
         if (Condicional.vazio(dao.getDataExclusao())){
        	 indAtivo = dao.getIndAtivo();
         }
         
	     resultado.atribuirGARANTIAS_CESTA_Id(idHabilitaIFGarantidor);	      
	     resultado.atribuirGARANTIAS_CESTA_CodigoTipoIF(codTipoIF);
	     resultado.atribuirGARANTIAS_CESTA_CodigoIF(codIF);
	     resultado.atribuirGARANTIAS_CESTA_TipoHabilitacao(TipoHabilitacao.HABILITA_CODIGOIF);
	     resultado.atribuirGARANTIAS_CESTA_Data(new Data(datInclusao.toString()));
	     resultado.atribuirGARANTIAS_CESTA_NomeAbreviado(destinoGarantia);
	     resultado.atribuirGARANTIAS_CESTA_NomeUsuario(nomeUsuario);
	     resultado.atribuirGARANTIAS_CESTA_Booleano(indAtivo);
	     
	     resultado.novaLinha();
	     
      }

      return resultado;
      
   }
}