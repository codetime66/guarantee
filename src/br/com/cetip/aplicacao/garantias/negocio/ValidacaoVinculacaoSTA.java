package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasNaoCetip;
import br.com.cetip.aplicacao.garantias.apinegocio.ITipoGarantiaCesta;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaVinculacaoCestaGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.IInstrumentoFinanceiro;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.ITipoIF;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.TipoIFFactory;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.CreditoDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoCreditoDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.agronegocio.AgronegocioSTADO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;

/**
 * Valida a vinculacao de uma cesta de Garantias a um ativo STA (CDCA, CRA e LCA)
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
class ValidacaoVinculacaoSTA extends ValidacaoVinculacaoTitulo {

   public void validar(RequisicaoServicoValidaVinculacaoCestaGarantias req) {
      super.validar(req);

      CodigoIF codigoIF = req.obterGARANTIAS_CODIGO_IF_CodigoIF();
      NumeroCestaGarantia numero = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      Id idCesta = numero.copiarParaId();

      // Validando os itens q compoem a cesta no caso do item garantido ser um STA
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "STA>>> Vinculacao de Cesta a um STA ***");
         Logger.debug(this, "Cesta Garantias: " + numero);
         Logger.debug(this, "Ativo Garantido: " + codigoIF);
         Logger.debug(this, "STA>>> Validando ativos q compoem a cesta ***");
      }

      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = icg.obterCestaDeGarantias(numero);
      boolean ehMultiplaVinculacao = cesta.getStatusCesta().equals(StatusCestaDO.VINCULADA);

      IInstrumentoFinanceiro iif = InstrumentoFinanceiroFactory.getInstance();
      InstrumentoFinanceiroDO ifGarantido = iif.obterInstrumentoFinanceiro(codigoIF);

      Set tipoAceitos = ehMultiplaVinculacao ? getTiposGarantidoresVinculacaoMultipla() : getTiposGarantidores();

      validaTiposIFValidos(idCesta, ifGarantido.getTipoIF().getCodigoTipoIF(), tipoAceitos);

      validaTiposAgricolasValidos(idCesta, ifGarantido.getTipoIF().getCodigoTipoIF());

      validaMesmoTipoGarantia(cesta, ((AgronegocioSTADO) ifGarantido).getGarantia().getTipoGarantia()
            .getNumIdTipoGarantia());

      validaDataVencimento(cesta, ifGarantido.getDataVencimento());

      if (ehMultiplaVinculacao) {
         validaMultiplaVinculacao(cesta, ifGarantido.getTipoIF().getCodigoTipoIF());
      }
   }

   private void validaDataVencimento(CestaGarantiasDO cesta, Data dataVencGarantido) {
      String hql2 = "select count(*) from "
            + DetalheGarantiaDO.class.getName()
            + " d where d.instrumentoFinanceiro.dataVencimento > ? and d.cestaGarantias = ? and d.quantidadeGarantia > 0";
      Integer count = (Integer) getGp().find(hql2, new Object[] { dataVencGarantido, cesta }).get(0);

      IGarantiasNaoCetip igNaoCetip = getFactory().getGarantiasNaoCetip();
      boolean vencSuperior = count.intValue() > 0 || igNaoCetip.possuiSomenteNaoCetipados(cesta);
      if (!vencSuperior) {
         throw new Erro(CodigoErro.CESTA_VINCULACAO_STA_LASTRO_INVALIDO_VENCIMENTO);
      }
   }

   private void validaMesmoTipoGarantia(CestaGarantiasDO cesta, IdTipoGarantia tipoGarantiaAtivoGarantido) {
      if (tipoGarantiaAtivoGarantido.mesmoConteudo(cesta.getTipoGarantia().getNumIdTipoGarantia()) == false) {
         throw new Erro(CodigoErro.CESTA_TIPO_GARANTIA_DIFERENTE);
      }
   }

   private void validaTiposAgricolasValidos(Id idCesta, CodigoTipoIF tipoIFGarantido) {
      String hql4 = "select count(*) from "
            + CreditoDO.class.getName()
            + " c where c.instrumentoFinanceiro in (select d.instrumentoFinanceiro from "
            + DetalheGarantiaDO.class.getName()
            + " d where d.instrumentoFinanceiro.tipoIF.codigoTipoIF in (:tipos) and d.cestaGarantias.numIdCestaGarantias = :cesta and d.quantidadeGarantia > 0) and c.tipoCredito.numIdTipoCredito <> :credito)";
      IConsulta c = getGp().criarConsulta(hql4);
      c.setAtributo("cesta", idCesta);
      c.setAtributo("credito", TipoCreditoDO.OPERACAO_CREDITO_AGRONEGOCIO);
      c.setParameterList("tipos", getTiposGarantidoresCreditoAgricola());

      Integer count = (Integer) c.list().get(0);

      if (count.intValue() > 0) {
         Erro erro = new Erro(CodigoErro.CESTA_VINCULACAO_STA_LASTRO_INVALIDO);
         erro.parametroMensagem(tipoIFGarantido, 0);
         throw erro;
      }
   }

   private void validaTiposIFValidos(Id idCesta, CodigoTipoIF tipoIFGarantido, Set tipos) {
      String hql3 = "select count(*) from "
            + DetalheGarantiaDO.class.getName()
            + " d where d.instrumentoFinanceiro.tipoIF.codigoTipoIF not in (:tipos) and d.cestaGarantias.numIdCestaGarantias = :cesta and d.quantidadeGarantia > 0";
      IConsulta c = getGp().criarConsulta(hql3);
      c.setAtributo("cesta", idCesta);

      c.setParameterList("tipos", tipos);
      c.setMaxResults(1);

      Integer count = (Integer) c.list().get(0);
      if (count.intValue() > 0) {
         Erro erro = new Erro(CodigoErro.CESTA_VINCULACAO_STA_LASTRO_INVALIDO);
         erro.parametroMensagem(tipoIFGarantido, 0);
         throw erro;
      }
   }

   private void validaMultiplaVinculacao(CestaGarantiasDO cesta, CodigoTipoIF codigoTipoIF) {
      // A vinculacao multipla deve ocorrer com ativos do mesmo tipo
      // basta comparar com o primeiro ativo vinculado
      Iterator i = cesta.getAtivosVinculados().iterator();
      CestaGarantiasIFDO cestaIF = (CestaGarantiasIFDO) i.next();
      if (!cestaIF.getInstrumentoFinanceiro().getTipoIF().getCodigoTipoIF().mesmoConteudo(codigoTipoIF)) {
         throw new Erro(CodigoErro.CESTA_VINCULACAO_MULTIPLA_DEVE_OCORRE_COM_MESMOS_TIPOS_IF);
      }

      // Valida Tipo de Garantia da Cesta
      ITipoGarantiaCesta tgc = getFactory().getInstanceTipoGarantiaCesta();
      IdTipoGarantia idTipoGarantia = tgc.obterTipoGarantia(cesta);

      Set s = getTiposGarantiasVincMultipla();
      if (!s.contains(idTipoGarantia)) {
         throw new Erro(CodigoErro.CESTA_VINCULACAO_MULTIPLA_DEVE_SER_PENHOR_EMISSOR);
      }
   }

   public void registrar(TiposValidacaoVinculacaoTitulo f) {
      f.registrar(CodigoTipoIF.CDCA, this);
      f.registrar(CodigoTipoIF.CRA, this);
   }

   /**
    * Os tipos que podem compor cestas vinculadas a mais de um ativo
    * @return
    */
   protected Set getTiposGarantidoresVinculacaoMultipla() {
      // alguns tipos da vinc. simples nao sao permitidos para 
      // compor cestas que terao multiplas vinculacoes
      Set garantidoresSimples = getTiposGarantidores();

      garantidoresSimples.remove(CodigoTipoIF.LCA);
      garantidoresSimples.remove(CodigoTipoIF.CRA);

      return garantidoresSimples;
   }

   /**
    * Tipos de Ativos comuns que podem garantir ativos STA (LCA, CDCA e CRA)
    */
   protected Set getTiposGarantidores() {
      Set garantidores = super.getTiposGarantidores();

      garantidores.remove(new CodigoTipoIF("CSEC"));
      garantidores.remove(new CodigoTipoIF("TDA"));
      garantidores.remove(CodigoTipoIF.DEB);
      garantidores.remove(CodigoTipoIF.CDB);
      garantidores.remove(CodigoTipoIF.DI);

      //remove os codigoIF Selic
      ITipoIF t = TipoIFFactory.getInstance();
      List l = t.obterTiposSelicados();

      for (Iterator i = l.iterator(); i.hasNext();) {
         TipoIFDO tdo = (TipoIFDO) i.next();
         CodigoTipoIF codTipoIF = tdo.getCodigoTipoIF();
         garantidores.remove(codTipoIF);
      }

      // LCA/CRA nao garantem ativos STA (BL 4539)
      garantidores.remove(CodigoTipoIF.LCA);
      garantidores.remove(CodigoTipoIF.CRA);

      // CDCA pode garantir CDCA
      garantidores.add(CodigoTipoIF.CDCA);

      return garantidores;
   }

   /**
    * <p>Tipos de Ativos que nao sao naturalmente agricolas, 
    * mas possuem tipo de credito agricola, 
    * e por isso, podem compor cestas garantidoras de STAs</p>
    * @return
    */
   protected Set getTiposGarantidoresCreditoAgricola() {
      Set garantidores = new HashSet(1);

      garantidores.add(CodigoTipoIF.CCB);

      return garantidores;
   }

   /**
    * Tipos de Garantias que permitem vincular uma cesta a mais de um ativo
    * @return
    */
   protected Set getTiposGarantiasVincMultipla() {
      Set garantidores = new HashSet();

      garantidores.add(IdTipoGarantia.PENHOR_NO_EMISSOR);

      return garantidores;
   }

}
