package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IRetirarGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoRetirada;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico para RETIRAR garantias de uma determinada Cesta
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 * 
 * @requisicao.class
 * @requisicao.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_LIBERACAO"
 * 
 * @requisicao.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_LIBERAR_QUANTIDADE"
 * 
 * @requisicao.method atributo="Booleano" pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="GARANTIA"
 *  
 * @requisicao.method atributo="NumeroOperacao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="OPERACAO" 
 *                    
 * @requisicao.method atributo="Funcao" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_ACESSO"                              
 * 
 * @resultado.class
 * 
 */
public class ServicoLiberarGarantias extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      RequisicaoServicoLiberarGarantias req;
      req = (RequisicaoServicoLiberarGarantias) requisicao;

      List idGarantias = req.obterListaGARANTIAS_LIBERACAO_Id();
      List qtdadesARetirar = req.obterListaGARANTIAS_LIBERAR_QUANTIDADE_Quantidade();
      Booleano sohValidacao = req.obterGARANTIA_Booleano();
      List numerosOperacao = req.obterListaOPERACAO_NumeroOperacao();
      Funcao tipoAcesso = req.obterGARANTIAS_TIPO_ACESSO_Funcao();

      if (Condicional.vazio(tipoAcesso)) {
         tipoAcesso = ICestaDeGarantias.FUNCAO_GARANTIDO;
      }

      if (idGarantias.isEmpty()) {
         return new ResultadoServicoLiberarGarantias();
      }

      // Valida retirada customizada
      Id idPrimeiraGarantia = (Id) idGarantias.get(0);
      IGerenciadorPersistencia gp = getGp();
      CestaGarantiasDO cesta = ((DetalheGarantiaDO) gp.load(DetalheGarantiaDO.class, idPrimeiraGarantia))
            .getCestaGarantias();

      IGarantias factory = getFactory();

      CodigoTipoIF tipoIF = obtemTipoIFGarantido(cesta);
      IValidacaoRetirada validacaoRetirada = factory.getInstanceValidacaoRetirada(tipoIF);
      if (validacaoRetirada != null) {
         validacaoRetirada.validaRetirada(idGarantias, qtdadesARetirar, numerosOperacao);
      }

      // Se era soh para validar, retorna
      if (!Condicional.vazio(sohValidacao) && sohValidacao.ehVerdadeiro()) {
         return new ResultadoServicoLiberarGarantias();
      }

      //Remove as garantias 
      IRetirarGarantias irg = factory.getInstanceRetirarGarantias();
      irg.retirarGarantias(idGarantias, qtdadesARetirar, numerosOperacao, Booleano.FALSO, getDataHoje(), tipoAcesso);

      return new ResultadoServicoLiberarGarantias();
   }

   private CodigoTipoIF obtemTipoIFGarantido(CestaGarantiasDO cesta) {
      Iterator i = cesta.getAtivosVinculados().iterator();
      CodigoTipoIF tipoIF = null;
      if (i.hasNext()) {
         InstrumentoFinanceiroDO ifDO = ((CestaGarantiasIFDO) i.next()).getInstrumentoFinanceiro();
         tipoIF = ifDO.getTipoIF().getCodigoTipoIF();
      }
      return tipoIF;
   }

   public Resultado executarConsulta(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}