package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.VinculacaoContratoVO;
import br.com.cetip.aplicacao.instrumentofinanceiro.servico.swap.RequisicaoServicoVinculaCestaContrato;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.servico.roteador.Roteador;

/**
 * @author brunob
 */
class ValidacaoVinculacaoSWAP extends ValidacaoVinculacaoContrato {

   public void validarAtivo(VinculacaoContratoVO vc) {
      IContratosCesta icc = getFactory().getInstanceContratosCesta();
      RequisicaoServicoVinculaCestaContrato r = (RequisicaoServicoVinculaCestaContrato) icc.construirRequisicaoSwap(vc);

      Booleano reset = r.obterRESET_Booleano();

      if (reset == null) {
         Erro erro = new Erro(CodigoErro.CESTA_REGRA_LIBERACAO_INVALIDA);
         erro.parametroMensagem(vc.ativo.getCodigoIF(), 0);
         throw erro;
      }

      // apenas valida
      r.atribuirTIPO_SERVICO_Booleano(new Booleano(Booleano.VERDADEIRO));

      Roteador.executar(r, getContextoAtivacao());
   }

   public void registrar(TiposValidacaoVinculacaoContrato f) {
      f.registrar(CodigoTipoIF.SWAP, this);
   }

}
