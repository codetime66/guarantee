package br.com.cetip.aplicacao.garantias.servico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.Descricao;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CESTA_GARANTIA"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CESTA_GARANTIA"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_STATUS"
 *                   
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="RELACAO_TIPO"
 *
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="TIPO_GARANTIA"
 *                   
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 *                   
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 */
public class ServicoConsultaCestaPorIF extends BaseGarantias implements Servico {

   private Texto texto;

   private Texto ativoGarantido;

   private Texto ativoGarantidor;

   private CodigoIF codigoARastrear;

   private boolean ehCETIP;

   public Resultado executar(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.ERRO, "METODO NAO IMPLEMENTADO NESTE SERVICO");
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoConsultaCestaPorIF reqConIF = (RequisicaoServicoConsultaCestaPorIF) requisicao;
      ResultadoServicoConsultaCestaPorIF res = new ResultadoServicoConsultaCestaPorIF();

      ContextoAtivacaoVO ca = getContextoAtivacao();
      ehCETIP = ca.ehCETIP();

      codigoARastrear = reqConIF.obterCESTA_GARANTIA_CodigoIF();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "RASTREANDO ATIVO: " + codigoARastrear);
      }

      ativoGarantido = new Texto(Contexto.RELACAO_TIPO, "ATIVO GARANTIDO");
      ativoGarantidor = new Texto(Contexto.RELACAO_TIPO, "ATIVO GARANTIDOR");

      List lista = null;

      // lista de cestas quando o ativo eh garantido
      lista = getCestasVinculadas();
      texto = ativoGarantido;
      preencheResultado(lista, res);

      // cestas quando o ativo eh garantidor
      lista = getCestasContendoGarantia();
      texto = ativoGarantidor;
      preencheResultado(lista, res);

      // movimentacoes de bloqueio e aporte pendente
      lista = getCestasContendoMovimentacoesPendentes();
      preencheResultado(lista, res);

      if (res.obterNumeroDeLinhas() == 0) {
         throw new Erro(CodigoErro.VALORES_FILTRO_VAZIO);
      }

      return res;
   }

   private List getCestasVinculadas() {
      String hql = " exists (select c2 from CestaGarantiasDO c2 where c2 = c and c2.ativosVinculados.instrumentoFinanceiro.codigoIF = ?) ";
      return consultar(hql, new Object[] { codigoARastrear });
   }

   private List getCestasContendoGarantia() {
      String subquery = "select d.cestaGarantias from DetalheGarantiaDO d where d.quantidadeGarantia > 0"
            + " and d.codIfNCetipado = ? and d.cestaGarantias = c";

      String subquery2 = "select d2.cestaGarantias from DetalheGarantiaDO d2 where d2.quantidadeGarantia > 0"
            + " and d2.instrumentoFinanceiro.codigoIF = ? and d2.cestaGarantias = c";

      String hql = " (exists (" + subquery + ") or exists (" + subquery2 + ")) ";
      return consultar(hql, new Object[] { codigoARastrear, codigoARastrear });
   }

   private List getCestasContendoMovimentacoesPendentes() {
      String subquery = "select m.cestaGarantias from MovimentacaoGarantiaDO m where "
            + " m.tipoMovimentacaoGarantia in (?,?) and m.statusMovimentacaoGarantia in (?,?,?,?) and m.codIfNCetipado = ? and m.cestaGarantias = c";

      String subquery2 = "select m.cestaGarantias from MovimentacaoGarantiaDO m where "
            + " m.tipoMovimentacaoGarantia in (?,?) and m.statusMovimentacaoGarantia in (?,?,?,?) and m.instrumentoFinanceiro.codigoIF = ? and m.cestaGarantias = c";

      String hql = " (exists (" + subquery + ") or exists(" + subquery2 + "))";

      Object[] params = new Object[] { TipoMovimentacaoGarantiaDO.BLOQUEIO, TipoMovimentacaoGarantiaDO.APORTE,
            StatusMovimentacaoGarantiaDO.PENDENTE, StatusMovimentacaoGarantiaDO.PENDENTE_ATUALIZA,
            StatusMovimentacaoGarantiaDO.APORTE_PENDENTE_GARANTIDO,
            StatusMovimentacaoGarantiaDO.APORTE_PENDENTE_GARANTIDOR, codigoARastrear,
            TipoMovimentacaoGarantiaDO.BLOQUEIO, TipoMovimentacaoGarantiaDO.APORTE,
            StatusMovimentacaoGarantiaDO.PENDENTE, StatusMovimentacaoGarantiaDO.PENDENTE_ATUALIZA,
            StatusMovimentacaoGarantiaDO.APORTE_PENDENTE_GARANTIDO,
            StatusMovimentacaoGarantiaDO.APORTE_PENDENTE_GARANTIDOR, codigoARastrear };

      return consultar(hql, params);
   }

   private void preencheResultado(List lista, ResultadoServicoConsultaCestaPorIF res) {
      Iterator i = lista.iterator();
      while (i.hasNext()) {
         Object[] r = (Object[]) i.next();
         Id idCesta = (Id) r[0];
         Texto status = (Texto) r[1];
         Descricao tipoGarantia = (Descricao) r[2];
         CodigoContaCetip contaGarantidor = (CodigoContaCetip) r[3];
         Nome nomeGarantidor = (Nome) r[4];

         res.atribuirCESTA_GARANTIA_NumeroCestaGarantia(new NumeroCestaGarantia(idCesta.obterConteudo()));
         res.atribuirGARANTIAS_STATUS_Texto(new Texto(status.obterConteudo()));
         res.atribuirTIPO_GARANTIA_Descricao(new Descricao(tipoGarantia.obterConteudo()));
         res.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(new CodigoContaCetip(contaGarantidor.obterConteudo()));
         res.atribuirGARANTIAS_PARTICIPANTE_Nome(new Nome(nomeGarantidor.obterConteudo()));
         res.atribuirRELACAO_TIPO_Texto(new Texto(texto.obterConteudo()));
         res.novaLinha();
      }
   }

   private Collection situacoesCesta() {
      List newParams = new ArrayList(11);
      newParams.add(StatusCestaDO.FINALIZADA);
      newParams.add(StatusCestaDO.INCOMPLETA);
      newParams.add(StatusCestaDO.EM_FINALIZACAO);
      newParams.add(StatusCestaDO.EM_MANUTENCAO);
      newParams.add(StatusCestaDO.EM_EDICAO);
      newParams.add(StatusCestaDO.EM_VINCULACAO);
      newParams.add(StatusCestaDO.INADIMPLENTE);
      newParams.add(StatusCestaDO.EM_LIBERACAO);
      newParams.add(StatusCestaDO.VINCULADA);
      newParams.add(StatusCestaDO.VINCULADA_AO_ATIVO);
      newParams.add(StatusCestaDO.VNC_PENDENTE);
      return newParams;
   }

   private List consultar(String hqlExtra, Object[] parametrosExtras) {
      StringBuffer hql = new StringBuffer();
      hql.append(getHQLSelectWhere());
      hql.append(hqlExtra);
      hql.append(getHQLSituacoesCesta());
      hql.append(getHQLPermissao());

      List params = new ArrayList();

      if (parametrosExtras != null && parametrosExtras.length > 0) {
         params.addAll(Arrays.asList(parametrosExtras));
      }

      params.addAll(situacoesCesta());

      return getGp().find(hql.toString(), params.toArray());
   }

   private String getHQLPermissao() {
      ContextoAtivacaoVO ca = getContextoAtivacao();
      if (ehCETIP) {
         return "";
      }

      StringBuffer hql = new StringBuffer(2000);

      Id idParticipante = new Id(ca.getIdParticipante().toString());
      Id idUsuario = new Id(ca.getIdUsuario().toString());

      String controleAcesso = "";
      try {
         controleAcesso = ContaParticipanteFactory.getInstance().obterSelectCodContasFamiliaPorCodAcesso(null,
               idParticipante, idUsuario);
      } catch (Exception e) {
         e.printStackTrace();
      }

      // Filtros por tipo
      // Garantido
      hql.append(" and ( ");
      hql.append(" garantido.codContaParticipante in ( ");
      hql.append(controleAcesso);
      hql.append(" ) or ");

      // Garantidor
      hql.append(" c2.garantidor.codContaParticipante in ( ");
      hql.append(controleAcesso);
      hql.append(" ) ) ");

      return hql.toString();
   }

   private String getHQLSituacoesCesta() {
      return " and c.statusCesta.numIdStatusCesta in (?,?,?,?,?,?,?,?,?,?,?) ";
   }

   private String getHQLSelectWhere() {
      StringBuffer hql = new StringBuffer();
      hql.append("select c.numIdCestaGarantias, c.statusCesta.nomStatusCesta,");
      hql.append("c.tipoGarantia.desTipoGarantia, c.garantidor.codContaParticipante,");
      hql.append("c.garantidor.nomeContaParticipante from CestaGarantiasDO c ");

      if (!ehCETIP) {
         hql.append(" , CestaGarantiasDO c2 left join c2.visualizadores g ");
         hql.append(" left join g.contaParticipante garantido ");
      }

      hql.append(" where ");

      if (!ehCETIP) {
         hql.append(" c2 = c and ");
      }

      return hql.toString();
   }

}
