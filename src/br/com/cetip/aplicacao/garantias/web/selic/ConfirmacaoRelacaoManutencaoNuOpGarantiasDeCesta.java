package br.com.cetip.aplicacao.garantias.web.selic;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoAlterarNuOpsGarantiasSelic;
import br.com.cetip.base.web.acao.Formulario;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.web.Tabela;
import br.com.cetip.infra.log.Logger;

public class ConfirmacaoRelacaoManutencaoNuOpGarantiasDeCesta extends Formulario {

   private Tabela tabela;

   private List listaItens;

   private boolean somenteValida;

   public void entrada(GrupoDeGrupos layout, Grupo parametros, Servicos servicos) throws Exception {
   }

   public void validar(Grupo dados, Servicos servicos) throws Exception {
      somenteValida = true;

      try {
         chamarServico(dados, servicos);
      } finally {
         somenteValida = false;
      }
   }

   public boolean confirmacao(Grupo parametros, Servicos servicos) throws Exception {
      return true;
   }

   private void getDadosTelaAnterior(Servicos servicos) throws Exception {
      RelacaoManutencaoNumeroOperacaoGarantiasDeCesta tela = ((RelacaoManutencaoNumeroOperacaoGarantiasDeCesta) servicos
            .obterTela(RelacaoManutencaoNumeroOperacaoGarantiasDeCesta.class));
      tabela = tela.obterTabela();
      listaItens = tela.obterListaItens();
   }

   /**
    * Monta tela de confirmacao
    */
   public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
      GrupoDeGrupos grupo = layout.grupoDeGrupos(1);
      GrupoDeAtributos grupo1 = grupo.grupoDeAtributos(1);
      getDadosTelaAnterior(servicos);
      grupo1.atributoNaoEditavel(tabela);
   }

   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      getDadosTelaAnterior(servicos);
      RequisicaoServicoAlterarNuOpsGarantiasSelic req = new RequisicaoServicoAlterarNuOpsGarantiasSelic();

      AtributosColunados ac = tabela.obterAtributosColunados();
      Iterator itens = listaItens.iterator();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Total de Ativos a Retirar: " + listaItens.size());
      }

      while (ac.avancarLinha()) {
         Id idGarantia = (Id) itens.next();
         NumeroOperacao numOperacao = (NumeroOperacao) ac.obterAtributo(NumeroOperacao.class, Contexto.OPERACAO);

         if (idGarantia == null) {
            throw new IllegalArgumentException("Id deve estar preenchido!");
         }

         req.atribuirGARANTIA_Booleano(new Booleano(somenteValida ? Booleano.VERDADEIRO : Booleano.FALSO));
         req.atribuirGARANTIA_Id(idGarantia);
         req.atribuirOPERACAO_NumeroOperacao(numOperacao);

      }

      servicos.chamarServico(req);

      return new Notificacao("ManutencaoNuOpsGarantiasSelic.Sucesso");
   }

   public Class obterDestino(Grupo parametros, Servicos servicos) throws Exception {
      return null;
   }

   public boolean ciencia(Grupo parametros, Servicos servicos) throws Exception {
      return false;
   }

   public void ciencia(GrupoDeGrupos layout, Grupo dados, Servicos servicos) throws Exception {
   }

}
