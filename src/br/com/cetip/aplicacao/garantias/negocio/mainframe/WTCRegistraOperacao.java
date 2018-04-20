package br.com.cetip.aplicacao.garantias.negocio.mainframe;

import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleLancamento;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.utilitario.Condicional;

public class WTCRegistraOperacao extends WTCAbstrato {

   private NumeroInteiro codigoSistema = new NumeroInteiro(16); // SNA AVSISUNICO

   private NumeroInteiro codigoTipo = new NumeroInteiro(802); // Vinculacao AVSOLICITA

   private String indAcao; // ACAO

   private CodigoIF codigoIF; // AVATIVO

   private Id numeroCesta; // AVCESTA

   private String depositado; // AVDEPOSITADO

   private String contaParte; // AVCTVENDEDOR

   private String contaContraParte; // AVCTCOMPRADOR

   private NumeroControleLancamento numeroControle; // AVNOPER

   private Quantidade qtd; // AVQTDE

   private Quantidade pu; // AVPU

   private Id modalidade; // AVMODLIQ

   // Auditoria
   private String idContaParte; // AVVDMEMBRO

   private String nomeLoginParte; // AVVDOPERADOR

   private String idContraParte; // AVCPMEMBRO

   private String nomeLoginContraParte; // AVCPOPERADOR

   private String dataOperacao; // AVCPDATOP

   private NumeroInteiro sequencia; // AVSEQUENCIA

   public String obterNomeServico() {
      return "SEAVISOSOBE";
   }

   public String obterMensagem() {
      StringBuffer sb = new StringBuffer();

      sb.append("<WTC>");

      sb.append("<indAcao>");
      sb.append(indAcao);
      sb.append("</indAcao>");

      sb.append("<numSistemaNX>");
      sb.append(codigoSistema.obterConteudo());
      sb.append("</numSistemaNX>");

      sb.append("<idSolicita>");
      sb.append(codigoTipo.obterConteudo());
      sb.append("</idSolicita>");

      sb.append("<codIF>");
      if (!Condicional.vazio(codigoIF)) {
         sb.append(codigoIF.obterConteudo());
      }
      sb.append("</codIF>");

      sb.append("<numCesta>");
      if (!Condicional.vazio(numeroCesta)) {
         sb.append(numeroCesta.obterConteudo());
      }
      sb.append("</numCesta>");

      sb.append("<indDepositado>");
      sb.append(depositado);
      sb.append("</indDepositado>");

      sb.append("<codContaGarantidor>");
      if (contaParte != null) {
         sb.append(contaParte);
      }
      sb.append("</codContaGarantidor>");

      sb.append("<codContaGarantido>");
      if (contaContraParte != null) {
         sb.append(contaContraParte);
      }
      sb.append("</codContaGarantido>");

      sb.append("<numLancamento>");
      if (!Condicional.vazio(numeroControle)) {
         sb.append(numeroControle.obterConteudo());
      }
      sb.append("</numLancamento>");

      sb.append("<qtdDeposito>");
      if (!Condicional.vazio(qtd)) {
         sb.append(qtd.obterConteudo());
      }
      sb.append("</qtdDeposito>");

      sb.append("<valPUDeposito>");
      if (!Condicional.vazio(pu)) {
         sb.append(pu.obterConteudo());
      }
      sb.append("</valPUDeposito>");

      sb.append("<codModalidade>");
      if (!Condicional.vazio(modalidade)) {
         sb.append(modalidade.obterConteudo());
      }
      sb.append("</codModalidade>");

      // Auditoria

      sb.append("<codContaLancadorGarantidor>");
      if (idContaParte != null) {
         sb.append(idContaParte);
      }
      sb.append("</codContaLancadorGarantidor>");

      sb.append("<nomUsuarioGarantidor>");
      if (nomeLoginParte != null) {
         sb.append(nomeLoginParte);
      }
      sb.append("</nomUsuarioGarantidor>");

      sb.append("<codContaLancadorGarantido>");
      if (idContraParte != null) {
         sb.append(idContraParte);
      }
      sb.append("</codContaLancadorGarantido>");

      sb.append("<nomUsuarioGarantido>");
      if (nomeLoginContraParte != null) {
         sb.append(nomeLoginContraParte);
      }
      sb.append("</nomUsuarioGarantido>");

      sb.append("<datOperacao>");
      if (dataOperacao != null) {
         sb.append(dataOperacao);
      }
      sb.append("</datOperacao>");

      sb.append("<numAviso>");
      if (!Condicional.vazio(sequencia)) {
         sb.append(sequencia);
      }
      sb.append("</numAviso>");

      sb.append("</WTC>");
      return sb.toString();
   }

   public void setContaContraParte(String contaContraParte) {
      this.contaContraParte = contaContraParte;
   }

   public void setContaParte(String contaParte) {
      this.contaParte = contaParte;
   }

   public void setDataOperacao(String dataOperacao) {
      this.dataOperacao = dataOperacao;
   }

   public void setIdContaParte(String idContaParte) {
      this.idContaParte = idContaParte;
   }

   public void setIdContraParte(String idContraParte) {
      this.idContraParte = idContraParte;
   }

   public void setModalidade(Id modalidade) {
      this.modalidade = modalidade;
   }

   public void setNomeLoginContraParte(String nomeLoginContraParte) {
      this.nomeLoginContraParte = nomeLoginContraParte;
   }

   public void setNomeLoginParte(String nomeLoginParte) {
      this.nomeLoginParte = nomeLoginParte;
   }

   public void setNumeroControle(NumeroControleLancamento numeroControle) {
      this.numeroControle = numeroControle;
   }

   public void setPu(Quantidade pu) {
      this.pu = pu;
   }

   public void setQtd(Quantidade qtd) {
      this.qtd = qtd;
   }

   public void setSequencia(NumeroInteiro sequencia) {
      this.sequencia = sequencia;
   }

   public void setCodigoSistema(NumeroInteiro codigoSistema) {
      this.codigoSistema = codigoSistema;
   }

   public void setIndAcao(String indAcao) {
      this.indAcao = indAcao;
   }

   public void setDepositado(String depositado) {
      this.depositado = depositado;
   }

   public void setCodigoTipo(NumeroInteiro codigoTipo) {
      this.codigoTipo = codigoTipo;
   }

   public void setCodigoIF(CodigoIF codigoIF) {
      this.codigoIF = codigoIF;
   }

   public void setNumeroCesta(Id numeroCesta) {
      this.numeroCesta = numeroCesta;
   }

}
