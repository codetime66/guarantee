package br.com.cetip.aplicacao.garantias.web;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaHistoricoCesta;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaStatusTiposMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaAcaoCesta;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoListaStatusTiposMovimentacoesGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.utilitario.Condicional;

/**
 * Formulario para consulta de movimentacoes.
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class FormularioConsultaHistoricoCesta extends AbstractFormularioGarantias {

   private static final Id BRANCO = new Id("           ", "");

   public void entrada(GrupoDeGrupos layout, Grupo parametros, Servicos servicos) throws Exception {

      layout.contexto(Contexto.GARANTIAS_MOVIMENTACAO);
      GrupoDeGrupos grupoDados = layout.grupoDeGrupos(1);
      GrupoDeAtributos principal = grupoDados.grupoDeAtributos(1);
      principal.atributoObrigatorio(new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO));

      ResultadoServicoListaStatusTiposMovimentacoesGarantias res;
      res = (ResultadoServicoListaStatusTiposMovimentacoesGarantias) servicos
            .executarServico(new RequisicaoServicoListaStatusTiposMovimentacoesGarantias());

      List listaStatus = res.obterListaGARANTIAS_STATUS_MOV_Id();
      principal.atributo(criaCombo(Contexto.GARANTIAS_STATUS_MOV, listaStatus));

      List listaTipos = res.obterListaGARANTIAS_TIPO_MOV_Id();
      principal.atributo(criaCombo(Contexto.GARANTIAS_TIPO_MOV, listaTipos));
   }

   private Atributo criaCombo(Contexto ctx, List lista) {
      Id ids = new Id(ctx);
      ids.getDomain().add(BRANCO);

      for (Iterator it = lista.iterator(); it.hasNext();) {
         Id _campo = (Id) it.next();
         if (!Condicional.vazio(_campo)) {
            ids.getDomain().add(_campo);
         }
      }

      return ids;
   }

   public void validar(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoValidaAcaoCesta req;
      req = new RequisicaoServicoValidaAcaoCesta();

      req.atribuirACAO_Funcao(ICestaDeGarantias.CONSULTAR_HISTORICO);
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) dados.obterAtributo(
            NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO));

      servicos.executarServico(req);
   }

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      NumeroCestaGarantia numero = null;
      numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);

      Id tipo = null;
      tipo = (Id) dados.obterAtributo(Id.class, Contexto.GARANTIAS_TIPO_MOV);

      Id status = null;
      status = (Id) dados.obterAtributo(Id.class, Contexto.GARANTIAS_STATUS_MOV);

      RequisicaoServicoListaHistoricoCesta req = new RequisicaoServicoListaHistoricoCesta();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
      req.atribuirGARANTIAS_TIPO_MOV_Id(tipo);
      req.atribuirGARANTIAS_STATUS_MOV_Id(status);

      servicos.chamarServico(req);

      return null;
   }

   public Class obterDestino(Grupo parametros, Servicos servicos) throws Exception {
      return RelacaoHistoricoCesta.class;
   }

}
