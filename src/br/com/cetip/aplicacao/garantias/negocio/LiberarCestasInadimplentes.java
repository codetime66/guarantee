package br.com.cetip.aplicacao.garantias.negocio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.ILiberarCestasInadimplentes;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoExecutarCestaGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoGarantiaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.roteador.Roteador;

final class LiberarCestasInadimplentes extends BaseGarantias implements ILiberarCestasInadimplentes {

   /* (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.negocio.ILiberarCestasInadimplentes#liberarCestasInadimplentes(br.com.cetip.infra.atributo.tipo.tempo.Data)
    */
   public void liberarCestasInadimplentes(Data dataProcesso) {
      IGerenciadorPersistencia gp = getGp();

      Data hoje = Condicional.vazio(dataProcesso) ? getDataHoje() : dataProcesso;

      // HQL para Consulta de Cestas Inadimplentes
      String hql = hql();

      // Objeto de Consulta
      IConsulta consulta = gp.criarConsulta(hql);

      // Carrega a informacao de prazo de inadimplencia
      int prazo = getFactory().getInadimplencia();

      // Data de Hoje menos os dias corridos do prazo para filtrar diretamente
      // no HQL
      Data dp = hoje.subtrairDiasCorridos(prazo);

      // Parametros da Consulta
      consulta.setAtributo("ind", Booleano.VERDADEIRO);
      consulta.setAtributo("status", StatusCestaDO.INADIMPLENTE);
      consulta.setAtributo("data", dp);

      // CESTAS PENHOR NO EMISSOR NAO SAO LIBERADAS
      List tipos = new ArrayList();
      tipos.add(TipoGarantiaDO.PENHOR_EMISSOR);
      consulta.setParameterList("tipos", tipos);

      // Executa a consulta e itera pela lista
      List resultado = consulta.list();
      Iterator i = resultado.iterator();

      // Para cada Cesta inadimplente ainda no prazo da
      // liberacao
      while (i.hasNext()) {
         Object[] row = (Object[]) i.next();
         Id idCesta = (Id) row[0];
         CodigoContaCetip conta = (CodigoContaCetip) row[1];
         liberar(idCesta, conta, hoje);
      }
   }

   private void liberar(Id idCesta, CodigoContaCetip participante, Data data) {
      RequisicaoServicoExecutarCestaGarantias req;
      req = new RequisicaoServicoExecutarCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(new NumeroCestaGarantia(idCesta.obterConteudo()));
      req.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(participante);
      req.atribuirOPERACAO_Data(data);

      Roteador.executarAssincrono(req, getContextoAtivacao());
   }

   public void liberarCestaInadimplente(Id id, Data data) {
      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = icg.obterCestaDeGarantias(new NumeroCestaGarantia(id.obterConteudo()));
      CodigoContaCetip participante = getFactory().getInstanceGarantidoCesta().obterGarantidoCesta(cesta)
            .getCodContaParticipante();

      liberar(id, participante, data);
   }

   private String hql() {
      StringBuffer hql = new StringBuffer(500);
      hql.append("select c.numIdCestaGarantias, c.garantido.codContaParticipante from CestaGarantiasDO c ");
      hql.append(" inner join c.statusCesta status where status = :status and ");
      hql.append(" c.indInadimplencia = :ind and ");
      hql.append(" c.datInadimplencia < :data and ");
      hql.append(" c.tipoGarantia.numIdTipoGarantia not in (:tipos) ");

      return hql.toString();
   }

}
