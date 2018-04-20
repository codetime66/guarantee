package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFSistemaDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="TIPO_INSTRUMENTO_FINANCEIRO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_IF_GARANTIDO"
 * 
 * @resultado.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_IF_GARANTIDOR"
 *                   
 * @resultado.method atributo="CodigoSistema" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="SISTEMA_GARANTIDO"
 * 
 * @resultado.method atributo="CodigoSistema" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="SISTEMA_GARANTIDOR"
 */
public class ServicoObterCombosTipoInstrumentoFinanceiro extends BaseGarantias implements Servico {

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      RequisicaoServicoObterCombosTipoInstrumentoFinanceiro req;
      req = (RequisicaoServicoObterCombosTipoInstrumentoFinanceiro) arg0;

      Texto tipoIF = req.obterTIPO_INSTRUMENTO_FINANCEIRO_Texto();
      String tipo = null;
      if (tipoIF != null) {
         if (tipoIF.obterConteudo().equals("CONTRATO")) {
            tipo = "CONTRATO";
         } else if (tipoIF.obterConteudo().equals("TITULO")) {
            tipo = "TÍTULO";
         }
      }

      ResultadoServicoObterCombosTipoInstrumentoFinanceiro res = new ResultadoServicoObterCombosTipoInstrumentoFinanceiro();

      CodigoTipoIF listaTipoIFGarantido = obterComboTipoIFGarantido(tipo);
      res.atribuirTIPO_IF_GARANTIDO_CodigoTipoIF(listaTipoIFGarantido);

      CodigoTipoIF listaTipoIFGarantidor = obterComboTipoIFGarantidor(tipo);
      res.atribuirTIPO_IF_GARANTIDOR_CodigoTipoIF(listaTipoIFGarantidor);

      CodigoSistema listaSistemasGarantidos = obterSistemasComAtivosGarantidos(tipo);
      res.atribuirSISTEMA_GARANTIDO_CodigoSistema(listaSistemasGarantidos);

      CodigoSistema listaSistemasGarantidores = obterSistemasComAtivosGarantidores(tipo);
      res.atribuirSISTEMA_GARANTIDOR_CodigoSistema(listaSistemasGarantidores);

      return res;
   }

   private CodigoSistema obterSistemasComAtivosGarantidos(String formaAtivo) {
      return obterSistemas("indGarantido", formaAtivo);
   }

   private CodigoSistema obterSistemasComAtivosGarantidores(String formaAtivo) {
      return obterSistemas("indGarantidor", formaAtivo);
   }

   private CodigoSistema obterSistemas(String tipo, String formaAtivo) {
      StringBuffer hql = new StringBuffer();
      hql.append("select distinct tis.sistema from ");
      hql.append(TipoIFSistemaDO.class.getName());
      hql.append(" tis inner join tis.tipoIF tipo ");
      hql.append(" where tis.").append(tipo).append(" = :indicacao ");

      if (formaAtivo != null) {
         hql.append("       and tipo.tipoIFGrupo.nomFormaAtivo = :formaAtivo ");
      }

      hql.append(" order by tis.sistema.codSistema asc");

      IGerenciadorPersistencia gep = getGp();
      IConsulta cons = gep.criarConsulta(hql.toString());
      cons.setCacheable(true);
      cons.setCacheRegion("MMG");

      if (formaAtivo != null) {
         cons.setAtributo("formaAtivo", new Nome(formaAtivo));
      }

      cons.setAtributo("indicacao", Booleano.VERDADEIRO);

      List lista = cons.list();
      Iterator iterator = lista.iterator();

      CodigoSistema codigoSistema = new CodigoSistema();
      codigoSistema.getDomain().add(new CodigoSistema(""));
      while (iterator.hasNext()) {
         SistemaDO sistemaGarantido = null;
         sistemaGarantido = (SistemaDO) iterator.next();
         codigoSistema.getDomain().add(new CodigoSistema(sistemaGarantido.getCodSistema().obterConteudo()));
      }

      return codigoSistema;
   }

   private CodigoTipoIF obterComboTipoIFGarantidor(String formaAtivo) {
      CodigoTipoIF codigoTipoIF = obterComboTipoIF("indGarantidor", formaAtivo);
      codigoTipoIF.getDomain().add(CodigoTipoIF.SELIC);
      return codigoTipoIF;
   }

   private CodigoTipoIF obterComboTipoIFGarantido(String formaAtivo) {
      return obterComboTipoIF("indGarantido", formaAtivo);
   }

   private CodigoTipoIF obterComboTipoIF(String tipo, String formaAtivo) {
      StringBuffer hql = new StringBuffer();
      hql.append("select distinct tipo from ");
      hql.append(TipoIFSistemaDO.class.getName());
      hql.append(" tis inner join tis.tipoIF tipo ");
      hql.append(" where tis.").append(tipo).append(" = :indicacao ");

      if (formaAtivo != null) {
         hql.append("       and tipo.tipoIFGrupo.nomFormaAtivo = :formaAtivo ");
      }

      hql.append("       and upper(tipo.localCustodia) <> :excecaoCustodia ");
      hql.append(" order by tipo.codigoTipoIF asc");

      IConsulta cons = getGp().criarConsulta(hql.toString());
      cons.setCacheable(true);
      cons.setCacheRegion("MMG");
      cons.setAtributo("indicacao", Booleano.VERDADEIRO);

      if (formaAtivo != null) {
         cons.setAtributo("formaAtivo", new Nome(formaAtivo));
      }

      cons.setAtributo("excecaoCustodia", new Nome("SELIC"));

      List list = cons.list();

      CodigoTipoIF codigoTipoIF = new CodigoTipoIF();
      codigoTipoIF.getDomain().add(new CodigoTipoIF(""));
      for (int i = 0; i < list.size(); i++) {
         TipoIFDO tipoIFDO = (TipoIFDO) list.get(i);
         CodigoTipoIF c = tipoIFDO.getCodigoTipoIF();
         codigoTipoIF.getDomain().add(c);
      }

      return codigoTipoIF;
   }

   public Resultado executar(Requisicao arg0) throws Exception {
      return null;
   }

}
