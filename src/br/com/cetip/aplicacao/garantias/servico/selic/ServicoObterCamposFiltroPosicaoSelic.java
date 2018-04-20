package br.com.cetip.aplicacao.garantias.servico.selic;

import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 *
 * @resultado.class
 * 
 * @resultado.method
 *     atributo="CodigoContaSelic"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CONTA"
 * 
 * @resultado.method
 *     atributo="CodigoIF"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CODIGO_IF"
 * 
 */

public class ServicoObterCamposFiltroPosicaoSelic implements Servico {

   public Resultado executar(Requisicao arg0) throws Exception {
      // TODO Auto-generated method stub
      return null;
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {

      ResultadoServicoObterCamposFiltroPosicaoSelic res = new ResultadoServicoObterCamposFiltroPosicaoSelic();

      res.novaLinha();
      res.atribuirAtributo(new CodigoContaSelic(Contexto.CONTA));
      res.atribuirAtributo(new CodigoIF(Contexto.CODIGO_IF));

      return res;
   }

}
