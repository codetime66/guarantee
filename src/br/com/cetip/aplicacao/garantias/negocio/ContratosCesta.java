package br.com.cetip.aplicacao.garantias.negocio;

import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IContratosCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.VinculacaoContratoVO;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.sec.ComitenteFactory;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.sec.IComitente;
import br.com.cetip.aplicacao.instrumentofinanceiro.servico.swap.RequisicaoServicoVinculaCestaContrato;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.ContratoCestaGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ComplementoContratoDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ParametroPontaDO;
import br.com.cetip.dados.aplicacao.sap.ComitenteDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIFContrato;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;

final class ContratosCesta extends BaseGarantias implements IContratosCesta {

   public ContratoCestaGarantiaDO obterVinculoContrato(CodigoIF contrato) {
      IGerenciadorPersistencia gp = getGp();
      String hql = "from ContratoCestaGarantiaDO cc where cc.contrato.codigoIF = ?";
      List contratos = gp.find(hql, new Object[] { contrato });

      if (!contratos.isEmpty()) {
         return (ContratoCestaGarantiaDO) contratos.get(0);
      }

      return null;
   }

   public ContratoCestaGarantiaDO obterVinculoContrato(CestaGarantiasDO cesta) {
      IGerenciadorPersistencia gp = getGp();
      String hql = "from ContratoCestaGarantiaDO cc where cc.cestaParte = ? or cc.cestaContraparte = ?";
      List contratos = gp.find(hql, new Object[] { cesta, cesta });

      if (!contratos.isEmpty()) {
         return (ContratoCestaGarantiaDO) contratos.get(0);
      }

      return null;
   }

   public void desvinculaPontaCesta(CestaGarantiasDO cesta) {
      ParametroPontaDO ponta = cesta.getParametroPonta();
      ponta.setPossuiCestaGarantia(new Booleano(Booleano.FALSO));
   }

   public CestaGarantiasDO obterCestaPorPonta(Id idParametroPonta) {
      IGerenciadorPersistencia gp = getGp();
      String hql = "from CestaGarantiasDO cg where cg.parametroPonta.idParametroPonta = ?";

      List cesta = gp.find(hql, new Object[] { idParametroPonta });

      if (!cesta.isEmpty()) {
         return (CestaGarantiasDO) cesta.get(0);
      }

      return null;
   }

   public ContratoCestaGarantiaDO obterVinculoContrato(ComplementoContratoDO contrato) {
      IGerenciadorPersistencia gp = getGp();
      String hql = "from ContratoCestaGarantiaDO cc where cc.contrato.codigoIF = ?";
      List contratos = gp.find(hql, new Object[] { contrato.getCodigoIF() });

      if (!contratos.isEmpty()) {
         return (ContratoCestaGarantiaDO) contratos.get(0);
      }

      return null;
   }

   public CestaGarantiasDO obterCestaPorPonta(ParametroPontaDO ponta) {
      IGerenciadorPersistencia gp = getGp();
      String hql = "from CestaGarantiasDO cg where cg.parametroPonta.idParametroPonta = ?";

      List cesta = gp.find(hql, new Object[] { ponta.getIdParametroPonta() });

      if (!cesta.isEmpty()) {
         return (CestaGarantiasDO) cesta.get(0);
      }

      return null;
   }

   public ParametroPontaDO[] obterPontas(ComplementoContratoDO contrato) {
      String hql = "from ParametroPontaDO p where p.contrato = ?";
      List l = getGp().find(hql, contrato);
      ParametroPontaDO[] pontas = (ParametroPontaDO[]) l.toArray(new ParametroPontaDO[l.size()]);
      return pontas;
   }

   public ParametroPontaDO obterPonta(ComplementoContratoDO contrato, ContaParticipanteDO conta, CPFOuCNPJ cpfCnpj) {
      if (conta.getCodContaParticipante().ehContaCliente()) {
         ComitenteDO comitente = null;

         try {
            IComitente cDao = ComitenteFactory.getInstance();
            comitente = cDao.obterComitente(cpfCnpj, cpfCnpj.obterNatureza(), conta.getCodContaParticipante());
         } catch (Exception e) {
            Logger.error(this, e);
            throw new Erro(CodigoErro.ERRO, "SIC: " + e.getMessage());
         }

         StringBuffer hql = new StringBuffer();
         hql.append(" select ppc.parametroPonta ");
         hql.append(" from ParametroPontaComitenteDO ppc ");
         hql.append(" where ppc.comitente = :comitente and ppc.parametroPonta.contrato = :contrato ");
         hql.append(" and ppc.parametroPonta.contaParticipante = :conta ");

         IConsulta c = getGp().criarConsulta(hql.toString());
         c.setAtributo("comitente", comitente);
         c.setAtributo("contrato", contrato);
         c.setAtributo("conta", conta);
         List l = c.list();

         if (l.size() != 1) {
            Erro erro = new Erro(CodigoErro.ERRO);
            erro.parametroMensagem("Contrato de conta cliente sem comitente.", 0);
            throw erro;
         }

         return (ParametroPontaDO) l.get(0);
      }

      StringBuffer hql = new StringBuffer();
      hql.append(" select pp ");
      hql.append(" from ParametroPontaDO pp ");
      hql.append(" where pp.contrato = :contrato ");
      hql.append(" and pp.contaParticipante = :conta ");

      IConsulta c = getGp().criarConsulta(hql.toString());
      c.setAtributo("contrato", contrato);
      c.setAtributo("conta", conta);
      List l = c.list();

      if (l.size() != 1) {
         Erro erro = new Erro(CodigoErro.ERRO);
         erro.parametroMensagem("Parametro ponta nao encontrado.", 0);
         throw erro;
      }

      return (ParametroPontaDO) l.get(0);
   }

   public ComplementoContratoDO obterContrato(CodigoIF codigoIF) {
      String hql = "select c from ComplementoContratoDO c where c.codigoIF = ? and c.dataHoraExclusao is null";
      List l = getGp().find(hql, codigoIF);

      if (l.isEmpty()) {
         throw new Erro(CodigoErro.INSTRUMENTO_FINANCEIRO_INEXISTENTE);
      }

      return (ComplementoContratoDO) l.get(0);
   }

   public Requisicao construirRequisicaoSwap(VinculacaoContratoVO vc) {
      RequisicaoServicoVinculaCestaContrato r = new RequisicaoServicoVinculaCestaContrato();

      // PARTE
      Id idCestaParte = null;
      if (!Condicional.vazio(vc.cestaParte)) {
         idCestaParte = vc.cestaParte.getNumIdCestaGarantias();
      }

      r.atribuirPARTE_CPFOuCNPJ(vc.comitenteParte);
      r.atribuirPARTE_CodigoContaCetip(vc.contaParte.getCodContaParticipante());
      r.atribuirPARTE_Id(idCestaParte);

      // CONTRA-PARTE
      Id idCestaContraparte = null;
      if (!Condicional.vazio(vc.cestaContraparte)) {
         idCestaContraparte = vc.cestaContraparte.getNumIdCestaGarantias();
      }

      r.atribuirCONTRA_PARTE_CPFOuCNPJ(vc.comitenteContraParte);
      r.atribuirCONTRA_PARTE_CodigoContaCetip(vc.contaContraparte.getCodContaParticipante());
      r.atribuirCONTRA_PARTE_Id(idCestaContraparte);

      // CONTRATO
      CodigoIFContrato codigoIFContrato = new CodigoIFContrato(vc.ativo.getCodigoIF().obterConteudo());
      r.atribuirINSTRUMENTO_FINANCEIRO_CodigoIFContrato(codigoIFContrato);

      Booleano reset = null;
      Texto regra = new Texto(Contexto.RESET, vc.regraLiberacao.obterConteudo());
      if (regra.mesmoConteudo(IContratosCesta.AJUSTES_EVENTO_VENC)) {
         reset = Booleano.VERDADEIRO;
      } else if (regra.mesmoConteudo(IContratosCesta.EVENTOS_VENCIMENTO)) {
         reset = Booleano.FALSO;
      }
      r.atribuirRESET_Booleano(reset);

      return r;
   }

   public CestaGarantiasDO obterCestaVinculadaPonta(Id idPonta) {
      CestaGarantiasDO cesta = obterCestaPorPonta(idPonta);

      if (cesta != null && (cesta.getStatusCesta().isVinculada() || cesta.getStatusCesta().isInadimplente())) {
         return cesta;
      }

      return null;
   }
}
