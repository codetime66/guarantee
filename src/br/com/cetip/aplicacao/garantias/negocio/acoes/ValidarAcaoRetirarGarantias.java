package br.com.cetip.aplicacao.garantias.negocio.acoes;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IPenhorNoEmissor;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.custodia.TipoPosicaoCarteiraDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.log.Logger;

public class ValidarAcaoRetirarGarantias extends AbstractValidarAcao {

   public boolean validarAcao(Funcao tipoAcesso, CestaGarantiasDO cesta) {
      IPenhorNoEmissor pe = getFactory().getInstancePenhorNoEmissor();
      boolean ehPenhorNoEmissor = pe.eCestaPenhorNoEmissor(cesta);
      boolean vinculada = cesta.getStatusCesta().isVinculada();
      boolean usuarioCetip = getContextoAtivacao().ehCETIP();
      boolean cestaSegundoNivel = isCestaSegundoNivel(cesta);
      boolean cestaSelic = isCestaComAtivosSelic(cesta);
      boolean ehGarantido = !usuarioCetip ? isGarantido(cesta) : false; // se nao for cetip eh false para permitir validar regras de negocio
      boolean ehGarantidor = !usuarioCetip ? isGarantidor(cesta) : false; // mesma coisa, pois o usuario cetip possui todas as contas e retornaria true
      boolean acessoGarantidor = tipoAcesso.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDOR);
      boolean ativoPossuiMovs = false;
      boolean custodiaAtivosGarantidos = false;

      if (vinculada) {
         ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
         ativoPossuiMovs = icg.cestaPossuiAtivoGarantidoComOperacaoPendente(cesta);

         // soh verifica a custodia se for o caso de cesta sem movs pendentes
         // eh penhor emissor
         // e o participante eh garantidor da cesta
         if (!ativoPossuiMovs && ehGarantidor && ehPenhorNoEmissor) {
            custodiaAtivosGarantidos = validaCustodiaAtivosVinculados(cesta);
         }
      }

      boolean validacaoBasica = vinculada && !ativoPossuiMovs && !cestaSegundoNivel;
      boolean sePenhorPode = ehPenhorNoEmissor && (usuarioCetip || (ehGarantidor && custodiaAtivosGarantidos));
      boolean seCessaoPode = !ehPenhorNoEmissor && (usuarioCetip || ehGarantido || (cestaSelic && acessoGarantidor));

      return validacaoBasica && (sePenhorPode || seCessaoPode);
   }

   /*
    * Backlog 4079
    */
   private boolean validaCustodiaAtivosVinculados(CestaGarantiasDO cesta) {
      ContaParticipanteDO garantidor = cesta.getGarantidor();
      CodigoContaCetip contaGarantidor = garantidor.getCodContaParticipante();

      String hql = "select distinct cap.tipoPosicaoCarteira.codigo, cap.contaParticipante "
            + "from CarteiraParticipanteDO cap, CestaGarantiasIFDO c where c.instrumentoFinanceiro.id = cap.instrumentoFinanceiro.id "
            + "and c.cestaGarantia = ?";
      List l = getGp().find(hql, cesta.getNumIdCestaGarantias());
      Iterator i = l.iterator();
      while (i.hasNext()) {
         Object[] linha = (Object[]) i.next();
         Id codTipoPosicaoCarteira = (Id) linha[0];
         ContaParticipanteDO conta = (ContaParticipanteDO) linha[1];

         final boolean ehPropriaLivre = codTipoPosicaoCarteira.mesmoConteudo(TipoPosicaoCarteiraDO.PROPRIA_LIVRE);
         final CodigoContaCetip codContaCarteira = conta.getCodContaParticipante();
         final boolean ehContaCliente = codContaCarteira.ehContaCliente();

         if (!ehPropriaLivre || !ehContaCliente) {
            return false;
         }

         boolean mesmoParticipante = contaGarantidor.mesmoConteudo(codContaCarteira);
         if (!mesmoParticipante) {
            try {
               IContaParticipante iCP = ContaParticipanteFactory.getInstance();
               mesmoParticipante = iCP.eMesmoParticipanteRegra(contaGarantidor, codContaCarteira);
            } catch (Exception e) {
               Logger.error(e);
               throw new Erro(CodigoErro.ERRO, "SAP Erro: " + e.getMessage());
            }
         }

         if (!mesmoParticipante) {
            return false;
         }
      }

      return true;
   }

   public Funcao getAcao() {
      return ICestaDeGarantias.RETIRAR_GARANTIAS;
   }

}
