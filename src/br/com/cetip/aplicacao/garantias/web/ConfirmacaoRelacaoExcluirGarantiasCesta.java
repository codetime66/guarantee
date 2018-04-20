package br.com.cetip.aplicacao.garantias.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoExcluirGarantiasCesta;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.tipo.web.Tabela;

/**
 * Tela de Confirmacao para Manutencao de Garantias
 * 
 * A solicitacao da construcao desta tela eh para confirmar apenas exclusao de garantias.
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ConfirmacaoRelacaoExcluirGarantiasCesta extends AbstractFormularioGarantias {

   private Tabela tabela;

   private List parametros;

   public boolean confirmacao(Grupo grupo, Servicos servicos) throws Exception {
      return true;
   }

   /**
    * Monta tela de confirmacao
    */
   public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      GrupoDeGrupos grupo = layout.grupoDeGrupos(1);
      GrupoDeAtributos grupo1 = grupo.grupoDeAtributos(1);

      RelacaoExcluirGarantiasDeCesta tela = (RelacaoExcluirGarantiasDeCesta) servicos
            .obterTela(RelacaoExcluirGarantiasDeCesta.class);
      tabela = tela.obterTabela();
      parametros = tela.obterParametros();
      grupo1.atributoNaoEditavel(tabela);
   }

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoExcluirGarantiasCesta req = new RequisicaoServicoExcluirGarantiasCesta();
      Iterator ac = parametros.iterator();

      while (ac.hasNext()) {
         Map params = (Map) ac.next();
         Id idGarantia = (Id) params.get(Id.class);
         Texto txtGarantia = (Texto) params.get(Texto.class);
         req.atribuirGARANTIAS_ITENS_Id(idGarantia);
         req.atribuirGARANTIAS_ITENS_Texto(txtGarantia);
      }

      servicos.chamarServico(req);
      Notificacao n = new Notificacao("ExcluirGarantiasCesta.Sucesso");

      return n;
   }

}