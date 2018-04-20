package br.com.cetip.aplicacao.garantias.servico.colateral;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.AutorizacaoPublicGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.ParametroPontaDO;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIFContrato;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSituacaoAutorizacaoGarantias;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTA_GARANTIDOR_MANUT_AUTORIZ"
 *                    
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTA_GARANTIDO_MANUT_AUTORIZ"
 *                    
 * @requisicao.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CPF_CNPJ_GARANTIDOR_MANUT_AUTORIZ"
 *                    
 * @requisicao.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CPF_CNPJ_GARANTIDO_MANUT_AUTORIZ"
 *                    
 * @requisicao.method atributo="CodigoIFContrato" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTRATO"
 *                    
 * @requisicao.method atributo="CodigoSituacaoAutorizacaoGarantias" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="SITUACAO"
 *                    
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTA_GARANTIDOR_MANUT_AUTORIZ"
 *                    
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTA_GARANTIDO_MANUT_AUTORIZ"
 *                    
 * @resultado.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CPF_CNPJ_GARANTIDOR_MANUT_AUTORIZ"
 *                    
 * @resultado.method atributo="CPFOuCNPJ" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CPF_CNPJ_GARANTIDO_MANUT_AUTORIZ"
 *                    
 * @resultado.method atributo="CodigoIFContrato" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTRATO"
 *                    
 * @resultado.method atributo="CodigoSituacaoAutorizacaoGarantias" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="SITUACAO"
 *                    
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CESTA"
 *                    
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="AUTORIZACAO_PUBLICIDADE_GARANTIAS"
 * 
 */
public class ServicoConsultarAutorizacoes extends BaseGarantias implements Servico {

   private CodigoContaCetip parte;
   private CodigoContaCetip contraparte;
   private CPFOuCNPJ docParte;
   private CPFOuCNPJ docContraparte;
   private CodigoSituacaoAutorizacaoGarantias situacao;
   private CodigoIFContrato contrato;
   private List hqlParams = new ArrayList();
   private StringBuffer hql = new StringBuffer();

   public Resultado executarConsulta(Requisicao r) throws Exception {
      RequisicaoServicoConsultarAutorizacoes req = (RequisicaoServicoConsultarAutorizacoes) r;

      receberValores(req);
      montarHQL();
      ResultadoServicoConsultarAutorizacoes res = consultar();
      return res;
   }

   private ResultadoServicoConsultarAutorizacoes consultar() {
      ResultadoServicoConsultarAutorizacoes res = new ResultadoServicoConsultarAutorizacoes();
      List l = getGp().find(hql.toString(), hqlParams.toArray());

      if (l.isEmpty()) {
         throw new Erro(CodigoErro.GARANTIAS_LISTA_VAZIA);
      }

      Iterator i = l.iterator();
      while (i.hasNext()) {
         Object[] row = (Object[]) i.next();
         final AutorizacaoPublicGarantiasDO autorizacao = (AutorizacaoPublicGarantiasDO) row[0];
         final CestaGarantiasDO cesta = (CestaGarantiasDO) row[1];
         final ParametroPontaDO pontaContraparte = (ParametroPontaDO) row[2];
         final ParametroPontaDO pontaParte = autorizacao.getParametroPonta();
         final CodigoIF codigoIF = pontaParte.getContrato().getCodigoIF();

         res.atribuirGARANTIAS_CESTA_Id(cesta.getNumIdCestaGarantias());
         res.atribuirCONTRATO_CodigoIFContrato(new CodigoIFContrato(codigoIF));
         res.atribuirSITUACAO_CodigoSituacaoAutorizacaoGarantias(autorizacao.getSituacao().getCodigo());
         res.atribuirCONTA_GARANTIDOR_MANUT_AUTORIZ_CodigoContaCetip(pontaParte.getContaParticipante().getCodContaParticipante());
         res.atribuirCONTA_GARANTIDO_MANUT_AUTORIZ_CodigoContaCetip(pontaContraparte.getContaParticipante().getCodContaParticipante());
         res.atribuirCPF_CNPJ_GARANTIDOR_MANUT_AUTORIZ_CPFOuCNPJ(pontaParte.getCPFOuCNPJ());
         res.atribuirCPF_CNPJ_GARANTIDO_MANUT_AUTORIZ_CPFOuCNPJ(pontaContraparte.getCPFOuCNPJ());
         res.atribuirAUTORIZACAO_PUBLICIDADE_GARANTIAS_Id(autorizacao.getId());
         res.novaLinha();
      }
      return res;
   }

   private void receberValores(RequisicaoServicoConsultarAutorizacoes req) {
      parte = req.obterCONTA_GARANTIDOR_MANUT_AUTORIZ_CodigoContaCetip();
      contraparte = req.obterCONTA_GARANTIDO_MANUT_AUTORIZ_CodigoContaCetip();
      docParte = req.obterCPF_CNPJ_GARANTIDOR_MANUT_AUTORIZ_CPFOuCNPJ();
      docContraparte = req.obterCPF_CNPJ_GARANTIDO_MANUT_AUTORIZ_CPFOuCNPJ();
      situacao = req.obterSITUACAO_CodigoSituacaoAutorizacaoGarantias();
      contrato = req.obterCONTRATO_CodigoIFContrato();
   }

   private void montarHQL() {
      hql.append("select a, c, p from AutorizacaoPublicGarantiasDO a, CestaGarantiasDO c, ParametroPontaDO p ");
      hql.append(" where a.parametroPonta = c.parametroPonta");
      hql.append("   and a.parametroPonta.contrato = p.contrato");
      hql.append("   and p <> a.parametroPonta");

      param(CodigoSituacaoAutorizacaoGarantias.INATIVO, "and a.situacao.codigo <> ?");
      param(contrato, "and a.parametroPonta.contrato.codigoIF = ?");
      param(parte, "and a.parametroPonta.contaParticipante.codContaParticipante = ?");
      param(contraparte, "and p.contaParticipante.codContaParticipante = ?");
      param(situacao, "and a.situacao.codigo = ?");
      param(docParte, "and a.parametroPonta.CPFOuCNPJ = ?");
      param(docContraparte, "and p.CPFOuCNPJ = ?");
   }

   private void param(Atributo param, String s) {
      if (!Condicional.vazio(param)) {
         hql.append(' ').append(s);
         hqlParams.add(param);
      }
   }

   public Resultado executar(Requisicao r) throws Exception {
      throw new UnsupportedOperationException();
   }

}
