package br.com.cetip.aplicacao.garantias.servico.selic;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.GarantidorCestaIFDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
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
 * @resultado.class
 *
 */
public class ServicoExclusaoSelicMMG extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      RequisicaoServicoExclusaoSelicMMG req = (RequisicaoServicoExclusaoSelicMMG) requisicao;

      CodigoIF codigoIF = req.obterINSTRUMENTO_FINANCEIRO_CodigoIF();

      IGerenciadorPersistencia gp = getGp();
      GarantidorCestaIFDO ativo = getFactory().getInstanceGarantiasSelic().obterAtivoSelicMMG(codigoIF);
      ativo.setDataExclusao(getDataHoje());
      gp.update(ativo);

      return new ResultadoServicoExclusaoSelicMMG();
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      throw new ExcecaoServico(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

}
