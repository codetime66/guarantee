package br.com.cetip.aplicacao.garantias.servico.selic;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.TipoIFFactory;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.Descricao;
import br.com.cetip.infra.atributo.tipo.texto.DescricaoLimitada;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.tipo.texto.TextoLimitado;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico de consulta de garantias para manutencao de numero de operacao de uma
 * determinada cesta.
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="NumeroCestaGarantia"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_ITENS"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="TIPO_IF"
 * 
 * @resultado.method atributo="CodigoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_IF"
 * 
 * @resultado.method atributo="Descricao" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="TIPO_GARANTIA"
 * 
 * @resultado.method atributo="Quantidade"
 *                   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="GARANTIAS_QUANTIDADE"
 * 
 * @resultado.method atributo="Booleano"
 *                   pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="GARANTIAS_ITENS"
 * 
 * @resultado.method atributo="NumeroCestaGarantia"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="CodigoContaCetip"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *                   contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @resultado.method atributo="Funcao"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="ACAO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_ITENS"
 * 
 * @resultado.method atributo="DescricaoLimitada"
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="GARANTIAS_ITENS"
 * 
 * @resultado.method atributo="Booleano"
 *                   pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="INADIMPLENTE"
 * 
 * @resultado.method atributo="Booleano"
 *                   pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="INADIMPLENTE_EMISSOR"
 * 
 * @resultado.method atributo="Booleano"
 *                   pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="GARANTIDO"
 * 
 * @resultado.method atributo="TextoLimitado"
 *                   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *                   contexto="CESTA_GARANTIA"
 * 
 * @resultado.method atributo="NumeroOperacao"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="OPERACAO"
 */
public class ServicoListaGarantiasCestaParaManutencaoNumOp extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Entrou no servico");
      }

      RequisicaoServicoListaGarantiasCestaParaManutencaoNumOp req = (RequisicaoServicoListaGarantiasCestaParaManutencaoNumOp) requisicao;
      ResultadoServicoListaGarantiasCestaParaManutencaoNumOp res = new ResultadoServicoListaGarantiasCestaParaManutencaoNumOp();

      NumeroCestaGarantia idCesta = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      CestaGarantiasDO cesta = getFactory().getInstanceCestaDeGarantias().obterCestaDeGarantias(idCesta);

      List list = getFactory().getInstanceGarantiasSelic().obterMovimentacaoAtivosSelicados(idCesta);
      atribuirGarantias(list.iterator(), res, cesta);

      res.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(cesta.getGarantidor().getCodContaParticipante());
      res.atribuirGARANTIAS_PARTICIPANTE_Nome(cesta.getGarantidor().getNomeContaParticipante());
      res.atribuirGARANTIAS_DATA_CRIACAO_Data(cesta.getDatCriacao());

      IGarantidoCesta igc = getFactory().getInstanceGarantidoCesta();
      ContaParticipanteDO garantido = igc.obterGarantidoCesta(cesta);
      if (garantido != null) {
         res.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(garantido.getCodContaParticipante());
         res.atribuirGARANTIAS_CONTRAPARTE_Nome(garantido.getNomeContaParticipante());
      }

      return res;
   }

   private void atribuirGarantias(Iterator garantias, ResultadoServicoListaGarantiasCestaParaManutencaoNumOp res,
         CestaGarantiasDO cesta) {

      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();

      while (garantias.hasNext()) {
         Object[] linha = (Object[]) garantias.next();

         Id idLinha = (Id) linha[0];
         Booleano indDireitos = (Booleano) linha[1];
         Quantidade quantidade = (Quantidade) linha[2];
         Descricao desTipoGarantia = (Descricao) linha[3];
         Texto descricao = (Texto) linha[4];
         CodigoIF codigoIF = (CodigoIF) linha[6];
         Booleano indAtivoInadimplente = (Booleano) linha[7];
         Booleano indEmissorInadimplente = (Booleano) linha[8];
         CodigoTipoIF codTipoIF = (CodigoTipoIF) linha[9];
         Id idAtivo = (Id) linha[11];
         NumeroOperacao numOperacao = (NumeroOperacao) linha[12];

         res.novaLinha();
         res.atribuirGARANTIAS_ITENS_Texto(new Texto("M"));

         res.atribuirGARANTIAS_CODIGO_IF_CodigoIF(codigoIF);
         res.atribuirINADIMPLENTE_Booleano(indAtivoInadimplente == null ? new Booleano(Booleano.FALSO)
               : indAtivoInadimplente);

         NumeroCestaGarantia cestaQueGaranteIF = icg.obterCestaGarantindoIF(idAtivo);

         if (cestaQueGaranteIF == null) {
            res.atribuirGARANTIDO_Booleano(new Booleano(Booleano.FALSO));
            res.atribuirCESTA_GARANTIA_TextoLimitado(new TextoLimitado(Contexto.CESTA_GARANTIA, "-"));
         } else {
            res.atribuirGARANTIDO_Booleano(new Booleano(Booleano.VERDADEIRO));
            res.atribuirCESTA_GARANTIA_TextoLimitado(new TextoLimitado(Contexto.CESTA_GARANTIA, cestaQueGaranteIF
                  .toString()));
         }

         res.atribuirINADIMPLENTE_EMISSOR_Booleano(new Booleano(indEmissorInadimplente == null ? Booleano.FALSO
               : indEmissorInadimplente));

         // Tipo IF
         boolean ehSelic;
         try {
            ehSelic = TipoIFFactory.getInstance().ehTipoIFSelicado(codTipoIF);
         } catch (Exception e) {
            ehSelic = false;
            Logger.error(e);
         }
         res.atribuirTIPO_IF_Nome(new Nome(ehSelic ? CodigoTipoIF.SELIC.toString() : codTipoIF.toString()));

         // Descricao da Garantia
         res.atribuirGARANTIAS_ITENS_DescricaoLimitada(new DescricaoLimitada(descricao == null ? "" : descricao
               .toString()));

         // Tipo Garantia
         res.atribuirTIPO_GARANTIA_Descricao(desTipoGarantia);

         // Quantidade
         res.atribuirGARANTIAS_QUANTIDADE_Quantidade(quantidade);

         // Direitos Garantidor
         res.atribuirGARANTIAS_ITENS_Booleano(indDireitos);

         // Número da operação do ativo selicado
         res.atribuirOPERACAO_NumeroOperacao(numOperacao);

         res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(new NumeroCestaGarantia(cesta.getNumIdCestaGarantias()));

         res.atribuirGARANTIAS_ITENS_Id(idLinha);

         Funcao f = new Funcao(Contexto.ACAO);
         f.getDomain().add(new Funcao(""));
         f.getDomain().add(ICestaDeGarantias.ALTERAR_NUMERO_OPERACAO);
         res.atribuirACAO_Funcao(f);
      }
   }
}