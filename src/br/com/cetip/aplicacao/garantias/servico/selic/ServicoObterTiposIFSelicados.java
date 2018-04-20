package br.com.cetip.aplicacao.garantias.servico.selic;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.ITipoIF;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.TipoIFFactory;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.data.element.DataElement.Domain;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoTipoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CODIGO_TIPO_IF"
 */
public class ServicoObterTiposIFSelicados extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao arg0) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {

      ResultadoServicoObterTiposIFSelicados res = new ResultadoServicoObterTiposIFSelicados();

      ITipoIF tipoIF = TipoIFFactory.getInstance();
      List listaTipoIFSelic = tipoIF.obterTiposSelicados();

      CodigoTipoIF cod = new CodigoTipoIF();
      Domain domain = cod.getDomain();
      Iterator i = listaTipoIFSelic.iterator();
      while (i.hasNext()) {
         domain.add(new CodigoTipoIF(((TipoIFDO) i.next()).getCodigoTipoIF().toString()));
      }
      res.atribuirCODIGO_TIPO_IF_CodigoTipoIF(cod);

      return res;
   }
}
