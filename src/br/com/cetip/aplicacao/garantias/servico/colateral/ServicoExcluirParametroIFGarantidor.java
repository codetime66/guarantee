package br.com.cetip.aplicacao.garantias.servico.colateral;

import java.util.List;

import br.com.cetip.dados.aplicacao.garantias.HabilitaIFGarantidorDO;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.persistencia.GerenciadorPersistenciaFactory;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 *  @requisicao.method 
 *   atributo="Id"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIAS_PARAM_IF_GARANTIDOR"
 *                       
 * @resultado.class
 * 
 */

public class ServicoExcluirParametroIFGarantidor implements Servico {

   /* (non-Javadoc)
    * @see br.com.cetip.infra.servico.interfaces.Servico#executar(br.com.cetip.infra.servico.interfaces.Requisicao)
    */
   public Resultado executar(Requisicao requisicao) throws Exception {

      RequisicaoServicoExcluirParametroIFGarantidor req = (RequisicaoServicoExcluirParametroIFGarantidor) requisicao;
      ResultadoServicoExcluirParametroIFGarantidor res = new ResultadoServicoExcluirParametroIFGarantidor();

      List lIdHabIFGarantidor = req.obterListaGARANTIAS_PARAM_IF_GARANTIDOR_Id();

      IGerenciadorPersistencia gp = GerenciadorPersistenciaFactory.getGerenciadorPersistencia();

      for (int i = 0; i < lIdHabIFGarantidor.size(); i++) {
         HabilitaIFGarantidorDO habIFGarantidorDO = (HabilitaIFGarantidorDO) gp.load(HabilitaIFGarantidorDO.class,
               new Id(lIdHabIFGarantidor.get(i).toString()));
         gp.delete(habIFGarantidorDO);
      }

      return res;
   }

   /* (non-Javadoc)
    * @see br.com.cetip.infra.servico.interfaces.Servico#executarConsulta(br.com.cetip.infra.servico.interfaces.Requisicao)
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      return null;
   }
}
