package br.com.cetip.aplicacao.garantias.negocio;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasNaoCetip;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoRetirada;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaIFDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoCDA;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoCDAWA;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoWA;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

class ValidacaoRetiradaSTA extends BaseGarantias implements IValidacaoRetirada {

   public void registrar(TiposValidacaoRetirada f) {
      f.registrar(CodigoTipoIF.LCA, this);
      f.registrar(CodigoTipoIF.CDCA, this);
      f.registrar(CodigoTipoIF.CRA, this);
   }

   private Set irmaosCDAWAVerificados = new HashSet();

   public void validaRetirada(List idGarantias, List qtdadesARetirar, List numerosOperacao) {
      Id idPrimeiraGarantia = (Id) idGarantias.get(0);
      IGerenciadorPersistencia gp = getGp();
      CestaGarantiasDO cesta = ((DetalheGarantiaDO) gp.load(DetalheGarantiaDO.class, idPrimeiraGarantia))
            .getCestaGarantias();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Validacao de RETIRADA em Cesta vinculada a STA");
      }

      // Data de Vencimento do Ativo STA
      Object[] parametros = new Object[] { cesta.getNumIdCestaGarantias(),
            StatusCestaIFDO.VINCULADA.getNumIdStatusCesta() };

      String sql = "select max(ev.instrumentoFinanceiro.dataVencimento) from CestaGarantiasIFDO ev where ev.cestaGarantia = ? and ev.status.numIdStatusCesta = ?";

      List list = gp.find(sql, parametros);
      Data datVenc = (Data) list.get(0);
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Data Vencimento Ativo Garantido STA: " + datVenc);
      }

      boolean pulaValidacao = false;
      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      List listGarantias = icg.listarGarantiasCesta(cesta.getNumCestaGarantias());

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Total de Garantias a serem validadas: " + listGarantias.size());
      }

      verificaCDAWAaRetirar(idGarantias, qtdadesARetirar, cesta);

      Iterator garantias = listGarantias.iterator();
      while (garantias.hasNext()) {
         DetalheGarantiaDO garantia = (DetalheGarantiaDO) garantias.next();

         Id idGarantia = garantia.getNumIdDetalheGarantia();
         idGarantia = new Id(Contexto.GARANTIAS_LIBERACAO, idGarantia.obterConteudo());
         if (!idGarantias.contains(idGarantia)) {
            continue;
         }

         int indice = idGarantias.indexOf(idGarantia);
         Quantidade qtRetirar = (Quantidade) qtdadesARetirar.get(indice);
         Data vencGarantia = null;
         Quantidade qtdade = new Quantidade(garantia.getQuantidadeGarantia().obterConteudo());
         Quantidade saldo = qtdade.subtrair(qtRetirar);

         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Indice da garantia " + idGarantia + ": " + indice);
         }

         //tratamento para não cetipados
         if (garantia.getIndCetipado().ehVerdadeiro()) {
            CodigoIF codigoIF = garantia.getInstrumentoFinanceiro().getCodigoIF();
            if (Logger.estaHabilitadoDebug(this)) {
               Logger.debug(this, "Validacao de Retirada da Garantia: " + codigoIF);
            }

            vencGarantia = garantia.getInstrumentoFinanceiro().getDataVencimento();
         }

         if (qtRetirar.compareTo(qtdade) == 1) {
            throw new Erro(CodigoErro.QUANTIDADE_SUPERIOR_A_SALDO);
         }

         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Data Venc: " + vencGarantia);
            Logger.debug(this, "Quantidade a retirar: " + qtRetirar);
            Logger.debug(this, "Quantidade garantida: " + qtdade);
            Logger.debug(this, "Quantidade em saldo :" + saldo);
         }

         if (saldo.obterConteudo().compareTo(new BigDecimal("0")) <= 0) {
            if (Logger.estaHabilitadoDebug(this)) {
               Logger.debug(this, "Saldo zerou ou ficou negativo. Garantia sera retirada do set.");
            }

            garantias.remove();
         } else if (vencGarantia != null && vencGarantia.comparar(datVenc) == 1) {
            if (Logger.estaHabilitadoDebug(this)) {
               Logger
                     .debug(this, "Saldo eh positivo e data de vencimento eh superior ao do STA. Nao valida mais nada.");
            }

            pulaValidacao = true;
            break;
         }
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Total de Garantias restantes, para validar data: " + listGarantias.size());
         Logger.debug(this, "Pula validacao? " + pulaValidacao);
      }

      boolean disparaErro = false;
      if (listGarantias.isEmpty()) {
         pulaValidacao = true;
         disparaErro = true;
      }

      IGarantiasNaoCetip igNaoCetip = getFactory().getGarantiasNaoCetip();
      if (igNaoCetip.possuiSomenteNaoCetipados(cesta)) {
         pulaValidacao = true;
         disparaErro = false;
      }

      if (!pulaValidacao) {
         // Itera novamente para ver se sobrou alguma garantia com data de vencimento maior que a do ativo STA
         // garantido
         disparaErro = true;
         garantias = listGarantias.iterator();
         while (garantias.hasNext()) {
            DetalheGarantiaDO garantia = (DetalheGarantiaDO) garantias.next();

            if (garantia.getIndCetipado().ehVerdadeiro()) {
               Data vencGarantia = garantia.getInstrumentoFinanceiro().getDataVencimento();
               if (vencGarantia.comparar(datVenc) == 1) {
                  disparaErro = false;
                  break;
               }
            }
         }
      }

      if (disparaErro) {
         throw new Erro(CodigoErro.CESTA_VINCULACAO_STA_LASTRO_INVALIDO_VENCIMENTO);
      }
   }

   private void verificaCDAWAaRetirar(List idGarantias, List qtdadesARetirar, CestaGarantiasDO cesta) {
      final NumeroCestaGarantia numCesta = cesta.getNumCestaGarantias();
      final ICestaDeGarantias instanceCestaDeGarantias = getFactory().getInstanceCestaDeGarantias();
      Collection detalhes = instanceCestaDeGarantias.listarGarantiasCesta(numCesta);
      Iterator garantias = detalhes.iterator();

      while (garantias.hasNext()) {
         DetalheGarantiaDO garantia = (DetalheGarantiaDO) garantias.next();

         Id idGarantia = garantia.getNumIdDetalheGarantia();
         idGarantia = new Id(Contexto.GARANTIAS_LIBERACAO, idGarantia.obterConteudo());
         if (!idGarantias.contains(idGarantia)) {
            continue;
         }

         // CDA WA - prepara lista secundaria para tirar cda/wa
         if (garantia.getIndCetipado().ehVerdadeiro()) {
            CodigoIF codigoIF = garantia.getInstrumentoFinanceiro().getCodigoIF();

            CodigoTipoIF codigoTipoIF = garantia.getInstrumentoFinanceiro().getTipoIF().getCodigoTipoIF();
            if (codigoTipoIF.ehCDA() || codigoTipoIF.ehWA()) {
               complementaIrmaoCDAWA(codigoIF, codigoTipoIF, idGarantias, qtdadesARetirar, cesta);
            }
         }
      }
   }

   private void complementaIrmaoCDAWA(CodigoIF codigoIF, CodigoTipoIF codigoTipoIF, List idGarantias,
         List qtdadesARetirar, CestaGarantiasDO cesta) {
      if (irmaosCDAWAVerificados.contains(codigoIF)) {
         return;
      }

      String conteudoCodigoIF = codigoIF.obterConteudo();

      CodigoCDAWA cdaWA = null;
      if (codigoTipoIF.ehWA()) {
         cdaWA = new CodigoWA(conteudoCodigoIF);
      } else if (codigoTipoIF.ehCDA()) {
         cdaWA = new CodigoCDA(conteudoCodigoIF);
      } else {
         throw new IllegalArgumentException("CodigoTipoIF deve ser CDA ou WA");
      }

      CodigoIF irmaoCDAWA = cdaWA.obterCDAOuWA();

      Id idGarantiaIrmaoCDAWA = obtemIdGarantiaIrmaoCDAWA(irmaoCDAWA, cesta);
      idGarantiaIrmaoCDAWA = new Id(Contexto.GARANTIAS_LIBERACAO, idGarantiaIrmaoCDAWA.obterConteudo());
      if (!idGarantias.contains(idGarantiaIrmaoCDAWA)) {
         idGarantias.add(idGarantiaIrmaoCDAWA);
         qtdadesARetirar.add(new Quantidade("1"));

         irmaosCDAWAVerificados.add(codigoIF);
         irmaosCDAWAVerificados.add(irmaoCDAWA);
      }
   }

   private Id obtemIdGarantiaIrmaoCDAWA(CodigoIF irmaoCDAWA, CestaGarantiasDO cesta) {
      String hql = "select d.numIdDetalheGarantia from DetalheGarantiaDO d where d.cestaGarantias = ? and d.instrumentoFinanceiro.codigoIF = ?";
      List l = getGp().find(hql, new Object[] { cesta, irmaoCDAWA });

      return (Id) l.get(0);
   }
}
