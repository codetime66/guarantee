package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaHistoricoCesta;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.DataHora;
import br.com.cetip.infra.atributo.tipo.texto.Descricao;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.tipo.texto.Nome;

/**
 * Relacao de Historico da Cesta
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class RelacaoHistoricoCesta extends Relacao {

   /**
    * 
    */
   public void informarColunas(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {
      atributos.atributo(new Nome(Contexto.TIPO_IF));
      atributos.atributo(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
      atributos.atributo(new Descricao(Contexto.TIPO_GARANTIA));
      atributos.atributo(new Quantidade(Contexto.GARANTIAS_QUANTIDADE));
      atributos.atributo(new Booleano(Contexto.GARANTIAS_ITENS));
      atributos.atributo(new Nome(Contexto.GARANTIAS_TIPO_MOV));
      atributos.atributo(new Nome(Contexto.GARANTIAS_STATUS_MOV));
      atributos.atributo(new DataHora(Contexto.GARANTIAS_MOVIMENTACAO));
      atributos.atributo(new NumeroOperacao(Contexto.OPERACAO));
      atributos.atributo(new DescricaoLimitada(Contexto.GARANTIAS_ITENS));
   }

   /**
    * 
    */
   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {

      NumeroCestaGarantia numero = null;
      numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);

      Id tipo = null;
      tipo = (Id) dados.obterAtributo(Id.class, Contexto.GARANTIAS_TIPO_MOV);

      Id status = null;
      status = (Id) dados.obterAtributo(Id.class, Contexto.GARANTIAS_STATUS_MOV);

      RequisicaoServicoListaHistoricoCesta req = new RequisicaoServicoListaHistoricoCesta();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
      req.atribuirGARANTIAS_TIPO_MOV_Id(tipo);
      req.atribuirGARANTIAS_STATUS_MOV_Id(status);

      GrupoDeAtributos retorno = servicos.chamarServico(req);
      return retorno;
   }

   /**
    * Tela apenas de consulta. Sem parametros...
    */
   public void informarParametros(GrupoDeAtributos parametros, Grupo dados, Servicos servicos) throws Exception {
   }

   /**
    * Tela apenas de consulta. Sem destino...
    */
   public Class obterDestino(Atributo atributo, Grupo parametros, Servicos servicos) throws Exception {
      return null;
   }

   /**
    * Tela apenas de consulta. Sem links...
    */
   public void informarLinks(Links links, Grupo linha, Servicos servicos) throws Exception {
   }

}