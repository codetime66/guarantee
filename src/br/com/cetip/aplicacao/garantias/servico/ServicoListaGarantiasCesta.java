package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidaAcoes;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.Descricao;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.tipo.texto.TextoLimitado;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico de consulta de garantias para determinada cesta.
 *
 * @requisicao.class
 *
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 *
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_ACESSO"
 *
 * @resultado.class
 *
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_ITENS"
 *
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="TIPO_IF"
 *
 * @resultado.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_IF"
 *
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="TIPO_GARANTIA"
 *
 * @resultado.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_QUANTIDADE"
 *
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIAS_ITENS"
 *
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 *
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 *
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 *
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_PARTICIPANTE"
 *
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_CONTRAPARTE"
 *
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="GARANTIAS_DATA_CRIACAO"
 *
 * @resultado.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_LIBERAR_QUANTIDADE"
 *
 * @resultado.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="ACAO"
 *
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_ITENS"
 *
 * @resultado.method atributo="DescricaoLimitada" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_ITENS"
 *
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="INADIMPLENTE"
 *
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="INADIMPLENTE_EMISSOR"
 *
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIDO"
 *
 * @resultado.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIDOR"
 *
 * @resultado.method atributo="TextoLimitado" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="CESTA_GARANTIA"
 *
 * @resultado.method atributo="NumeroOperacao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="OPERACAO"
 */
public class ServicoListaGarantiasCesta extends BaseGarantias implements Servico {

   private IGarantias factory;

   private ICestaDeGarantias icg;
   private boolean podeExcluirGarantias = false;
   private boolean podeRetirarGarantias = false;

   public Resultado executar(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Entrou no servico");
      }

      RequisicaoServicoListaGarantiasCesta req = (RequisicaoServicoListaGarantiasCesta) requisicao;
      ResultadoServicoListaGarantiasCesta res = new ResultadoServicoListaGarantiasCesta();

      factory = getFactory();

      icg = factory.getInstanceCestaDeGarantias();
      IValidaAcoes ima = factory.getInstanceValidaAcoes();
      NumeroCestaGarantia numero = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      Funcao acesso = req.obterGARANTIAS_TIPO_ACESSO_Funcao();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Listando Garantias da Cesta: " + numero);
      }

      CestaGarantiasDO cesta = icg.obterCestaDeGarantias(numero);

      boolean possuiMovimentacoes = cestaPossueMovimentacoes(cesta);
      boolean possuiGarantias = cestaPossueGarantias(cesta);

      if (!possuiGarantias && !possuiMovimentacoes) {
         throw new Erro(CodigoErro.GARANTIAS_LISTA_VAZIA);
      }

      if (!Condicional.vazio(acesso)) {
         if (possuiMovimentacoes || possuiGarantias) {
            podeExcluirGarantias = ima.podeExecutarAcao(ICestaDeGarantias.EXCLUIR_GARANTIAS, acesso, cesta);
         }

         if (possuiGarantias) {
            podeRetirarGarantias = ima.podeExecutarAcao(ICestaDeGarantias.RETIRAR_GARANTIAS, acesso, cesta);
         }
      }

      int totalItens = 0;

      if (possuiMovimentacoes) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Movimentacoes de Bloqueio... Listando!");
         }

         StringBuffer hql = new StringBuffer(200);
         hql.append("select m.numIdMovimentacaoGarantia,m.indDireitosGarantidor,");
         hql.append("m.qtdGarantia,tipoGarantia.desTipoGarantia,");
         hql.append("m.txtDescricao,m.codIfNCetipado,ativo.codigoIF,");
         hql.append("ativo.indInadimplencia,emissor.indInadimplencia,");
         hql.append("tipoIF.codigoTipoIF, m.indCetipado, ativo.id, sistema.numero ");
         hql.append(" from ");
         hql.append(obtemHQLMovimentacoes());

         Iterator movGarantias = getGp().find(
               hql.toString(),
               new Object[] { cesta, TipoMovimentacaoGarantiaDO.BLOQUEIO, StatusMovimentacaoGarantiaDO.OK,
                     StatusMovimentacaoGarantiaDO.PENDENTE_ATUALIZA,
                     StatusMovimentacaoGarantiaDO.MOVIMENTACAO_CANCELADA }).iterator();

         totalItens += atribuirGarantias(movGarantias, res, cesta, acesso, "M");
      }

      if (possuiGarantias) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Garantias... Listando!");
         }
         StringBuffer hql = new StringBuffer(200);
         hql.append("select d.numIdDetalheGarantia,d.indDireitosGarantidor,");
         hql.append("d.quantidadeGarantia,tipoGarantia.desTipoGarantia,");
         hql.append("d.txtDescricao,d.codIfNCetipado,ativo.codigoIF,");
         hql.append("ativo.indInadimplencia,emissor.indInadimplencia,");
         hql.append("tipoIF.codigoTipoIF, d.indCetipado, ativo.id, sistema.numero ");
         hql.append(" from ");
         hql.append(obtemHQLGarantias());

         Iterator garantias = getGp().find(hql.toString(), new Object[] { cesta }).iterator();

         totalItens += atribuirGarantias(garantias, res, cesta, acesso, "G");
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

      if (totalItens == 0) {
         throw new Erro(CodigoErro.VALORES_FILTRO_VAZIO);
      } else if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "- Numero de linhas: " + totalItens);
      }

      return res;
   }

   protected String obtemHQLGarantias() {
      /* Nao obtem o numeroOperacao pq 
       * esta inf so consta na MovimentacaoGarantia e 
       * o ativo pode ter diversas movimentacoes 
       * (cada qual com seu nro - aporte, Retirada) 
       */

      StringBuffer hql = new StringBuffer(2000);
      hql.append(DetalheGarantiaDO.class.getName());
      hql
            .append(" d left join d.instrumentoFinanceiro ativo left join ativo.sistema sistema left join d.cestaGarantias.tipoGarantia tipoGarantia ");
      hql.append(" left join ativo.contaParticipante emissor left join ativo.tipoIF tipoIF ");
      hql.append(" where d.cestaGarantias = ? and ");
      hql.append(" d.quantidadeGarantia > 0 ");
      return hql.toString();
   }

   private boolean cestaPossueGarantias(CestaGarantiasDO cesta) {
      // contagem de garantias, caso a cesta jah tenha sido finalizada uma vez
      int countGarantias = 0;
      if (cesta.getDatFechamento() != null) {
         StringBuffer hql = new StringBuffer(400);
         hql.append("select count(*) from ").append(obtemHQLGarantias());

         countGarantias = ((Integer) getGp().find(hql.toString(), new Object[] { cesta }).get(0)).intValue();
      }
      return countGarantias > 0;
   }

   protected String obtemHQLMovimentacoes() {
      StringBuffer hql = new StringBuffer(2000);
      hql.append(MovimentacaoGarantiaDO.class.getName() + " m ");
      hql.append(" left join m.instrumentoFinanceiro ativo ");
      hql.append(" left join ativo.sistema sistema  ");
      hql.append(" left join m.cestaGarantias.tipoGarantia tipoGarantia ");
      hql.append(" left join ativo.contaParticipante emissor ");
      hql.append(" left join ativo.tipoIF tipoIF ");
      hql.append(" where m.cestaGarantias = ? and ");
      hql
            .append(" ((m.tipoMovimentacaoGarantia = ? and m.statusMovimentacaoGarantia <> ? and m.statusMovimentacaoGarantia <> ? and m.statusMovimentacaoGarantia <> ?))");
      return hql.toString();
   }

   private boolean cestaPossueMovimentacoes(CestaGarantiasDO cesta) {
      // Contagem de movimentacoes de bloqueio pendentes, caso a cesta nao tenha sido finalizada ainda
      // Se nao tiver movimentacoes, nem carrega depois
      int countMovs = 0;
      if (cesta.getStatusCesta().equals(StatusCestaDO.EM_EDICAO)
            || cesta.getStatusCesta().equals(StatusCestaDO.EM_MANUTENCAO)
            || cesta.getStatusCesta().equals(StatusCestaDO.INCOMPLETA)) {
         StringBuffer hql = new StringBuffer(400);
         hql.append("select count(*) from ").append(obtemHQLMovimentacoes());

         countMovs = ((Integer) getGp().find(
               hql.toString(),
               new Object[] { cesta, TipoMovimentacaoGarantiaDO.BLOQUEIO, StatusMovimentacaoGarantiaDO.OK,
                     StatusMovimentacaoGarantiaDO.PENDENTE_ATUALIZA,
                     StatusMovimentacaoGarantiaDO.MOVIMENTACAO_CANCELADA }).get(0)).intValue();
      }
      return countMovs > 0;
   }

   /*
    * OBS: O atributo GARANTIDO_POR_CESTA_NumeroCestaGarantia do resultado retorna <b>null</b> quando nao encontrada uma
    * cesta que garanta o ativo.
    *
    * @param setGarantias
    * @param res
    * @param cesta
    * @param acesso
    */
   private int atribuirGarantias(Iterator garantias, ResultadoServicoListaGarantiasCesta res, CestaGarantiasDO cesta,
         Funcao acesso, String tipoLinha) {

      int total = 0;

      while (garantias.hasNext()) {
         Object[] linha = (Object[]) garantias.next();
         adicionarLinhaResultado(linha, res, cesta, acesso, tipoLinha);
         total++;
      }

      return total;
   }

   /**
    * Adiciona a linha ao resultado que sera apresentado
    */
   private void adicionarLinhaResultado(Object[] linha, ResultadoServicoListaGarantiasCesta res,
         CestaGarantiasDO cesta, Funcao acesso, String tipoLinha) {
      Id idLinha = (Id) linha[0];
      Booleano indDireitos = (Booleano) linha[1];
      Quantidade quantidade = (Quantidade) linha[2];
      Descricao desTipoGarantia = (Descricao) linha[3];
      Texto descricao = (Texto) linha[4];
      Texto codIFNaoCetipado = (Texto) linha[5];
      CodigoIF codigoIF = (CodigoIF) linha[6];
      Booleano indAtivoInadimplente = (Booleano) linha[7];
      Booleano indEmissorInadimplente = (Booleano) linha[8];
      CodigoTipoIF codTipoIF = (CodigoTipoIF) linha[9];
      Booleano indCetipado = (Booleano) linha[10];
      Id idAtivo = (Id) linha[11];
      Id sistema = (Id) linha[12];
      NumeroOperacao numOperacao = new NumeroOperacao();

      res.novaLinha();
      res.atribuirGARANTIAS_ITENS_Texto(new Texto(tipoLinha));

      boolean ehCetipado = indCetipado.ehVerdadeiro();
      boolean ehGarantiaSelic = sistema != null ? sistema.mesmoConteudo(SistemaDO.SELIC) : false;

      // Codigo IF
      if (ehCetipado) {
         res.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);
         res.atribuirINADIMPLENTE_Booleano(indAtivoInadimplente == null ? new Booleano(Booleano.FALSO)
               : indAtivoInadimplente);

         atribuiCestaGarantidora(res, idAtivo);

         res.atribuirINADIMPLENTE_EMISSOR_Booleano(new Booleano(indEmissorInadimplente == null ? Booleano.FALSO
               : indEmissorInadimplente));
         res.atribuirTIPO_IF_Nome(new Nome(ehGarantiaSelic ? CodigoTipoIF.SELIC.toString() : codTipoIF.toString()));
      } else {
         CodigoIF nCetipado = new CodigoIF(Contexto.GARANTIAS_CODIGO_IF);
         nCetipado.atribuirConteudo(codIFNaoCetipado.obterConteudo());
         res.atribuirGARANTIAS_CODIGO_IF_CodigoIF(nCetipado);
         res.atribuirTIPO_IF_Nome(new Nome("NAO CETIPADO"));
         res.atribuirINADIMPLENTE_Booleano(new Booleano(Booleano.FALSO));
         res.atribuirINADIMPLENTE_EMISSOR_Booleano(new Booleano(Booleano.FALSO));
         res.atribuirGARANTIDO_Booleano(new Booleano(Booleano.FALSO));
         res.atribuirCESTA_GARANTIA_TextoLimitado(new TextoLimitado(Contexto.CESTA_GARANTIA, "-"));
      }

      // Descricao da Garantia
      res
            .atribuirGARANTIAS_ITENS_DescricaoLimitada(new DescricaoLimitada(descricao == null ? "" : descricao
                  .toString()));
      res.atribuirTIPO_GARANTIA_Descricao(desTipoGarantia);// Tipo Garantia
      res.atribuirGARANTIAS_QUANTIDADE_Quantidade(quantidade);// Quantidade
      res.atribuirGARANTIAS_LIBERAR_QUANTIDADE_Quantidade(quantidade);
      res.atribuirGARANTIAS_ITENS_Booleano(indDireitos);// Direitos Garantidor
      res.atribuirOPERACAO_NumeroOperacao(numOperacao);//Número da operação do ativo selicado
      res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(new NumeroCestaGarantia(cesta.getNumIdCestaGarantias()));
      res.atribuirGARANTIAS_ITENS_Id(idLinha);

      if (!Condicional.vazio(acesso)) {
         Funcao f = new Funcao(Contexto.ACAO);
         f.getDomain().add(new Funcao(""));
         res.atribuirACAO_Funcao(f);

         boolean acessoGarantidor = acesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDOR);
         if ((acesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDO) || (acessoGarantidor && ehGarantiaSelic))
               && podeRetirarGarantias && "G".equals(tipoLinha)) {
            f.getDomain().add(ICestaDeGarantias.RETIRAR_GARANTIAS);
         } else if (acesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDOR) && podeExcluirGarantias) {
            f.getDomain().add(ICestaDeGarantias.EXCLUIR_GARANTIA);
         }
      }
   }

   private void atribuiCestaGarantidora(ResultadoServicoListaGarantiasCesta res, Id idAtivo) {
      Id idCesta = obterCestaGarantindoAtivo(idAtivo);

      if (idCesta == null) {
         res.atribuirGARANTIDO_Booleano(new Booleano(Booleano.FALSO));
         res.atribuirCESTA_GARANTIA_TextoLimitado(new TextoLimitado(Contexto.CESTA_GARANTIA, "-"));
      } else {
         res.atribuirGARANTIDO_Booleano(new Booleano(Booleano.VERDADEIRO));
         res.atribuirCESTA_GARANTIA_TextoLimitado(new TextoLimitado(Contexto.CESTA_GARANTIA, idCesta.toString()));
      }
   }

   private Id obterCestaGarantindoAtivo(Id idAtivo) {
      Id idCesta;
      NumeroCestaGarantia numCesta = icg.obterCestaGarantindoIF(idAtivo);

      if (numCesta != null) {
         idCesta = numCesta.copiarParaId();
      } else {
         idCesta = null;
      }

      return idCesta;
   }

}