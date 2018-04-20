package br.com.cetip.aplicacao.garantias.servico;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.IMIGAcionador;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.MIGAcionadorFactory;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.tempo.DataHora;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="DATA_VENCIMENTO"
 * 
 * @requisicao.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="TIPO_IF"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="NumeroInteiro" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ServicoRemoverGarantiasVencidas extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Processo de remover (desbloquear ou retirar) Garantias "
               + "quando do Vencimento para Ativos terminados");
      }

      RequisicaoServicoRemoverGarantiasVencidas req = (RequisicaoServicoRemoverGarantiasVencidas) requisicao;

      Data data = null;
      if (!Condicional.vazio(req.obterDATA_VENCIMENTO_Data())) {
         data = req.obterDATA_VENCIMENTO_Data();
      } else {
         data = getDataHoje();
      }

      List tiposIF = req.obterListaTIPO_IF_CodigoTipoIF();
      if (tiposIF == null) {
         tiposIF = Collections.EMPTY_LIST;
      }

      int qtdAtualizada = removerGarantiasVencimento(data, tiposIF);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Total de garantias removidas = " + qtdAtualizada);
      }

      ResultadoServicoRemoverGarantiasVencidas res = new ResultadoServicoRemoverGarantiasVencidas();
      res.atribuirINSTRUMENTO_FINANCEIRO_NumeroInteiro(new NumeroInteiro(qtdAtualizada));
      return res;
   }

   private int removerGarantiasVencimento(Data data, List tiposIF) {
      IGerenciadorPersistencia gp = getGp();

      // Recupera Ativos que vencem na data ou jah venceram
      StringBuffer hqlb = new StringBuffer(500);
      hqlb.append("from ");
      hqlb.append(DetalheGarantiaDO.class.getName()).append(" dg ");
      hqlb.append(" inner join fetch dg.cestaGarantias cesta ");
      hqlb.append(" where dg.quantidadeGarantia > 0 and ");
      hqlb.append(" dg.instrumentoFinanceiro.dataVencimento <= :data ");
      hqlb.append(" and dg.cestaGarantias.datExclusao is null ");

      if (tiposIF.isEmpty() == false) {
         hqlb.append(" and dg.instrumentoFinanceiro.tipoIF.codigoTipoIF in (:tiposIF) ");
      }

      IConsulta cons = gp.criarConsulta(hqlb.toString());
      cons.setAtributo("data", data);

      if (tiposIF.isEmpty() == false) {
         cons.setParameterList("tiposIF", tiposIF);
      }

      List garantias = cons.list();

      Iterator it = garantias.iterator();
      if (it.hasNext() == false) {
         return 0;
      }

      Quantidade qtdZero = new Quantidade("0");

      Set situacoesParaBloqueio = new HashSet();
      situacoesParaBloqueio.add(StatusCestaDO.FINALIZADA);
      situacoesParaBloqueio.add(StatusCestaDO.INCOMPLETA);
      situacoesParaBloqueio.add(StatusCestaDO.EM_EDICAO);

      DataHora dtHoje = new DataHora(new Date());

      while (it.hasNext()) {
         DetalheGarantiaDO detGarantia = (DetalheGarantiaDO) it.next();

         /*
          * Consulta se o ativo vinculado a cesta desta garantia, garante outra cesta. Caso sim, entao o ativo esta em
          * carteira de segundo nivel e caso a cesta esteja vinculada e a operacao de RETIRADA seja processada, entao
          * carregar tipo de debito de segundo nivel (ativoVincGaranteCesta = true)
          */
         CestaGarantiasDO cesta = detGarantia.getCestaGarantias();
         StatusCestaDO statusCesta = cesta.getStatusCesta();

         IMIGAcionador mig = MIGAcionadorFactory.getInstance();

         // Cria uma entrada de DESBLOQUEIO na tabela MOVIMENTACAO_GARANTIA
         if (situacoesParaBloqueio.contains(statusCesta)) {
            IMovimentacoesGarantias imovs = getFactory().getInstanceMovimentacoesGarantias();
            MovimentacaoGarantiaDO mov = imovs.incluirMovimentacaoDesbloqueio(detGarantia);
            mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);
            gp.update(mov);
            mig.acionarDesbloqueioFinalizado(mov);
         } else {
            // Cria uma entrada na tabela MOVIMENTACAO_GARANTIA
            IMovimentacoesGarantias imov = getFactory().getInstanceMovimentacoesGarantias();
            final Quantidade quantidadeGarantia = detGarantia.getQuantidadeGarantia();
            MovimentacaoGarantiaDO mov = imov.incluirMovimentacaoRetirada(detGarantia, quantidadeGarantia);
            mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);
            gp.update(mov);
            mig.acionarRetiradaFinalizada(mov);
         }

         detGarantia.setQuantidadeGarantia(qtdZero);
         detGarantia.setDataAlteracao(dtHoje);
         gp.update(detGarantia);
      }

      return garantias.size();
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      return null;
   }

}
