package br.com.cetip.aplicacao.garantias.transfarquivo;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoRegistraArqMsgAutorizacaoPublicGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.servico.consulta.RequisicaoServicoVerificaHeaderArquivo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.visitante.tradutor.VisitanteTradutorLayout;
import br.com.cetip.infra.servico.arquivo.Campo;
import br.com.cetip.infra.servico.arquivo.CampoPosicional;
import br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoPosicional;
import br.com.cetip.infra.servico.arquivo.quebrabloco.IQuebraBloco;
import br.com.cetip.infra.servico.arquivo.quebrabloco.QuebraBlocoPorLinha;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.roteador.Servicos;

public class ProcessadorArquivoIncluiAutorizacaoPublicGarantias extends ProcessadorArquivoPosicional {

   // Constantes com referência as colunas do registro header do arquivo
   public static final int COL_HEADER_TIPO_ARQUIVO = 0;
   public static final int COL_HEADER_TIPO_REGISTRO = 1;
   public static final int COL_HEADER_ACAO = 2;
   public static final int COL_HEADER_NOME_SIMPLIFICADO_PARTICIPANTE = 3;
   public static final int COL_HEADER_DATA = 4;
   public static final int COL_HEADER_VERSAO = 5;

   // Constantes com referência as colunas do registro titulo do arquivo
   public static final int COL_BLOCO_TIPO_ARQUIVO = 0;
   public static final int COL_BLOCO_TIPO_REGISTRO = 1;
   public static final int COL_BLOCO_ACAO = 2;

   private Texto codTipoArquivo;

   public void validarCabecalho(List atributos, Servicos servicos) {
      codTipoArquivo = (Texto) atributos.get(COL_HEADER_TIPO_ARQUIVO);
      if (!codTipoArquivo.mesmoConteudo(new Texto("CESTA"))) {
         throw new Erro(CodigoErro.ID_SISTEMA_INVALIDO, codTipoArquivo.toString());
      }

      if (!((Id) atributos.get(COL_HEADER_TIPO_REGISTRO)).mesmoConteudo(new Id("0"))) {
         throw new Erro(CodigoErro.ID_TIPO_LINHA, atributos.get(COL_HEADER_TIPO_REGISTRO).toString());
      }

      Funcao funcao = (Funcao) atributos.get(COL_HEADER_ACAO);
      if (!funcao.mesmoConteudo(ICestaDeGarantias.AUTORIZACAO)
            && !funcao.mesmoConteudo(ICestaDeGarantias.DESAUTORIZACAO)) {
         throw new Erro(CodigoErro.ACAO_NAO_EXISTE, atributos.get(COL_HEADER_ACAO).toString());
      }

      Texto versao = (Texto) atributos.get(COL_HEADER_VERSAO);
      if (!versao.obterConteudo().equals("00001")) {
         throw new Erro(CodigoErro.ARQUIVO_INVALIDO);
      }

      Data data = (Data) atributos.get(COL_HEADER_DATA);

      RequisicaoServicoVerificaHeaderArquivo req = new RequisicaoServicoVerificaHeaderArquivo();
      req.atribuirARQUIVO_Data(data);
      servicos.executar(req);
   }

   public void validarRodape(List atributos, Servicos servicos) {
   }

   public void validarRegistro(List atributosBloco, Servicos servicos) {
      if (!((Texto) atributosBloco.get(COL_BLOCO_TIPO_ARQUIVO)).mesmoConteudo(new Texto("CESTA"))) {

         throw new Erro(CodigoErro.ID_SISTEMA_INVALIDO, atributosBloco.get(COL_BLOCO_TIPO_ARQUIVO).toString());
      }

      if (!((Id) atributosBloco.get(COL_BLOCO_TIPO_REGISTRO)).mesmoConteudo(new Id("1"))) {

         throw new Erro(CodigoErro.ID_TIPO_LINHA, atributosBloco.get(COL_BLOCO_TIPO_REGISTRO).toString());
      }

      Funcao funcaoBloco = (Funcao) atributosBloco.get(COL_BLOCO_ACAO);
      if (!funcaoBloco.mesmoConteudo(Funcao.INCLUSAO) && !funcaoBloco.mesmoConteudo(Funcao.EXCLUSAO)) {

         throw new Erro(CodigoErro.ACAO_NAO_EXISTE, funcaoBloco.toString());
      }
   }

   public Requisicao obterRequisicao(List atributos) {
      return new RequisicaoServicoRegistraArqMsgAutorizacaoPublicGarantias();
   }

   public Requisicao montarRequisicaoBloco(Requisicao req, List linhasBloco, List atributosTitulo, Servicos servicos) {
      // monta a requisicao do servico principal
      RequisicaoServicoRegistraArqMsgAutorizacaoPublicGarantias reqPrinc = (RequisicaoServicoRegistraArqMsgAutorizacaoPublicGarantias) req;
      servicos.preencherRequisicao(reqPrinc, atributosTitulo);
      Iterator it = linhasBloco.iterator();
      while (it.hasNext()) {
         it.next();
         reqPrinc.novaLinha();
      }
      return reqPrinc;
   }

   /* (non-Javadoc)
    * @see br.com.cetip.infra.servico.arquivo.ProcessadorArquivo#obterTradutorLayout()
    */
   public VisitanteTradutorLayout obterTradutorLayout() {
      return new VisitanteTradutorLayoutGarantias();
   }

   public Campo[] obterCamposCabecalho() {
      return new Campo[] { new CampoPosicional(5, new Texto(Contexto.TIPO_ARQUIVO), true), //0
            new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), //1
            new CampoPosicional(4, new Funcao(Contexto.CONTRATO), true), //2
            new CampoPosicional(20, new NomeSimplificado(Contexto.GERADOR_ARQUIVO), false), //3
            new CampoPosicional(8, new Data(Contexto.ARQUIVO), true), //4
            new CampoPosicional(5, new Texto(Contexto.VERSAO), true), //5
      };
   }

   public Campo[] obterCamposRegistro() {
      return obterCamposRegistro00001();
   }

   public Campo[] obterCamposRodape() {
      return null;
   }

   public boolean executarAssincrono() {
      return true;
   }

   private Campo[] obterCamposRegistro00001() {
      return new Campo[] { new CampoPosicional(5, new Texto(Contexto.TIPO_ARQUIVO), true), //00
            new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), //01
            new CampoPosicional(4, new Funcao(Contexto.INSTRUMENTO_FINANCEIRO), true), //02
            new CampoPosicional(14, new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO_CONTRATO), false), //03
            new CampoPosicional(8, new CodigoContaCetip(Contexto.PARTE_CONTRATO), false), //05
            new CampoPosicional(18, new CPFOuCNPJ(Contexto.PARTE_CONTRATO), false), //06
      };
   }

   public IQuebraBloco obterQuebraBloco() {
      return new QuebraBlocoPorLinha(this);
   }

}
