package br.com.cetip.aplicacao.garantias.servico;

import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Servico para validacao dos dados inseridos pelo usuario na interface grafica de compra/venda de cda/wa.
 * 
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="GARANTIAS_DATA_CRIACAO"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_CONTRAPARTE"
 * 
 * @resultado.method atributo="Nome" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @resultado.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_PARTICIPANTE"
 * 
 * 
 * @requisicao.class
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_PARTICIPANTE"
 * 
 * @requisicao.method atributo="CodigoContaCetip" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CONTRAPARTE"
 */
public class ServicoObterDataContaCestaGarantias extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      return null;
   }

   public Resultado executarConsulta(Requisicao requisicao) {
      RequisicaoServicoObterDataContaCestaGarantias req = (RequisicaoServicoObterDataContaCestaGarantias) requisicao;
      ResultadoServicoObterDataContaCestaGarantias res = new ResultadoServicoObterDataContaCestaGarantias();

      CodigoContaCetip participante = req.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();
      CodigoContaCetip contraParte = req.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();

      Data d0 = getDataHoje();
      Nome[] nomes = obterNomeSimplificado(participante, contraParte);

      res.atribuirGARANTIAS_PARTICIPANTE_CodigoContaCetip(participante);
      res.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contraParte);
      res.atribuirGARANTIAS_DATA_CRIACAO_Data(d0);
      res.atribuirGARANTIAS_PARTICIPANTE_Nome(nomes[0]);
      res.atribuirGARANTIAS_CONTRAPARTE_Nome(nomes[1]);

      return res;
   }

   private Nome[] obterNomeSimplificado(CodigoContaCetip conta, CodigoContaCetip contraParte) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Obtencao de nome simplificado dos participantes das contas: " + conta + ", e "
               + contraParte);
      }

      IConsulta c = getGp().criarConsulta(
            "select c.codContaParticipante,c.nomeContaParticipante from ContaParticipanteDO c "
                  + "where (c.codContaParticipante = ? or c.codContaParticipante = ?) ");

      c.setAtributo(0, conta);
      c.setAtributo(1, contraParte);
      c.setCacheable(true);
      c.setCacheRegion("MMG");

      List l = c.list();

      Nome nomes[] = new Nome[2];
      Object[] linha1 = (Object[]) l.get(0);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Obteve " + l.size() + " nomes.");
      }

      if (l.size() == 2) {
         Object[] linha2 = (Object[]) l.get(1);
         if (conta.mesmoConteudo((CodigoContaCetip) linha1[0])) {
            nomes[0] = (Nome) linha1[1];
            nomes[1] = (Nome) linha2[1];
         } else {
            nomes[0] = (Nome) linha2[1];
            nomes[1] = (Nome) linha1[1];
         }

         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Nomes: " + nomes[0] + " e " + nomes[1]);
         }

         return nomes;
      } else if (l.size() == 1) {
         linha1 = (Object[]) l.get(0);
         nomes[0] = (Nome) linha1[1];

         if (conta.mesmoConteudo(contraParte)) {
            if (Logger.estaHabilitadoDebug(this)) {
               Logger.debug(this, "Parte e Contra parte sao iguais.");
            }

            nomes[1] = nomes[0];
         } else {
            if (Logger.estaHabilitadoDebug(this)) {
               Logger.debug(this, "Nao ha contra parte");
            }

            nomes[1] = new Nome("");
         }

         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Nome: " + nomes[0]);
         }

         return nomes;
      } else {
         throw new Erro(CodigoErro.CONTA_INEXISTENTE);
      }
   }

}
