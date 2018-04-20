package br.com.cetip.aplicacao.garantias.servico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.DataHora;
import br.com.cetip.infra.atributo.tipo.texto.Descricao;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico de consulta de garantias para determinada cesta.
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_STATUS_MOV"
 * 
 * @requisicao.method atributo="Id" 
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_MOV"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Nome" 
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="TIPO_IF"
 * 
 * @resultado.method atributo="CodigoIF" 
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_IF"
 * 
 * @resultado.method atributo="Descricao" 
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="TIPO_GARANTIA"
 * 
 * @resultado.method atributo="Quantidade" 
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_QUANTIDADE"
 * 
 * @resultado.method atributo="Booleano" 
 *                   pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="GARANTIAS_ITENS"
 * 
 * @resultado.method atributo="DescricaoLimitada" 
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_ITENS"
 * 
 * @resultado.method atributo="NumeroCestaGarantia"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="NumeroOperacao"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador" 
 *                   contexto="OPERACAO"
 * 
 * @resultado.method atributo="Nome" 
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="Nome" 
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="Data" 
 *                   pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @resultado.method atributo="DataHora" 
 *                   pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="GARANTIAS_MOVIMENTACAO"
 * 
 * @resultado.method atributo="Nome" 
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_STATUS_MOV"
 * 
 * @resultado.method atributo="Nome" 
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_TIPO_MOV"
 */
public class ServicoListaHistoricoCesta extends BaseGarantias implements Servico {

   private static final Nome SELIC = new Nome("SELIC");

   public Resultado executar(Requisicao req) throws Exception {
      return null;
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoListaHistoricoCesta req;
      req = (RequisicaoServicoListaHistoricoCesta) requisicao;
      ResultadoServicoListaHistoricoCesta res = new ResultadoServicoListaHistoricoCesta();

      IGarantias factory = getFactory();

      ICestaDeGarantias icg = factory.getInstanceCestaDeGarantias();

      NumeroCestaGarantia numero = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      Id tipo = req.obterGARANTIAS_TIPO_MOV_Id();
      Id status = req.obterGARANTIAS_STATUS_MOV_Id();

      CestaGarantiasDO cesta = icg.obterCestaDeGarantias(numero);
      List movs = listarMovimentacoes(cesta, tipo, status);

      if (movs.isEmpty()) {
         throw new Erro(CodigoErro.GARANTIAS_LISTA_VAZIA);
      }

      Iterator movimentacoes = movs.iterator();

      TipoMovimentacaoGarantiaDO mockTipoMov = new TipoMovimentacaoGarantiaDO();
      while (movimentacoes.hasNext()) {
         res.novaLinha();

         Object[] linha = (Object[]) movimentacoes.next();

         Booleano indDireitos = (Booleano) linha[0];
         Quantidade quantidade = (Quantidade) linha[1];
         Descricao desTipoGarantia = (Descricao) linha[2];
         Texto descricao = (Texto) linha[3];
         Texto codIFNaoCetipado = (Texto) linha[4];
         CodigoIF codigoIF = (CodigoIF) linha[5];
         CodigoTipoIF codTipoIF = (CodigoTipoIF) linha[6];
         Booleano indCetipado = (Booleano) linha[7];

         // novos
         Id idTipoMov = (Id) linha[8]; // tipo.numIdTipoMovGarantia
         Nome nomeStatus = (Nome) linha[9]; // status.nomStatusMovGarantia
         Nome nomeTipo = (Nome) linha[10]; // tipo.nomTipoMovGarantia
         DataHora dataMov = (DataHora) linha[11]; // m.dataMovimentacao
         NumeroOperacao numOperacao = (NumeroOperacao) linha[12];
         Nome localCustodia = (Nome) linha[13];

         // Codigo IF
         if (indCetipado.ehVerdadeiro()) {
            res.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);

            // Tipo IF
            if (codTipoIF != null) {
               boolean ehSelic = localCustodia == null ? false : localCustodia.mesmoConteudo(SELIC);
               res.atribuirTIPO_IF_Nome(new Nome(ehSelic ? CodigoTipoIF.SELIC.toString() : codTipoIF.toString()));
            }
         } else if (codIFNaoCetipado != null) {
            CodigoIF nCetipado = new CodigoIF(Contexto.GARANTIAS_CODIGO_IF);
            nCetipado.atribuirConteudo(codIFNaoCetipado.obterConteudo());
            res.atribuirGARANTIAS_CODIGO_IF_CodigoIF(nCetipado);
            res.atribuirTIPO_IF_Nome(new Nome("NAO_CETIPADO"));
         }

         if (!Condicional.vazio(descricao)) {
            res.atribuirGARANTIAS_ITENS_DescricaoLimitada(new DescricaoLimitada(descricao.obterConteudo()));
         }

         // exibe tipo garantia, quantidade e eventos
         // quando for movimentacao de garantia
         mockTipoMov.setNumIdTipoMovGarantia(idTipoMov);
         if (mockTipoMov.ehMovimentacaoGarantia()) {
            res.atribuirTIPO_GARANTIA_Descricao(desTipoGarantia);
            res.atribuirGARANTIAS_QUANTIDADE_Quantidade(quantidade);
            res.atribuirGARANTIAS_ITENS_Booleano(indDireitos);
         }

         res.atribuirGARANTIAS_STATUS_MOV_Nome(nomeStatus);
         res.atribuirGARANTIAS_TIPO_MOV_Nome(nomeTipo);
         res.atribuirGARANTIAS_MOVIMENTACAO_DataHora(dataMov);
         res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(new NumeroCestaGarantia(cesta.getNumIdCestaGarantias()
               .obterConteudo()));
         res.atribuirOPERACAO_NumeroOperacao(numOperacao);
      }

      res.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(cesta.getGarantidor().getCodContaParticipante());
      res.atribuirGARANTIAS_PARTICIPANTE_Nome(cesta.getGarantidor().getNomeContaParticipante());
      res.atribuirGARANTIAS_DATA_CRIACAO_Data(cesta.getDatCriacao());

      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
      ContaParticipanteDO garantido = igc.obterGarantidoCesta(cesta);
      if (garantido != null) {
         res.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(garantido.getCodContaParticipante());
         res.atribuirGARANTIAS_CONTRAPARTE_Nome(garantido.getNomeContaParticipante());
      }

      return res;
   }

   private List listarMovimentacoes(CestaGarantiasDO cesta, Id tipo, Id status) {
      IGerenciadorPersistencia gp = getGp();
      StringBuffer hql = new StringBuffer(2000);
      hql.append("select m.indDireitosGarantidor,");
      hql.append("m.qtdGarantia,tipoGarantia.desTipoGarantia,");
      hql.append("m.txtDescricao,m.codIfNCetipado,ativo.codigoIF,");
      hql.append("tipoIF.codigoTipoIF, m.indCetipado, tipo.numIdTipoMovGarantia, ");
      hql.append(" status.nomStatusMovGarantia, tipo.nomTipoMovGarantia, ");
      hql.append(" m.dataMovimentacao, m.numOperacao, tipoIF.localCustodia from ");
      hql.append(MovimentacaoGarantiaDO.class.getName());
      hql.append(" m left join m.instrumentoFinanceiro ativo left join m.cestaGarantias.tipoGarantia tipoGarantia ");
      hql.append(" left join ativo.tipoIF tipoIF join m.tipoMovimentacaoGarantia tipo ");
      hql.append(" join m.statusMovimentacaoGarantia status ");
      hql.append(" where m.cestaGarantias = ? ");

      List values = new ArrayList(3);
      values.add(cesta);

      if (!Condicional.vazio(tipo)) {
         TipoMovimentacaoGarantiaDO tipoDo = new TipoMovimentacaoGarantiaDO();
         tipoDo.setNumIdTipoMovGarantia(tipo);
         values.add(tipoDo);
         hql.append(" and m.tipoMovimentacaoGarantia = ? ");
      }

      if (!Condicional.vazio(status)) {
         StatusMovimentacaoGarantiaDO statusDo = new StatusMovimentacaoGarantiaDO();
         statusDo.setNumIdStatusMovGarantia(new IdStatusMovimentacaoGarantia(status.toString()));
         values.add(statusDo);
         hql.append(" and m.statusMovimentacaoGarantia = ? ");
      }

      hql.append(" order by m.numIdMovimentacaoGarantia asc ");

      List resultado = gp.find(hql.toString(), values.toArray());
      if (resultado == null || resultado.isEmpty()) {
         return Collections.EMPTY_LIST;
      }

      return resultado;
   }

}