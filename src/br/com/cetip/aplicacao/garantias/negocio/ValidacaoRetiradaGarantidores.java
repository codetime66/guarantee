package br.com.cetip.aplicacao.garantias.negocio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoRetirada;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoRetiradaGarantia;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

class ValidacaoRetiradaGarantidores extends BaseGarantias implements IValidacaoRetirada {

   public void registrar(TiposValidacaoRetirada f) {
      f.registrar(null, this);
   }

   public void validaRetirada(List idGarantias, List qtdadesARetirar, List numerosOperacao) {

      Id idPrimeiraGarantia = (Id) idGarantias.get(0);
      IGerenciadorPersistencia gp = getGp();
      CestaGarantiasDO cesta = ((DetalheGarantiaDO) gp.load(DetalheGarantiaDO.class, idPrimeiraGarantia))
            .getCestaGarantias();

      List listGarantias = getFactory().getInstanceCestaDeGarantias()
            .listarGarantiasCesta(cesta.getNumCestaGarantias());
      List setGarantias = new ArrayList(listGarantias);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Total de Garantias a serem validadas: " + setGarantias.size());
      }

      Iterator garantias = setGarantias.iterator();
      while (garantias.hasNext()) {
         DetalheGarantiaDO garantia = (DetalheGarantiaDO) garantias.next();

         Id idGarantia = garantia.getNumIdDetalheGarantia();
         idGarantia = new Id(Contexto.GARANTIAS_LIBERACAO, idGarantia.obterConteudo());
         if (!idGarantias.contains(idGarantia)) {
            continue;
         }

         int indice = idGarantias.indexOf(idGarantia);

         Quantidade qtde = (Quantidade) qtdadesARetirar.get(indice);
         Quantidade q = garantia.getQuantidadeGarantia();
         if (qtde.compareTo(q) == 1) {
            throw new Erro(CodigoErro.QUANTIDADE_SUPERIOR_A_SALDO);
         }

         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Indice da garantia " + idGarantia + ": " + indice);
         }

         NumeroOperacao nuOp = (NumeroOperacao) ((numerosOperacao.size() > indice) ? numerosOperacao.get(indice) : null);
         boolean nuOpValidoNaRelacao = Condicional.vazio(nuOp) ? true : ehValidoNuOpNaRelacao(nuOp, indice,
               numerosOperacao);

         if (garantia.ehGarantiaCetipada()) {
            if (nuOpValidoNaRelacao) {
               InstrumentoFinanceiroDO ativoGarantia = garantia.getInstrumentoFinanceiro();
               IValidacaoRetiradaGarantia rg = getFactory().getInstanceValidacaoRetiradaGarantia(
                     ativoGarantia.getSistema().getNumero());
               rg.validaRetirada(cesta.getNumIdCestaGarantias(), ativoGarantia.getId(), qtde, nuOp);
            } else {
               throw new Erro(CodigoErro.CESTA_OPERACAO_RETIRADA_INVALIDA);
            }
         } else {
            Logger.info("RETIRADA DE GARANTIA NAO CETIPADA PARA [" + garantia.getCodIfNCetipado() + "]");
         }

      }
   }

   private boolean ehValidoNuOpNaRelacao(NumeroOperacao numero, int indice, List numeros) {
      for (int i = 0; i < numeros.size(); i++) {
         if (i == indice) {
            continue;
         }
         NumeroOperacao numeroOperacao = (NumeroOperacao) numeros.get(i);
         if (numeroOperacao != null && numeroOperacao.obterConteudo() != null && numero.mesmoConteudo(numeroOperacao)) {
            return false;
         }
      }

      return true;
   }

}
