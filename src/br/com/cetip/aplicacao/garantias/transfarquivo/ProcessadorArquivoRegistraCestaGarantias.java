package br.com.cetip.aplicacao.garantias.transfarquivo;

import java.util.ArrayList;
import java.util.List;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoRegistraCadastroCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoRegistraItensCestaGarantiasViaArquivo;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoRegistraCadastroCestaGarantias;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.visitante.tradutor.VisitanteTradutorLayout;
import br.com.cetip.infra.servico.arquivo.Campo;
import br.com.cetip.infra.servico.arquivo.CampoPosicional;
import br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoPosicional;
import br.com.cetip.infra.servico.arquivo.quebrabloco.IQuebraBloco;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.roteador.Servicos;

public class ProcessadorArquivoRegistraCestaGarantias extends ProcessadorArquivoPosicional {

   private Id idCesta;

   public Campo[] obterCamposCabecalho() {
      return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true), // 0
            new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), // 1
            new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), true), // 2
            new CampoPosicional(20, new NomeSimplificado(Contexto.GERADOR_ARQUIVO), false), // 3
            new CampoPosicional(8, new Data(Contexto.ARQUIVO), true), // 4
            new CampoPosicional(5, new Texto(Contexto.VERSAO), true), // 5
            new CampoPosicional(8, new CodigoContaCetip(Contexto.GARANTIAS_PARTICIPANTE), true), // 06
            new CampoPosicional(8, new CodigoContaCetip(Contexto.GARANTIAS_CONTRAPARTE), false) // 07
      };
   }

   public Campo[] obterCamposRegistro() {
      Texto versao = (Texto) obterValoresCabecalho().get(5);
      if (versao.obterConteudo().equals("00001")) {
         return obterCamposRegistro00001();
      } else if (versao.obterConteudo().equals("00002")) {
         return obterCamposRegistro00002();
      }

      return null;
   }

   private Campo[] obterCamposRegistro00001() {
      return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true), // 0
            new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), // 1
            new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), true), // 2
            new CampoPosicional(5, new CodigoTipoIF(Contexto.GARANTIAS_CODIGO_TIPO), true), // 3
            new CampoPosicional(14, new CodigoIF(Contexto.GARANTIAS_CODIGO_IF), true), // 4
            new CampoPosicional(1, new IdTipoGarantia(Contexto.GARANTIAS_CODIGO), true), // 5
            new CampoPosicional(18, new Quantidade(Contexto.GARANTIAS_QUANTIDADE), true), // 6
            new CampoPosicional(1, new Booleano(Contexto.GARANTIAS_ITENS), true), // 7
            new CampoPosicional(100, new DescricaoLimitada(Contexto.GARANTIAS_ITENS), false) // 8
      };
   }

   private Campo[] obterCamposRegistro00002() {
      return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true), // 0
            new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), // 1
            new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), true), // 2
            new CampoPosicional(5, new CodigoTipoIF(Contexto.GARANTIAS_CODIGO_TIPO), true), // 3
            new CampoPosicional(14, new CodigoIF(Contexto.GARANTIAS_CODIGO_IF), true), // 4
            new CampoPosicional(1, new IdTipoGarantia(Contexto.GARANTIAS_CODIGO), true), // 5
            new CampoPosicional(18, new Quantidade(Contexto.GARANTIAS_QUANTIDADE), true), // 6
            new CampoPosicional(1, new Booleano(Contexto.GARANTIAS_ITENS), true), // 7
            new CampoPosicional(100, new DescricaoLimitada(Contexto.GARANTIAS_ITENS), false), // 8
            new CampoPosicional(6, new NumeroOperacao(Contexto.OPERACAO), false) // 9
      };
   }

   public Campo[] obterCamposRodape() {
      return null;
   }

   public void validarCabecalho(List atributos, Servicos servicos) {
      if (!((Id) atributos.get(1)).mesmoConteudo(new Id("0"))) {
         throw new Erro(CodigoErro.ID_TIPO_LINHA, atributos.get(1).toString());
      }

      RequisicaoServicoRegistraCadastroCestaGarantias reqCesta = new RequisicaoServicoRegistraCadastroCestaGarantias();
      reqCesta.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip((CodigoContaCetip) atributos.get(6));
      reqCesta.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip((CodigoContaCetip) atributos.get(7));
      reqCesta.atribuirGARANTIAS_CODIGO_QuantidadeInteiraPositiva(new QuantidadeInteiraPositiva(0));
      reqCesta.atribuirGARANTIAS_CODIGO_Booleano(new Booleano(Booleano.VERDADEIRO));
      ResultadoServicoRegistraCadastroCestaGarantias res;

      res = (ResultadoServicoRegistraCadastroCestaGarantias) servicos.executar(reqCesta);
      idCesta = res.obterGARANTIAS_CODIGO_Id();
   }

   public void validarRodape(List atributos, Servicos servicos) {
   }

   public void validarRegistro(List atributos, Servicos servicos) {
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
   }

   public Requisicao obterRequisicao(List arg0) {
      RequisicaoServicoRegistraItensCestaGarantiasViaArquivo requisicao = new RequisicaoServicoRegistraItensCestaGarantiasViaArquivo();
      requisicao.atribuirGARANTIAS_CODIGO_Id(idCesta);
      return requisicao;
   }

   public Requisicao montarRequisicao(Requisicao arg0, List atributos, Servicos servicos) {
      RequisicaoServicoRegistraItensCestaGarantiasViaArquivo requisicao = (RequisicaoServicoRegistraItensCestaGarantiasViaArquivo) arg0;

      requisicao.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF((CodigoTipoIF) atributos.get(3));
      requisicao.atribuirGARANTIAS_CODIGO_IF_CodigoIF((CodigoIF) atributos.get(4));
      requisicao.atribuirGARANTIAS_CODIGO_IdTipoGarantia((IdTipoGarantia) atributos.get(5));
      requisicao.atribuirGARANTIAS_QUANTIDADE_Quantidade((Quantidade) (atributos.get(6)));
      requisicao.atribuirGARANTIAS_ITENS_Booleano((Booleano) atributos.get(7));
      requisicao.atribuirGARANTIAS_ITENS_DescricaoLimitada((DescricaoLimitada) (atributos.get(8)));

      if (atributos.size() >= 10) {
         requisicao.atribuirOPERACAO_NumeroOperacao((NumeroOperacao) atributos.get(9));
      }

      requisicao.novaLinha();

      return requisicao;
   }

   public VisitanteTradutorLayout obterTradutorLayout() {
      return new VisitanteTradutorLayoutGarantias();
   }

   public boolean gravarResposta() {
      return true;
   }

   public String obterCabecalhoAdicionalResposta() {
      return "Codigo Cesta";
   }

   public List obterRespostaTransferencia(Erro arg0, List linhas) {
      List lista = new ArrayList(1);
      lista.add(arg0.obterTextoMensagem());
      return lista;
   }

   public List obterRespostaTransferencia(Resultado r, List linhas) {
      List l = new ArrayList(1);
      l.add(idCesta.toString());

      return l;
   }

   public IQuebraBloco obterQuebraBloco() {
      return new QuebraBlocoPorLote(this, 5000);
   }

   public boolean executarAssincrono() {
      return true;
   }
}
