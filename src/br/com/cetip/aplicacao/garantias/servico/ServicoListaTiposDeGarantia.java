package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico que lista as operacoes disponiveis para a interface grafica de compra/venda de cda/wa.
 * 
 * @author <a href="mailto:daniel.goncalves@summa-tech.com">Daniel B. G. Goncalves</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @since Maio/2005
 * 
 * @requisicao.class
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="IdTipoGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CODIGO"
 */
public class ServicoListaTiposDeGarantia extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new UnsupportedOperationException();
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      ResultadoServicoListaTiposDeGarantia resultado = new ResultadoServicoListaTiposDeGarantia();
      resultado.atribuirGARANTIAS_CODIGO_IdTipoGarantia(new IdTipoGarantia("", ""));
      resultado.novaLinha();
      resultado.atribuirGARANTIAS_CODIGO_IdTipoGarantia(new IdTipoGarantia("PENHOR NO EMISSOR", "16"));
      resultado.novaLinha();
      resultado.atribuirGARANTIAS_CODIGO_IdTipoGarantia(new IdTipoGarantia("CESSAO FIDUCIARIA", "5"));
      return resultado;
   }

}
