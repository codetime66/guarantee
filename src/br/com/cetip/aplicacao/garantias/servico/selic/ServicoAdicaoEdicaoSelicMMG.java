package br.com.cetip.aplicacao.garantias.servico.selic;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.TipoIFFactory;
import br.com.cetip.dados.aplicacao.garantias.GarantidorCestaIFDO;
import br.com.cetip.dados.aplicacao.sca.UsuarioDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.contexto.ContextoAtivacao;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * @requisicao.class
 *
 * @requisicao.method atributo="CodigoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="INSTRUMENTO_FINANCEIRO"
 *
 * @requisicao.method atributo="Id"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="ACESSO"
 * @requisicao.method atributo="Id"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="TIPO_COLATERAL"
 *
 * @requisicao.method atributo="CodigoTipoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_TIPO"
 * @resultado.class
 *
 */
public class ServicoAdicaoEdicaoSelicMMG extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      RequisicaoServicoAdicaoEdicaoSelicMMG req = (RequisicaoServicoAdicaoEdicaoSelicMMG) requisicao;

      Data data = getDataHoje();
      CodigoIF codigoIF = req.obterINSTRUMENTO_FINANCEIRO_CodigoIF();
      Booleano indAtivo = new Booleano(req.obterACESSO_Id().obterConteudo().equals("ATIVO") ? Booleano.VERDADEIRO
            : Booleano.FALSO);
      CodigoTipoIF codigoTipoIF = req.obterGARANTIAS_CODIGO_TIPO_CodigoTipoIF();

      GarantidorCestaIFDO selic = null;

      selic = getFactory().getInstanceGarantiasSelic().obterQualquerAtivoSelicMMG(codigoIF);
      if (selic != null) {
         selic.setDataAlteracao(data);
      } else {
         selic = new GarantidorCestaIFDO();
         selic.setDataInclusao(data);
         selic
               .setInstrumentoFinanceiro(InstrumentoFinanceiroFactory.getInstance()
                     .obterInstrumentoFinanceiro(codigoIF));
      }
      selic.setUsuarioAtualiz((UsuarioDO) getGp().load(UsuarioDO.class,
            new Id(ContextoAtivacao.getContexto().getIdUsuario().toString())));

      selic.setIndAtivo(indAtivo);
      selic.setDataExclusao(null);
      selic.setTipoColateral(req.obterTIPO_COLATERAL_Id());
      selic.setTipoIF(Condicional.vazio(codigoTipoIF) ? null : TipoIFFactory.getInstance().obterTipo(codigoTipoIF));

      IGerenciadorPersistencia gp = getGp();
      gp.saveOrUpdate(selic);

      return new ResultadoServicoAdicaoEdicaoSelicMMG();
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      throw new ExcecaoServico(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

}
