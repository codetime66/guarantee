package br.com.cetip.aplicacao.garantias.servico;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.GarantidosAstecVDO;
import br.com.cetip.dados.aplicacao.sca.ObjetoServicoDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="ARQUIVO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="Data" pacote="br.com.cetip.infra.atributo.tipo.tempo" contexto="ARQUIVO"
 * 
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador" contexto="CESTA_GARANTIA"
 * 
 * @resultado.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_IF_GARANTIDO"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="CARACTERISTICA_CESTA"
 * 
 * @resultado.method atributo="Quantidade" pacote="br.com.cetip.infra.atributo.tipo.numero" contexto="CESTA_GARANTIA"
 * 
 * @resultado.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="VOLUME_FINANCEIRO"
 * 
 * @resultado.method atributo="CodigoTipoIF" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_IF_GARANTIDOR"
 * 
 * @resultado.method atributo="ValorMonetario" pacote="br.com.cetip.infra.atributo.tipo.numero"
 *                   contexto="VOLUME_FINANCEIRO_GARANTIAS"
 * 
 * @resultado.method atributo="Texto" pacote="br.com.cetip.infra.atributo.tipo.texto" contexto="REFERENCIA"
 */
public class ServicoGeraArquivoCestaAstecGarantidos extends BaseGarantias implements Servico {

   private ResultadoServicoGeraArquivoCestaAstecGarantidos res = new ResultadoServicoGeraArquivoCestaAstecGarantidos();

   private IGerenciadorPersistencia gp;

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      gp = getGp();
      Data dataOperacao = getDataHoje();

      if (Condicional.vazio(dataOperacao)) {
         dataOperacao = getControleOperacional().obterDataBatch(new NumeroInteiro(0),
               new NumeroInteiro(ObjetoServicoDO.TIPO_IF_CCB.toString()));
      }

      List listaGarantidos = obterListaDeGarantidos();
      if (listaGarantidos.size() == 0) {
         if (Logger.estaHabilitadoDebug(this)) {
            Logger.debug(this, "Nao existem garantidos");
         }
         return res;
      }

      Iterator itGarantidos = listaGarantidos.iterator();
      while (itGarantidos.hasNext()) {
         GarantidosAstecVDO garantidoDO = (GarantidosAstecVDO) itGarantidos.next();

         res.novaLinha();
         res.atribuirARQUIVO_Data(dataOperacao);
         res.atribuirCESTA_GARANTIA_Id(/* Id idCesta */null);

         // preenche o resultado
         res.atribuirCESTA_GARANTIA_Quantidade(garantidoDO.getQtdeIFs());
         res.atribuirVOLUME_FINANCEIRO_ValorMonetario(garantidoDO.getVolumeFinanceiro());
         res.atribuirTIPO_IF_GARANTIDO_CodigoTipoIF(garantidoDO.getCodigoTipoIFGarantido());
         res.atribuirCARACTERISTICA_CESTA_Texto(new Texto(obterListaDeIFs(garantidoDO.getComposicaoCesta())));
         res.atribuirTIPO_IF_GARANTIDOR_CodigoTipoIF(garantidoDO.getCodigoTipoIFGarantias());
         res.atribuirVOLUME_FINANCEIRO_GARANTIAS_ValorMonetario(garantidoDO.getVolumeFinanceiroGarantias());

         Texto Ano = new Texto(dataOperacao.obterAno().toString());
         Texto Mes = dataOperacao.obterMes().obterInt() < 10 ? new Texto("0" + dataOperacao.obterMes().toString())
               : new Texto(dataOperacao.obterMes().toString());
         Texto Dia = dataOperacao.obterDia().obterInt() < 10 ? new Texto("0" + dataOperacao.obterDia().toString())
               : new Texto(dataOperacao.obterDia().toString());
         res.atribuirREFERENCIA_Texto(new Texto("DAT_REFERENCIA=" + Ano + Mes + Dia));
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Res aki:\n" + res);
      }
      return res;
   }

   private List obterListaDeGarantidos() {
      IConsulta con = null;
      con = gp.criarConsulta("from " + GarantidosAstecVDO.class.getName());

      return con.list();
   }

   private String obterListaDeIFs(Texto composicao) {
      // composicao eh um numero real com a seguinte formatacao: ...999.11223344556677.. (12 casas decimais)
      // cada tres digitos da casa decimal eh o codigo de um IF que compoem uma cesta de garantias
      // a parte inteira deve ser trabalhada de tras para frente de 3 em 3 tambem

      BigDecimal num = new BigDecimal(composicao.obterConteudo());

      // Refaz o numero passando o ponto decimal para frente de 3 em 3 ateh formar um numero 0.xxxyyyzzz...
      int parteInteira = 0;
      while (num.compareTo(new BigDecimal("1")) > 0) {
         num = num.movePointLeft(3);
         parteInteira++;
      }

      // Pega os valores da antiga parte decimal de 3 em 3 - sao 12 casas decimais, 4 trincas
      // Leva em conta as trincas achadas na parte inteira
      List list = new ArrayList();
      for (int i = 0; i < 4 + parteInteira; i++) {
         num = num.movePointRight(3);
         int numIF = num.intValue();
         if (numIF > 0) {
            list.add(Integer.toString(numIF));
         }
         num = num.subtract(new BigDecimal(Integer.toString(numIF)));
      }

      Iterator it = list.iterator();
      StringBuffer s = new StringBuffer();
      while (it.hasNext()) {
         if (s.length() > 0) {
            s.append(",");
         }
         s.append((String) it.next());
      }

      return s.toString();
   }

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new ExcecaoServico(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

}
