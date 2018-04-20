package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * @resultado.class
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_STATUS_MOV"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_TIPO_MOV"
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ServicoListaStatusTiposMovimentacoesGarantias extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      ResultadoServicoListaStatusTiposMovimentacoesGarantias res;
      res = new ResultadoServicoListaStatusTiposMovimentacoesGarantias();

      IConsulta cStatus = getGp().criarConsulta(
            "from ".concat(StatusMovimentacaoGarantiaDO.class.getName()).concat(" c order by c.nomStatusMovGarantia"));
      cStatus.setCacheable(true);
      cStatus.setCacheRegion("MMG");
      List listaStatus = cStatus.list();

      IConsulta cTipos = getGp().criarConsulta(
            "from ".concat(TipoMovimentacaoGarantiaDO.class.getName()).concat(
                  " c where c.numIdTipoMovGarantia <> 13 order by c.nomTipoMovGarantia"));
      cTipos.setCacheable(true);
      cTipos.setCacheRegion("MMG");
      List listaTipos = cTipos.list();

      Iterator itTipos = listaTipos.iterator();
      Iterator itStatus = listaStatus.iterator();

      while (itTipos.hasNext() || itStatus.hasNext()) {

         if (itTipos.hasNext()) {
            TipoMovimentacaoGarantiaDO tipo = (TipoMovimentacaoGarantiaDO) itTipos.next();
            res.novaLinha();

            String nome = tipo.getNomTipoMovGarantia().toString();
            Id _tipo = tipo.getNumIdTipoMovGarantia();
            Id novoId = new Id(nome, _tipo.toString());
            _tipo.atribuirRepresentacao(nome);
            res.atribuirGARANTIAS_TIPO_MOV_Id(novoId);
         }

         if (itStatus.hasNext()) {
            res.novaLinha();

            StatusMovimentacaoGarantiaDO statusDo = (StatusMovimentacaoGarantiaDO) itStatus.next();
            String nome = statusDo.getNomStatusMovGarantia().toString();
            Id _status = statusDo.getNumIdStatusMovGarantia();
            Id novoId = new Id(nome, _status.toString());
            res.atribuirGARANTIAS_STATUS_MOV_Id(novoId);
         }
      }

      return res;
   }

}