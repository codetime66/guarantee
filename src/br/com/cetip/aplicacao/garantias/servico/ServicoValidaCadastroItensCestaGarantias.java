package br.com.cetip.aplicacao.garantias.servico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IPenhorNoEmissor;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidaAtivoGarantidor;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidarTipoIF;
import br.com.cetip.aplicacao.garantias.apinegocio.colateral.ParametroIFGarantidorFactory;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.HabilitaIFGarantidorDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.TipoMovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.mid.AgenteCalculoDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * Servico para validacao dos dados inseridos pelo usuario na interface grafica
 * de compra/venda de cda/wa.
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="Data"
 *                   pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @resultado.method atributo="CodigoTipoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @resultado.method atributo="CodigoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_IF"
 * 
 * @resultado.method atributo="IdTipoGarantia"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="NumeroCestaGarantia"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="Quantidade"
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_QUANTIDADE"
 * 
 * @resultado.method atributo="DescricaoLimitada"
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_ITENS"
 * 
 * @resultado.method atributo="NumeroOperacao"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="OPERACAO"
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoContaCetip"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @requisicao.method atributo="Data"
 *                    pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                    contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @requisicao.method atributo="Booleano"
 *                    pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                    contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="CodigoTipoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @requisicao.method atributo="CodigoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO_IF"
 * 
 * @requisicao.method atributo="IdTipoGarantia"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="NumeroCestaGarantia"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="Quantidade"
 *                    pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_QUANTIDADE"
 * 
 * @requisicao.method atributo="DescricaoLimitada"
 *                    pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                    contexto="GARANTIAS_ITENS"
 * 
 * @requisicao.method atributo="NumeroOperacao"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="OPERACAO"
 * 
 * @requisicao.method atributo="Funcao"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_TIPO_ACESSO"
 * 
 * @requisicao.method atributo="Funcao"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="ACAO"
 * 
 */
public class ServicoValidaCadastroItensCestaGarantias extends BaseGarantias implements Servico {

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de
    * dados.
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new Erro(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   /*
    * Realiza a validacao dos dados digitados via motor de regras.
    * @param requisicao requisicao que contem os dados para validacao
    * @return o Resultado contendo se a validacao foi aceita ou nao
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "VALIDANDO CADASTRO DE ITENS NA CESTA DE GARANTIAS");
      }

      RequisicaoServicoValidaCadastroItensCestaGarantias req = (RequisicaoServicoValidaCadastroItensCestaGarantias) requisicao;

      CodigoContaCetip participante = req.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();
      CodigoContaCetip contraparte = req.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
      Booleano eventos = req.obterGARANTIAS_ITENS_Booleano();
      Data dataCriacao = req.obterGARANTIAS_DATA_CRIACAO_Data();
      CodigoTipoIF tipoIF = req.obterGARANTIAS_CODIGO_TIPO_CodigoTipoIF();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Tipo de instrumento financeiro: " + tipoIF);
      }

      CodigoIF codigoIF = req.obterGARANTIAS_CODIGO_IF_CodigoIF();
      IdTipoGarantia tipoGarantia = req.obterGARANTIAS_CODIGO_IdTipoGarantia();
      Quantidade qt = req.obterGARANTIAS_QUANTIDADE_Quantidade();
      DescricaoLimitada descricao = req.obterGARANTIAS_ITENS_DescricaoLimitada();
      Funcao origem = req.obterGARANTIAS_TIPO_ACESSO_Funcao();
      // parametro que vai nortear que criticas devem ser feitas
      Funcao acao = req.obterACAO_Funcao();
      NumeroOperacao numOperacao = req.obterOPERACAO_NumeroOperacao();
      NumeroCestaGarantia numero = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      Id sistema = null;

      if (tipoIF.ehNAO_CETIPADO()) {
         Logger.debug("Tipo IF NAO CETIPADO");
         sistema = SistemaDO.CETIP21;

      } else {
         InstrumentoFinanceiroDO ativo = InstrumentoFinanceiroFactory.getInstance()
               .obterInstrumentoFinanceiro(codigoIF);

         // alteracao para que a regra de a mensagem de campo obrigatorio
         sistema = ativo.getSistema().getNumero();

         // obtem o codigo do tipo IF de ativos Selic
         if (CodigoTipoIF.SELIC.mesmoConteudo(tipoIF)) {
            tipoIF = ativo.getTipoIF().getCodigoTipoIF();
         }

         if (sistema != null && tipoIF != null) {
            IValidarTipoIF ivt = getFactory().getInstanceValidarTipoIF();
            if (!ivt.validarGarantidor(tipoIF, sistema)) {
               Erro e = new Erro(CodigoErro.CESTA_INCOMPATIVEL);
               e.parametroMensagem("Tipo IF e Sistema do ativo", 0);
               throw e;
            }
         }

         IMovimentacoesGarantias imovs = getFactory().getInstanceMovimentacoesGarantias();
         ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();

         CestaGarantiasDO cesta = null;
         if (!Condicional.vazio(numero)) {
            cesta = icg.obterCestaDeGarantias(numero);
            if (acao.mesmoConteudo(ICestaDeGarantias.APORTAR_GARANTIA)) {
               CodigoTipoIF codTipoIFGarantido = icg.obterTipoIFGarantidoCesta(cesta);
               IValidaAtivoGarantidor validaAtivoGarantidor = getFactory().getInstanceValidaAtivoGarantidor(codTipoIFGarantido);
               validaAtivoGarantidor.validar(cesta, ativo);
               
               //valida se o ativo possui habilitacao para MtM
               Booleano ehCetipAgenteCalc =  ParametroIFGarantidorFactory.getInstance().ehAtivoCetipComoAgenteCalculo(ativo.getId());
               if ((codTipoIFGarantido.ehSWAP() ||
            	    codTipoIFGarantido.ehContratoOpcao() || 
            	    codTipoIFGarantido.ehContratoTermo()) && ehCetipAgenteCalc.ehVerdadeiro()) {
                  if (ehAtivoHabilitadoMtM(ativo).ehFalso()){
                	  throw new ExcecaoServico(CodigoErro.IF_GARANTIDOR_NAO_HABILITADO_MTM);
                  }
               }
               

               if (origem.mesmoConteudo(ICestaDeGarantias.FUNCAO_GARANTIDO)) {
                  IPenhorNoEmissor pe = getFactory().getInstancePenhorNoEmissor();
                  boolean ehPenhorNoEmissor = pe.eCestaPenhorNoEmissor(cesta);

                  // Se a cesta for penhor no emissor, o garantido nao
                  // pode aciona-la!
                  if (ehPenhorNoEmissor) {
                     throw new ExcecaoServico(CodigoErro.ACAO_INVALIDA_CESTA);
                  }
               }

               Map map = new HashMap(2);
               map.put(ICestaDeGarantias.FUNCAO_GARANTIDOR, StatusMovimentacaoGarantiaDO.APORTE_PENDENTE_GARANTIDO);
               map.put(ICestaDeGarantias.FUNCAO_GARANTIDO, StatusMovimentacaoGarantiaDO.APORTE_PENDENTE_GARANTIDOR);
               StatusMovimentacaoGarantiaDO status = (StatusMovimentacaoGarantiaDO) map.get(origem);

               Object o = imovs.obterMovimentacaoParaAtivo(cesta, ativo.getCodigoIF(),
                     TipoMovimentacaoGarantiaDO.APORTE, status);
               if (o != null) {
                  throw new Erro(CodigoErro.CESTA_ITEM_JA_INCLUIDO, codigoIF.obterConteudo());
               }
            } else if (acao.mesmoConteudo(ICestaDeGarantias.INCLUIR_GARANTIA)) {
               Object o = imovs.obterMovimentacaoParaAtivo(cesta, ativo.getCodigoIF(),
                     TipoMovimentacaoGarantiaDO.BLOQUEIO, StatusMovimentacaoGarantiaDO.PENDENTE);
               if (o != null) {
                  throw new Erro(CodigoErro.CESTA_ITEM_JA_INCLUIDO, codigoIF.obterConteudo());
               }
            }
         }
      }

      List ac = new ArrayList(15);
      ac.add(participante);
      ac.add(contraparte);
      ac.add(tipoIF);
      ac.add(descricao);
      ac.add(eventos);
      ac.add(codigoIF);
      ac.add(dataCriacao);
      ac.add(tipoGarantia);
      ac.add(qt);
      ac.add(sistema);
      ac.add(origem);
      ac.add(acao);
      ac.add(numero);
      ac.add(numOperacao);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Parametros da regra podeRegistrarCadastroItensCestaGarantias " + ac.toString());
      }

      // chama uma regra para validar os campos
      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeRegistrarCadastroItensCestaGarantias,
            ac, true);

      return new ResultadoServicoValidaCadastroItensCestaGarantias();
   }
   
   private Booleano ehAtivoHabilitadoMtM(InstrumentoFinanceiroDO ativo){
	   Booleano res = Booleano.VERDADEIRO;
      
	   Booleano ehAtivoHabilitadoMtM = ParametroIFGarantidorFactory.getInstance().existeCodigoIFJaHabilitado(ativo.getSistema().getCodSistema(), ativo.getTipoIF().getCodigoTipoIF(), ativo.getCodigoIF());
	       if (ehAtivoHabilitadoMtM.ehFalso()){
	           	 Booleano ehTodosIFHabilitado = ParametroIFGarantidorFactory.getInstance().ehHabilitadoPorModuloSistema(ativo.getSistema().getCodSistema(), ativo.getTipoIF().getCodigoTipoIF());
	            res = ehTodosIFHabilitado;
	       }
	       
       return res;
   }
   
}
