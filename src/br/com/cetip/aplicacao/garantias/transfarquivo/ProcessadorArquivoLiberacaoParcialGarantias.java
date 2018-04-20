package br.com.cetip.aplicacao.garantias.transfarquivo;

import java.util.List;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoExecutarParcialCestaGarantias;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.visitante.tradutor.VisitanteTradutorLayout;
import br.com.cetip.infra.servico.arquivo.Campo;
import br.com.cetip.infra.servico.arquivo.CampoPosicional;
import br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivoPosicional;
import br.com.cetip.infra.servico.arquivo.quebrabloco.IQuebraBloco;
import br.com.cetip.infra.servico.arquivo.quebrabloco.QuebraBlocoPorLinha;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.roteador.Servicos;

/**
 * Processador de arquivo para LIBERACAO PARCIAL de Garantias de Cesta
 * 
 * Codigo: CESTALIBP
 * 
 */
public class ProcessadorArquivoLiberacaoParcialGarantias extends ProcessadorArquivoPosicional {

   public Campo[] obterCamposCabecalho() {
      return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true), // 0
            new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), // 1
            new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), true), // 2
            new CampoPosicional(20, new NomeSimplificado(Contexto.GERADOR_ARQUIVO), false), // 3
            new CampoPosicional(8, new Data(Contexto.ARQUIVO), true), // 4
            new CampoPosicional(5, new Texto(Contexto.VERSAO), true), // 5
            new CampoPosicional(8, new Id(Contexto.GARANTIAS_CODIGO), true) // 6
      };
   }

   public Campo[] obterCamposRegistro() {
      Texto versao = (Texto) obterValoresCabecalho().get(5);

      if (versao.obterConteudo().equals("00001")) {
         return obterCamposRegistro00001();
      }
      return null;
   }

   private Campo[] obterCamposRegistro00001() {
      return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true), // 0
            new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), // 1
            new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), true), // 2
            new CampoPosicional(5, new CodigoTipoIF(Contexto.GARANTIAS_CODIGO_TIPO), true), // 3
            new CampoPosicional(14, new CodigoIF(Contexto.GARANTIAS_CODIGO_IF), true), // 4
            new CampoPosicional(18, new Quantidade(Contexto.GARANTIAS_QUANTIDADE), true), // 5
            new CampoPosicional(8, new CodigoContaCetip(Contexto.GARANTIAS_GARANTIDO), true), // 6
            new CampoPosicional(14, new CPFOuCNPJ(Contexto.GARANTIAS_CONTRAPARTE), false), // 7
            new CampoPosicional(6, new NumeroOperacao(Contexto.OPERACAO), false) // 8
      };
   }

   public Campo[] obterCamposRodape() {
      return null;
   }

   public void validarCabecalho(List atributos, Servicos servicos) {
      if (!((DescricaoLimitada) atributos.get(0)).mesmoConteudo(new DescricaoLimitada("CESTA"))) {
         throw new Erro(CodigoErro.ID_SISTEMA_INVALIDO, atributos.get(0).toString());
      }

      if (!((Id) atributos.get(1)).mesmoConteudo(new Id("0"))) {
         throw new Erro(CodigoErro.ID_TIPO_LINHA, atributos.get(1).toString());
      }

      if (!((Funcao) atributos.get(2)).mesmoConteudo(new Funcao("LIBERACAO PARCIAL"))) {
         throw new Erro(CodigoErro.ACAO_NAO_EXISTE, atributos.get(2).toString());
      }
   }

   public void validarRodape(List atributos, Servicos servicos) {
   }

   public void validarRegistro(List atributos, Servicos servicos) {
      if (!((DescricaoLimitada) atributos.get(0)).mesmoConteudo(new DescricaoLimitada("CESTA"))) {
         throw new Erro(CodigoErro.ID_SISTEMA_INVALIDO, atributos.get(0).toString());
      }

      if (!((Id) atributos.get(1)).mesmoConteudo(new Id("1"))) {
         throw new Erro(CodigoErro.ID_TIPO_LINHA, atributos.get(1).toString());
      }

      if (!((Funcao) atributos.get(2)).mesmoConteudo(new Funcao("LIBERACAO PARCIAL"))) {
         throw new Erro(CodigoErro.ID_TIPO_LINHA, atributos.get(2).toString());
      }
   }

   public Requisicao obterRequisicao(List atributos) {
      return new RequisicaoServicoExecutarParcialCestaGarantias();
   }

   public Requisicao montarRequisicao(Requisicao req, List atributos, Servicos servicos) {
      RequisicaoServicoExecutarParcialCestaGarantias requisicao = (RequisicaoServicoExecutarParcialCestaGarantias) req;

      // Dado do cabecalho
      final Id idCesta = (Id) obterValoresCabecalho().get(6);
      requisicao.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(new NumeroCestaGarantia(idCesta));

      // Dados do registro
      requisicao.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(((CodigoTipoIF) atributos.get(3)));
      requisicao.atribuirGARANTIAS_CODIGO_IF_CodigoIF((CodigoIF) atributos.get(4));
      requisicao.atribuirGARANTIAS_QUANTIDADE_Quantidade((Quantidade) atributos.get(5));
      requisicao.atribuirGARANTIAS_GARANTIDO_CodigoContaCetip((CodigoContaCetip) atributos.get(6));
      requisicao.atribuirGARANTIAS_CONTRAPARTE_CPFOuCNPJ((CPFOuCNPJ) atributos.get(7));
      requisicao.atribuirOPERACAO_NumeroOperacao((NumeroOperacao) atributos.get(8));

      return requisicao;
   }

   public VisitanteTradutorLayout obterTradutorLayout() {
      return new VisitanteTradutorLayoutGarantias();
   }

   public boolean executarAssincrono() {
      return true;
   }

   public IQuebraBloco obterQuebraBloco() {
      return new QuebraBlocoPorLinha(this);
   }

}
