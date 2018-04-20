package br.com.cetip.aplicacao.garantias.transfarquivo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoRegistraVinculacaoCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaVinculacaoCestaGarantias;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleLancamento;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.visitante.tradutor.VisitanteTradutorLayout;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.arquivo.Campo;
import br.com.cetip.infra.servico.arquivo.CampoPosicional;
import br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoPosicional;
import br.com.cetip.infra.servico.arquivo.quebrabloco.IQuebraBloco;
import br.com.cetip.infra.servico.arquivo.quebrabloco.QuebraBlocoPorLinha;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.roteador.Servicos;

/**
 * 
 * @author brunob
 */
public class ProcessadorArquivoVinculacao extends ProcessadorArquivoPosicional {

   private Set codigosIF = new HashSet();

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
   public void validarRegistro(List atributos, Servicos servicos) {

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Tamanho do Titulo: " + atributos.size());
      }

      DescricaoLimitada cesta = (DescricaoLimitada) obterValoresCabecalho().get(0);

      if (cesta != null && !((DescricaoLimitada) atributos.get(0)).mesmoConteudo(cesta)) {
         throw new Erro(CodigoErro.ID_SISTEMA_INVALIDO, atributos.get(0).toString());
      }

      if (!((Id) atributos.get(1)).mesmoConteudo(new Id("1"))) {
         throw new Erro(CodigoErro.ID_TIPO_LINHA, atributos.get(1).toString());
      }

      Funcao funcao = (Funcao) obterValoresCabecalho().get(2);
      if (!((Funcao) atributos.get(2)).mesmoConteudo(funcao)) {
         throw new Erro(CodigoErro.ACAO_NAO_EXISTE, atributos.get(2).toString());
      }

      CodigoIF codigoIF = (CodigoIF) atributos.get(8);
      String sCodIF = codigoIF.obterConteudo().trim();
      if (codigosIF.contains(sCodIF)) {
         throw new Erro(CodigoErro.ERRO, "Instrumento Financeiro duplicado: " + sCodIF);
      }
      codigosIF.add(sCodIF);

      RequisicaoServicoValidaVinculacaoCestaGarantias req = new RequisicaoServicoValidaVinculacaoCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) atributos.get(3));
      req.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip((CodigoContaCetip) atributos.get(4));
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip((CodigoContaCetip) atributos.get(5));
      req.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);
      req.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF((CodigoTipoIF) atributos.get(7));
      req.atribuirGARANTIAS_DEPOSITADO_Booleano((Booleano) atributos.get(10));
      req.atribuirGARANTIAS_PARTICIPANTE_CPFOuCNPJ((CPFOuCNPJ) atributos.get(15));

      Object tipoFuncao = atributos.get(6);
      if (tipoFuncao.toString().equalsIgnoreCase("01")) {
         req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(ICestaDeGarantias.FUNCAO_GARANTIDOR);
      } else if (tipoFuncao.toString().equalsIgnoreCase("02")) {
         req.atribuirGARANTIAS_TIPO_ACESSO_Funcao(ICestaDeGarantias.FUNCAO_GARANTIDO);
      } else {
         throw new Erro(CodigoErro.ACAO_NAO_EXISTE, tipoFuncao.toString());
      }

      req.atribuirGARANTIAS_PU_Quantidade((Quantidade) atributos.get(13));
      req.atribuirGARANTIAS_QT_OPERACAO_NumeroControleLancamento((NumeroControleLancamento) atributos.get(11));
      req.atribuirGARANTIAS_QT_OPERACAO_Quantidade((Quantidade) atributos.get(12));
      req.atribuirGARANTIAS_SISTEMA_CodigoSistema((CodigoSistema) atributos.get(9));

      Object modalidadeLiq = atributos.get(14);
      if (modalidadeLiq.toString().length() != 0 && modalidadeLiq.toString().equalsIgnoreCase("0")) {
         req.atribuirMODALIDADE_LIQUIDACAO_Id(new Id("6"));
      } else {
         req.atribuirMODALIDADE_LIQUIDACAO_Id((Id) modalidadeLiq);
      }

      servicos.executar(req);
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivo#obterRequisicao(java.util.List)
    */
   public Requisicao obterRequisicao(List atributos) {
      return new RequisicaoServicoRegistraVinculacaoCestaGarantias();
   }

   /*
    * (non-Javadoc)
    * 
    * @see br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoBloco#montarRequisicaoBloco(br.com.cetip.infra.servico.interfaces.Requisicao,
    *      java.util.List, java.util.List, br.com.cetip.infra.servico.roteador.Servicos)
    */
   public Requisicao montarRequisicao(Requisicao req, List atributos, Servicos servicos) {
      RequisicaoServicoRegistraVinculacaoCestaGarantias requisicao = (RequisicaoServicoRegistraVinculacaoCestaGarantias) req;
      requisicao.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) atributos.get(3));
      requisicao.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip((CodigoContaCetip) atributos.get(4));
      requisicao.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip((CodigoContaCetip) atributos.get(5));
      requisicao.atribuirGARANTIAS_CODIGO_IF_CodigoIF((CodigoIF) atributos.get(8));
      requisicao.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF((CodigoTipoIF) atributos.get(7));
      requisicao.atribuirGARANTIAS_DEPOSITADO_Booleano((Booleano) atributos.get(10));
      requisicao.atribuirGARANTIAS_PARTICIPANTE_CPFOuCNPJ((CPFOuCNPJ) atributos.get(15));

      String codFuncao = atributos.get(6).toString();
      if (codFuncao.equalsIgnoreCase("01")) {
         requisicao.atribuirGARANTIAS_TIPO_ACESSO_Funcao(ICestaDeGarantias.FUNCAO_GARANTIDOR);
      } else if (codFuncao.equalsIgnoreCase("02")) {
         requisicao.atribuirGARANTIAS_TIPO_ACESSO_Funcao(ICestaDeGarantias.FUNCAO_GARANTIDO);
      } else {
         throw new Erro(CodigoErro.ACAO_NAO_EXISTE, codFuncao);
      }

      requisicao.atribuirGARANTIAS_PU_Quantidade((Quantidade) atributos.get(13));
      requisicao.atribuirGARANTIAS_QT_OPERACAO_NumeroControleLancamento((NumeroControleLancamento) atributos.get(11));
      requisicao.atribuirGARANTIAS_QT_OPERACAO_Quantidade((Quantidade) atributos.get(12));
      requisicao.atribuirGARANTIAS_SISTEMA_CodigoSistema((CodigoSistema) atributos.get(9));

      Object modalidadeLiq = atributos.get(14);
      if (modalidadeLiq.toString().length() != 0 && modalidadeLiq.toString().equalsIgnoreCase("0")) {
         requisicao.atribuirMODALIDADE_LIQUIDACAO_Id(new Id("6"));
      } else {
         requisicao.atribuirMODALIDADE_LIQUIDACAO_Id((Id) modalidadeLiq);
      }

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
   public Campo[] obterCamposRegistro() {
      Texto versao = (Texto) obterValoresCabecalho().get(5);
      if (versao.obterConteudo().equals("00001")) {
         return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true), // 00
               new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), // 01
               new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), false), // 02
               new CampoPosicional(8, new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO), true), // 03
               new CampoPosicional(8, new CodigoContaCetip(Contexto.GARANTIAS_PARTICIPANTE), true), // 04
               new CampoPosicional(8, new CodigoContaCetip(Contexto.GARANTIAS_CONTRAPARTE), true), // 05
               new CampoPosicional(2, new DescricaoLimitada(Contexto.GARANTIAS_LANCADOR), true), // 06
               new CampoPosicional(5, new CodigoTipoIF(Contexto.GARANTIAS_CODIGO_TIPO), true), // 07
               new CampoPosicional(14, new CodigoIF(Contexto.GARANTIAS_CODIGO_IF), true), // 08
               new CampoPosicional(20, new CodigoSistema(Contexto.GARANTIAS_SISTEMA), true), // 09
               new CampoPosicional(8, new Booleano(Contexto.GARANTIAS_DEPOSITADO), true), // 10
               new CampoPosicional(10, new NumeroControleLancamento(Contexto.GARANTIAS_QT_OPERACAO), false), // 11
               new CampoPosicional(18, new Quantidade(Contexto.GARANTIAS_QT_OPERACAO), false), // 12
               new CampoPosicional(18, new Quantidade(Contexto.GARANTIAS_PU), false), // 13
               new CampoPosicional(1, new Id(Contexto.GARANTIAS_MODALIDADE), false), // 14
               new CampoPosicional(14, new CPFOuCNPJ(Contexto.GARANTIAS_PARTICIPANTE), false) // 15
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
      return "Vincula Cesta";
   }

   public IQuebraBloco obterQuebraBloco() {
      return new QuebraBlocoPorLinha(this);
   }

   public boolean executarAssincrono() {
      return true;
   }
}
