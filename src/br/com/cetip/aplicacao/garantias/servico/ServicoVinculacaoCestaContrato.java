package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularContratoCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.ContratoCestaGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ComplementoContratoDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="PARTE"
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="PARTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTRA_PARTE"
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTRA_PARTE"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador" 
 *                    contexto="RESET"
 * 
 * @requisicao.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="PARTE"
 * 
 * @requisicao.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTRA_PARTE"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="OPERACAO"
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * 
 */
public class ServicoVinculacaoCestaContrato extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao arg0) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "*** Vinculacao de Contrato Swap a Cesta(s) de Garantias ***");
      }
      RequisicaoServicoVinculacaoCestaContrato req;
      req = (RequisicaoServicoVinculacaoCestaContrato) arg0;

      NumeroCestaGarantia codCestaParte = req.obterPARTE_NumeroCestaGarantia();
      NumeroCestaGarantia codCestaContraparte = req.obterCONTRA_PARTE_NumeroCestaGarantia();
      CodigoContaCetip codContaParte = req.obterPARTE_CodigoContaCetip();
      CodigoContaCetip codContaContraparte = req.obterCONTRA_PARTE_CodigoContaCetip();
      CodigoIF codIF = req.obterINSTRUMENTO_FINANCEIRO_CodigoIF();
      CPFOuCNPJ comitenteParte = req.obterPARTE_CPFOuCNPJ();
      CPFOuCNPJ comitenteContraParte = req.obterCONTRA_PARTE_CPFOuCNPJ();
      Funcao regraLiberacao = req.obterRESET_Funcao();

      // DAO Participantes
      IContaParticipante contaDao = ContaParticipanteFactory.getInstance();

      // DAO Cestas
      ICestaDeGarantias dao = getFactory().getInstanceCestaDeGarantias();

      // DAO Persistencia
      CodigoIF codigoIF = new CodigoIF(codIF.toString());

      CestaGarantiasDO cestaParte = null;
      CestaGarantiasDO cestaContraparte = null;

      if (codCestaParte != null && !codCestaParte.vazio()) {
         cestaParte = dao.obterCestaDeGarantias(codCestaParte);
      }

      if (codCestaContraparte != null && !codCestaContraparte.vazio()) {
         cestaContraparte = dao.obterCestaDeGarantias(codCestaContraparte);
      }

      if (cestaParte == null && cestaContraparte == null) {
         throw new Erro(CodigoErro.CONTEUDO_INCONSISTENTE);
      }

      ContaParticipanteDO contaParte = contaDao.obterContaParticipanteDO(codContaParte);
      ContaParticipanteDO contaContraparte = contaDao.obterContaParticipanteDO(codContaContraparte);

      IContratosCesta icc = getFactory().getInstanceContratosCesta();
      ComplementoContratoDO instrumento = icc.obterContrato(codigoIF);

      ContratoCestaGarantiaDO contratoCestas = icc.obterVinculoContrato(codigoIF);

      AtributosColunados ac1 = new AtributosColunados();
      ac1.atributo(codContaParte);
      ac1.atributo(codContaContraparte);

      // boolean simplesComando = false;
      boolean simplesComando = FabricaDeMotorDeRegra.getMotorDeRegra().avalia(
            ConstantesDeNomeDeRegras.eSimplesComandoVinculacaoContratoCesta, ac1, false);

      if (Logger.estaHabilitadoDebug(this)) {
         if (simplesComando) {
            Logger.debug(this, "*** Vinculacao de SIMPLES Comando! ***");
         } else {
            Logger.debug(this, "*** Vinculacao de DUPLO Comando! ***");
         }
      }

      boolean primeiraChamada = false;
      if (contratoCestas == null) {
         // Contrato nao existe, entao eh a primeira entrada (de uma das partes)
         contratoCestas = novoContrato(contaParte, contaContraparte, cestaParte, cestaContraparte, instrumento,
               regraLiberacao, comitenteParte, comitenteContraParte);
         primeiraChamada = true;

         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "*** Controle de Duplo comando do MMG cadastrado. ***");
         }
      }

      /** contrato da cesta eh diferente da cestaParte
       * Incluir movimentacao de vinculacao
		 configurando status da cesta como StatusCestaDO.VNC_PENDENTE 
       * */
      if (!Condicional.vazio(contratoCestas) && 
    		  ((!Condicional.vazio(contratoCestas.getCestaParte()) && contratoCestas.getCestaParte().getStatusCesta().isVinculada())|| 
    		   (!Condicional.vazio(contratoCestas.getCestaContraparte()) && contratoCestas.getCestaContraparte().getStatusCesta().isVinculada()) ) && 
    		   (Condicional.vazio(contratoCestas.getCestaContraparte()) || Condicional.vazio(contratoCestas.getCestaParte()) )){

    	
          contratoCestas = criarMovimentacaoPontaContrato(contratoCestas,contaParte, contaContraparte,cestaParte, cestaContraparte, instrumento,regraLiberacao, comitenteParte, comitenteContraParte);
          primeiraChamada = true;    	  
      }
      

      boolean duploComandoOk = false;
      if (!simplesComando && !primeiraChamada) {
         matchDuploComando(contratoCestas, req);
         duploComandoOk = true;
      }

      boolean vinculou = false;
      if (simplesComando || duploComandoOk) {
         if (!simplesComando && !Condicional.vazio(comitenteParte)) {
            contratoCestas.setComitenteContraparte(comitenteParte);

            if (contratoCestas.getCestaContraparte() != null) {
               IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
               MovimentacaoGarantiaDO mov = imov.obterUltimaMovimentacao(contratoCestas.getCestaContraparte(),
                     TipoMovimentacaoGarantiaDO.VINCULACAO, StatusMovimentacaoGarantiaDO.PENDENTE);
               mov.setCpfOuCnpjComitente(comitenteParte);
            }
         }

         IVincularContratoCesta ivc = getFactory().getInstanceVincularContratoCesta();
         ivc.vincularContrato(contratoCestas);

         vinculou = true;
      }

      ResultadoServicoVinculacaoCestaContrato res = new ResultadoServicoVinculacaoCestaContrato();
      res.atribuirOPERACAO_Booleano(new Booleano(Contexto.OPERACAO, (vinculou ? Booleano.VERDADEIRO : Booleano.FALSO)));

      return res;

   }

   /*
    * Executa o servico de Swap para vincular as cestas ao contrato
    * 
    * @param contrato
    */

   /*
    * Faz o match da segunda operacao com o previo registro de contrato de cesta com swap
    * 
    * @param contrato
    * 
    * @return
    */
   private void matchDuploComando(ContratoCestaGarantiaDO contrato, RequisicaoServicoVinculacaoCestaContrato req) {
      AtributosColunados ac = new AtributosColunados();
      ac.atributo(contrato.getParte().getCodContaParticipante());
      ac.atributo(contrato.getContraparte().getCodContaParticipante());

      if (contrato.getCestaParte() != null) {
         ac.atributo(contrato.getCestaParte().getNumIdCestaGarantias());
      } else {
         ac.atributo(new NumeroCestaGarantia(Contexto.PARTE));
      }

      if (contrato.getCestaContraparte() != null) {
         ac.atributo(contrato.getCestaContraparte().getNumIdCestaGarantias());
      } else {
         ac.atributo(new NumeroCestaGarantia(Contexto.CONTRA_PARTE));
      }

      ac.atributo(contrato.getContrato().getCodigoIF());
      ac.atributo(contrato.getIndRegraLiberacao());

      NumeroCestaGarantia codCestaParte = req.obterPARTE_NumeroCestaGarantia();
      NumeroCestaGarantia codCestaContraparte = req.obterCONTRA_PARTE_NumeroCestaGarantia();
      CodigoContaCetip codContaParte = req.obterPARTE_CodigoContaCetip();
      CodigoContaCetip codContaContraparte = req.obterCONTRA_PARTE_CodigoContaCetip();
      CodigoIF codIF = req.obterINSTRUMENTO_FINANCEIRO_CodigoIF();
      Funcao reset = req.obterRESET_Funcao();

      ac.atributo(codContaParte);
      ac.atributo(codContaContraparte);
      ac.atributo(codCestaParte);
      ac.atributo(codCestaContraparte);
      ac.atributo(reset);
      ac.atributo(codIF);

      // Avalia entrada de parametros de parte e contra-parte (match de duplo
      // comando)
      try {
         FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.eMesmaVinculacaoContratoCesta, ac,
               true);
      } catch (Exception e) {
         if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
         }

         throw new RuntimeException(e);
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "*** Match de DUPLO Comando realizado: OK! ***");
      }
   }

   /*
    * Cria novo registro de contrato de cesta com swap, para controle de duplo comando
    * 
    * @param gp
    * 
    * @param contaParte
    * 
    * @param contaContraparte
    * 
    * @param cestaParte
    * 
    * @param cestaContraparte
    * 
    * @param instrumento
    * 
    * @param reset
    * 
    * @param comitente
    * 
    * @return
    */
   private ContratoCestaGarantiaDO novoContrato(ContaParticipanteDO contaParte, ContaParticipanteDO contaContraparte,
         CestaGarantiasDO cestaParte, CestaGarantiasDO cestaContraparte, ComplementoContratoDO instrumento,
         Texto reset, CPFOuCNPJ comitente, CPFOuCNPJ comitenteContraParte) {
      Data dataHoje = getDataHoje();

      ContratoCestaGarantiaDO contrato = new ContratoCestaGarantiaDO();
      contrato.setParte(contaParte);
      contrato.setContraparte(contaContraparte);
      contrato.setDataInclusao(dataHoje);
      contrato.setContrato(instrumento);
      contrato.setIndRegraLiberacao(reset);
      contrato.setComitenteParte(comitente);
      contrato.setComitenteContraparte(comitenteContraParte);
      contrato.setCestaParte(cestaParte);
      contrato.setCestaContraparte(cestaContraparte);

      if (cestaParte != null) {
         criarMovimentacaoVinc(instrumento, cestaParte, StatusCestaDO.VNC_PEND_GRTDO, reset, comitente);
      }

      if (cestaContraparte != null) {
         criarMovimentacaoVinc(instrumento, cestaContraparte, StatusCestaDO.VNC_PEND_GRTDOR, reset,
               comitenteContraParte);
      }

      IGerenciadorPersistencia gp = getGp();
      gp.save(contrato);
      gp.flush();

      return contrato;
   }
   
   /*
    * Inclui uma movimentacao de vinculacao pendente, e marca a cesta com o status indicado
    */
   private void criarMovimentacaoVinc(InstrumentoFinanceiroDO instrumento, CestaGarantiasDO cesta,
         StatusCestaDO status, Texto reset, CPFOuCNPJ comitente) {
      IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
      MovimentacaoGarantiaDO mov = imov.incluirMovimentacaoVinculacao(cesta, instrumento);
      mov.setCpfOuCnpjComitente(comitente);

      Data dataHoje = getDataHoje();
      cesta.setDatAlteracaoStatusCesta(dataHoje);
      cesta.setStatusCesta(status);
      cesta.setIndRegraLiberacao(reset);
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      throw new UnsupportedOperationException();
   }

   private ContratoCestaGarantiaDO criarMovimentacaoPontaContrato(ContratoCestaGarantiaDO contrato, ContaParticipanteDO contaParte, ContaParticipanteDO contaContraparte,
		   CestaGarantiasDO cestaParte, CestaGarantiasDO cestaContraparte, ComplementoContratoDO instrumento,
	         Texto reset, CPFOuCNPJ comitente, CPFOuCNPJ comitenteContraParte) {

	      contrato.setParte(contaParte);
	      contrato.setContraparte(contaContraparte);	      
	      contrato.setContrato(instrumento);
	      contrato.setIndRegraLiberacao(reset);
	      contrato.setComitenteParte(comitente);
	      contrato.setCestaParte(cestaParte);
	      contrato.setCestaContraparte(cestaContraparte);
	      contrato.setComitenteContraparte(comitenteContraParte);
	   
	      if (cestaParte != null) {
	         criarMovimentacaoVinc(instrumento, cestaParte, StatusCestaDO.VNC_PENDENTE, reset, comitente);
	      }

	      if (cestaContraparte != null) {
	         criarMovimentacaoVinc(instrumento, cestaContraparte, StatusCestaDO.VNC_PENDENTE, reset,
	               comitenteContraParte);
	      }
	      IGerenciadorPersistencia gp = getGp();
	      gp.saveOrUpdate(contrato);

	      return contrato;
	   }   
   
}
