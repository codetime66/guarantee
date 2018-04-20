package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.GarantidoresAstecVDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFDO;
import br.com.cetip.dados.aplicacao.sca.ObjetoServicoDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="ARQUIVO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="ARQUIVO"
 * 
 * @resultado.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_IF_GARANTIDOR"
 * 
 * @resultado.method atributo="QuantidadeInteira" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_QUANTIDADE"
 * 
 * @resultado.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="VOLUME_FINANCEIRO"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="CESTA_GARANTIA"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="REFERENCIA"
 */
public class ServicoGeraArquivoCestaAstecGarantidores extends BaseGarantias implements Servico {

   private ResultadoServicoGeraArquivoCestaAstecGarantidores res = new ResultadoServicoGeraArquivoCestaAstecGarantidores();

   private IGerenciadorPersistencia gp;

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoGeraArquivoCestaAstecGarantidores req = (RequisicaoServicoGeraArquivoCestaAstecGarantidores) requisicao;

      gp = getGp();
      Data dataOperacao = req.obterARQUIVO_Data();

      if (Condicional.vazio(dataOperacao)) {
         dataOperacao = getControleOperacional().obterDataBatch(new NumeroInteiro(0),
               new NumeroInteiro(ObjetoServicoDO.TIPO_IF_CCB.toString()));
      }

      List listaGarantidores = obterListaDeGarantidores();
      if (listaGarantidores.size() == 0) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Nao existem garantidores");
         }
         return res;
      }

      Iterator itGarantidores = listaGarantidores.iterator();
      while (itGarantidores.hasNext()) {

         GarantidoresAstecVDO garantidorDO = (GarantidoresAstecVDO) itGarantidores.next();
         TipoIFDO tipoIFDO = (TipoIFDO) gp.load(TipoIFDO.class, garantidorDO.getNumeroTipoIF());
         // preenche o resultado
         res.novaLinha();
         res.atribuirARQUIVO_Data(dataOperacao);
         res.atribuirGARANTIAS_QUANTIDADE_QuantidadeInteira(garantidorDO.getQuantidadeIFs());
         res.atribuirTIPO_IF_GARANTIDOR_CodigoTipoIF(tipoIFDO.getCodigoTipoIF());
         res.atribuirVOLUME_FINANCEIRO_ValorMonetario(garantidorDO.getVolumeFinanceiro());
         res.atribuirCESTA_GARANTIA_Texto(new Texto("QTD_CESTAS="
               + garantidorDO.getQuantidadeCestas().obterConteudo().intValue()));
         Texto Ano = new Texto(dataOperacao.obterAno().toString());
         Texto Mes = dataOperacao.obterMes().obterInt() < 10 ? new Texto("0" + dataOperacao.obterMes().toString())
               : new Texto(dataOperacao.obterMes().toString());
         Texto Dia = dataOperacao.obterDia().obterInt() < 10 ? new Texto("0" + dataOperacao.obterDia().toString())
               : new Texto(dataOperacao.obterDia().toString());
         res.atribuirREFERENCIA_Texto(new Texto("DAT_REFERENCIA=" + Ano + Mes + Dia));
      }

      return res;
   }

   private List obterListaDeGarantidores() {
      IConsulta con = null;
      con = gp.criarConsulta("from " + GarantidoresAstecVDO.class.getName());

      return con.list();
   }

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}
