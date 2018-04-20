package br.com.cetip.aplicacao.garantias.servico;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiaVO;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidarTipoIF;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.TipoIFFactory;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.cdawa.CDAWAFactory;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.cdawa.ICDAWA;
import br.com.cetip.dados.aplicacao.garantias.AcessoCestaDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.GarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TituloDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.AtributosColunadosIterator;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoCDA;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoWA;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico que registra um cadastro da cesta de garantias
 * 
 * @resultado.class
 * @resultado.method atributo="Id"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="Booleano"
 *                    pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="CodigoTipoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @requisicao.method atributo="CodigoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_IF"
 * 
 * @requisicao.method atributo="IdTipoGarantia"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Id"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * @requisicao.method atributo="Quantidade"
 *                    pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_QUANTIDADE"
 * 
 * @requisicao.method atributo="NumeroOperacao"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="OPERACAO"
 * 
 * @requisicao.method atributo="DescricaoLimitada"
 *                    pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                    contexto="GARANTIAS_ITENS"
 * 
 */
public class ServicoRegistraItensCestaGarantiasViaArquivo extends BaseGarantias implements Servico {

   private static interface FiltroLote {
      boolean filtrar(AtributosColunadosIterator atributos, Lote lote);
   }

   private Map idAtivos;
   private Set cdaWaValidados;
   private static final int TAMANHO_LOTE = 1000;
   private Id idCesta;
   private IMovimentacoesGarantias iMov;
   private IConsulta consultaTipoAgro;
   private IConsulta consultaAtivo;
   private ICDAWA cdaWaDao;
   private CestaGarantiasDO cesta;
   private Map mapaGarantidores;

   public Resultado executar(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Servico de inclusao de Itens na Cesta de Garantias Via Arquivo ***** ");
      }

      final RequisicaoServicoRegistraItensCestaGarantiasViaArquivo req = (RequisicaoServicoRegistraItensCestaGarantiasViaArquivo) requisicao;

      idCesta = req.obterGARANTIAS_CODIGO_Id();
      cesta = (CestaGarantiasDO) getGp().load(CestaGarantiasDO.class, idCesta);

      // valida o penhor no emissor - parte e contraparte devem ser iguais
      IdTipoGarantia tipoGarantia = req.obterGARANTIAS_CODIGO_IdTipoGarantia();
      validaGarantidoEGarantidor(tipoGarantia);

      if (cesta.getTipoGarantia() == null) {
         cesta.setTipoGarantia(new TipoGarantiaDO(tipoGarantia));
         getGp().update(cesta);
         getGp().flush();
      }

      cesta = new CestaGarantiasDO();
      cesta.setNumIdCestaGarantias(idCesta);

      carregaTiposIFValidos();
      validaTipoGarantia(req.obterListaGARANTIAS_CODIGO_IdTipoGarantia());
      validaFlagEventosGarantidor(tipoGarantia, req.obterListaGARANTIAS_ITENS_Booleano());

      // Obtem todos os itens e processa validacoes em lote
      AtributosColunados ac = req.obterAtributosColunados();
      AtributosColunadosIterator atributos = ac.iterar();

      int totalAtivos = ac.obterNumeroDeLinhas();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Total de ativos a serem incluidos: " + totalAtivos);
      }

      if (totalAtivos == 0) {
         return new ResultadoServicoRegistraItensCestaGarantiasViaArquivo();
      }

      idAtivos = new HashMap(totalAtivos);
      cdaWaValidados = new HashSet(totalAtivos);

      Lote lote = new Lote();
      iMov = getFactory().getInstanceMovimentacoesGarantias();

      // Valida lotes Agro (CDCA, CRA e LCA)
      while (atributos.avancarLinha()) {
         montaLote(atributos, lote, new FiltroLote() {

            public boolean filtrar(AtributosColunadosIterator atributosColunadosIterator, Lote loteAFiltrar) {
               CodigoTipoIF tipo = (CodigoTipoIF) atributosColunadosIterator.obterAtributo(CodigoTipoIF.class,
                     Contexto.GARANTIAS_CODIGO_TIPO);

               if (tipo.ehCRA() || tipo.ehLCA() || tipo.ehCDCA()) {
                  return true;
               }

               return false;
            }
         });

         if (lote.isEmpty() == false) {
            validaLoteAgro(lote, tipoGarantia);
         }
      }

      cdaWaDao = CDAWAFactory.getInstance();

      // Valida lote generico
      atributos.reiniciar();
      while (atributos.avancarLinha()) {
         montaLote(atributos, lote, null);

         if (lote.isEmpty() == false) {
            validaLote(lote, tipoGarantia);
         }
      }

      atributos.reiniciar();
      int loteCount = 0;
      while (atributos.avancarLinha()) {
         incluirAtivo(atributos);

         if (++loteCount % TAMANHO_LOTE == 0) {
            // limpa o cache e reinicia a contagem
            loteCount = 0;
            clearSession();
         }
      }

      ResultadoServicoRegistraItensCestaGarantiasViaArquivo res;
      res = new ResultadoServicoRegistraItensCestaGarantiasViaArquivo();
      res.atribuirGARANTIAS_CODIGO_Id(idCesta);
      return res;
   }

   private void carregaTiposIFValidos() {
      IValidarTipoIF validaTipoIF = getFactory().getInstanceValidarTipoIF();
      mapaGarantidores = validaTipoIF.obterMapaGarantidores();
   }

   private void validaGarantidoEGarantidor(IdTipoGarantia tipoGarantia) {
      if (tipoGarantia.mesmoConteudo(IdTipoGarantia.PENHOR_NO_EMISSOR) == false) {
         return;
      }

      if (cesta.getVisualizadores() != null && cesta.getVisualizadores().isEmpty() == false) {
         ContaParticipanteDO garantidor = cesta.getGarantidor();
         ContaParticipanteDO garantido = ((AcessoCestaDO) cesta.getVisualizadores().iterator().next())
               .getContaParticipante();

         if (garantidor.getId().mesmoConteudo(garantido.getId()) == false) {
            Erro erro = new Erro(CodigoErro.CESTA_INCOMPATIVEL);
            erro.parametroMensagem("Cesta é Penhor - Garantido e Garantidor devem ser iguais", 0);
            throw erro;
         }
      }
   }

   private void clearSession() {
      getGp().flush();
      getGp().clear();
   }

   private void incluirAtivo(AtributosColunadosIterator a) {
      CodigoIF codIF = (CodigoIF) a.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF);
      CodigoTipoIF tipoIF = (CodigoTipoIF) a.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CODIGO_TIPO);
      Quantidade quantidade = (Quantidade) a.obterAtributo(Quantidade.class, Contexto.GARANTIAS_QUANTIDADE);
      Booleano indEventos = (Booleano) a.obterAtributo(Booleano.class, Contexto.GARANTIAS_ITENS);
      NumeroOperacao noOp = (NumeroOperacao) a.obterAtributo(NumeroOperacao.class, Contexto.OPERACAO);
      DescricaoLimitada descricao = (DescricaoLimitada) a.obterAtributo(DescricaoLimitada.class,
            Contexto.GARANTIAS_ITENS);

      GarantiaVO garantia = new GarantiaVO();
      garantia.indDireitoGarantidor = indEventos;
      garantia.quantidade = quantidade;
      garantia.descricao = descricao;
      garantia.numeroOperacao = noOp;

      InstrumentoFinanceiroDO ativo = (InstrumentoFinanceiroDO) idAtivos.get(codIF.obterConteudo());
      if (ativo == null && !tipoIF.ehN_CTP()) {
         Erro erro = new Erro(CodigoErro.ARQUIVO_INCONSISTENTE_IF_NAO_ENCONTRADO);
         erro.parametroMensagem(codIF, 0);
         throw erro;
      }

      if (ativo != null) {
         garantia.ativoCetipado = ativo;
      } else {
         garantia.codIfNCetipado = codIF;
      }

      if (ativo == null && tipoIF.ehN_CTP() && indEventos.ehVerdadeiro()) {
    	  Logger.error("Eventos para garantidor nao permitido para ativos NAO CETIPADOS " + codIF);
          Erro erro = new Erro(CodigoErro.EVENTOS_PARA_GARANTIDOR_NAO_PERMITIDO);
          erro.parametroMensagem(codIF, 0);
          throw erro;
       }
      
      
      MovimentacaoGarantiaDO mov = iMov.criarMovimentacaoBloqueio(garantia);
      mov.setCestaGarantias(cesta);
      getGp().save(mov);
   }

   private void validaLoteAgro(Lote lote, IdTipoGarantia tipoGarantia) {
      // Confere regras para ativos de Agronegocio
      List codigosIF = lote.get(CodigoIF.class);
      validaTiposAgro(codigosIF, tipoGarantia);
   }

   private void validaFlagEventosGarantidor(IdTipoGarantia idTipoGarantia, List flags) {
      if (!idTipoGarantia.mesmoConteudo(IdTipoGarantia.PENHOR_NO_EMISSOR)) {
         return;
      }

      Set s = new HashSet(flags);
      boolean contemFalso = s.contains(new Booleano(Booleano.FALSO));

      if (s.size() > 1 || contemFalso) {
         Erro erro = new Erro(CodigoErro.CESTA_INCOMPATIVEL);
         erro.parametroMensagem("Penhor no Emissor exige Eventos para Garantidor a todos os ativos", 0);
         throw erro;
      }
   }

   private void validaTipoGarantia(List l) {
      Set s = new HashSet(l);

      if (s.size() > 1) {
         throw new Erro(CodigoErro.CESTA_MAIS_DE_UM_TIPO_GARANTIA);
      }
   }

   private void validaLote(Lote lote, IdTipoGarantia tipoGarantia) {
      // Valida existencia dos ativos no lote
      List codigosIF = lote.get(CodigoIF.class);
      List codigosTipoIF = lote.get(CodigoTipoIF.class);
      List quantidades = lote.get(Quantidade.class);

      // Confere os tipos do lote
      validaTiposAtivos(codigosIF, codigosTipoIF, quantidades, tipoGarantia);
   }

   private void validaTiposAgro(List codigosIF, IdTipoGarantia idTipoGarantia) {
      // valida o lote
      // ativos devem estar vinculados a uma cesta de garantias

      if (consultaTipoAgro == null) {
         StringBuffer sql = new StringBuffer(1000);
         sql
               .append("select count(distinct cg) from ")
               .append(CestaGarantiasDO.class.getName())
               .append(" cg, ")
               .append(GarantiaDO.class.getName())
               .append(" g, ")
               .append(CestaGarantiasIFDO.class.getName())
               .append(
                     " cif where cg.numIdCestaGarantias = cif.cestaGarantia and cif.instrumentoFinanceiro.codigoIF in (:idIF) ")
               .append(" and cg.statusCesta.numIdStatusCesta in (:status) and ").append(
                     " cif.instrumentoFinanceiro = g.instrumentoFinanceiro ").append(
                     " and g.tipoGarantia.numIdTipoGarantia = :idTipoGarantia");

         consultaTipoAgro = getGp().criarConsulta(sql.toString());

         Id[] status = { StatusCestaDO.EM_VINCULACAO.getNumIdStatusCesta(),
               StatusCestaDO.VINCULADA.getNumIdStatusCesta(), StatusCestaDO.VINCULADA_AO_ATIVO.getNumIdStatusCesta(),
               StatusCestaDO.INADIMPLENTE.getNumIdStatusCesta() };

         consultaTipoAgro.setParameterList("status", status);
      }
      consultaTipoAgro.setParameterList("idIF", codigosIF);
      consultaTipoAgro.setAtributo("idTipoGarantia", idTipoGarantia);

      List l = consultaTipoAgro.list();
      Integer count = (Integer) l.get(0);

      if (count.intValue() < codigosIF.size()) {
         Erro erro = new Erro(CodigoErro.CESTA_INCOMPATIVEL);
         erro.parametroMensagem("Existem ativos CDCA/LCA/CRA sem lastro ou com Tipo de Garantia diferente.", 0);
         throw erro;
      }
   }

   private void validaTiposAtivos(List codigosIF, List codigosTipoIF, List quantidades, IdTipoGarantia tipoGarantia) {

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Validacao de " + codigosIF.size() + " ativos");
      }

      Set idsIF = new HashSet(codigosIF);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Total de " + codigosIF.size() + " ativos distintos");
      }

      if (consultaAtivo == null) {
         StringBuffer b = new StringBuffer(1000);
         b.append("select i from ");
         b.append(InstrumentoFinanceiroDO.class.getName());
         b.append(" i where i.codigoIF in (:lista) and i.dataHoraExclusao is null and i.dataVencimento > :data");

         consultaAtivo = getGp().criarConsulta(b.toString());
         consultaAtivo.setAtributo("data", getDataHoje());
      }

      consultaAtivo.setParameterList("lista", idsIF);

      List l = consultaAtivo.list();
      Iterator i = l.iterator();
      while (i.hasNext()) {
         InstrumentoFinanceiroDO ativo = (InstrumentoFinanceiroDO) i.next();

         CodigoTipoIF tipo = ativo.getTipoIF().getCodigoTipoIF();
         CodigoIF cod = ativo.getCodigoIF();
         Id sistema = ativo.getSistema().getNumero();

         int index = codigosIF.indexOf(cod);
         CodigoTipoIF tipoOutro = (CodigoTipoIF) codigosTipoIF.get(index);

         if (tipoOutro.mesmoConteudo(CodigoTipoIF.SELIC)) {
            //obtem o codigo do tipo IF de ativos Selic
            IGarantiasSelic gs = getFactory().getInstanceGarantiasSelic();
            tipo = gs.obterAtivoSelic(cod).getTipoIF().getCodigoTipoIF();
            if (!sistema.equals(SistemaDO.SELIC)) {
               throw new Erro(CodigoErro.TIPO_IF_INCOMPATIVEL_CODIGO_IF);
            }
            if (!gs.ehSelicHabilitadoMMG(cod)) {
               throw new Erro(CodigoErro.MMG_SELIC_NAO_HABILITADO);
            }
         } else if (tipo.mesmoConteudo(tipoOutro) == false) {
            throw new Erro(CodigoErro.TIPO_IF_INCOMPATIVEL_CODIGO_IF);
         }

         if (tipoGarantidorAceito(tipo, sistema) == false) {
            throw new Erro(CodigoErro.CESTA_TIPO_IF_INVALIDO);
         }

         boolean ehGarantiaPenhor = tipoGarantia.mesmoConteudo(IdTipoGarantia.PENHOR_NO_EMISSOR);

         if (tipoGarantidorAceito(tipo, sistema) == false && !ehGarantiaPenhor) {
            throw new Erro(CodigoErro.CESTA_TIPO_IF_INVALIDO);
         }

         if (ehGarantiaPenhor && TipoIFFactory.getInstance().ehTipoIFSelicado(tipo)) {
            throw new Erro(CodigoErro.MMG_SELIC_GARANTIA_CESSAO_FIDUCIARIA);
         }

         if (tipo.mesmoConteudo(CodigoTipoIF.CTRA) && !ehGarantiaPenhor) {
            throw new Erro(CodigoErro.CESTA_TIPO_IF_INVALIDO);
         }

         QuantidadeInteiraPositiva qtd = ativo instanceof TituloDO ? ((TituloDO) ativo).getQtdEmitida()
               : new QuantidadeInteiraPositiva("0");

         // CDCA soh pode ser parcial quando penhor no emissor
         if (tipo.ehCDCA() && !tipoGarantia.mesmoConteudo(IdTipoGarantia.PENHOR_NO_EMISSOR)
               && qtd.compareTo(quantidades.get(index)) != 0) {
            Erro erro = new Erro(CodigoErro.QTD_EMITIDA_DEVE_SER_IGUAL_OPERACAO);
            erro.parametroMensagem(cod, 0);
            throw erro;
         }

         // CDA e WA
         if (tipo.ehCDA() || tipo.ehWA()) {
            if (qtd.compareTo(new Quantidade("1")) != 0) {
               throw new Erro(CodigoErro.CDA_WA_QUANTIDADE_NAO_UNITARIA);
            }

            boolean resultado = false;
            Object codCDAWA = null;

            if (tipo.ehCDA()) {
               codCDAWA = new CodigoCDA(cod.obterConteudo());
            } else {
               codCDAWA = new CodigoWA(cod.obterConteudo());
            }

            if (tipo.ehCDA() && !cdaWaValidados.contains(codCDAWA)) {
               CodigoWA wa = ((CodigoCDA) codCDAWA).obterRespectivoWA();
               try {
                  resultado = cdaWaDao.temMesmoDetentorCDAWA(cod, CodigoTipoIF.CDA);
               } catch (Exception e) {
                  e.printStackTrace();
                  throw new RuntimeException(e);
               }
               cdaWaValidados.add(wa);
            } else if (!cdaWaValidados.contains(codCDAWA)) {
               CodigoCDA cda = ((CodigoWA) codCDAWA).obterRespectivoCDA();
               try {
                  resultado = cdaWaDao.temMesmoDetentorCDAWA(cod, CodigoTipoIF.WA);
               } catch (Exception e) {
                  e.printStackTrace();
                  throw new RuntimeException(e);
               }
               cdaWaValidados.add(cda);
            }

            if (resultado == false) {
               Erro erro = new Erro(CodigoErro.CESTA_INCOMPATIVEL);
               erro.parametroMensagem("Existem ativos CDA/WA cujo detentor nao confere", 0);
               throw erro;
            }
         }

         idAtivos.put(cod.obterConteudo(), ativo);
      }
   }

   private boolean tipoGarantidorAceito(CodigoTipoIF tipo, Id sistema) {
      Collection tipos = (Collection) mapaGarantidores.get(sistema);
      return tipos.contains(tipo);
   }

   private void montaLote(AtributosColunadosIterator atributos, Lote lote, FiltroLote filtroLote) {
      lote.clear();
      int tamLote = TAMANHO_LOTE;
      do {
         boolean addIt = filtroLote == null ? true : filtroLote.filtrar(atributos, lote);
         if (!addIt) {
            continue;
         }

         CodigoIF codigoIF = (CodigoIF) atributos.obterAtributo(CodigoIF.class, Contexto.GARANTIAS_CODIGO_IF);
         Quantidade quantidade = (Quantidade) atributos.obterAtributo(Quantidade.class, Contexto.GARANTIAS_QUANTIDADE);
         CodigoTipoIF tipo = (CodigoTipoIF) atributos.obterAtributo(CodigoTipoIF.class, Contexto.GARANTIAS_CODIGO_TIPO);

         lote.add(codigoIF);
         lote.add(tipo);
         lote.add(quantidade);

         if (--tamLote == 0) {
            break;
         }
      } while (atributos.avancarLinha());
   }

   /*
    * Realiza a validacao dos dados digitados via motor de regras.
    * 
    * @param requisicao requisicao que contem os dados para validacao
    * 
    * @return o Resultado contendo se a validacao foi aceita ou nao
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      return null;
   }

   static class Lote {

      private Map mapa = new HashMap();

      public void clear() {
         Iterator i = mapa.entrySet().iterator();
         while (i.hasNext()) {
            Map.Entry entry = (Entry) i.next();
            List l = (List) entry.getValue();
            l.clear();
         }
      }

      public boolean isEmpty() {
         Iterator i = mapa.entrySet().iterator();
         while (i.hasNext()) {
            Map.Entry entry = (Entry) i.next();
            List l = (List) entry.getValue();
            if (!l.isEmpty()) {
               return false;
            }
         }

         return true;
      }

      public void add(Atributo objeto) {
         objeto.atribuirContexto(null);
         List lista = (List) mapa.get(objeto.getClass());

         if (lista == null) {
            lista = new ArrayList(TAMANHO_LOTE);
            mapa.put(objeto.getClass(), lista);
         }

         lista.add(objeto);
      }

      public List get(Class clazz) {
         List l = (List) mapa.get(clazz);
         if (l == null) {
            l = Collections.EMPTY_LIST;
         }
         return l;
      }

   }
}
