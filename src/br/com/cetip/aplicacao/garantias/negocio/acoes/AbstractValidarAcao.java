package br.com.cetip.aplicacao.garantias.negocio.acoes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.AcessoCestaDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;

abstract class AbstractValidarAcao extends BaseGarantias implements IValidarAcao {

   private Funcao tipoAcesso;

   public final void setTipoAcesso(Funcao tipoAcesso) {
      this.tipoAcesso = tipoAcesso;
   }

   protected final Funcao getTipoAcesso() {
      return tipoAcesso;
   }

   private Set getContas() {
      ContextoAtivacaoVO contexto = getContextoAtivacao();
      Set contas = new HashSet(contexto.getListaContasConsulta());
      contas.addAll(contexto.getListaContasLiquidacao());
      contas.add(contexto.getNumeroContaParticipante());

      return contas;
   }

   protected final boolean isGarantidor(CestaGarantiasDO cesta) {
      String contaGarantidor = cesta.getGarantidor().getCodContaParticipante().toString();
      boolean ehGarantidorCesta = getContas().contains(contaGarantidor);
      return ehGarantidorCesta;
   }

   protected final boolean isGarantido(CestaGarantiasDO cesta) {
      boolean ehGarantidoCesta = false;

      Set contasGarantidos = cesta.getVisualizadores();
      if (cesta.getVisualizadores().isEmpty()) {
         return false;
      }

      for (Iterator i = contasGarantidos.iterator(); i.hasNext();) {
         AcessoCestaDO ac = (AcessoCestaDO) i.next();
         String conta = ac.getContaParticipante().getCodContaParticipante().toString();
         if (getContas().contains(conta)) {
            ehGarantidoCesta = true;
         }

         if (!isAcaoConsulta()) {
            break;
         }
      }

      return ehGarantidoCesta;
   }

   protected final boolean isAcaoConsulta() {
      HashSet set = new HashSet(2);
      set.add(ICestaDeGarantias.CONSULTAR_HISTORICO);
      set.add(ICestaDeGarantias.CONSULTAR_GARANTIAS);

      return set.contains(getAcao());
   }

   protected final boolean isCestaSegundoNivel(CestaGarantiasDO cesta) {
      return cesta.getIndSegundoNivel().ehVerdadeiro();
   }

   protected final boolean isCestaComAtivosSelic(CestaGarantiasDO cesta) {
      IGarantiasSelic gselic = getFactory().getInstanceGarantiasSelic();
      return gselic.temSelicNaCesta(cesta);
   }

}
