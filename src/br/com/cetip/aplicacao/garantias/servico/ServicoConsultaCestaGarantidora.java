package br.com.cetip.aplicacao.garantias.servico;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * @requisicao.method atributo="Id" 
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="INSTRUMENTO_FINANCEIRO"
 * 
 * @requisicao.method atributo="Id" 
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="PARAMETRO"
 *
 * @resultado.class
 * 
 * @resultado.method atributo="Id" 
 *                    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *                    contexto="CESTA_GARANTIA"
 * 
 * @author brunob
 */
public class ServicoConsultaCestaGarantidora extends BaseGarantias implements Servico {

   public Resultado executar(Requisicao arg0) throws Exception {
      return null;
   }

   public Resultado executarConsulta(Requisicao arg0) throws Exception {
      RequisicaoServicoConsultaCestaGarantidora req = (RequisicaoServicoConsultaCestaGarantidora) arg0;
      ResultadoServicoConsultaCestaGarantidora res = new ResultadoServicoConsultaCestaGarantidora();

      Id numIF = req.obterINSTRUMENTO_FINANCEIRO_Id();
      Id idPonta = req.obterPARAMETRO_Id();

      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      NumeroCestaGarantia numero = new NumeroCestaGarantia();

      if (Condicional.vazio(idPonta)) {
         numero = icg.obterCestaGarantindoIF(numIF);
      } else {
         numero = icg.obterCestaGarantindoIF(numIF, idPonta);
      }

      String sNumero = numero != null ? numero.toString() : "";

      res.atribuirCESTA_GARANTIA_Id(new Id(sNumero));
      return res;
   }

}
