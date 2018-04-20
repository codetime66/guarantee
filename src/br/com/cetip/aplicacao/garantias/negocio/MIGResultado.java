package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.administracao.apinegocio.ControleOperacionalFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.GarantiasFactory;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IConsultaGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMIGResultado;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoAcionaFechamentoCesta;
import br.com.cetip.aplicacao.garantias.servico.ServicoAcionaFechamentoCesta;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaIFDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.IdStatusMovimentacaoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.GerenciadorPersistenciaFactory;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

/**
 * <p>
 * Mudanca do status da movimentacao com o retorno da operacao
 * </p>
 * <p>
 * Mudanca do status da cesta caso algumas regras sejam satisfatorias
 * </p>
 * 
 * @author marco sergio
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class MIGResultado implements IMIGResultado {

   private IGerenciadorPersistencia gp;

   private IGarantias garantias;

   public MIGResultado() {
      garantias = GarantiasFactory.getInstance();
   }

   public void resultadoOperacao(MovimentacaoGarantiaDO mov, IdStatusMovimentacaoGarantia idStatusMovGarantia,
         Texto txtOperacao) {

      gp = GerenciadorPersistenciaFactory.getGerenciadorPersistencia();

      if (!Condicional.vazio(txtOperacao)) {
         mov.setTxtDescricao(txtOperacao);
      }

      mov.setStatusMovimentacaoGarantia(new StatusMovimentacaoGarantiaDO(idStatusMovGarantia));

      gp.update(mov);

      if (mov.getTipoMovimentacaoGarantia().equals(TipoMovimentacaoGarantiaDO.BLOQUEIO)
            && mov.getInstrumentoFinanceiro().getSistema().getNumero().mesmoConteudo(SistemaDO.CETIP21) == false
            && !idStatusMovGarantia.mesmoConteudo(IdStatusMovimentacaoGarantia.PENDENTE_ATUALIZA)) {
         RequisicaoServicoAcionaFechamentoCesta rv = new RequisicaoServicoAcionaFechamentoCesta();
         rv.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(new NumeroCestaGarantia(mov.getCestaGarantias()
               .getNumIdCestaGarantias().obterConteudo()));
         try {
            new ServicoAcionaFechamentoCesta().executar(rv);
         } catch (Exception e) {
            throw new Erro(CodigoErro.ERRO, e.getMessage());
         }
      }

      boolean statusMovOK = idStatusMovGarantia.mesmoConteudo(IdStatusMovimentacaoGarantia.OK);
      if (statusMovOK) {
         avaliaStatusCesta(mov);
      }
   }

   private void avaliaStatusCesta(MovimentacaoGarantiaDO mov) {
      CestaGarantiasDO cesta = mov.getCestaGarantias();
      TipoMovimentacaoGarantiaDO tipoMov = mov.getTipoMovimentacaoGarantia();
      InstrumentoFinanceiroDO ifDO = mov.getInstrumentoFinanceiro();
      StatusCestaDO statusCesta = cesta.getStatusCesta();

      Data hoje;
      try {
         hoje = ControleOperacionalFactory.getInstance().obterD0();
      } catch (Exception e) {
         throw new Erro(CodigoErro.ERRO, e.getMessage());
      }

      IConsultaGarantias ics = garantias.getConsultaGarantias();

      // Se o tipo de movimentacao eh de vinculacao e a
      // cesta esta vinculada no mainframe, atualiza
      // status da cesta para VINCULADA (status final de vinculacao)
      if ((statusCesta.isVinculadaAoAtivo() || statusCesta.isVinculada())
            && tipoMov.equals(TipoMovimentacaoGarantiaDO.VINCULACAO) && !ics.existeGarantiasAltaPlataforma(cesta)) {

         CestaGarantiasIFDO cgi = garantias.getInstanceCestaDeGarantias().obterIFDOVinculado(cesta, ifDO);
         cgi.setStatus(StatusCestaIFDO.VINCULADA);
         cgi.setDatAlteracao(hoje);

         if (statusCesta.isVinculadaAoAtivo()) {
            cesta.setStatusCesta(StatusCestaDO.VINCULADA, hoje);
         }

         gp.update(cgi);
         gp.update(cesta);
         return;
      }

      // Se o tipo de movimentacao eh de LIBERACAO e a
      // cesta esta desvinculada no mainframe, atualiza
      // status da cesta para GRT_LIBERADAS (status final de vinculacao)
      if (statusCesta.equals(StatusCestaDO.EM_LIBERACAO) && tipoMov.equals(TipoMovimentacaoGarantiaDO.LIBERACAO)) {
         IMovimentacoesGarantias im = garantias.getInstanceMovimentacoesGarantias();
         MovimentacaoGarantiaDO ultimaMov = im.obterUltimaMovimentacao(cesta, TipoMovimentacaoGarantiaDO.DESVINCULACAO,
               StatusMovimentacaoGarantiaDO.PENDENTE);
         if (ultimaMov != null) {
            ultimaMov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);
            gp.update(ultimaMov);
         }

         cesta.setStatusCesta(StatusCestaDO.GRT_LIBERADAS, hoje);
         gp.update(cesta);
         return;
      }

      if ((tipoMov.equals(TipoMovimentacaoGarantiaDO.LIBERACAO_PENHOR_EMISSOR)
            || tipoMov.equals(TipoMovimentacaoGarantiaDO.LIBERACAO_PARCIAL) || tipoMov
            .equals(TipoMovimentacaoGarantiaDO.RETIRADA))) {
         ICestaDeGarantias cdg = garantias.getInstanceCestaDeGarantias();
         cdg.verificaNecessidadeDesvincularCesta(cesta);
         return;
      }

      if (tipoMov.equals(TipoMovimentacaoGarantiaDO.BLOQUEIO_EM_LASTRO)) {
         cesta.setIndSegundoNivel(Booleano.VERDADEIRO);
         return;
      }

      if (tipoMov.equals(TipoMovimentacaoGarantiaDO.RETIRADA_EM_LASTRO)) {
         cesta.setIndSegundoNivel(Booleano.FALSO);
         return;
      }
   }

}
