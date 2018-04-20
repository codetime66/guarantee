package br.com.cetip.aplicacao.garantias.servico;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * Consulta lista de cestas de garantias
 * 
 * @requisicao.class
 * @requisicao.method atributo="NumeroCestaGarantia"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.class
 * 
 * @resultado.method atributo="CodigoSistema"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="SISTEMA"
 * 
 * @resultado.method atributo="NumeroCestaGarantia"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO"
 * 
 * @resultado.method atributo="CodigoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="PARTICIPANTE"
 * 
 * @resultado.method atributo="CodigoTipoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="PARTICIPANTE"
 * 
 * @resultado.method atributo="Booleano"
 *                   pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="INADIMPLENTE"
 * 
 */
public class ServicoListaAtivosVinculados extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws Exception {
      throw new Erro(CodigoErro.SERVICO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoListaAtivosVinculados req = (RequisicaoServicoListaAtivosVinculados) requisicao;
      NumeroCestaGarantia numero = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();

      StringBuffer hql = new StringBuffer(500);
      hql.append("select c.instrumentoFinanceiro.codigoIF, ");
      hql.append("c.instrumentoFinanceiro.tipoIF.codigoTipoIF, ");
      hql.append("c.instrumentoFinanceiro.indInadimplencia, ");
      hql.append("c.instrumentoFinanceiro.sistema.codSistema ");
      hql.append(" from ");
      hql.append(CestaGarantiasIFDO.class.getName());
      hql.append(" c where c.cestaGarantia = ? ");

      List l = getGp().find(hql.toString(), new Object[] { numero.copiarParaId() });

      ResultadoServicoListaAtivosVinculados res = new ResultadoServicoListaAtivosVinculados();
      Iterator i = l.iterator();
      while (i.hasNext()) {
         Object[] linha = (Object[]) i.next();

         CodigoIF codigoIF = (CodigoIF) linha[0];
         CodigoTipoIF tipoIF = (CodigoTipoIF) linha[1];
         Booleano indInadimplencia = (Booleano) linha[2];
         CodigoSistema codSistema = (CodigoSistema) linha[3];

         res.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
         res.atribuirPARTICIPANTE_CodigoIF(codigoIF);
         res.atribuirPARTICIPANTE_CodigoTipoIF(tipoIF);
         res.atribuirINADIMPLENTE_Booleano(Condicional.vazio(indInadimplencia) ? Booleano.FALSO : indInadimplencia);
         res.atribuirSISTEMA_CodigoSistema(codSistema);

         res.novaLinha();
      }

      return res;
   }

}