package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.AcessoCestaDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIA"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIA"
 * 
 * @resultado.method atributo="NomeSimplificado" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIA"
 */
public class ServicoConsultaVisualizadores extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoConsultaVisualizadores r = (RequisicaoServicoConsultaVisualizadores) requisicao;
      ResultadoServicoConsultaVisualizadores resultado = new ResultadoServicoConsultaVisualizadores();

      NumeroCestaGarantia numCesta = r.obterGARANTIAS_CODIGO_NumeroCestaGarantia();

      IGarantias factory = getFactory();
      ICestaDeGarantias icg = factory.getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = icg.obterCestaDeGarantias(numCesta);

      Set lista = cesta.getVisualizadores();

      for (Iterator iterator = lista.iterator(); iterator.hasNext();) {
         AcessoCestaDO acesso = (AcessoCestaDO) iterator.next();
         resultado.novaLinha();

         resultado.atribuirGARANTIA_NumeroCestaGarantia(new NumeroCestaGarantia(numCesta.toString()));
         resultado.atribuirGARANTIA_CodigoContaCetip(new CodigoContaCetip(acesso.getContaParticipante()
               .getCodContaParticipante().toString()));
         resultado.atribuirGARANTIA_NomeSimplificado(new NomeSimplificado(acesso.getContaParticipante()
               .getParticipante().getNomSimplificadoEntidade().toString()));
      }

      return resultado;
   }
}
