package br.com.cetip.aplicacao.garantias.negocio.colateral;

import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.colateral.IAutorizacaoPublicGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.AutorizacaoPublicGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.SituacaoAutorizPublicGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.TipoParametroPublicGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ParametroPontaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.IConsulta;

/**
 * @author debora
 */
public class AutorizacaoPublicGarantias extends BaseGarantias implements IAutorizacaoPublicGarantias {

   public void incluirAutorizacaoPublicGarantias(ParametroPontaDO paramPonta) {
      AutorizacaoPublicGarantiasDO autDO = obterAutorizacaoPublicGarantias(paramPonta);

      if (!Condicional.vazio(autDO)) {
         autDO.setSituacao(SituacaoAutorizPublicGarantiasDO.INATIVO);
         autDO.setDataAlteracao(getDataHoje());
      } else {
         //cria uma nova autorizacao
         autDO = new AutorizacaoPublicGarantiasDO();

         autDO.setDataAlteracao(getDataHoje());
         autDO.setDataInclusao(getDataHoje());
         autDO.setUsuarioAtualiz(new Id(getContextoAtivacao().getIdUsuario().toString()));
         autDO.setParametroPonta(paramPonta);
         autDO.setSituacao(SituacaoAutorizPublicGarantiasDO.INATIVO);
      }

      getGp().saveOrUpdate(autDO);
   }

   public AutorizacaoPublicGarantiasDO obterAutorizacaoPublicGarantias(ParametroPontaDO paramPonta) {
      AutorizacaoPublicGarantiasDO autPublicDO = null;

      Object[] parametros = new Object[] { paramPonta };

      String sql = "from AutorizacaoPublicGarantiasDO aut where aut.parametroPonta = ? ";
      List param = getGp().find(sql, parametros);

      if (!param.isEmpty()) {
         autPublicDO = (AutorizacaoPublicGarantiasDO) param.get(0);
      }

      return autPublicDO;
   }

   public TipoParametroPublicGarantiasDO obterParametroPublicAtivo() {
      String sql = "from TipoParametroPublicGarantiasDO p where p.indAtivo = :ativa ";
      IConsulta c = getGp().criarConsulta(sql);
      c.setAtributo("ativa", Booleano.VERDADEIRO);
      c.setCacheable(true);
      c.setCacheRegion("MMG");
      List param = c.list();

      TipoParametroPublicGarantiasDO retorno = null;

      if (param.isEmpty()) {
         throw new Erro(CodigoErro.PARAMETRO_ATIVO_INEXISTENTE);
      } else if (param.size() > 1) {
         throw new Erro(CodigoErro.ENCONTRADO_MAIS_DE_UM_PARAMETRO_ATIVO);
      } else {
         retorno = (TipoParametroPublicGarantiasDO) param.get(0);
      }

      return retorno;
   }

   public List obterAutorizacoesInativasPorContrato(CodigoIF codigoContrato) {
      SituacaoAutorizPublicGarantiasDO[] codTipoSitAut = { SituacaoAutorizPublicGarantiasDO.INATIVO };

      String query = "from AutorizacaoPublicGarantiasDO aut " + "where aut.parametroPonta.contrato.codigoIF = :codIf "
            + "and aut.situacao in (:codTipoSituacao) ";

      IConsulta consulta = getGp().criarConsulta(query);
      consulta.setAtributo("codIf", codigoContrato);
      consulta.setParameterList("codTipoSituacao", codTipoSitAut);

      return consulta.list();

   }

   public List obterAutorizacaoPendentePorContrato(CodigoIF codigoContrato) {
      SituacaoAutorizPublicGarantiasDO[] codTipoSitAut = {
            SituacaoAutorizPublicGarantiasDO.PENDENTE_CONFIRMACAO_GARANTIDO,
            SituacaoAutorizPublicGarantiasDO.PENDENTE_CONFIRMACAO_GARANTIDOR };

      String query = "from AutorizacaoPublicGarantiasDO aut " + "where aut.parametroPonta.contrato.codigoIF = :codIf "
            + "and aut.situacao in (:codTipoSituacao) ";

      IConsulta consulta = getGp().criarConsulta(query);
      consulta.setAtributo("codIf", codigoContrato);
      consulta.setParameterList("codTipoSituacao", codTipoSitAut);

      return consulta.list();
   }
}
