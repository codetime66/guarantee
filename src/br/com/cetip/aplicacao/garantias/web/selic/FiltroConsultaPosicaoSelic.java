package br.com.cetip.aplicacao.garantias.web.selic;

import br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoObterCamposFiltroPosicaoSelic;
import br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoValidarFiltroConsultaPosicaoSelic;
import br.com.cetip.aplicacao.garantias.servico.selic.ResultadoServicoObterCamposFiltroPosicaoSelic;
import br.com.cetip.base.web.acao.Filtro;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;

public class FiltroConsultaPosicaoSelic extends Filtro {

   public void informarCampos(GrupoDeAtributos layout, Grupo parametros, Servicos servicos) throws Exception {

      ResultadoServicoObterCamposFiltroPosicaoSelic res = (ResultadoServicoObterCamposFiltroPosicaoSelic) servicos
            .executarServico(new RequisicaoServicoObterCamposFiltroPosicaoSelic());

      layout.atributoObrigatorio(res.obterCONTA_CodigoContaSelic());
      layout.atributoObrigatorio(res.obterCODIGO_IF_CodigoIF());

   }

   public void validar(Grupo parametros, Servicos servicos) throws Exception {

      RequisicaoServicoValidarFiltroConsultaPosicaoSelic req = new RequisicaoServicoValidarFiltroConsultaPosicaoSelic();
      servicos.executarServico(req, parametros);

   }

   public Class obterDestino(Grupo dados, Servicos servicos) throws Exception {
      return RelacaoPosicaoSelic.class;
   }
}
