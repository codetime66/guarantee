package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @resultado.class
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_STATUS"
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ServicoListaStatusCestaGarantias extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      return null;
   }

   public Resultado executarConsulta(Requisicao req) throws Exception {
      ResultadoServicoListaStatusCestaGarantias res;
      res = new ResultadoServicoListaStatusCestaGarantias();

      IGerenciadorPersistencia gp = getGp();
      IConsulta c = gp.criarConsulta("from ".concat(StatusCestaDO.class.getName()));
      c.setCacheable(true);
      c.setCacheRegion("MMG");

      List listaStatus = c.list();
      for (Iterator it = listaStatus.iterator(); it.hasNext();) {
         StatusCestaDO statusDo = (StatusCestaDO) it.next();
         String nome = statusDo.getNomStatusCesta().obterConteudo();
         Id _status = statusDo.getNumIdStatusCesta();
         _status.atribuirRepresentacao(nome);
         res.novaLinha();
         res.atribuirGARANTIAS_STATUS_Id(_status);
      }

      return res;
   }

}