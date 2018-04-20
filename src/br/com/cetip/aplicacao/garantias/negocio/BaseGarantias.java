package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Date;

import br.com.cetip.aplicacao.administracao.apinegocio.ControleOperacionalFactory;
import br.com.cetip.aplicacao.administracao.apinegocio.IControleOperacional;
import br.com.cetip.aplicacao.garantias.apinegocio.GarantiasFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.IBaseGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.operacao.apinegocio.ControleOperacaoFactory;
import br.com.cetip.aplicacao.operacao.apinegocio.IControleOperacao;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.tempo.DataHora;
import br.com.cetip.infra.atributo.tipo.tempo.Hora;
import br.com.cetip.infra.persistencia.GerenciadorPersistenciaFactory;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.contexto.ContextoAtivacao;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;

public abstract class BaseGarantias implements IBaseGarantias {

   private boolean initialized;

   private IGarantias garantias;

   private Data hoje;

   private IGerenciadorPersistencia gp;

   private IControleOperacional controleOperacional;

   private IControleOperacao controleOperacao;

   private ContextoAtivacaoVO contextoAtivacao;

   public void inicializar() {
   }

   protected final Data getDataHoje() {
      try {
         if (hoje == null) {
            hoje = getControleOperacional().obterD0();
         }
      } catch (Exception e) {
         throw new Erro(CodigoErro.ERRO, e.getMessage());
      }

      return hoje;
   }

   protected final DataHora getDataHoraHoje() {
      DataHora dataHora = new DataHora(getDataHoje(), new Hora(new Date()));
      return dataHora;
   }

   protected final IGarantias getFactory() {
      init();
      return garantias;
   }

   private void init() {
      if (initialized) {
         return;
      }

      inicializar();
      garantias = GarantiasFactory.getInstance();
      initialized = true;
   }

   protected final ContextoAtivacaoVO getContextoAtivacao() {
      if (contextoAtivacao == null) {
         contextoAtivacao = ContextoAtivacao.getContexto();
      }

      return contextoAtivacao;
   }

   protected final IControleOperacao getControleOperacao() {
      if (controleOperacao == null) {
         controleOperacao = ControleOperacaoFactory.getControleOperacao();
      }

      return controleOperacao;
   }

   protected final IControleOperacional getControleOperacional() {
      try {
         if (controleOperacional == null) {
            controleOperacional = ControleOperacionalFactory.getInstance();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return controleOperacional;
   }

   protected final IGerenciadorPersistencia getGp() {
      if (gp == null) {
         gp = GerenciadorPersistenciaFactory.getGerenciadorPersistencia();
      }

      return gp;
   }

   protected final boolean ehCanalMensageria() {
      return getContextoAtivacao().getCanal().equals(Integer.valueOf("3"));
   }

   public final void setGarantias(IGarantias garantias) {
      this.garantias = garantias;
      this.initialized = garantias != null;
   }

}
