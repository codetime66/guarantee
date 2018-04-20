package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IRetirarGarantia;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

class RetirarGarantiaCetip21 extends BaseGarantias implements IRetirarGarantia {

   public void retirarGarantia(DetalheGarantiaDO detalheGarantia, Quantidade quantidade,
         NumeroOperacao numerosOperacao, Booleano indBatch, Data dataOperacao, Funcao tipoAcesso) {
      IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO mov = imov.incluirMovimentacaoRetirada(detalheGarantia, quantidade);
      getFactory().getInstanceCestaDeGarantias().acionaMIG(mov, indBatch, dataOperacao);
   }

   public void registrar(TiposRetiradaGarantia tipos) {
      tipos.registrar(SistemaDO.CETIP21, this);
   }

}
