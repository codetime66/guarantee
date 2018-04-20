package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.apinegocio.ILiberacaoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico para LIBERACAO dos ativos de Cesta de Garantia
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="NumeroCestaGarantia"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="Data"
 *                   pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="CPFOuCNPJ"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @requisicao.method atributo="Data"
 *                    pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                    contexto="OPERACAO"
 * 
 * @requisicao.method atributo="Booleano"
 *                    pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="GARANTIA"
 */
public class ServicoExecutarCestaGarantias extends BaseGarantias implements Servico {

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao req) throws Exception {
      RequisicaoServicoExecutarCestaGarantias requisicao = (RequisicaoServicoExecutarCestaGarantias) req;
      ResultadoServicoExecutarCestaGarantias res = new ResultadoServicoExecutarCestaGarantias();

      NumeroCestaGarantia nrCesta = requisicao.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      Data data = requisicao.obterOPERACAO_Data();
      CPFOuCNPJ comitente = requisicao.obterGARANTIAS_CONTRAPARTE_CPFOuCNPJ();

      IGarantias factory = getFactory();
      IMovimentacoesGarantias img = factory.getInstanceMovimentacoesGarantias();
      ICestaDeGarantias dao = factory.getInstanceCestaDeGarantias();

      CestaGarantiasDO cesta = dao.obterCestaDeGarantias(nrCesta);

      // Lanca liberacao parcial para todas as garantias SELIC
      liberaGarantiasSelic(cesta);

      // insere uma liberacao total associada a um dos ativos garantidos
      Iterator itVinculados = cesta.getAtivosVinculados().iterator();
      CestaGarantiasIFDO vinculo = (CestaGarantiasIFDO) itVinculados.next();
      InstrumentoFinanceiroDO ativo = vinculo.getInstrumentoFinanceiro();

      // Insere tipoMov e statusMov necessarios para inserir uma MovGarantia e posteriormente executar esta cesta
      // associa a um dos ativos
      MovimentacaoGarantiaDO movLiberacaoTotal = img.incluirMovimentacaoLiberacao(cesta, ativo);
      movLiberacaoTotal.setCpfOuCnpjComitente(comitente);

      // insere movimentacao de desvinculacao para todos os ativos garantidos e remove o vinculo
      desvincularGarantidos(img, cesta, itVinculados);

      // aciona a liberacao total
      acionaGarantiasLiberadas(cesta, data, ativo.getTipoIF(), ativo.getSistema());

      res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(nrCesta);
      return res;
   }

   private void desvincularGarantidos(IMovimentacoesGarantias img, CestaGarantiasDO cesta, Iterator itVinculados) {
      while (itVinculados.hasNext()) {
         CestaGarantiasIFDO iVinculo = (CestaGarantiasIFDO) itVinculados.next();

         InstrumentoFinanceiroDO iAtivo = iVinculo.getInstrumentoFinanceiro();
         MovimentacaoGarantiaDO movDesvinc = img.incluirMovimentacaoDesvinculacao(cesta, iAtivo);

         // marca como OK se nao eh a ultima garantia
         if (itVinculados.hasNext()) {
            movDesvinc.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);
         }

         // deleta o vinculo entre cesta e ativo
         itVinculados.remove();
         getGp().delete(iVinculo);
      }
   }

   private void liberaGarantiasSelic(CestaGarantiasDO cesta) {
      IGarantiasSelic gselic = getFactory().getInstanceGarantiasSelic();
      if (gselic.temSelicNaCesta(cesta)) {
         gselic.liberarGarantias(cesta);
      }
   }

   private void acionaGarantiasLiberadas(CestaGarantiasDO cesta, Data data, TipoIFDO tipoIF, SistemaDO sistema) {
      Data d0 = Condicional.vazio(data) ? getDataHoje() : data;
      cesta.setStatusCesta(StatusCestaDO.EM_LIBERACAO);
      cesta.setDatAlteracaoStatusCesta(d0);
      cesta.setDatExecucao(d0);

      // elimina as garantias "nao cetipadas"
      IGerenciadorPersistencia gp = getGp();
      String hql = "from DetalheGarantiaDO d where d.indCetipado = :indCetipado and d.cestaGarantias = :cesta and d.quantidadeGarantia > 0 ";

      IConsulta c = gp.criarConsulta(hql);
      c.setAtributo("indCetipado", new Booleano(Booleano.FALSO));
      c.setAtributo("cesta", cesta);

      Iterator iter = c.list().iterator();
      while (iter.hasNext()) {
         DetalheGarantiaDO detalhe = (DetalheGarantiaDO) iter.next();
         detalhe.setQuantidadeGarantia(new Quantidade("0"));
         gp.update(detalhe);
      }

      ILiberacaoCesta liberacao = getFactory().getInstanceLiberacaoCesta(sistema.getNumero(), tipoIF.getCodigoTipoIF());
      liberacao.liberar(cesta, data);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      return null;
   }

}