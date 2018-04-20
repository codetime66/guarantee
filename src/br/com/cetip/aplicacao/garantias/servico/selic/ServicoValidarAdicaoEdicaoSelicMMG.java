package br.com.cetip.aplicacao.garantias.servico.selic;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
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
 *                    
 * @requisicao.method atributo="Id"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="TIPO_COLATERAL"
 *                    
 * @requisicao.method atributo="CodigoTipoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_TIPO"
 *                    
 * @resultado.class
 * 
 */
public class ServicoValidarAdicaoEdicaoSelicMMG extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new ExcecaoServico(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoValidarAdicaoEdicaoSelicMMG req = (RequisicaoServicoValidarAdicaoEdicaoSelicMMG) requisicao;

      Id acesso = req.obterACESSO_Id();
      Id colateral = req.obterTIPO_COLATERAL_Id();
      CodigoTipoIF codTipoIF = req.obterGARANTIAS_CODIGO_TIPO_CodigoTipoIF();

      Logger.info(this, "Acesso/Colateral: " + acesso + "/" + colateral);

      Id ativo = new Id("ATIVO");
      if (acesso.mesmoConteudo(ativo)) {
         throw new Erro(CodigoErro.MMG_SELIC_CONDICAO_ACESSO_INVALIDA);
      }

      Id colCetip = new Id("CETIP");
      if (colateral.mesmoConteudo(colCetip)) {
         throw new Erro(CodigoErro.MMG_SELIC_TIPO_COLATERAL_INVALIDO);
      } else if (!Condicional.vazio(codTipoIF)) {
         Erro erro = new Erro(CodigoErro.CAMPO_NAO_DEVE_SER_PREENCHIDO);
         erro.parametroMensagem("Subclassificação de Colateral", 0);
         throw erro;
      }

      return new ResultadoServicoValidarAdicaoEdicaoSelicMMG();
   }

}
