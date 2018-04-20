package br.com.cetip.aplicacao.garantias.servico.selic;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoContaSelic"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CONTA"
 * 
 * @requisicao.method atributo="CodigoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CODIGO_IF"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Quantidade"
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="QUANTIDADE"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CESTA_GARANTIA"
 *                   
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" 
 * 					 contexto="GARANTIAS_ITENS"                  
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIDOR"
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CONTA_GARANTIDOR_PROPRIA_SELIC"
 * 
 * @resultado.method atributo="NomeSimplificado"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIDOR"
 * 
 * @resultado.method atributo="CodigoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CODIGO_IF"
 * 
 * @resultado.method atributo="CodigoTipoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CODIGO_TIPO_IF"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="DATA_VENCIMENTO"
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIDO"
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CONTA_GARANTIDO_PROPRIA_SELIC"
 * 
 * @resultado.method atributo="NomeSimplificado"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIDO"
 * 
 */
public class ServicoConsultaDetalhesGarantiasSelic extends BaseGarantias implements Servico {

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {

      RequisicaoServicoConsultaDetalhesGarantiasSelic req = (RequisicaoServicoConsultaDetalhesGarantiasSelic) requisicao;
      ResultadoServicoConsultaDetalhesGarantiasSelic res = new ResultadoServicoConsultaDetalhesGarantiasSelic();

      CodigoIF codIF = req.obterCODIGO_IF_CodigoIF();

      IGarantias gf = getFactory();
      IGarantiasSelic dao = gf.getInstanceGarantiasSelic();
      List detalhes = dao.obterDetalhesPosicaoSaldo(codIF);

      Iterator i = detalhes.iterator();

      while (i.hasNext()) {
         DetalheGarantiaDO detalhe = (DetalheGarantiaDO) i.next();
         CestaGarantiasDO cesta = detalhe.getCestaGarantias();

         CodigoContaCetip contaGarantido = cesta.getGarantido() == null ? null : cesta.getGarantido()
               .getCodContaParticipante();
         CodigoContaCetip contaSelicGarantidor = dao.obterContaSelic(cesta.getGarantidor().getCodContaParticipante())
               .getCodContaParticipante();
         CodigoContaCetip conta60SelicGarantido = contaGarantido == null ? new CodigoContaCetip() : dao
               .obterContaGarantiaSelic(contaGarantido).getCodContaParticipante();

         Iterator ativosGarantido = cesta.getAtivosVinculados().iterator();
         InstrumentoFinanceiroDO ifDO = null;
         //ativos selic so contem um ativo vinculado
         if (ativosGarantido.hasNext()) {
            ifDO = ((CestaGarantiasIFDO) ativosGarantido.next()).getInstrumentoFinanceiro();
         }

         res.novaLinha();
         res.atribuirQUANTIDADE_Quantidade(detalhe.getQuantidadeGarantia());
         res.atribuirCESTA_GARANTIA_Id(detalhe.getCestaGarantias().getNumIdCestaGarantias());
         res.atribuirGARANTIAS_ITENS_Booleano(detalhe.getIndDireitosGarantidor());
         res.atribuirGARANTIDOR_CodigoContaCetip(detalhe.getCestaGarantias().getGarantidor().getCodContaParticipante());
         res.atribuirCONTA_GARANTIDOR_PROPRIA_SELIC_CodigoContaCetip(contaSelicGarantidor);
         res.atribuirGARANTIDOR_NomeSimplificado(detalhe.getCestaGarantias().getGarantidor().getParticipante()
               .getNomSimplificadoEntidade());
         res.atribuirCODIGO_TIPO_IF_CodigoTipoIF(ifDO == null ? null : ifDO.getTipoIF().getCodigoTipoIF());
         res.atribuirCODIGO_IF_CodigoIF(ifDO == null ? null : ifDO.getCodigoIF());
         res.atribuirDATA_VENCIMENTO_Data(ifDO == null ? null : ifDO.getDataVencimento());
         res.atribuirGARANTIDO_CodigoContaCetip(contaGarantido);
         res.atribuirCONTA_GARANTIDO_PROPRIA_SELIC_CodigoContaCetip(conta60SelicGarantido);
         res.atribuirGARANTIDO_NomeSimplificado(contaGarantido == null ? null : cesta.getGarantido().getParticipante()
               .getNomSimplificadoEntidade());
      }

      return res;
   }

   public Resultado executar(Requisicao arg0) throws Exception {
      throw new ExcecaoServico(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

}
