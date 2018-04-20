package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.GarantiaVO;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IRetirarGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IRetirarGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoCDA;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoCDAWA;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoWA;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

class RetirarGarantias extends BaseGarantias implements IRetirarGarantias {

   private Object obtemDaLista(List lista, int indice) {
      Object obj = null;

      if (lista != null && lista.size() > indice) {
         obj = lista.get(indice);
      }

      return obj;
   }

   public void retirarGarantias(List idGarantias, List quantidades, List numerosOperacao, Booleano indBatch,
         Data dataOperacao, Funcao tipoAcesso) {

      DetalheGarantiaDO ultimaGarantia = null;
      Set setCDAWA = new HashSet();
      for (int i = 0; i < idGarantias.size(); i++) {
         Quantidade quantidade = (Quantidade) obtemDaLista(quantidades, i);
         NumeroOperacao numeroOperacao = (NumeroOperacao) obtemDaLista(numerosOperacao, i);
         Id idGarantia = (Id) obtemDaLista(idGarantias, i);
         ultimaGarantia = (DetalheGarantiaDO) getGp().load(DetalheGarantiaDO.class, idGarantia);

         armazenaCDAWA(setCDAWA, ultimaGarantia);
         retirar(ultimaGarantia, quantidade, numeroOperacao, indBatch, dataOperacao, tipoAcesso);
      }

      if (ultimaGarantia == null) {
         return;
      }

      CestaGarantiasDO cesta = ultimaGarantia.getCestaGarantias();
      retiraRespectivosCDAWA(indBatch, dataOperacao, tipoAcesso, cesta, setCDAWA);

      // Insere as movimentacoes de retirada
      getGp().flush();

      getFactory().getInstanceCestaDeGarantias().verificaNecessidadeDesvincularCesta(cesta);
   }

   private CestaGarantiasDO retiraRespectivosCDAWA(Booleano indBatch, Data dataOperacao, Funcao tipoAcesso,
         CestaGarantiasDO cesta, Set setCDAWA) {

      Set respectivosARetirar = new HashSet();
      Iterator i = setCDAWA.iterator();
      while (i.hasNext()) {
         CodigoCDAWA cdawa = (CodigoCDAWA) i.next();
         CodigoIF respectivo = cdawa.obterCDAOuWA();

         if (setCDAWA.contains(cdawa) && !setCDAWA.contains(respectivo)) {
            respectivosARetirar.add(respectivo);
         }
      }

      ICestaDeGarantias c = getFactory().getInstanceCestaDeGarantias();
      Iterator iCDAWA = respectivosARetirar.iterator();
      while (iCDAWA.hasNext()) {
         GarantiaVO vo = new GarantiaVO();
         vo.codIF = (CodigoIF) iCDAWA.next();
         DetalheGarantiaDO garantia = c.obterGarantiaCesta(cesta, vo);
         retirar(garantia, garantia.getQuantidadeGarantia(), null, indBatch, dataOperacao, tipoAcesso);
      }
      return cesta;
   }

   private void armazenaCDAWA(Set setCDAWA, DetalheGarantiaDO ultimaGarantia) {
      if (ultimaGarantia.getIndCetipado().ehVerdadeiro()) {
         InstrumentoFinanceiroDO ativo = ultimaGarantia.getInstrumentoFinanceiro();
         CodigoTipoIF codigoTipoIF = ativo.getTipoIF().getCodigoTipoIF();

         String conteudoCodigoIF = ativo.getCodigoIF().obterConteudo();

         if (codigoTipoIF.ehCDA()) {
            setCDAWA.add(new CodigoCDA(conteudoCodigoIF));
         } else if (codigoTipoIF.ehWA()) {
            setCDAWA.add(new CodigoWA(conteudoCodigoIF));
         }
      }
   }

   /*
    * (non-Javadoc)
    * @see br.com.cetip.aplicacao.garantias.apinegocio.IRetirarGarantias#retirarGarantiasPorDesvinculacao(java.util.Collection, br.com.cetip.infra.atributo.tipo.expressao.Booleano, br.com.cetip.infra.atributo.tipo.tempo.Data)
    */
   public void retirarGarantiasPorDesvinculacao(CestaGarantiasDO cesta, Booleano indBatch, Data dataOperacao) {
      final NumeroCestaGarantia numCesta = cesta.getNumCestaGarantias();
      final List garantias = getFactory().getInstanceCestaDeGarantias().listarGarantiasCesta(numCesta);

      Iterator i = garantias.iterator();
      while (i.hasNext()) {
         DetalheGarantiaDO garantia = (DetalheGarantiaDO) i.next();
         Quantidade quantidade = garantia.getQuantidadeGarantia();

         retirar(garantia, quantidade, null, indBatch, dataOperacao, null);
      }

      // Insere as movimentacoes de retirada
      getGp().flush();

      // pode ter somente garantia nao cetipada, entao verifica se pode marcar como desvinculada
      getFactory().getInstanceCestaDeGarantias().verificaNecessidadeDesvincularCesta(cesta);
   }

   private void retirar(DetalheGarantiaDO garantia, Quantidade qtd, NumeroOperacao numero, Booleano indBatch,
         Data dataOperacao, Funcao tipoAcesso) {
      Id numSistema = null; // pode ser garantia nao cetipada - nesse caso sistema eh nulo
      if (garantia.getInstrumentoFinanceiro() != null) {
         numSistema = garantia.getInstrumentoFinanceiro().getSistema().getNumero();
      }

      IRetirarGarantia rg = getFactory().getInstanceRetirarGarantia(numSistema);
      rg.retirarGarantia(garantia, qtd, numero, indBatch, dataOperacao, tipoAcesso);
   }

}
