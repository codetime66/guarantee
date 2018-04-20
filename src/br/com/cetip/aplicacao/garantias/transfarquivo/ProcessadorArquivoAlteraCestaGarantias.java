package br.com.cetip.aplicacao.garantias.transfarquivo;

import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoManutencaoItensCestaGarantiasViaArquivo;
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
import br.com.cetip.infra.servico.arquivo.quebrabloco.QuebraBlocoPorArquivo;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.roteador.Servicos;

public class ProcessadorArquivoAlteraCestaGarantias extends ProcessadorArquivoPosicional {

   public Campo[] obterCamposCabecalho() {
      return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true), // 0
            new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), // 1
            new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), true), // 2
            new CampoPosicional(20, new NomeSimplificado(Contexto.GERADOR_ARQUIVO), false), // 3
            new CampoPosicional(8, new Data(Contexto.ARQUIVO), true), // 4
            new CampoPosicional(5, new Texto(Contexto.VERSAO), true), // 5
            new CampoPosicional(8, new NumeroCestaGarantia(Contexto.GARANTIAS_CODIGO), true), // 06
            new CampoPosicional(8, new CodigoContaCetip(Contexto.GARANTIAS_PARTICIPANTE), true), // 07
            new CampoPosicional(8, new CodigoContaCetip(Contexto.GARANTIAS_CONTRAPARTE), false), // 08
            new CampoPosicional(2, new Id(Contexto.GARANTIAS_LANCADOR), false) // 09
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

   private Campo[] obterCamposRegistro00002() {
      return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true), // 00
            new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), // 01
            new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), true), // 02
            new CampoPosicional(5, new CodigoTipoIF(Contexto.GARANTIAS_CODIGO_TIPO), true), // 3
            new CampoPosicional(14, new CodigoIF(Contexto.GARANTIAS_CODIGO_IF), true), // 4
            new CampoPosicional(1, new IdTipoGarantia(Contexto.GARANTIAS_CODIGO), true), // 5
            new CampoPosicional(18, new Quantidade(Contexto.GARANTIAS_QUANTIDADE), true), // 6
            new CampoPosicional(1, new Booleano(Contexto.GARANTIAS_ITENS), true), // 7
            new CampoPosicional(100, new DescricaoLimitada(Contexto.GARANTIAS_ITENS), false), // 8
            new CampoPosicional(6, new NumeroOperacao(Contexto.OPERACAO), false) // 9
      };
   }

   private Campo[] obterCamposRegistro00001() {
      return new Campo[] { new CampoPosicional(5, new DescricaoLimitada(Contexto.GARANTIAS_CODIGO), true), // 00
            new CampoPosicional(1, new Id(Contexto.TIPO_LINHA), true), // 01
            new CampoPosicional(4, new Funcao(Contexto.GARANTIAS_CODIGO), true), // 02
            new CampoPosicional(5, new CodigoTipoIF(Contexto.GARANTIAS_CODIGO_TIPO), true), // 3
            new CampoPosicional(14, new CodigoIF(Contexto.GARANTIAS_CODIGO_IF), true), // 4
            new CampoPosicional(1, new IdTipoGarantia(Contexto.GARANTIAS_CODIGO), true), // 5
            new CampoPosicional(18, new Quantidade(Contexto.GARANTIAS_QUANTIDADE), true), // 6
            new CampoPosicional(1, new Booleano(Contexto.GARANTIAS_ITENS), true), // 7
            new CampoPosicional(100, new DescricaoLimitada(Contexto.GARANTIAS_ITENS), false), // 8
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

      if (!((Funcao) atributos.get(2)).mesmoConteudo(new Funcao("MANUTENCAO"))
            && !((Funcao) atributos.get(2)).mesmoConteudo(new Funcao("APORTE"))) {
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

      if (!((Funcao) atributos.get(2)).mesmoConteudo(Funcao.INCLUSAO)
            && !((Funcao) atributos.get(2)).mesmoConteudo(Funcao.EXCLUSAO)
            && !((Funcao) atributos.get(2)).mesmoConteudo(new Funcao("APORTE"))) {
         throw new Erro(CodigoErro.ID_TIPO_LINHA, atributos.get(2).toString());
      }

      Funcao funcaoRegistro = (Funcao) atributos.get(2);

      if (funcaoRegistro.mesmoConteudo(Funcao.APORTE)) {
         if (((Id) obterValoresCabecalho().get(9)).vazio()) {
            throw new Erro(CodigoErro.ACAO_NAO_EXISTE, obterValoresCabecalho().get(9).toString());
         }

         if (!((Id) obterValoresCabecalho().get(9)).mesmoConteudo(new Id("01"))
               && !((Id) obterValoresCabecalho().get(9)).mesmoConteudo(new Id("02"))) {
            throw new Erro(CodigoErro.ACAO_NAO_EXISTE, obterValoresCabecalho().get(9).toString());
         }
      }
   }

   public Requisicao obterRequisicao(List atributos) {
      return new RequisicaoServicoManutencaoItensCestaGarantiasViaArquivo();
   }

   public Requisicao montarRequisicao(Requisicao req, List registro, Servicos servicos) {
      RequisicaoServicoManutencaoItensCestaGarantiasViaArquivo requisicao = (RequisicaoServicoManutencaoItensCestaGarantiasViaArquivo) req;
      requisicao.atribuirGARANTIAS_CODIGO_Id(new Id(obterValoresCabecalho().get(6).toString()));
      requisicao.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia((NumeroCestaGarantia) obterValoresCabecalho().get(6));
      requisicao.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip((CodigoContaCetip) obterValoresCabecalho().get(7));
      requisicao.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip((CodigoContaCetip) obterValoresCabecalho().get(8));
      requisicao.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(((CodigoTipoIF) registro.get(3)));
      requisicao.atribuirGARANTIAS_CODIGO_IF_CodigoIF((CodigoIF) registro.get(4));
      requisicao.atribuirGARANTIAS_CODIGO_IdTipoGarantia((IdTipoGarantia) registro.get(5));
      requisicao.atribuirGARANTIAS_QUANTIDADE_Quantidade((Quantidade) registro.get(6));
      requisicao.atribuirGARANTIAS_ITENS_Booleano((Booleano) registro.get(7));
      requisicao.atribuirGARANTIAS_ITENS_DescricaoLimitada((DescricaoLimitada) registro.get(8));

      if (registro.size() >= 10) {
         requisicao.atribuirOPERACAO_NumeroOperacao((NumeroOperacao) registro.get(9));
      }

      Funcao funcaoRegistro = ((Funcao) registro.get(2));
      funcaoRegistro.atribuirContexto(Contexto.ACAO);
      requisicao.atribuirACAO_Funcao(funcaoRegistro);

      if (funcaoRegistro.mesmoConteudo(Funcao.APORTE)
            && !((Id) obterValoresCabecalho().get(9)).obterConteudo().equals("01")) {
         requisicao.atribuirGARANTIAS_TIPO_ACESSO_Funcao(ICestaDeGarantias.FUNCAO_GARANTIDO);
      } else {
         requisicao.atribuirGARANTIAS_TIPO_ACESSO_Funcao(ICestaDeGarantias.FUNCAO_GARANTIDOR);
      }

      requisicao.novaLinha();

      return requisicao;
   }

   public VisitanteTradutorLayout obterTradutorLayout() {
      return new VisitanteTradutorLayoutGarantias();
   }

   public IQuebraBloco obterQuebraBloco() {
      return new QuebraBlocoPorArquivo(this);
   }
}
