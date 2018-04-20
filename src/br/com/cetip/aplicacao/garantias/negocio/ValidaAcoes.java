package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.cetip.aplicacao.custodia.apinegocio.CarteiraParticipanteFactory;
import br.com.cetip.aplicacao.custodia.apinegocio.ICarteiraParticipante;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMapaAcoes;
import br.com.cetip.aplicacao.garantias.apinegocio.IPenhorNoEmissor;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidaAcoes;
import br.com.cetip.aplicacao.garantias.negocio.acoes.IValidarAcao;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.custodia.CarteiraParticipanteDO;
import br.com.cetip.dados.aplicacao.garantias.AcessoCestaDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;

class ValidaAcoes extends BaseGarantias implements IValidaAcoes {

   public void validaAcao(Funcao acao, Funcao tipo, CestaGarantiasDO cesta) {
      boolean podeExecutar = innerPodeExecutarAcao(acao, tipo, cesta/* , true */);

      if (podeExecutar == false) {
         throw new Erro(CodigoErro.ACAO_INVALIDA_CESTA);
      }
   }

   public boolean podeExecutarAcao(Funcao acao, Funcao tipo, CestaGarantiasDO cesta) {
      return innerPodeExecutarAcao(acao, tipo, cesta/* , false */);
   }

   private boolean innerPodeExecutarAcao(Funcao _acao, Funcao _tipo, CestaGarantiasDO cesta/* , boolean disparaErro */) {
      // TODO disparar erro com mensagem descritiva sobre o motivo, caso 'disparaErro' = true

      Funcao acao = new Funcao(Contexto.ACAO, _acao.obterConteudo());
      Funcao tipo = null;
      if (_tipo != null) {
         tipo = new Funcao(Contexto.GARANTIAS_TIPO_ACESSO, _tipo.obterConteudo());
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "## Verificacao de permissao do Garantias ##");
         Logger.debug(this, "- Acao: " + acao);
         Logger.debug(this, "- Tipo de Acesso: " + tipo);
         Logger.debug(this, "- Numero da Cesta: " + cesta.getNumIdCestaGarantias());
      }

      // Valida campos
      if (Condicional.vazio(acao)) {
         throw new Erro(CodigoErro.ACAO_NAO_INDICADA);
      } else if (cesta == null) {
         Erro e = new Erro(CodigoErro.CAMPO_OBRIGATORIO);
         e.parametroMensagem("NumeroCestaGarantia", 0);
         throw e;
      }

      // valida via pojo
      IValidarAcao iva = getFactory().getInstanceValidarAcao(acao);
      if (iva != null) {
         return iva.validarAcao(tipo, cesta);
      }

      StatusCestaDO statusCesta = cesta.getStatusCesta();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "- Status da Cesta: " + statusCesta.getCodStatusCesta());
      }

      boolean cestaVinculada = statusCesta.equals(StatusCestaDO.VINCULADA);
      boolean cestaVinculadaAoAtivo = statusCesta.equals(StatusCestaDO.VINCULADA_AO_ATIVO);
      boolean cestaVincFalhou = statusCesta.equals(StatusCestaDO.VINCULACAO_FALHOU);
      boolean cestaDesvinculada = statusCesta.equals(StatusCestaDO.DESVINCULADA);
      boolean cestaDesvincFalhou = statusCesta.equals(StatusCestaDO.DESVNC_FALHOU);
      boolean cestaFinalizada = statusCesta.equals(StatusCestaDO.FINALIZADA);
      boolean cestaEmFinalizacao = statusCesta.equals(StatusCestaDO.EM_FINALIZACAO);
      boolean cestaEmEdicao = statusCesta.equals(StatusCestaDO.EM_EDICAO);
      boolean cestaEmManutencao = statusCesta.equals(StatusCestaDO.EM_MANUTENCAO);
      boolean cestaEmVinculacao = statusCesta.equals(StatusCestaDO.EM_VINCULACAO);
      boolean cestaCancelada = statusCesta.equals(StatusCestaDO.CANCELADA);
      boolean cestaInadimplente = statusCesta.equals(StatusCestaDO.INADIMPLENTE);
      boolean cestaIncompleta = statusCesta.equals(StatusCestaDO.INCOMPLETA);
      boolean cestaPendGarantido = statusCesta.equals(StatusCestaDO.VNC_PEND_GRTDO);
      boolean cestaPendGarantidor = statusCesta.equals(StatusCestaDO.VNC_PEND_GRTDOR);
      boolean cestaGrtRetiradas = statusCesta.equals(StatusCestaDO.GRT_RETIRADAS);
      boolean cestaGrtRegatadas = statusCesta.equals(StatusCestaDO.GRT_RESGATADAS);
      boolean cestaGrtLiberadas = statusCesta.equals(StatusCestaDO.GRT_LIBERADAS);

      ContextoAtivacaoVO contexto = getContextoAtivacao();
      Set contas = new HashSet(contexto.getListaContasConsulta());
      contas.addAll(contexto.getListaContasLiquidacao());
      contas.add(contexto.getNumeroContaParticipante());

      String contaGarantidor = cesta.getGarantidor().getCodContaParticipante().toString();
      String contaGarantido = null;

      boolean ehUsuarioCetip = contexto.ehCETIP();
      boolean ehGarantidoCesta = false;
      boolean ehVisualizadorCesta = false;
      boolean ehGarantidorCesta = contas.contains(contaGarantidor);

      IMapaAcoes mapaAcoes = getFactory().getInstanceMapaAcoes();

      boolean acaoConsulta = mapaAcoes.ehAcaoConsulta(acao);

      Set contasGarantidos = cesta.getVisualizadores();
      if (!cesta.getVisualizadores().isEmpty()) {
         for (Iterator i = contasGarantidos.iterator(); i.hasNext();) {
            AcessoCestaDO ac = (AcessoCestaDO) i.next();
            String conta = ac.getContaParticipante().getCodContaParticipante().toString();
            if (contas.contains(conta)) {
               ehGarantidoCesta = true && ac.getIndVisualizador().ehFalso();
               ehVisualizadorCesta = ac.getIndVisualizador().ehVerdadeiro();
               contaGarantido = conta;
            }

            if (!acaoConsulta) {
               break;
            }
         }
      }

      boolean ehCestaSegundoNivel = false;
      if (cesta.getAtivosVinculados() != null && cesta.getAtivosVinculados().size() > 0) {
         ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
         Iterator i = cesta.getAtivosVinculados().iterator();
         InstrumentoFinanceiroDO ifDO = ((CestaGarantiasIFDO) i.next()).getInstrumentoFinanceiro();

         ehCestaSegundoNivel = icg.ativoGaranteCesta(ifDO.getId());
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "- Garantidor: " + contaGarantidor);
         Logger.debug(this, "- Garantidos: ");
         for (Iterator i = contasGarantidos.iterator(); i.hasNext();) {
            AcessoCestaDO ac = (AcessoCestaDO) i.next();
            String conta = ac.getContaParticipante().getCodContaParticipante().toString();
            Logger.debug(this, "      " + conta);

            if (!acaoConsulta) {
               break;
            }
         }
         Logger.debug(this, "- CETIP: " + ehUsuarioCetip);
         Logger.debug(this, "- Eh Garantido(R): " + ehGarantidorCesta);
         Logger.debug(this, "- Eh Garantido: " + ehGarantidoCesta);
         Logger.debug(this, "- Contas CA: " + contas);
      }

      // CONSULTA DE HISTORICO
      if (acao.mesmoConteudo(ICestaDeGarantias.CONSULTAR_HISTORICO)) {
         if (ehGarantidorCesta || ehUsuarioCetip) {
            return true;
         } else if ((ehGarantidoCesta || ehVisualizadorCesta)
               && (cestaVinculada || cestaFinalizada || cestaVinculadaAoAtivo || cestaVincFalhou || cestaEmFinalizacao
                     || cestaDesvinculada || cestaDesvincFalhou || cestaCancelada || cestaInadimplente
                     || cestaGrtRetiradas || cestaGrtLiberadas || cestaEmVinculacao)) {
            return true;
         }
         return false;
      }

      // CONSULTA DE GARANTIAS
      if (acao.mesmoConteudo(ICestaDeGarantias.CONSULTAR_GARANTIAS)) {
         return (((ehGarantidorCesta || ehUsuarioCetip)) || ((ehGarantidoCesta || ehVisualizadorCesta) && (cestaVinculada
               || cestaFinalizada
               || cestaVinculadaAoAtivo
               || cestaPendGarantido
               || cestaPendGarantidor
               || cestaVincFalhou || cestaEmVinculacao || cestaDesvincFalhou || cestaDesvinculada || cestaCancelada || cestaInadimplente)))
               && !cestaGrtRetiradas && !cestaGrtLiberadas && !cestaGrtRegatadas;
      }

      // CESTAS EM FINALIZACAO OU CANCELADA NAO PODEM SOFRER QUALQUER ACAO
      if (cestaEmFinalizacao || cestaCancelada) {
         return false;
      }

      // ALTERAR CESTA
      if (acao.mesmoConteudo(ICestaDeGarantias.ALTERAR_CESTA)) {
         return (ehGarantidorCesta || ehUsuarioCetip)
               && (cestaEmManutencao || cestaEmEdicao || cestaIncompleta || (cestaFinalizada && cesta
                     .getVisualizadores().isEmpty())); // Defeito 5062
         // - alterado
         // por Viviane
      }

      // ALTERAR NUMERO OPERACAO
      boolean possuiMovsBloqueioSelic = getFactory().getInstanceCestaDeGarantias().possuiMovsBloqueioSelic(cesta);
      if (acao.mesmoConteudo(ICestaDeGarantias.ALTERAR_NUMERO_OPERACAO)) {
         return possuiMovsBloqueioSelic
               && (ehGarantidorCesta || ehUsuarioCetip)
               && (cestaEmManutencao || cestaEmEdicao || cestaIncompleta || (cestaFinalizada && cesta
                     .getVisualizadores().isEmpty()));
      }

      boolean ehValidoIncluirExcluirFinalizar = (ehGarantidorCesta || ehUsuarioCetip)
            && (cestaEmEdicao || cestaEmManutencao || cestaIncompleta);

      // INCLUIR GARANTIAS
      if (acao.mesmoConteudo(ICestaDeGarantias.INCLUIR_GARANTIAS)) {
         return ehValidoIncluirExcluirFinalizar;
      }

      // EXCLUIR GARANTIAS
      if (acao.mesmoConteudo(ICestaDeGarantias.EXCLUIR_GARANTIAS)) {
         return ehValidoIncluirExcluirFinalizar;
      }

      // FINALIZAR CESTA
      if (acao.mesmoConteudo(ICestaDeGarantias.FINALIZAR_CESTA)) {
         boolean garantidorEqualsGarantido = contaGarantidor.equals(contaGarantido);
         if (ehValidoIncluirExcluirFinalizar && possuiMovsBloqueioSelic && garantidorEqualsGarantido) {
            Logger.debug(this, "- finalizar cesta c/ Selic: garantidor deve ser diferente de garantido");
            ehValidoIncluirExcluirFinalizar = false;
         }

         return ehValidoIncluirExcluirFinalizar;
      }

      // APORTAR GARANTIAS GARANTIDOR
      if (acao.mesmoConteudo(ICestaDeGarantias.APORTAR_GARANTIAS)
            && (tipo != null && tipo.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDOR))) {
         return (ehGarantidorCesta || ehUsuarioCetip) && (cestaVinculada);
      }

      // APORTAR GARANTIAS GARANTIDO
      if (acao.mesmoConteudo(ICestaDeGarantias.APORTAR_GARANTIAS)
            && (tipo != null && tipo.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDO))) {
         return (ehGarantidoCesta || ehUsuarioCetip) && (cestaVinculada);
      }

      // LIBERAR GARANTIAS
      if (acao.mesmoConteudo(ICestaDeGarantias.LIBERAR_GARANTIAS)) {

         IPenhorNoEmissor pe = getFactory().getInstancePenhorNoEmissor();

         return ((ehGarantidoCesta || ehUsuarioCetip) && (cestaInadimplente) && !pe.eCestaPenhorNoEmissor(cesta) && !ehCestaSegundoNivel);
      }

      // LIBERAR GARANTIAS PARCIAL
      if (acao.mesmoConteudo(ICestaDeGarantias.LIBERAR_GARANTIAS_PARCIAL)) {

         IPenhorNoEmissor pe = getFactory().getInstancePenhorNoEmissor();

         return ((ehGarantidoCesta || ehUsuarioCetip) && (cestaInadimplente) && !pe.eCestaPenhorNoEmissor(cesta) && !ehCestaSegundoNivel);
      }

      // LIBERAR CESTA PARA MANUTENCAO
      if (acao.mesmoConteudo(ICestaDeGarantias.LIBERAR_CESTA)) {
         if (ehGarantidoCesta || ehUsuarioCetip) {
            return (cestaFinalizada || cestaIncompleta);
         } else if (ehGarantidorCesta) {
            return cestaIncompleta;
         }

         return false;
      }

      // VINCULAR CESTA
      if (acao.mesmoConteudo(ICestaDeGarantias.VINCULAR_CESTA)) {
         IPenhorNoEmissor pe = getFactory().getInstancePenhorNoEmissor();

         boolean cestaPode = cestaFinalizada || cestaVincFalhou;
         boolean multiplaPode = cestaVinculada && pe.eCestaPenhorNoEmissor(cesta);
         boolean garantidorPode = (ehGarantidorCesta || ehUsuarioCetip)
               && (cestaPode || multiplaPode || cestaPendGarantidor);
         boolean garantidoPode = (ehGarantidoCesta || ehUsuarioCetip) && (cestaPode || cestaPendGarantido);

         return garantidorPode || garantidoPode;
      }

      // EXCLUIR CESTA DE GARANTIAS
      if (acao.mesmoConteudo(ICestaDeGarantias.EXCLUIR_CESTA)) {
         if (!cestaVinculada) {
            if (cestaVincFalhou
                  && getFactory().getInstanceGarantiasSelic().temSelicEmDetalhes(cesta.getNumIdCestaGarantias())) {
               return false;
            }

            return (ehGarantidorCesta || ehUsuarioCetip)
                  && (cestaEmEdicao || cestaEmManutencao || cestaFinalizada || cestaIncompleta || cestaPendGarantido
                        || cestaPendGarantidor || cestaVincFalhou);
         }

         return getFactory().getInstanceCestaDeGarantias().verificaCustodiaAtivosCesta(cesta);
      }

      // CARACTERISTICAS DA CESTA
      if (acao.mesmoConteudo(ICestaDeGarantias.CARACTERISTICA_CESTA)) {
         return (ehGarantidorCesta || ehUsuarioCetip || ehGarantidoCesta)
               && (cestaFinalizada || cestaVinculada || cestaVinculadaAoAtivo || cestaPendGarantido
                     || cestaPendGarantidor || cestaInadimplente || cestaEmVinculacao);
      }

      // LIBERAR GARANTIAS DE PENHOR NO EMISSOR
      if (acao.mesmoConteudo(ICestaDeGarantias.LIBERAR_PENHOR_EMISSOR)) {
         CodigoContaCetip conta;
         try {
            IContaParticipante iConta = ContaParticipanteFactory.getInstance();
            conta = iConta.obterContaPrincipal(new Id(contexto.getIdParticipante().toString()))
                  .getCodContaParticipante();
         } catch (Exception e) {
            Logger.error(e);
            throw new Erro(CodigoErro.ERRO, "Sap: " + e.getMessage());
         }

         boolean ativosGarantidosEmContaCliente = ativosGarantidosEmContaClientePropria(cesta, conta);

         return (ehUsuarioCetip || (ehGarantidoCesta && ativosGarantidosEmContaCliente)) && cestaInadimplente
               && !ehCestaSegundoNivel;
      }

      return false;
   }

   private boolean ativosGarantidosEmContaClientePropria(CestaGarantiasDO cesta, CodigoContaCetip contaPrincipal) {
      ICarteiraParticipante icp;
      IContaParticipante iCP;

      try {
         icp = CarteiraParticipanteFactory.getInstance();
         iCP = ContaParticipanteFactory.getInstance();
      } catch (Exception e) {
         Logger.error(e);
         throw new Erro(CodigoErro.ERRO, "Sap: " + e.getMessage());
      }

      Data hoje = getDataHoje();

      boolean ativosEmContaCliente = true;

      Iterator i = cesta.getAtivosVinculados().iterator();
      while (i.hasNext()) {
         InstrumentoFinanceiroDO ifDO = ((CestaGarantiasIFDO) i.next()).getInstrumentoFinanceiro();

         // Verifica posicao de deposito de cada ativo
         List carteiras;

         try {
            carteiras = icp.obterPosicoesDetentores(hoje, ifDO.getId(), ifDO.getSistema().getNumero());
         } catch (Exception e) {
            Logger.error(e);
            throw new Erro(CodigoErro.ERRO, "Custodia: " + e.getMessage());
         }

         Iterator iCart = carteiras.iterator();
         while (iCart.hasNext()) {
            CarteiraParticipanteDO carteira = (CarteiraParticipanteDO) iCart.next();
            ContaParticipanteDO conta = carteira.getContaParticipante();

            boolean mesmoParticipante = true;
            if (!contaPrincipal.mesmoConteudo(conta.getCodContaParticipante())) {
               try {
                  mesmoParticipante = iCP.eMesmoParticipanteRegra(contaPrincipal, conta.getCodContaParticipante());
               } catch (Exception e) {
                  Logger.error(e);
                  throw new Erro(CodigoErro.ERRO, "SAP: " + e.getMessage());
               }
            }

            boolean contaCliente = conta.getCodContaParticipante().ehContaCliente();

            if (mesmoParticipante) {
               if (!contaCliente) {
                  ativosEmContaCliente = false;
               }
            } else {
               ativosEmContaCliente = false;
            }
         }
      }
      return ativosEmContaCliente;
   }

}
