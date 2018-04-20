package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.batch.design.RelatorioCestaGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="ARQUIVO"
 * 
 * @requisicao.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="ARQUIVO"
 * 
 * @requisicao.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="RELATORIO"
 * 
 * @resultado.class
 * 
 */
public class ServicoRelatorioCestaGarantias extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      return null;
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoRelatorioCestaGarantias req = (RequisicaoServicoRelatorioCestaGarantias) requisicao;
      ResultadoServicoRelatorioCestaGarantias res = new ResultadoServicoRelatorioCestaGarantias();
      RelatorioCestaGarantias relatorio = new RelatorioCestaGarantias();

      Data dataParam = req.obterARQUIVO_Data();
      Texto pathArquivo = req.obterARQUIVO_Texto();
      Nome nomeRelParam = req.obterRELATORIO_Nome();

      if (Condicional.vazio(dataParam)) {
         dataParam = getControleOperacional().obterDataBatch(new NumeroInteiro(0), new NumeroInteiro(47));
      }

      relatorio.execute(dataParam, pathArquivo, nomeRelParam);
      return res;
   }

}