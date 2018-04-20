package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IContaGarantia;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IPenhorNoEmissor;
import br.com.cetip.dados.aplicacao.garantias.AcessoCestaDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.utilitario.Condicional;

/**
 * 
 * @author brunob
 * 
 */
class GarantidoCesta extends BaseGarantias implements IGarantidoCesta {

   public ContaParticipanteDO obterGarantidoCesta(CestaGarantiasDO cesta) {
      if (!Condicional.vazio(cesta.getGarantido())) {
         return cesta.getGarantido();
      }

      ContaParticipanteDO garantido = null;

      IPenhorNoEmissor pe = getFactory().getInstancePenhorNoEmissor();
      if (pe.eCestaPenhorNoEmissor(cesta)) {
         garantido = cesta.getGarantidor();
      } else {
         Iterator i = cesta.getVisualizadores().iterator();
         if (i.hasNext()) {
            AcessoCestaDO acessoCesta = (AcessoCestaDO) i.next();
            garantido = acessoCesta.getContaParticipante();
         }
      }

      return garantido;
   }

   private AcessoCestaDO obterAcessoCesta(CestaGarantiasDO cesta, ContaParticipanteDO conta) {
      String hql = "from AcessoCestaDO a where a.cestaGarantias = ? and a.contaParticipante = ?";
      List l = getGp().find(hql, new Object[] { cesta, conta });

      if (l.isEmpty() == false) {
         return (AcessoCestaDO) l.get(0);
      }

      return null;
   }

   /**
    * Utilizado pelo servico de cadastro de visualizadores de cestas penhor emissor
    */
   public AcessoCestaDO cadastrarAcessoVisualizador(CestaGarantiasDO cesta, ContaParticipanteDO conta) {
      AcessoCestaDO acesso = cadastrarAcesso(cesta, conta);
      acesso.setIndVisualizador(Booleano.VERDADEIRO);
      return acesso;
   }

   /**
    * Acessos a cesta sao cadastrados por padrao como garantido da cesta
    * 
    * Verifica se ja nao existe acesso para a conta. Caso exista, retorna o mesmo.
    * 
    * @param cesta
    * @param conta
    * @return
    */
   private AcessoCestaDO cadastrarAcesso(CestaGarantiasDO cesta, ContaParticipanteDO conta) {
      AcessoCestaDO acesso = obterAcessoCesta(cesta, conta);
      if (acesso != null) {
         return acesso;
      }

      acesso = new AcessoCestaDO();
      acesso.setIndVisualizador(Booleano.FALSO);
      acesso.setContaParticipante(conta);

      if (cesta.getVisualizadores() == null) {
         cesta.setVisualizadores(new HashSet());
      }

      cesta.addAcessoCesta(acesso);

      getGp().save(acesso);

      return acesso;
   }

   /**
    * Cadastra acesso da conta como garantido
    */
   public AcessoCestaDO cadastrarAcessoGarantido(CestaGarantiasDO cesta, ContaParticipanteDO conta) {
      AcessoCestaDO acesso = cadastrarAcesso(cesta, conta);
      acesso.setIndVisualizador(Booleano.FALSO);
      return acesso;
   }

   /**
    * Associa a conta do acesso e a conta 60 do acesso na Cesta
    * @param acesso
    */
   public void associarGarantidoNaCesta(CestaGarantiasDO cesta, ContaParticipanteDO conta) {
      cadastrarAcessoGarantido(cesta, conta);

      // Conta 60
      IContaGarantia cg = getFactory().getInstanceContaGarantia();
      ContaParticipanteDO conta60 = cg.obterConta60(conta);

      //Conta Garantia Selic
      IGarantiasSelic gselic = getFactory().getInstanceGarantiasSelic();
      if (gselic.temSelicNaCesta(cesta)) {
         //verifica se garantido possui conta garantia selic 
         gselic.obterContaGarantiaSelic(conta.getCodContaParticipante());
      }

      cesta.setGarantido(conta);
      cesta.setConta60Garantido(conta60);
   }

   /** 
    * Indica se a conta informada eh visualizadora de cesta. Todos os garantidos sao visualizadores automaticamente.
    * 
    * @param cesta
    * @param contaParticipante
    * @return
    */
   public boolean ehVisualizador(CestaGarantiasDO cesta, ContaParticipanteDO contaParticipante) {
      String hql = "select count(*) from AcessoCestaDO a where a.cestaGarantias = ? and a.contaParticipante = ?";

      List l = getGp().find(hql, new Object[] { cesta, contaParticipante });

      Integer i = (Integer) l.get(0);
      return i.intValue() > 0;
   }

   /**
    * Remove o acesso dos participantes que nao possuem mais ativos garantidos por esta cesta
    * 
    * @param cesta
    * Backlog 4917
    */
   public void excluirGarantidosSemCustodia(CestaGarantiasDO cesta) {
      String hql = " from AcessoCestaDO ac where ac.cestaGarantias.id = ? and ac.indVisualizador = 'N'"
            + " and not exists (select cp.contaParticipante.id from CestaGarantiasIFDO cgi, "
            + " CarteiraParticipanteDO cp where cp.instrumentoFinanceiro = cgi.instrumentoFinanceiro "
            + " and cp.quantidade > 0 and ac.cestaGarantias.id = cgi.cestaGarantia "
            + " and cp.contaParticipante = ac.contaParticipante)";

      getGp().delete(hql, new Object[] { cesta.getNumIdCestaGarantias() });
   }

   public void removerAcesso(CestaGarantiasDO cesta, ContaParticipanteDO conta) {
      AcessoCestaDO acesso = obterAcessoCesta(cesta, conta);
      cesta.getVisualizadores().remove(acesso);
      getGp().delete(acesso);
   }

}
