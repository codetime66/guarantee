package br.com.cetip.aplicacao.garantias.web.selic;

import br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoRelacaoPosicaoSelic;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaSelic;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoParticipanteISPB;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleIF;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteira;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;

public class RelacaoPosicaoSelic extends Relacao {

   GrupoDeAtributos dadosPosicaoSaldoSelic = null;

   public void informarColunas(GrupoDeAtributos tela, Grupo dados, Servicos servicos) throws Exception {

      CodigoContaSelic contaSelic = (CodigoContaSelic) dadosPosicaoSaldoSelic.obterAtributo(CodigoContaSelic.class,
            Contexto.CONTA);
      CodigoIF codigoIF = (CodigoIF) dadosPosicaoSaldoSelic.obterAtributo(CodigoIF.class, Contexto.CODIGO_IF);

      contaSelic.atribuirContexto(Contexto.CONTA);
      codigoIF.atribuirContexto(Contexto.CODIGO_IF);

      GrupoDeAtributos cabecalho = new GrupoDeAtributos(2);
      cabecalho.atributo(contaSelic);
      cabecalho.atributo(codigoIF);

      tela.grupoDeColunas(new CodigoIF(Contexto.CODIGO_IF), cabecalho);
      tela.grupoDeColunas(new CodigoContaSelic(Contexto.CONTA), cabecalho);
      tela.grupoDeColunas(new QuantidadeInteira(Contexto.QUANTIDADE), cabecalho);
      tela.grupoDeColunas(new Data(Contexto.DATA_VENCIMENTO), cabecalho);
      tela.grupoDeColunas(new Data(Contexto.DATA_REFERENCIA), cabecalho);
      tela.grupoDeColunas(new CodigoParticipanteISPB(Contexto.PARTICIPANTE), cabecalho);
      tela.grupoDeColunas(new NumeroControleIF(Contexto.NUM_CONTROLE), cabecalho);
   }

   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {

      RequisicaoServicoRelacaoPosicaoSelic req = new RequisicaoServicoRelacaoPosicaoSelic();
      req
            .atribuirCONTA_CodigoContaSelic((CodigoContaSelic) dados.obterAtributo(CodigoContaSelic.class,
                  Contexto.CONTA));
      req.atribuirCODIGO_IF_CodigoIF((CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.CODIGO_IF));
      dadosPosicaoSaldoSelic = servicos.chamarServico(req);

      if (dadosPosicaoSaldoSelic.obterAtributosColunados().obterNumeroDeAtributos() == 0) {
         throw new Erro(CodigoErro.RESULTADO_INEXISTENTE);
      }
      return dadosPosicaoSaldoSelic;

   }

   public void informarLinks(Links links, Grupo linha, Servicos servicos) throws Exception {
      CodigoIF codigoIF = (CodigoIF) linha.obterAtributo(CodigoIF.class, Contexto.CODIGO_IF);

      if (!Condicional.vazio(codigoIF)) {
         links.coluna(new CodigoIF(Contexto.CODIGO_IF));
      }
   }

   public void informarParametros(GrupoDeAtributos atributos, Grupo grupo, Servicos servicos) throws Exception {

      atributos.atributo(new CodigoContaSelic(Contexto.CONTA));
      atributos.atributo(new CodigoIF(Contexto.CODIGO_IF));
   }

   public Class obterDestino(Atributo atributo, Grupo grupo, Servicos servicos) throws Exception {
      return servicos.obterDestino("Garantias", "RelacaoDetalhesGarantiasSelic");
   }

}
