package br.com.cetip.aplicacao.garantias.servico;

import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.IInstrumentoFinanceiro;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * Servico para validacao dos dados inseridos pelo usuario na interface grafica de compra/venda de cda/wa.
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vincius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @since Abril/2006
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @resultado.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_IF"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_QUANTIDADE"
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_IF"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_QUANTIDADE"
 * 
 * @requisicao.method atributo="NumeroOperacao"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="OPERACAO"
 * 
 * @requisicao.method atributo="CPFOuCNPJ"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 */
public class ServicoValidaExecucaoParcialCestaGarantias extends BaseGarantias implements Servico {

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      return null;
   }

   /*
    * Realiza a validacao dos dados digitados via motor de regras.
    * 
    * @param requisicao requisicao que contem os dados para validacao
    * 
    * @return o Resultado contendo se a validacao foi aceita ou nao
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoValidaExecucaoParcialCestaGarantias req = (RequisicaoServicoValidaExecucaoParcialCestaGarantias) requisicao;
      ResultadoServicoValidaExecucaoParcialCestaGarantias res = new ResultadoServicoValidaExecucaoParcialCestaGarantias();

      NumeroCestaGarantia numero = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      CodigoTipoIF tipoIF = req.obterGARANTIAS_CODIGO_TIPO_CodigoTipoIF();
      CodigoIF codigoIF = req.obterGARANTIAS_CODIGO_IF_CodigoIF();
      Quantidade quantidade = req.obterGARANTIAS_QUANTIDADE_Quantidade();
      NumeroOperacao nuOp = req.obterOPERACAO_NumeroOperacao();
      CPFOuCNPJ comitente = req.obterGARANTIAS_CONTRAPARTE_CPFOuCNPJ();

      IGarantias factory = getFactory();

      ICestaDeGarantias dao = factory.getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = dao.obterCestaDeGarantias(numero);
      Id statusBD = cesta.getStatusCesta().getNumIdStatusCesta();

      IGarantidoCesta igc = factory.getInstanceGarantidoCesta();
      CodigoContaCetip contaBD = igc.obterGarantidoCesta(cesta).getCodContaParticipante();

      Id sistema = SistemaDO.CETIP21;
      InstrumentoFinanceiroDO ativo = null;

      if (!tipoIF.ehNAO_CETIPADO()) {
         IInstrumentoFinanceiro instFinDao = InstrumentoFinanceiroFactory.getInstance();
         ativo = instFinDao.obterInstrumentoFinanceiro(codigoIF);
         sistema = ativo.getSistema().getNumero();
      }

      AtributosColunados ac = new AtributosColunados();
      ac.atributo(numero);
      ac.atributo(tipoIF);
      ac.atributo(codigoIF);
      ac.atributo(quantidade);
      ac.atributo(statusBD);
      ac.atributo(contaBD);
      ac.atributo(sistema);
      ac.atributo(nuOp);
      ac.atributo(comitente);
      ac.atributo(comitente.obterNatureza());

      // chama uma regra criada pela cetip para validacao
      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeExecutarParcialCestaGarantias, ac,
            true);

      DetalheGarantiaDO vDetalhe = tipoIF.ehNAO_CETIPADO() ? obterGarantia(cesta, codigoIF) : obterGarantia(cesta,
            ativo);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "*** Parametros da Detalhe Garantia do BD e da tela... ***");
         Logger.debug(this, "Cesta Tela: " + numero);
         Logger.debug(this, "ID DETALHE: " + vDetalhe.getNumIdDetalheGarantia());
         Logger.debug(this, "Cesta BD: " + vDetalhe.getCestaGarantias().getNumIdCestaGarantias());
         Logger.debug(this, "CodigoIF Tela: " + codigoIF);
         if (!tipoIF.ehNAO_CETIPADO()) {
            Logger.debug(this, "Codigo IF BD: " + vDetalhe.getInstrumentoFinanceiro().getCodigoIF());
         }
         Logger.debug(this, "Qt Tela: " + quantidade);
         Logger.debug(this, "Qt BD: " + vDetalhe.getQuantidadeGarantia());
         Logger.debug(this, "Status Cesta: " + vDetalhe.getCestaGarantias().getStatusCesta().getNumIdStatusCesta());
         Logger.debug(this, "*** Fim dos Parametros... ***");
      }

      int compara = quantidade.compareTo(vDetalhe.getQuantidadeGarantia());
      if (compara > 0) {
         throw new Erro(CodigoErro.QUANTIDADE_INVALIDA, quantidade.toString());
      }

      res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
      res.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(tipoIF);
      res.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);
      res.atribuirGARANTIAS_QUANTIDADE_Quantidade(quantidade);
      res.atribuirGARANTIAS_CODIGO_Id(vDetalhe.getNumIdDetalheGarantia());

      Data d0 = getDataHoje();
      Data dataInadimplencia = cesta.getDatInadimplencia();

      int prazo = getFactory().getInadimplencia();
      int comparacaoData = dataInadimplencia.somarDiasCorridos(prazo).comparar(d0);
      if (!(comparacaoData >= 0)) {
         throw new Erro(CodigoErro.DT_INADIMPLENCIA);
      }

      return res;
   }

   private DetalheGarantiaDO obterGarantia(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ativo) {
      String hql = "from DetalheGarantiaDO d where d.cestaGarantias = ? and d.instrumentoFinanceiro = ? and d.quantidadeGarantia > 0";
      List l = getGp().find(hql, new Object[] { cesta, ativo });

      if (l.isEmpty()) {
         throw new Erro(CodigoErro.ERRO, "Garantia não encontrada");
      }

      return (DetalheGarantiaDO) l.get(0);
   }

   private DetalheGarantiaDO obterGarantia(CestaGarantiasDO cesta, CodigoIF codigoIF) {
      String hql = "from DetalheGarantiaDO d where d.cestaGarantias = ? and d.codIfNCetipado = ? and d.quantidadeGarantia > 0";
      List l = getGp().find(hql, new Object[] { cesta, codigoIF });

      if (l.isEmpty()) {
         throw new Erro(CodigoErro.ERRO, "Garantia não encontrada");
      }

      return (DetalheGarantiaDO) l.get(0);
   }
}
