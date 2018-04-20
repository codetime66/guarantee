package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.aplicacao.sap.apinegocio.IContaParticipante;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoGarantiaDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiroPositivo;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico que registra um cadastro da cesta de garantias
 * 
 * @resultado.class
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
 * @resultado.method atributo="Id"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="Funcao"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_TIPO"
 * 
 * @requisicao.method atributo="Id"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="CodigoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_IF"
 * 
 * @resultado.method atributo="IdTipoGarantia"
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
 * @requisicao.method atributo="Booleano"
 *                    pacote="br.com.cetip.infra.atributo.tipo.expressao"
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
 * @requisicao.method atributo="QuantidadeInteiraPositiva"
 *                    pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @requisicao.method atributo="NumeroOperacao"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="OPERACAO"
 * 
 * @requisicao.method atributo="Id"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="SISTEMA"
 */
public class ServicoRegistraCadastroCestaGarantias extends BaseGarantias implements Servico {

   private IGerenciadorPersistencia gp;

   /*
    * Este servico/metodo nao executa operacoes de alteracao e insercao de dados.
    */
   public Resultado executar(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "***** Chamada ao servico de inclusao de Cesta de Garantias ***** ");
      }

      RequisicaoServicoRegistraCadastroCestaGarantias req = (RequisicaoServicoRegistraCadastroCestaGarantias) requisicao;
      ResultadoServicoRegistraCadastroCestaGarantias res = new ResultadoServicoRegistraCadastroCestaGarantias();

      // validacao na mesma transacao?
      Booleano valida = req.obterGARANTIAS_CODIGO_Booleano();
      if (!Condicional.vazio(valida) && valida.ehVerdadeiro()) {
         RequisicaoServicoValidaCadastroCestaGarantias reqV = new RequisicaoServicoValidaCadastroCestaGarantias();
         reqV.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(req.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip());
         reqV.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(req.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip());
         ServicoValidaCadastroCestaGarantias servicoValida = new ServicoValidaCadastroCestaGarantias();
         servicoValida.setGarantias(getFactory());
         servicoValida.executarConsulta(reqV);
      }

      gp = getGp();

      CestaGarantiasDO nrCesta = criaEntradaCesta(req);
      Id codigo = nrCesta.getNumIdCestaGarantias();
      res.atribuirGARANTIAS_CODIGO_Id(codigo);
      return res;
   }

   /*
    * Realiza a validacao dos dados digitados via motor de regras.
    * 
    * @param requisicao requisicao que contem os dados para validacao
    * 
    * @return o Resultado contendo se a validacao foi aceita ou nao
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      throw new UnsupportedOperationException();
   }

   private CestaGarantiasDO criaEntradaCesta(RequisicaoServicoRegistraCadastroCestaGarantias requisicao) {
      Data d0 = getDataHoje();

      // Criacao da Cesta de Garantias
      CestaGarantiasDO cesta = new CestaGarantiasDO();
      cesta.setDatCriacao(d0);
      cesta.setNumPrazoExpiracao(new NumeroInteiroPositivo(getFactory().getPrazo()));
      cesta.setStatusCesta(StatusCestaDO.EM_MANUTENCAO, d0);
      cesta.setIndInadimplencia(MovimentacaoGarantiaDO.INADIMPLENCIA);

      CodigoContaCetip contaParte = requisicao.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();

      IContaParticipante cp;
      ContaParticipanteDO contaParteDO;
      try {
         cp = ContaParticipanteFactory.getInstance();
         contaParteDO = cp.obterContaParticipanteDO(contaParte);
         cesta.setGarantidor(contaParteDO);
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }

      Id id = obterIdCesta();
      cesta.setNumIdCestaGarantias(id);

      if (!Condicional.vazio(requisicao.obterGARANTIAS_CODIGO_IdTipoGarantia())) {
         TipoGarantiaDO tipoGarantia = new TipoGarantiaDO();
         tipoGarantia.setNumIdTipoGarantia(requisicao.obterGARANTIAS_CODIGO_IdTipoGarantia());
         cesta.setTipoGarantia(tipoGarantia);
      }

      gp.save(cesta);
      gp.flush();

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, cesta + " incluida.");
      }

      CodigoContaCetip contaContraparte = requisicao.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();

      // adiciona contraparte se tem
      if (!Condicional.vazio(contaContraparte)) {
         ContaParticipanteDO conta;
         try {
            conta = cp.obterContaParticipanteDO(contaContraparte);
         } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
         }

         IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
         igc.associarGarantidoNaCesta(cesta, conta);
      }

      // Verifica se os dados estao vindo do formulario ou da transferencia de arquivo
      // se for 1, entao eh da tela... se for 0, eh do arquivo, e nao cria o primeiro item
      if (requisicao.obterGARANTIAS_CODIGO_QuantidadeInteiraPositiva().obterConteudo().intValue() == 1) {
         criaItemCesta(requisicao, cesta);
      }

      return cesta;
   }

   private Id obterIdCesta() {
      Id id = (Id) gp.executarFunction("CETIP.GET_NUM_CESTA_GARANTIAS", null, Id.class);

      return id;
   }

   private void criaItemCesta(RequisicaoServicoRegistraCadastroCestaGarantias requisicao, CestaGarantiasDO cesta) {
      RequisicaoServicoRegistraItensCestaGarantias req = null;
      req = new RequisicaoServicoRegistraItensCestaGarantias();

      // obtem valores
      CodigoContaCetip contraParte = requisicao.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
      CodigoContaCetip parte = requisicao.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();
      Quantidade qt = requisicao.obterGARANTIAS_QUANTIDADE_Quantidade();
      CodigoTipoIF codTipoIf = requisicao.obterGARANTIAS_CODIGO_TIPO_CodigoTipoIF();
      DescricaoLimitada desc = requisicao.obterGARANTIAS_ITENS_DescricaoLimitada();
      CodigoIF codIF = requisicao.obterGARANTIAS_CODIGO_IF_CodigoIF();
      Booleano evento = requisicao.obterGARANTIAS_ITENS_Booleano();
      IdTipoGarantia tipoGarantia = requisicao.obterGARANTIAS_CODIGO_IdTipoGarantia();
      Id idCesta = cesta.getNumIdCestaGarantias();
      NumeroOperacao numOperacao = requisicao.obterOPERACAO_NumeroOperacao();

      // arruma contexto
      idCesta.atribuirContexto(Contexto.GARANTIAS_CODIGO);

      // atribui valores
      req.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(parte);
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contraParte);
      req.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(codTipoIf);
      req.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codIF);
      req.atribuirGARANTIAS_CODIGO_IdTipoGarantia(tipoGarantia);
      req.atribuirGARANTIAS_ITENS_Booleano(evento);
      req.atribuirGARANTIAS_QUANTIDADE_Quantidade(qt);
      req.atribuirGARANTIAS_ITENS_DescricaoLimitada(desc);
      req.atribuirGARANTIAS_CODIGO_Id(idCesta);
      req.atribuirACAO_Funcao(ICestaDeGarantias.INCLUIR_GARANTIA);
      req.atribuirOPERACAO_NumeroOperacao(numOperacao);

      // executa servico
      ServicoRegistraItensCestaGarantias ibg = new ServicoRegistraItensCestaGarantias();
      ibg.setGarantias(getFactory());
      try {
         ibg.executar(req);
      } catch (Exception e) {
         Logger.error(e);
         throw new RuntimeException(e);
      }
   }

}
