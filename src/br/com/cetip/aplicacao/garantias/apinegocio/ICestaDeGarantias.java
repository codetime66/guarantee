package br.com.cetip.aplicacao.garantias.apinegocio;

import java.util.List;

import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

/**
 * Interface de negocio do projeto Garantias. Define as operacoes de banco de dados necessarias.
 * 
 * @author <a href="mailto:vinicius@summa-tech.com">Vinicius S. Fernandes</a>
 * @author <a href="mailto:mike@summa-tech.com">Liaw Mike Djoesman</a>
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * @since Maio/2005
 * 
 * @see br.com.cetip.aplicacao.garantias.negocio.CestaDeGarantias
 */
public interface ICestaDeGarantias {

   public static final int PRECISAO_PARTE_INTEIRA_QUANTIDADE = 10;

   public static final int PRECISAO_DECIMAL_QUANTIDADE = 8;

   public static final Funcao FUNCAO_GARANTIDOR = new Funcao(Contexto.GARANTIAS_TIPO_ACESSO, "GARANTIDOR");

   public static final Funcao FUNCAO_GARANTIDO = new Funcao(Contexto.GARANTIAS_TIPO_ACESSO, "GARANTIDO");

   public static final Funcao CARACTERISTICA_CESTA = new Funcao(Contexto.ACAO, "CARACTERISTICA CESTA");

   public static final Funcao VINCULAR_CESTA = new Funcao(Contexto.ACAO, "VINCULAR_CESTA");

   public static final Funcao ALTERAR_CESTA = new Funcao(Contexto.ACAO, "ALTERAR_CESTA");

   public static final Funcao EXCLUIR_CESTA = new Funcao(Contexto.ACAO, "EXCLUIR_CESTA");

   public static final Funcao FINALIZAR_CESTA = new Funcao(Contexto.ACAO, "FINALIZAR_CESTA");

   public static final Funcao LIBERAR_CESTA = new Funcao(Contexto.ACAO, "LIBERAR_CESTA");

   public static final Funcao APORTAR_GARANTIAS = new Funcao(Contexto.ACAO, "APORTAR_GARANTIAS");

   public static final Funcao APORTAR_GARANTIA = new Funcao(Contexto.ACAO, "APORTAR_GARANTIA");

   public static final Funcao LIBERAR_GARANTIAS = new Funcao(Contexto.ACAO, "LIBERAR_GARANTIAS");

   public static final Funcao LIBERAR_GARANTIAS_PARCIAL = new Funcao(Contexto.ACAO, "LIBERAR_GARANTIAS_PARCIAL");

   public static final Funcao EXCLUIR_GARANTIAS = new Funcao(Contexto.ACAO, "EXCLUIR_GARANTIAS");

   public static final Funcao EXCLUIR_GARANTIA = new Funcao(Contexto.ACAO, "EXCLUIR_GARANTIA");

   public static final Funcao INCLUIR_GARANTIAS = new Funcao(Contexto.ACAO, "INCLUIR_GARANTIAS");

   public static final Funcao INCLUIR_GARANTIA = new Funcao(Contexto.ACAO, "INCLUIR_GARANTIA");

   public static final Funcao RETIRAR_GARANTIAS = new Funcao(Contexto.ACAO, "RETIRAR_GARANTIAS");

   public static final Funcao RETIRAR_GARANTIA = new Funcao(Contexto.ACAO, "RETIRAR_GARANTIA");

   public static final Funcao CONSULTAR_GARANTIAS = new Funcao(Contexto.ACAO, "CONSULTAR_GARANTIAS");

   public static final Funcao CONSULTAR_HISTORICO = new Funcao(Contexto.ACAO, "CONSULTAR_HISTORICO");

   public static final Funcao ENCERRAR_CADASTRO_ITENS_CESTA = new Funcao(Contexto.ACAO, "ENCERRAR_CADASTRO_ITENS_CESTA");

   public static final Funcao LIBERAR_PENHOR_EMISSOR = new Funcao(Contexto.ACAO, "LIBERAR_PENHOR_EMISSOR");

   public static final Funcao ALTERAR_NUMERO_OPERACAO = new Funcao(Contexto.ACAO, "ALTERAR_NUMERO_OPERACAO");

   public static final Funcao DESVINCULAR_GARANTIDO = new Funcao(Contexto.ACAO, "DESVINCULAR_GARANTIDO");

   public static final Funcao AUTORIZACAO = new Funcao("AUTORIZACAO");

   public static final Funcao DESAUTORIZACAO = new Funcao("DESAUTORIZACAO");

   public CestaGarantiasDO obterCestaDeGarantias(NumeroCestaGarantia codigo);

   public CestaGarantiasDO obterCestaDeGarantias(NumeroCestaGarantia codigo, Funcao tipoAcesso);

   /**
    * Metodo chamado somente em servicos de negocio executados pelo Garantido
    * 
    * @param numero
    * @return
    */
   public List listarGarantiasCesta(NumeroCestaGarantia numero);

   public void acionaMIG(MovimentacaoGarantiaDO movimentacao, Booleano indBatch, Data dataOperacao);

   /**
    * Inclui garantia nao-cetipada
    * 
    * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
    * @param mov
    */
   public void incluirGarantiaExterna(MovimentacaoGarantiaDO mov);

   public boolean possuiMovsBloqueioPendente(CestaGarantiasDO cesta);

   public boolean possuiMovsBloqueioSelic(CestaGarantiasDO cesta);

   /**
    * <p>
    * Identifica a existencia de movimentacoes defeituosas (diferentes de OK e PENDENTE).
    * </p>
    * 
    * @param cesta
    * @return true se existem movimentacoes que terminaram em erro.
    */
   public boolean possuiMovsDefeituosas(CestaGarantiasDO cesta);

   public int contarGarantias(CestaGarantiasDO cesta);

   public boolean possuiMovimentacaoBloqueio(NumeroCestaGarantia numeroCesta);

   public List listarCestasVinculadasSemAtivos();

   public void verificaNecessidadeDesvincularCesta(CestaGarantiasDO cesta);

   public boolean ehIFVinculado(Id numIF);

   public void cancelaMovimentacaoBloqueio(MovimentacaoGarantiaDO mov);

   /**
    * Verifica se o ativo ifdo esta contido em uma cesta de garantias
    * 
    * @param ifdo
    *           o instrumento financeiro que precisa estar contido na cesta
    * @return o id da cesta contendo ifdo
    */
   public Id getCestaDeGarantiaContendoIF(CodigoIF ifdo);

   public boolean existeCestaDeGarantias(NumeroCestaGarantia codigo);

   /**
    * Identifica se IF informado garante alguma cesta (se eh ativo garantidor de alguma cesta vinculada.)
    * 
    * @param numIf
    *           a ser verificado
    */
   public boolean ativoGaranteCesta(Id numIf);

   /**
    * Identifica o numero da cesta vinculada ao ativo informado
    * 
    * @param numIf
    * @return NumeroCestaGarantia
    */
   public NumeroCestaGarantia obterCestaGarantindoIF(Id numIf);

   public NumeroCestaGarantia obterCestaGarantindoIF(Id numIF, Id ponta);

   public CestaGarantiasDO obterCestaGarantindoIF(InstrumentoFinanceiroDO ifDO);

   public List listarCestasSegundoNivel(CestaGarantiasDO cesta);

   public void retirarCestaDeCesta(CestaGarantiasDO cesta);

   public void bloquearCestaDeCesta(CestaGarantiasDO cesta);

   public CestaGarantiasIFDO obterIFDOVinculado(CestaGarantiasDO cesta, InstrumentoFinanceiroDO numIF);

   public boolean possuiVinculacao(CestaGarantiasDO cesta);

   public NumeroInteiro obtemQtdAtivosVinculados(NumeroCestaGarantia numCesta);

   public boolean ehCestaSegundoNivel(CestaGarantiasDO cesta);

   public void desvinculaCestaSemGarantias(CestaGarantiasDO cesta);

   public boolean ativoGarantidoPossuiMovimentacoes(InstrumentoFinanceiroDO ifDO);

   public boolean verificaCustodiaAtivo(CestaGarantiasDO cesta, InstrumentoFinanceiroDO ativo);

   public boolean verificaCustodiaAtivosCesta(CestaGarantiasDO cesta);

   public DetalheGarantiaDO obterGarantiaCesta(CestaGarantiasDO cesta, GarantiaVO vo);

   public CodigoTipoIF obterTipoIFGarantidoCesta(CestaGarantiasDO cesta);

   public boolean cestaPossuiAtivoGarantidoComOperacaoPendente(CestaGarantiasDO cesta);

}
