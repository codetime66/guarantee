package br.com.cetip.aplicacao.garantias.transfarquivo;

import java.util.List;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoFechamentoCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaFechamentoCestaGarantias;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
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
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.roteador.Servicos;

/**
 * 
 * @author brunob
 */
public class ProcessadorArquivoFechamento extends ProcessadorArquivoBloco {

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
         Logger.debug(this, "Tamanho do Título: " + atributosTitulo.size());
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

      RequisicaoServicoValidaFechamentoCestaGarantias req = new RequisicaoServicoValidaFechamentoCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) atributosTitulo.get(3));
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip((CodigoContaCetip) atributosTitulo.get(5));

      servicos.executar(req);
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivo#obterRequisicao(java.util.List)
    */
   public Requisicao obterRequisicao(List atributos) {
      return new RequisicaoServicoFechamentoCestaGarantias();
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoBloco#montarRequisicaoBloco(br.com.cetip.infra.servico.interfaces.Requisicao,
    *      java.util.List, java.util.List, br.com.cetip.infra.servico.roteador.Servicos)
    */
   public Requisicao montarRequisicaoBloco(Requisicao req, List linhasBloco, List atributosTitulo, Servicos servicos) {

      // primeira execução muda o status para em finalização
      RequisicaoServicoFechamentoCestaGarantias requisicao = new RequisicaoServicoFechamentoCestaGarantias();
      requisicao.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) atributosTitulo.get(3));
      requisicao.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip((CodigoContaCetip) atributosTitulo.get(5));
      requisicao.atribuirGARANTIAS_CESTA_Booleano(new Booleano(Booleano.VERDADEIRO));

      servicos.executar(requisicao);

      // segunda execução finaliza a cesta

      RequisicaoServicoFechamentoCestaGarantias requisicaoAux = (RequisicaoServicoFechamentoCestaGarantias) req;
      requisicaoAux.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) atributosTitulo.get(3));
      requisicaoAux.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip((CodigoContaCetip) atributosTitulo.get(5));
      requisicaoAux.atribuirGARANTIAS_CESTA_Booleano(new Booleano(Booleano.FALSO));

      return requisicaoAux;
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
      return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true), // 0
            new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), // 1
            new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), true), // 2
            new CampoPosicional(20, new NomeSimplificado(Contexto.GERADOR_ARQUIVO), false), // 3
            new CampoPosicional(8, new Data(Contexto.ARQUIVO), true), // 4
            new CampoPosicional(5, new Texto(Contexto.VERSAO), true) // 5
      };
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoBloco#obterCamposTitulo()
    */
   public Campo[] obterCamposTitulo() {
      Texto versao = (Texto) obterValoresCabecalho().get(5);
      if (versao.obterConteudo().equals("00001")) {
         return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true), // 00
               new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), // 01
               new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), false), // 02
               new CampoPosicional(8, new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO), true), // 03
               new CampoPosicional(8, new CodigoContaCetip(Contexto.GARANTIAS_PARTICIPANTE), true), // 04
               new CampoPosicional(8, new CodigoContaCetip(Contexto.GARANTIAS_CONTRAPARTE), false), // 05
         };
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
      return "Fechamento Cesta";
   }

}
