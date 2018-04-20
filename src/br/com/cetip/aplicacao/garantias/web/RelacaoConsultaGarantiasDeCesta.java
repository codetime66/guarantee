package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoListaGarantiasCesta;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.Descricao;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.TextoLimitado;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;

/**
 * Relacao de Garantias da Cesta - Apenas para Consulta
 *
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class RelacaoConsultaGarantiasDeCesta extends Relacao {

   /*
    * (non-Javadoc)
    *
    * @see
    * br.com.cetip.base.web.acao.Relacao#informarColunas(br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarColunas(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {
      atributos.atributo(new Nome(Contexto.TIPO_IF));
      atributos.atributo(new CodigoIF(Contexto.GARANTIAS_CODIGO_IF));
      atributos.atributo(new Booleano(Contexto.INADIMPLENTE));
      atributos.atributo(new Booleano(Contexto.INADIMPLENTE_EMISSOR));
      atributos.atributo(new Descricao(Contexto.TIPO_GARANTIA));
      atributos.atributo(new Booleano(Contexto.GARANTIDO));
      atributos.atributo(new TextoLimitado(Contexto.CESTA_GARANTIA));
      atributos.atributo(new Quantidade(Contexto.GARANTIAS_QUANTIDADE));
      atributos.atributo(new Booleano(Contexto.GARANTIAS_ITENS));
      atributos.atributo(new DescricaoLimitada(Contexto.GARANTIAS_ITENS));
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#chamarServico(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    * br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {
      NumeroCestaGarantia numero = obterNumeroCesta(dados, servicos);

      RequisicaoServicoListaGarantiasCesta req = new RequisicaoServicoListaGarantiasCesta();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);

      GrupoDeAtributos gda = servicos.chamarServico(req);
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "retorno de servico: " + gda);
         Logger.debug(this, "Atributo: " + gda.obterAtributo(Booleano.class, Contexto.GARANTIDO));
      }

      return gda;
   }

   private NumeroCestaGarantia obterNumeroCesta(Grupo dados, Servicos servicos) throws Exception {
      NumeroCestaGarantia numero = null;

      if (Condicional.vazio(numero)) {
         numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);
         if (Logger.estaHabilitadoDebug(this) && numero != null) {
            Logger.debug(this, "NumeroCesta(GARANTIAS_CODIGO): " + numero);
         }
      }

      if (Condicional.vazio(numero)) {
         RelacaoConsultaCestasPorIF tela = (RelacaoConsultaCestasPorIF) servicos
               .obterTela(RelacaoConsultaCestasPorIF.class);
         if (tela != null) {
            numero = tela.getNumeroCestaGarantia();

            if (Logger.estaHabilitadoDebug(this) && numero != null) {
               Logger.debug(this, "NumeroCesta(" + numero.obterContexto() + ")/RelacaoConsultaCestasPorIF: " + numero);
            }
         }
      }

      // Veio da relacao?
      if (Condicional.vazio(numero)) {
         ConfirmacaoRelacaoCestasGarantias tela = (ConfirmacaoRelacaoCestasGarantias) servicos
               .obterTela(ConfirmacaoRelacaoCestasGarantias.class);

         if (tela != null && tela.obterTabela() != null) {
            AtributosColunados ac = tela.obterTabela().obterAtributosColunados();
            numero = (NumeroCestaGarantia) ac.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);

            if (Logger.estaHabilitadoDebug(this) && numero != null) {
               Logger.debug(this, "NumeroCesta(GARANTIAS_CODIGO)/ConfirmacaoRelacaoCestasGarantias: " + numero);
            }
         }
      }

      if (Condicional.vazio(numero)) {
         numero = (NumeroCestaGarantia) dados.obterAtributo(NumeroCestaGarantia.class, Contexto.CESTA_GARANTIA);
         if (Logger.estaHabilitadoDebug(this) && numero != null) {
            Logger.debug(this, "NumeroCesta(GARANTIDO): " + numero);
         }
      }

      if (Condicional.vazio(numero)) {
         Id id = (Id) dados.obterAtributo(Id.class, Contexto.GARANTIAS_CESTA);
         if (id != null) {
            numero = new NumeroCestaGarantia(id.obterConteudo());
         }
      }
      return numero;
   }

   /*
    * (non-Javadoc)
    *
    * @see
    * br.com.cetip.base.web.acao.Relacao#informarParametros(br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarParametros(GrupoDeAtributos parametros, Grupo dados, Servicos servicos) throws Exception {
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#obterDestino(br.com.cetip.infra.atributo.Atributo,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public Class obterDestino(Atributo atributo, Grupo parametros, Servicos servicos) throws Exception {
      return null;
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#informarLinks(br.com.cetip.base.web.acao.suporte.Links,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarLinks(Links links, Grupo linha, Servicos servicos) throws Exception {
   }

}