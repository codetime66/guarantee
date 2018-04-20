package br.com.cetip.aplicacao.garantias.web;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoLiberarGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.web.Tabela;
import br.com.cetip.infra.log.Logger;

public class ConfirmacaoRelacaoRetirarGarantiasCesta extends AbstractFormularioGarantias {

   private Tabela tabela;

   private List listaItens;

   private boolean somenteValida;

   private Funcao tipoAcesso;

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
      RelacaoRetirarGarantiasDeCesta tela = ((RelacaoRetirarGarantiasDeCesta) servicos
            .obterTela(RelacaoRetirarGarantiasDeCesta.class));
      tabela = tela.obterTabela();
      listaItens = tela.obterListaItens();
      tipoAcesso = tela.getTipoAcesso();
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
      // Servico de *RETIRADA* de Garantias (Nome do Servico ainda em versao antiga) - 19/02/2008
      getDadosTelaAnterior(servicos);
      RequisicaoServicoLiberarGarantias req = new RequisicaoServicoLiberarGarantias();

      AtributosColunados ac = tabela.obterAtributosColunados();
      Iterator itens = listaItens.iterator();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Total de Ativos a Retirar: " + listaItens.size());
      }

      while (ac.avancarLinha()) {
         Id idGarantia = (Id) itens.next();
         Quantidade qtdade = (Quantidade) ac.obterAtributo(Quantidade.class, Contexto.GARANTIAS_LIBERAR_QUANTIDADE);
         NumeroOperacao numOperacao = (NumeroOperacao) ac.obterAtributo(NumeroOperacao.class, Contexto.OPERACAO);

         if (idGarantia == null) {
            throw new IllegalArgumentException("Id deve estar preenchido!");
         }

         if (qtdade == null) {
            throw new IllegalArgumentException("Quantidade deve estar preenchido!");
         }

         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Id a retirar: " + idGarantia + " , qtdade: " + qtdade);
         }

         req.atribuirGARANTIA_Booleano(new Booleano(somenteValida ? Booleano.VERDADEIRO : Booleano.FALSO));
         req.atribuirGARANTIAS_LIBERACAO_Id(idGarantia);
         req.atribuirGARANTIAS_LIBERAR_QUANTIDADE_Quantidade(qtdade);
         req.atribuirOPERACAO_NumeroOperacao(numOperacao);
         req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(tipoAcesso);

      }

      servicos.chamarServico(req);

      return new Notificacao("GarantiasCestaLiberadas.Sucesso");
   }

}
