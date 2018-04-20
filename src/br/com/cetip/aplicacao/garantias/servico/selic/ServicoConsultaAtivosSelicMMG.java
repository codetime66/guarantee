package br.com.cetip.aplicacao.garantias.servico.selic;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IGarantiasSelic;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.infra.servico.util.ExcecaoServico;

/**
 * @requisicao.class
 *
 * @requisicao.method atributo="CodigoTipoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CODIGO_TIPO_IF"
 *
 * @requisicao.method atributo="CodigoIF"
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CODIGO_IF"
 *
 * @resultado.class
 *
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 *
 * @resultado.method atributo="CodigoTipoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="CODIGO_TIPO_IF"
 *
 * @resultado.method atributo="CodigoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="INSTRUMENTO_FINANCEIRO"
 *
 * @resultado.method atributo="CodigoSistema"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="SISTEMA"
 *
 * @resultado.method atributo="Booleano"
 *                   pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *                   contexto="ATIVO_GARANTIA"
 *
 * @resultado.method atributo="Id" pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="TIPO_COLATERAL"
 *
 * @resultado.method atributo="CodigoTipoIF"
 *                   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                   contexto="GARANTIAS_CODIGO_TIPO"
 *
 */
public class ServicoConsultaAtivosSelicMMG extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao req) throws ExcecaoServico {
      throw new ExcecaoServico(CodigoErro.METODO_NAO_IMPLEMENTADO);
   }

   public Resultado executarConsulta(Requisicao requisicao) throws Exception {

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, ">> Executando o servico ServicoConsultaAtivosSelicMMG");
      }

      RequisicaoServicoConsultaAtivosSelicMMG req = (RequisicaoServicoConsultaAtivosSelicMMG) requisicao;

      ResultadoServicoConsultaAtivosSelicMMG res = new ResultadoServicoConsultaAtivosSelicMMG();
      CodigoTipoIF codTipoIF = req.obterCODIGO_TIPO_IF_CodigoTipoIF();
      CodigoIF codIF = req.obterCODIGO_IF_CodigoIF();

      IGarantias gf = getFactory();
      IGarantiasSelic dao = gf.getInstanceGarantiasSelic();
      List ativos = dao.obterAtivosSelicMMG(codTipoIF, codIF);

      Logger.info(this, "Consulta de ativos mmg: antes do loop " + codTipoIF);

      if (ativos.isEmpty()) {
         throw new Erro(CodigoErro.RESULTADO_INEXISTENTE);
      }

      Iterator i = ativos.iterator();
      while (i.hasNext()) {
         Object[] ativo = (Object[]) i.next();

         Id numIF = (Id) ativo[0];
         CodigoTipoIF tipoIF = (CodigoTipoIF) ativo[1];
         CodigoIF codigoIF = (CodigoIF) ativo[2];
         CodigoSistema sistema = (CodigoSistema) ativo[3];
         Booleano indAtivo = (Booleano) ativo[4];
         Id colateral = (Id) ativo[5];
         CodigoTipoIF garantidor = (CodigoTipoIF) ativo[6];
         Data dataExclusao = (Data) ativo[7];
         Logger.info(this, "Consulta de ativos mmg: " + dataExclusao);

         res.novaLinha();
         res.atribuirINSTRUMENTO_FINANCEIRO_Id(numIF);
         res.atribuirCODIGO_TIPO_IF_CodigoTipoIF(tipoIF);
         res.atribuirINSTRUMENTO_FINANCEIRO_CodigoIF(codigoIF);
         res.atribuirSISTEMA_CodigoSistema(sistema);
         if (Condicional.vazio(dataExclusao)) {

            res.atribuirATIVO_GARANTIA_Booleano(indAtivo);
            res.atribuirTIPO_COLATERAL_Id(colateral);
            res.atribuirGARANTIAS_CODIGO_TIPO_CodigoTipoIF(garantidor);
         }
      }

      return res;
   }

}
