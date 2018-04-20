package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoConsultaCestaPorIF;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.Descricao;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.log.Logger;

/**
 * Tela de relacao entre IFs e cestas, conforme especificacao do MMG Migracao Agricolas: Requerimentos para o MMG e SIC,
 * versao 1, data 02/04/2007, pgs.21-22
 * 
 * @author cabreva
 * 
 */
public class RelacaoConsultaCestasPorIF extends Relacao {

   private NumeroCestaGarantia numeroCestaGarantia;

   /*
    * (non-Javadoc)
    * @see br.com.cetip.base.web.acao.Relacao#informarLinks(br.com.cetip.base.web.acao.suporte.Links, br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarLinks(Links links, Grupo linha, Servicos servicos) throws Exception {
      links.coluna(new NumeroCestaGarantia(Contexto.CESTA_GARANTIA));
   }

   /*
    * (non-Javadoc)
    * @see br.com.cetip.base.web.acao.Relacao#chamarServico(br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {
      CodigoIF codIF = (CodigoIF) dados.obterAtributo(CodigoIF.class, Contexto.CESTA_GARANTIA);
      RequisicaoServicoConsultaCestaPorIF req = new RequisicaoServicoConsultaCestaPorIF();
      req.atribuirCESTA_GARANTIA_CodigoIF(codIF);

      GrupoDeAtributos gda = servicos.chamarServico(req);
      gda.atributo(dados.obterAtributo(CodigoIF.class, Contexto.CESTA_GARANTIA));
      return gda;
   }

   /*
    * (non-Javadoc)
    * @see br.com.cetip.base.web.acao.Relacao#obterDestino(br.com.cetip.infra.atributo.Atributo, br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public Class obterDestino(Atributo atributo, Grupo parametros, Servicos servicos) throws Exception {
      if (atributo instanceof NumeroCestaGarantia) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Click NumeroCestaGarantia(" + atributo.obterContexto() + ") = " + atributo);
         }

         this.numeroCestaGarantia = (NumeroCestaGarantia) parametros.obterAtributo(atributo.getClass(), atributo
               .obterContexto());

         return RelacaoConsultaGarantiasDeCesta.class;
      }

      return null;
   }

   protected final NumeroCestaGarantia getNumeroCestaGarantia() {
      NumeroCestaGarantia temp = this.numeroCestaGarantia;
      this.numeroCestaGarantia = null;
      return temp;
   }

   /*
    * (non-Javadoc)
    * @see br.com.cetip.base.web.acao.Relacao#informarColunas(br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos, br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarColunas(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {
      atributos.atributo(new NumeroCestaGarantia(Contexto.CESTA_GARANTIA));
      atributos.atributo(new Texto(Contexto.GARANTIAS_STATUS));
      atributos.atributo(new Descricao(Contexto.TIPO_GARANTIA));
      atributos.atributo(new Texto(Contexto.RELACAO_TIPO));
      atributos.atributo(new Nome(Contexto.GARANTIAS_PARTICIPANTE));
      atributos.atributo(new CodigoContaCetip(Contexto.GARANTIAS_PARTICIPANTE));
   }

   /*
    * (non-Javadoc)
    * @see br.com.cetip.base.web.acao.Relacao#informarParametros(br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos, br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarParametros(GrupoDeAtributos atributos, Grupo linha, Servicos servicos) throws Exception {
      atributos.atributo(new NumeroCestaGarantia(Contexto.CESTA_GARANTIA));
   }

}
