package br.com.cetip.aplicacao.garantias.web.selic;

import br.com.cetip.aplicacao.garantias.servico.selic.RequisicaoServicoConsultaDetalhesGarantiasSelic;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;

public class RelacaoDetalhesGarantiasSelic extends Relacao {

   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoConsultaDetalhesGarantiasSelic req = new RequisicaoServicoConsultaDetalhesGarantiasSelic();
      GrupoDeAtributos gda = servicos.chamarServico(req, dados);
      return gda;
   }

   public void informarColunas(GrupoDeAtributos colunas, Grupo arg1, Servicos arg2) throws Exception {

      colunas.atributo(new Quantidade(Contexto.QUANTIDADE));
      colunas.atributo(new Id(Contexto.CESTA_GARANTIA));
      colunas.atributo(new Booleano(Contexto.GARANTIAS_ITENS));
      colunas.atributo(new CodigoContaCetip(Contexto.GARANTIDOR));
      colunas.atributo(new CodigoContaCetip(Contexto.CONTA_GARANTIDOR_PROPRIA_SELIC));
      colunas.atributo(new NomeSimplificado(Contexto.GARANTIDOR));
      colunas.atributo(new CodigoIF(Contexto.CODIGO_IF));
      colunas.atributo(new CodigoTipoIF(Contexto.CODIGO_TIPO_IF));
      colunas.atributo(new Data(Contexto.DATA_VENCIMENTO));
      colunas.atributo(new CodigoContaCetip(Contexto.GARANTIDO));
      colunas.atributo(new CodigoContaCetip(Contexto.CONTA_GARANTIDO_PROPRIA_SELIC));
      colunas.atributo(new NomeSimplificado(Contexto.GARANTIDO));
   }

   public void informarLinks(Links links, Grupo atributos, Servicos arg2) throws Exception {
   }

   public void informarParametros(GrupoDeAtributos parametros, Grupo arg1, Servicos arg2) throws Exception {
   }

   public Class obterDestino(Atributo coluna, Grupo grupo, Servicos servicos) throws Exception {
      return null;
   }

}
