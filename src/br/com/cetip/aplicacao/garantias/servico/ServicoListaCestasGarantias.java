package br.com.cetip.aplicacao.garantias.servico;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.FiltroCestaBean;
import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IFiltroCestas;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IMapaAcoes;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.Detalhe;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Consulta lista de cestas de garantias
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * 
 * @requisicao.class
 * @requisicao.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" 
 *                    contexto="GARANTIAS_STATUS"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_ACESSO"
 * 
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador" 
 *                    contexto="RESET"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="INADIMPLENTE_EMISSOR"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="INADIMPLENTE_ATIVO"
 *                    
 * @requisicao.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="TIPO_IF_GARANTIDO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="NumeroCestaGarantia" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" 
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" 
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" 
 *                   contexto="GARANTIAS_STATUS"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" 
 *                   contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @resultado.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador" 
 *                   contexto="ACAO"
 * 
 * @resultado.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador" 
 *                   contexto="RESET"
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="INADIMPLENTE_EMISSOR"
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="INADIMPLENTE"
 * 
 * @resultado.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="INADIMPLENTE_ATIVO"
 * 
 * @resultado.method atributo="Detalhe"
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="DETALHE_ADICIONAL"
 *                   
 */
public class ServicoListaCestasGarantias extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      return null;
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoListaCestasGarantias req;
      req = (RequisicaoServicoListaCestasGarantias) requisicao;

      NumeroCestaGarantia numero = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      CodigoContaCetip garantidor = req.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();
      CodigoContaCetip garantido = req.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
      Id status = req.obterGARANTIAS_STATUS_Id();
      CodigoTipoIF tipoIF = req.obterTIPO_IF_GARANTIDO_CodigoTipoIF();
      Funcao reset = req.obterRESET_Funcao();
      Funcao tipoAcesso = req.obterGARANTIAS_TIPO_ACESSO_Funcao();

      IGarantias factory = getFactory();
      IMapaAcoes ima = getFactory().getInstanceMapaAcoes();

      Collection c = ima.obterAcoesPara(tipoAcesso);
      List acoes = new ArrayList(c);

      if (tipoAcesso != null) {
         acoes.remove(ICestaDeGarantias.CONSULTAR_GARANTIAS);
         acoes.remove(ICestaDeGarantias.CONSULTAR_HISTORICO);
         acoes.remove(ICestaDeGarantias.LIBERAR_GARANTIAS);
      }

      List cestas = null;

      boolean filtroPorNumero = !Condicional.vazio(numero);
      if (filtroPorNumero) {
         cestas = new ArrayList(1);
         CestaGarantiasDO cesta = factory.getInstanceCestaDeGarantias().obterCestaDeGarantias(numero, tipoAcesso);
         cestas.add(cesta);
      } else {
         cestas = new ArrayList(200);
      }

      Booleano cestaComEmissorInadimplente = req.obterINADIMPLENTE_EMISSOR_Booleano();
      Booleano cestaComAtivoInadimplente = req.obterINADIMPLENTE_ATIVO_Booleano();

      FiltroCestaBean valores = new FiltroCestaBean();
      valores.numero = numero;
      valores.garantido = garantido;
      valores.garantidor = garantidor;
      valores.somenteComAtivoInadimplente = cestaComAtivoInadimplente;
      valores.somenteComEmissorInadimplente = cestaComEmissorInadimplente;
      valores.status = status;
      valores.reset = reset;
      valores.tipoIF = tipoIF;

      IFiltroCestas filtroCestas = factory.getInstanceFiltroCestas();
      if (ICestaDeGarantias.FUNCAO_GARANTIDO.equals(tipoAcesso)) {
         List _cestas = filtroCestas.filtrarCestasGarantidoPor(valores);
         cestas.addAll(_cestas);
      } else if (ICestaDeGarantias.FUNCAO_GARANTIDOR.equals(tipoAcesso)) {
         List _cestas = filtroCestas.filtrarCestasGarantidorPor(valores);
         cestas.addAll(_cestas);
      } else {
         List _cestas = filtroCestas.filtrarCestasPorAmbos(valores);
         cestas.addAll(_cestas);
      }

      if (filtroPorNumero) {
         if (cestas.size() == 1) {
            cestas.clear();
         } else if (cestas.size() == 2) {
            cestas.remove(1);
         }
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "*** INFORMACAO RESET: " + reset);
      }

      if (cestas.isEmpty()) {
         throw new Erro(CodigoErro.GARANTIAS_LISTA_VAZIA);
      }

      ResultadoServicoListaCestasGarantias res;
      res = new ResultadoServicoListaCestasGarantias();

      Iterator i = cestas.iterator();
      while (i.hasNext()) {
         CestaGarantiasDO cesta = (CestaGarantiasDO) i.next();

         res.novaLinha();

         Funcao funcao = new Funcao(Contexto.ACAO);
         funcao.getDomain().add(new Funcao(""));
         Iterator iAcoes = acoes.iterator();

         while (iAcoes.hasNext()) {
            Funcao acao = (Funcao) iAcoes.next();
            funcao.getDomain().add(new Funcao(acao));
         }

         res.atribuirACAO_Funcao(funcao);

         // Atributos
         res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(new NumeroCestaGarantia(cesta.getNumIdCestaGarantias()));
         res.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(cesta.getGarantidor().getCodContaParticipante());
         res.atribuirGARANTIAS_PARTICIPANTE_Nome(cesta.getGarantidor().getNomeContaParticipante());
         res.atribuirGARANTIAS_DATA_CRIACAO_Data(cesta.getDatCriacao());

         StatusCestaDO statusDO = cesta.getStatusCesta();
         if (statusDO.equals(StatusCestaDO.VINCULADA) || statusDO.equals(StatusCestaDO.INADIMPLENTE)) {
            res.atribuirDETALHE_ADICIONAL_Detalhe(new Detalhe("Ativo(s) Vinculado(s)"));
         } else {
            res.atribuirDETALHE_ADICIONAL_Detalhe(new Detalhe(""));
         }

         res.atribuirINADIMPLENTE_ATIVO_Booleano(cesta.getIndInadimplenciaAtivo());
         res.atribuirINADIMPLENTE_EMISSOR_Booleano(cesta.getIndInadimplenciaEmissor());

         Texto _status = cesta.getStatusCesta().getCodStatusCesta();
         res.atribuirGARANTIAS_STATUS_Texto(_status);

         // Garantido
         IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
         ContaParticipanteDO contaGarantido = igc.obterGarantidoCesta(cesta);
         if (contaGarantido != null) {
            res.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contaGarantido.getCodContaParticipante());
            res.atribuirGARANTIAS_CONTRAPARTE_Nome(contaGarantido.getNomeContaParticipante());
         } else {
            res.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(new CodigoContaCetip());
            res.atribuirGARANTIAS_CONTRAPARTE_Nome(new Nome());
         }

         Texto indLiberaGarantiaReset = cesta.getIndRegraLiberacao();
         if (indLiberaGarantiaReset == null || indLiberaGarantiaReset.vazio()) {
            res.atribuirRESET_Funcao(new Funcao(""));
         } else {
            res.atribuirRESET_Funcao(new Funcao(Contexto.RESET, indLiberaGarantiaReset.obterConteudo()));
         }
      }

      return res;
   }

}