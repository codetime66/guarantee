package br.com.cetip.aplicacao.garantias.web.selic;

import br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoObterTiposIFSelicados;
import br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoValidarFiltroTiposIFSelicadosMMG;
import br.com.cetip.aplicacao.garantias.servico.selic.ResultadoServicoObterTiposIFSelicados;
import br.com.cetip.base.web.acao.Filtro;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;

public class FiltroAtivosSelicMMG extends Filtro {

   public void informarCampos(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoObterTiposIFSelicados requisicao = new RequisicaoServicoObterTiposIFSelicados();
      ResultadoServicoObterTiposIFSelicados resultado = (ResultadoServicoObterTiposIFSelicados) servicos
            .executarServico(requisicao);
      atributos.atributoObrigatorio(resultado.obterCODIGO_TIPO_IF_CodigoTipoIF());
      atributos.atributo(new CodigoIF(Contexto.CODIGO_IF));
   }

   public Class obterDestino(Grupo arg0, Servicos arg1) throws Exception {
      return RelacaoAtivosSelicMMG.class;
   }

   public void validar(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoValidarFiltroTiposIFSelicadosMMG requisicao = new RequisicaoServicoValidarFiltroTiposIFSelicadosMMG();
      servicos.chamarServico(requisicao, dados);
   }

}
