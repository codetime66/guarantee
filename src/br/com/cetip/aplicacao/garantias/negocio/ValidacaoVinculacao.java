package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.IValidaAtivoGarantidor;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoGarantidorNatEcon;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidarTipoIF;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.GarantidorCestaIFDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.persistencia.IConsulta;

class ValidacaoVinculacao extends BaseGarantias implements IValidaAtivoGarantidor {

   protected final void validarTiposGarantidoresValidos(CestaGarantiasDO cesta) {
      Set tiposGarantidoresValidos = getTiposGarantidores();

      String hql3 = "select count(*) from "
            + DetalheGarantiaDO.class.getName()
            + " d where d.instrumentoFinanceiro.tipoIF.codigoTipoIF not in (:tipos) and d.cestaGarantias = :cesta and d.quantidadeGarantia > 0";
      IConsulta c = getGp().criarConsulta(hql3);
      c.setAtributo("cesta", cesta);

      c.setParameterList("tipos", tiposGarantidoresValidos);

      int count = ((Integer) c.list().get(0)).intValue();

      if (count > 0) {
         Erro erro = new Erro(CodigoErro.CESTA_INCOMPATIVEL);
         erro.parametroMensagem("Cesta possui ativos de tipos incompativeis com o ativo garantido", 0);
         throw erro;
      }
   }

   // metodo que valida se o tipo do instrumento financeiro pode ser garantido
   protected final boolean validaTipoInstrumentoFinanceiroGarantido(InstrumentoFinanceiroDO numIF) {
      IValidarTipoIF i = getFactory().getInstanceValidarTipoIF();
      return i.validarGarantido(numIF.getTipoIF().getNumTipoIF(), numIF.getSistema().getNumero());
   }

   /**
    * Um ativo nao pode ser garantido por outra cesta
    */
   protected final boolean validaInstrumentoFinanceiroGarantido(InstrumentoFinanceiroDO numIF) {
      StringBuffer hql = new StringBuffer(500);
      hql.append("select count(*) from ");
      hql.append(CestaGarantiasIFDO.class.getName());
      hql.append(" cgi where cgi.status.numIdStatusCesta in (7,10,14) ");
      hql.append(" and cgi.instrumentoFinanceiro = :if ");
      IConsulta consVinc = getGp().criarConsulta(hql.toString());

      consVinc.setAtributo("if", numIF);
      Integer count = (Integer) consVinc.list().get(0);

      return count.intValue() == 0;
   }

   /**
    * <p>Retorna todos os tipos habilitados no banco de dados, exceto CTRA.</p>
    * 
    * <p>O tipo CTRA soh garante LCA, entao veja {@link ValidacaoVinculacaoLCA}.</p>
    * 
    * @return Set de CodigoTipoIF
    * @see ValidacaoVinculacaoLCA
    */
   protected Set getTiposGarantidores() {
      Set tiposGarantidores = new SetCodigoTipoIF();

      IValidarTipoIF ivt = getFactory().getInstanceValidarTipoIF();

      for (Iterator i = ivt.obterMapaGarantidores().values().iterator(); i.hasNext();) {
         tiposGarantidores.addAll((Collection) i.next());
      }

      /*
       * ******************************************
       * ATIVOS QUE SOH GARANTEM OUTROS ESPECIFICOS
       * ******************************************
       */
      tiposGarantidores.remove(CodigoTipoIF.CTRA);
      tiposGarantidores.remove(CodigoTipoIF.CDCA);
      tiposGarantidores.remove(CodigoTipoIF.LCA);
      tiposGarantidores.remove(CodigoTipoIF.CRA);

      return tiposGarantidores;
   }

   protected final void complementaTiposGarantidoresSelic(Set tiposGarantidores) {
      StringBuffer hql = new StringBuffer(500);
      hql.append("select distinct tipoIF.codigoTipoIF from ");
      hql.append(GarantidorCestaIFDO.class.getName());
      hql.append(" d inner join d.instrumentoFinanceiro ativo inner join d.instrumentoFinanceiro.tipoIF tipoIF ");
      hql.append(" where d.dataExclusao is null ");

      List l = getGp().find(hql.toString());

      Iterator i = l.iterator();
      while (i.hasNext()) {
         CodigoTipoIF tipoIF = (CodigoTipoIF) i.next();
         tiposGarantidores.add(tipoIF);
      }
   }

   protected final void validaNaturezaGarantido(CestaGarantiasDO cesta) {
      StringBuffer hql = new StringBuffer(500);
      hql.append("select distinct tipoIF.codigoTipoIF from ");
      hql.append(DetalheGarantiaDO.class.getName());
      hql.append(" d inner join d.instrumentoFinanceiro ativo inner join d.instrumentoFinanceiro.tipoIF tipoIF where ");
      hql.append(" d.quantidadeGarantia > 0 and d.cestaGarantias = ? ");

      List l = getGp().find(hql.toString(), new Object[] { cesta });

      Iterator i = l.iterator();
      while (i.hasNext()) {
         CodigoTipoIF tipoIF = (CodigoTipoIF) i.next();
         IValidacaoGarantidorNatEcon ivgne = getFactory().getInstanceValidacaoGarantidorNatEcon(tipoIF);

         // se tipo do ativo garantidor possui validacao de natureza economica
         // entao ... valida ueh
         if (ivgne != null) {
            ivgne.validarNaturezaGarantido(cesta);
         }
      }
   }

   public final void validar(CestaGarantiasDO cesta, InstrumentoFinanceiroDO garantidor) {
      Set tiposGarantidores = getTiposGarantidores();
      CodigoTipoIF tipoIF = garantidor.getTipoIF().getCodigoTipoIF();

      if (tiposGarantidores.contains(tipoIF) == false) {
         throw new Erro(CodigoErro.CESTA_TIPO_IF_INVALIDO);
      }
   }

}
