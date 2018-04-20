package br.com.cetip.aplicacao.garantias.apinegocio;

import java.util.List;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;

public interface IMovimentacoesGarantias {

   /**
    * Cadastra movimentacao de desbloqueio para a garantia informada Retorna a nova movimentacao jah salva no
    * Gerenciador de Persistencia corrente
    * 
    * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
    * @param garantia
    * @return
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoDesbloqueio(DetalheGarantiaDO garantia);

   /**
    * 
    * @param cesta
    * @return
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoExclusao(CestaGarantiasDO cesta);

   /**
    * 
    * @param cesta
    * @return
    * @
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoDesvinculacao(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ifDO);

   /**
    * 
    * @param cesta
    * @param contaParticipante
    * @return
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoTransferencia(CestaGarantiasDO cesta,
         ContaParticipanteDO contaParticipante, InstrumentoFinanceiroDO ifDO);

   /**
    * 
    * @param cesta
    * @return
    * @
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoRetiradaLastro(CestaGarantiasDO cesta);

   /**
    * 
    * @param cesta
    * @return
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoBloqueioLastro(CestaGarantiasDO cesta);

   /**
    * 
    * @param cesta
    * @return
    * @
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoLiberacao(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ifDO);

   /**
    * 
    * @param garantia
    * @param quantidade
    * @return
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoLiberacaoParcial(DetalheGarantiaDO garantia, Quantidade quantidade,
         CodigoContaCetip destino, NumeroOperacao numeroOperacao);

   /**
    * 
    * @param cesta
    * @param instrumento
    * @return
    * @
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoVinculacao(CestaGarantiasDO cesta,
         InstrumentoFinanceiroDO instrumento);

   /**
    * 
    * @param idCesta
    * @param idTipo
    * @param idStatus
    * @return
    */
   public MovimentacaoGarantiaDO obterUltimaMovimentacao(CestaGarantiasDO cesta, TipoMovimentacaoGarantiaDO tipo,
         StatusMovimentacaoGarantiaDO status);

   /**
    * 
    * @param idCesta
    * @param idTipo
    * @param idStatus
    * @return
    */
   public MovimentacaoGarantiaDO obterMovimentacaoParaAtivo(CestaGarantiasDO cesta, Atributo codGarantia,
         TipoMovimentacaoGarantiaDO tipo, StatusMovimentacaoGarantiaDO status);

   /**
    * 
    * @param cesta
    * @param idTipoGarantia
    * @param indDireitosGarantidor
    * @param quantidade
    * @param codigoIF
    * @param descricao
    * @param codigoTipoIF
    * @return
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoBloqueio(CestaGarantiasDO cesta, GarantiaVO garantia);

   /**
    * 
    * @param cesta
    * @param idTipoGarantia
    * @param indDireitosGarantidor
    * @param quantidade
    * @param codigoIF
    * @param descricao
    * @param codigoTipoIF
    * @return
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoAporte(CestaGarantiasDO cesta, GarantiaVO garantia);

   /**
    * <p>Lista as movimentacoes de uma cesta de garantias, aplicando filtro de Tipo e de Status</p>
    * @param numIdCestaGarantias
    * @param tipo
    * @param status
    * @return
    */
   public List listarMovimentacoes(CestaGarantiasDO cesta, Object[] tipo, Object[] status);

   /**
    * 
    * @param idTipoGarantia
    * @param indDireitosGarantidor
    * @param quantidade
    * @param numIF
    * @param numSistema
    * @param descricao
    * @param codigoTipoIF
    * @return
    */
   public MovimentacaoGarantiaDO criarMovimentacaoBloqueio(GarantiaVO garantia);

   /**
    * <p>Consulta, via count(*), existencia de movimentacao de determinado tipo, em determinada situacao para o ativo, dentro da cesta especificada</p>
    * 
    * @param cesta
    * @param codigoIF
    * @param tipo
    * @param status
    * @return true se existe movimentacao
    */
   public boolean existeMovimentacaoParaAtivo(CestaGarantiasDO cesta, CodigoIF codigoIF,
         TipoMovimentacaoGarantiaDO tipo, StatusMovimentacaoGarantiaDO status);

   /**
    * 
    * @param garantia
    * @return
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoControleSelic(DetalheGarantiaDO garantia);

   /**
    * Obtem a Movimentacao referente a uma garantia externa (selic, por exemplo)
    * @param codigoOperacaoExterna
    * @param tipo
    * @param status
    * @param sistema
    * @return MovimentacaoGarantiaDO
    */
   public MovimentacaoGarantiaDO obterMovimentacaoParaGarantiaExterna(NumeroOperacao codigoOperacaoExterna,
         TipoMovimentacaoGarantiaDO tipo, StatusMovimentacaoGarantiaDO status);

   /**
    * Verifica se existem movimentacoes do sistema especificado pelo parametro sistema 
    * do tipo e status na cesta
    * @param cesta
    * @param tipo
    * @param status
    * @param sistema
    * @return
    */
   public boolean cestaAguardandoMovimentacaoExterna(CestaGarantiasDO cesta, TipoMovimentacaoGarantiaDO tipo,
         Object[] status, Id sistema);

   /**
    * 
    * @param garantia
    * @param quantidadeGarantia
    * @param numOperacao
    * @return
    */
   public MovimentacaoGarantiaDO incluirMovimentacaoRetirada(DetalheGarantiaDO garantia, Quantidade quantidadeGarantia);

}
