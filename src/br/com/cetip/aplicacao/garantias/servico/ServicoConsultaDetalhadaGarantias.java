package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.Descricao;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.utilitario.Texto;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="DescricaoLimitada" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                    contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero" contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="NumeroInteiro" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_ITENS"
 * 
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_ITENS"
 * 
 * @resultado.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero" contexto="GARANTIAS_ITENS"
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao" contexto="GARANTIAS_ITENS"
 * 
 * @resultado.method atributo="DescricaoLimitada" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_ITENS"
 * 
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_ITENS"
 * 
 * @resultado.method atributo="NumeroInteiro" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_ITENS"
 */
public class ServicoConsultaDetalhadaGarantias extends BaseGarantias implements Servico {

   private RequisicaoServicoConsultaDetalhadaGarantias req;

   private ResultadoServicoConsultaDetalhadaGarantias res;

   private Funcao tipoIFItem;

   private CodigoIF codigoIFItem;

   private Quantidade qtItem;

   private Booleano tipoDireitos;

   private DescricaoLimitada tipoDescricao;

   private Descricao tipoGarantia;

   private NumeroInteiro tipoItem;

   public Resultado executar(Requisicao requisicao) throws Exception {
      return null;
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {

      req = (RequisicaoServicoConsultaDetalhadaGarantias) requisicao;
      res = new ResultadoServicoConsultaDetalhadaGarantias();

      List listaTiposIF = req.obterListaGARANTIAS_ITENS_Funcao();
      Iterator itTiposIF = listaTiposIF.iterator();
      List listaItens = req.obterListaGARANTIAS_ITENS_CodigoIF();
      Iterator itItens = listaItens.iterator();
      List listaQT = req.obterListaGARANTIAS_ITENS_Quantidade();
      Iterator itQT = listaQT.iterator();
      List listaDireitos = req.obterListaGARANTIAS_ITENS_Booleano();
      Iterator itDireitos = listaDireitos.iterator();
      List listaDescricao = req.obterListaGARANTIAS_ITENS_DescricaoLimitada();
      Iterator itDescricao = listaDescricao.iterator();
      List listaTipo = req.obterListaGARANTIAS_CODIGO_Descricao();
      Iterator itTipo = listaTipo.iterator();
      List listaItem = req.obterListaGARANTIAS_ITENS_NumeroInteiro();
      Iterator itItem = listaItem.iterator();

      while (itItens.hasNext() && itQT.hasNext() && itTipo.hasNext() && itDireitos.hasNext() && itTiposIF.hasNext()) {
         Funcao tipoIF = (Funcao) itTiposIF.next();
         tipoIFItem = new Funcao(Texto.nullSafeToString(tipoIF));
         CodigoIF codigoIFGarantias = (CodigoIF) itItens.next();
         codigoIFItem = new CodigoIF(Texto.nullSafeToString(codigoIFGarantias));
         Quantidade qtGarantias = (Quantidade) itQT.next();
         qtItem = new Quantidade(Texto.nullSafeToString(qtGarantias));
         Booleano direitos = (Booleano) itDireitos.next();
         tipoDireitos = new Booleano(Texto.nullSafeToString(direitos));
         Descricao tipo = (Descricao) itTipo.next();
         tipoGarantia = new Descricao(Texto.nullSafeToString(tipo));
         Descricao descricao = (Descricao) itDescricao.next();
         tipoDescricao = new DescricaoLimitada(Texto.nullSafeToString(descricao));
         NumeroInteiro numero = (NumeroInteiro) itItem.next();
         tipoItem = new NumeroInteiro(Texto.nullSafeToString(numero));

         res.atribuirGARANTIAS_ITENS_CodigoIF(codigoIFItem);
         res.atribuirGARANTIAS_ITENS_Quantidade(qtItem);
         res.atribuirGARANTIAS_ITENS_Booleano(tipoDireitos);
         res.atribuirGARANTIAS_ITENS_DescricaoLimitada(tipoDescricao);
         res.atribuirGARANTIAS_CODIGO_Descricao(tipoGarantia);
         res.atribuirGARANTIAS_ITENS_NumeroInteiro(tipoItem);
         res.atribuirGARANTIAS_ITENS_Funcao(tipoIFItem);
      }

      return res;
   }

}