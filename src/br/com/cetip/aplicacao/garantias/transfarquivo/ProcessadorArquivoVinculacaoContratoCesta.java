package br.com.cetip.aplicacao.garantias.transfarquivo;

import java.util.List;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidarVinculacaoCestaContrato;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoVinculacaoCestaContrato;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.visitante.tradutor.VisitanteTradutorLayout;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.arquivo.Campo;
import br.com.cetip.infra.servico.arquivo.CampoPosicional;
import br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoBloco;
import br.com.cetip.infra.servico.arquivo.quebrabloco.IQuebraBloco;
import br.com.cetip.infra.servico.arquivo.quebrabloco.QuebraBlocoPorLinha;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.roteador.Servicos;

/**
 * Vinculacao de Contrato de Swap a Cestas de Garantias
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class ProcessadorArquivoVinculacaoContratoCesta extends ProcessadorArquivoBloco {

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivo#validarCabecalho(java.util.List,
    *      br.com.cetip.infra.servico.roteador.Servicos)
    */
   public void validarCabecalho(List atributos, Servicos servicos) {
      DescricaoLimitada cesta = (DescricaoLimitada) obterValoresCabecalho().get(0);
      if (cesta != null && !((DescricaoLimitada) atributos.get(0)).mesmoConteudo(cesta)) {
         throw new Erro(CodigoErro.ID_SISTEMA_INVALIDO, atributos.get(0).toString());
      }

      if (!((Id) atributos.get(1)).mesmoConteudo(new Id("0"))) {
         throw new Erro(CodigoErro.ID_TIPO_LINHA, atributos.get(1).toString());
      }

      Funcao funcao = (Funcao) obterValoresCabecalho().get(2);
      if (!((Funcao) atributos.get(2)).mesmoConteudo(funcao)) {
         throw new Erro(CodigoErro.ACAO_NAO_EXISTE, atributos.get(2).toString());
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivo#validarRodape(java.util.List,
    *      br.com.cetip.infra.servico.roteador.Servicos)
    */
   public void validarRodape(List atributos, Servicos servicos) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoBloco#validarBloco(java.util.List,
    *      java.util.List, br.com.cetip.infra.servico.roteador.Servicos)
    */
   public void validarBloco(List linhasBloco, List atributosTitulo, Servicos servicos) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Tamanho do Titulo: " + atributosTitulo.size());
      }

      DescricaoLimitada cesta = (DescricaoLimitada) obterValoresCabecalho().get(0);

      if (cesta != null && !((DescricaoLimitada) atributosTitulo.get(0)).mesmoConteudo(cesta)) {
         throw new Erro(CodigoErro.ID_SISTEMA_INVALIDO, atributosTitulo.get(0).toString());
      }

      if (!((Id) atributosTitulo.get(1)).mesmoConteudo(new Id("1"))) {
         throw new Erro(CodigoErro.ID_TIPO_LINHA, atributosTitulo.get(1).toString());
      }

      Funcao funcao = (Funcao) obterValoresCabecalho().get(2);
      if (!((Funcao) atributosTitulo.get(2)).mesmoConteudo(funcao)) {
         throw new Erro(CodigoErro.ACAO_NAO_EXISTE, atributosTitulo.get(2).toString());
      }

      RequisicaoServicoValidarVinculacaoCestaContrato req = new RequisicaoServicoValidarVinculacaoCestaContrato();
      req.atribuirPARTE_CodigoContaCetip((CodigoContaCetip) atributosTitulo.get(3));
      req.atribuirPARTE_NumeroCestaGarantia((NumeroCestaGarantia) atributosTitulo.get(4));
      req.atribuirCONTRA_PARTE_CodigoContaCetip((CodigoContaCetip) atributosTitulo.get(5));
      req.atribuirCONTRA_PARTE_NumeroCestaGarantia((NumeroCestaGarantia) atributosTitulo.get(6));
      req.atribuirPARTICIPANTE_CPFOuCNPJ((CPFOuCNPJ) atributosTitulo.get(9));
      req.atribuirCONTRA_PARTE_CPFOuCNPJ((CPFOuCNPJ) atributosTitulo.get(10));

      Texto codIF = (Texto) atributosTitulo.get(7);
      CodigoIF codIFContrato = new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO, codIF.obterConteudo());
      req.atribuirINSTRUMENTO_FINANCEIRO_CodigoIF(codIFContrato);

      req.atribuirRESET_Funcao(new Funcao(atributosTitulo.get(8).toString()));

      servicos.executar(req);
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivo#obterRequisicao(java.util.List)
    */
   public Requisicao obterRequisicao(List atributos) {
      return new RequisicaoServicoVinculacaoCestaContrato();
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoBloco#montarRequisicaoBloco(br.com.cetip.infra.servico.interfaces.Requisicao,
    *      java.util.List, java.util.List, br.com.cetip.infra.servico.roteador.Servicos)
    */
   public Requisicao montarRequisicaoBloco(Requisicao req, List linhasBloco, List atributosTitulo, Servicos servicos) {
      RequisicaoServicoVinculacaoCestaContrato requisicao = (RequisicaoServicoVinculacaoCestaContrato) req;
      requisicao.atribuirPARTE_CodigoContaCetip((CodigoContaCetip) atributosTitulo.get(3));
      requisicao.atribuirPARTE_NumeroCestaGarantia((NumeroCestaGarantia) atributosTitulo.get(4));
      requisicao.atribuirCONTRA_PARTE_CodigoContaCetip((CodigoContaCetip) atributosTitulo.get(5));
      requisicao.atribuirCONTRA_PARTE_NumeroCestaGarantia((NumeroCestaGarantia) atributosTitulo.get(6));
      requisicao.atribuirPARTE_CPFOuCNPJ((CPFOuCNPJ) atributosTitulo.get(9));
      requisicao.atribuirCONTRA_PARTE_CPFOuCNPJ((CPFOuCNPJ) atributosTitulo.get(10));

      Texto codIF = (Texto) atributosTitulo.get(7);
      CodigoIF codIFContrato = new CodigoIF(Contexto.INSTRUMENTO_FINANCEIRO, codIF.obterConteudo());

      requisicao.atribuirINSTRUMENTO_FINANCEIRO_CodigoIF(codIFContrato);
      requisicao.atribuirRESET_Funcao(new Funcao(atributosTitulo.get(8).toString()));

      return requisicao;
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivo#obterTradutorLayout()
    */
   public VisitanteTradutorLayout obterTradutorLayout() {
      return new VisitanteTradutorLayoutGarantias();
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoFormatado#obterCamposCabecalho()
    */
   public Campo[] obterCamposCabecalho() {
      return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true),
            new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true),
            new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), true),
            new CampoPosicional(20, new NomeSimplificado(Contexto.GERADOR_ARQUIVO), false),
            new CampoPosicional(8, new Data(Contexto.ARQUIVO), true),
            new CampoPosicional(5, new Texto(Contexto.VERSAO), true) };
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoBloco#obterCamposTitulo()
    */
   public Campo[] obterCamposTitulo() {
      Texto versao = (Texto) obterValoresCabecalho().get(5);

      if (versao.obterConteudo().equals("00001")) {
         return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true),
               new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true),
               new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), true),
               new CampoPosicional(8, new CodigoContaCetip(Contexto.PARTE), true),
               new CampoPosicional(8, new NumeroCestaGarantia(Contexto.PARTE), false),
               new CampoPosicional(8, new CodigoContaCetip(Contexto.CONTRA_PARTE), true),
               new CampoPosicional(8, new NumeroCestaGarantia(Contexto.CONTRA_PARTE), false),
               new CampoPosicional(14, new Texto(Contexto.INSTRUMENTO_FINANCEIRO), true),
               new CampoPosicional(8, new Texto(Contexto.RESET), true),
               new CampoPosicional(14, new CPFOuCNPJ(Contexto.PARTICIPANTE), false),
               new CampoPosicional(14, new CPFOuCNPJ(Contexto.CONTRA_PARTE), false) };
      }

      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoBloco#obterCamposBloco()
    */
   public Campo[] obterCamposBloco() {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoFormatado#obterCamposRodape()
    */
   public Campo[] obterCamposRodape() {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoBloco#obterAtributoQuantidadeLinhas()
    */
   public Atributo obterAtributoQuantidadeLinhas() {
      return new QuantidadeInteiraPositiva(Contexto.QUANTIDADE_EVENTOS);
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivo#gravarResposta()
    */
   public boolean gravarResposta() {
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivo#obterCabecalhoAdicionalResposta()
    */
   public String obterCabecalhoAdicionalResposta() {
      return "Vincula Cestas a Contrato";
   }

   public IQuebraBloco obterQuebraBloco() {
      return new QuebraBlocoPorLinha(this);
   }

   public boolean executarAssincrono() {
      return true;
   }

}
