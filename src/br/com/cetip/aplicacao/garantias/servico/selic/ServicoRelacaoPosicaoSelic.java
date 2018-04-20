package br.com.cetip.aplicacao.garantias.servico.selic;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.custodia.PosicaoSelicDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sap.IdentificacaoISPBDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoParticipanteISPB;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleIF;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteira;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method
 *     atributo="CodigoContaSelic"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CONTA"
 * 
 * @requisicao.method
 *     atributo="CodigoIF"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CODIGO_IF"
 *
 * @resultado.class
 * 
 * @resultado.method
 *     atributo="CodigoContaSelic"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CONTA"
 * 
 * @resultado.method
 *     atributo="CodigoIF"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CODIGO_IF"
 *     
 * @resultado.method
 *     atributo="Data"
 *     pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *     contexto="DATA_VENCIMENTO"
 *     
 * @resultado.method
 *     atributo="Data"
 *     pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *     contexto="DATA_REFERENCIA"
 *     
 * @resultado.method
 *     atributo="Data"
 *     pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *     contexto="DATA_REQUISICAO"
 *     
 * @resultado.method
 *     atributo="QuantidadeInteira"
 *     pacote="br.com.cetip.infra.atributo.tipo.numero"
 *     contexto="QUANTIDADE"
 *     
 * @resultado.method
 *     atributo="CodigoParticipanteISPB"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="PARTICIPANTE" 
 *     
 * @resultado.method
 *     atributo="NumeroControleIF"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="NUM_CONTROLE"         
 * 
 */
public class ServicoRelacaoPosicaoSelic extends BaseGarantias implements Servico {

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {

      RequisicaoServicoRelacaoPosicaoSelic req = (RequisicaoServicoRelacaoPosicaoSelic) requisicao;
      ResultadoServicoRelacaoPosicaoSelic res = new ResultadoServicoRelacaoPosicaoSelic();

      CodigoContaSelic contaSelic = req.obterCONTA_CodigoContaSelic();
      CodigoIF codIF = req.obterCODIGO_IF_CodigoIF();

      IGarantias gf = getFactory();
      IGarantiasSelic dao = gf.getInstanceGarantiasSelic();
      List posicaoSelic = dao.obterPosicaoSaldo(codIF, contaSelic);

      IGerenciadorPersistencia gp = getGp();

      Iterator i = posicaoSelic.iterator();

      while (i.hasNext()) {
         PosicaoSelicDO ativo = (PosicaoSelicDO) i.next();

         InstrumentoFinanceiroDO ifSelic = (InstrumentoFinanceiroDO) gp.load(InstrumentoFinanceiroDO.class, ativo
               .getNumIF());
         ContaParticipanteDO contaParticipanteSelic = (ContaParticipanteDO) gp.load(ContaParticipanteDO.class, ativo
               .getNumIdContaParticipante());
         IdentificacaoISPBDO ispb = (IdentificacaoISPBDO) gp.load(IdentificacaoISPBDO.class, ativo.getNumIdISPB());

         CodigoIF codigoIF = ifSelic.getCodigoIF();
         CodigoContaSelic codigoContaSelic = new CodigoContaSelic(contaParticipanteSelic.getCodContaParticipante()
               .obterConteudo());
         Quantidade qtdSaldo = ativo.getQtdeSaldo();
         Data dtaRequisicao = ativo.getDataRequisicao();
         CodigoParticipanteISPB participanteISPB = new CodigoParticipanteISPB(ispb.getCodParticISPB().obterConteudo());
         NumeroControleIF numControleIF = ativo.getNumeroControleIF();
         Data dtaVencimento = ifSelic.getDataVencimento();
         Data dtaReferencia = ativo.getDataReferencia();

         res.novaLinha();
         res.atribuirCODIGO_IF_CodigoIF(codigoIF);
         res.atribuirCONTA_CodigoContaSelic(codigoContaSelic);
         res.atribuirQUANTIDADE_QuantidadeInteira(new QuantidadeInteira(qtdSaldo.obterBigDecimal().intValue()));
         res.atribuirDATA_REQUISICAO_Data(dtaRequisicao);
         res.atribuirPARTICIPANTE_CodigoParticipanteISPB(participanteISPB);
         res.atribuirNUM_CONTROLE_NumeroControleIF(numControleIF);
         res.atribuirDATA_VENCIMENTO_Data(dtaVencimento);
         res.atribuirDATA_REFERENCIA_Data(dtaReferencia);
      }

      return res;
   }

   public Resultado executar(Requisicao arg0) throws Exception {
      // TODO Auto-generated method stub
      return null;
   }

}
