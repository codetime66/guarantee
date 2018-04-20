package br.com.cetip.aplicacao.garantias.servico.colateral;

import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.texto.NomeRegra;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

/**
 * @requisicao.class
 * 
 * @requisicao.method
 *     atributo="CodigoContaCetip"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CONTA_GARANTIDO"
 *     
 *  @requisicao.method 
 *    atributo="NomeSimplificado"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="NOME_SIMPLIFICADO_GARANTIDO"
 *     
 * @requisicao.method 
 *    atributo="Natureza"
 *    pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *    contexto="NATUREZA_GARANTIDO"
 *    
 *  @requisicao.method 
 *    atributo="CPFOuCNPJ"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="CPF_CNPJ_GARANTIDO"
 *    
 *  @requisicao.method 
 *    atributo="CodigoTipoIF"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="TIPO_IF_GARANTIDO" 
 *       
 *  @requisicao.method 
 *    atributo="CodigoIF"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="CODIGO_IF_GARANTIDO" 
 *    
 * @requisicao.method
 *     atributo="CodigoContaCetip"
 *     pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *     contexto="CONTA_GARANTIDOR"
 *     
 *  @requisicao.method 
 *    atributo="NomeSimplificado"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="NOME_SIMPLIFICADO_GARANTIDOR"
 *     
 * @requisicao.method 
 *    atributo="Natureza"
 *    pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *    contexto="NATUREZA_GARANTIDOR"
 *    
 *  @requisicao.method 
 *    atributo="CPFOuCNPJ"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="CPF_CNPJ_GARANTIDOR"
 *    
 *  @requisicao.method 
 *    atributo="CodigoTipoIF"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="TIPO_IF_GARANTIDOR" 
 *       
 *       
 *  @requisicao.method 
 *    atributo="CodigoIF"
 *    pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *    contexto="CODIGO_IF_GARANTIDOR"
 *    
 *        
 *        
 * @resultado.class
 */
public class ServicoValidaConsultaPosicaoIFGarantia implements Servico {

   /* (non-Javadoc)
    * @see br.com.cetip.infra.servico.interfaces.Servico#executar(br.com.cetip.infra.servico.interfaces.Requisicao)
    */
   public Resultado executar(Requisicao arg0) throws Exception {
      return null;
   }

   /* (non-Javadoc)
    * @see br.com.cetip.infra.servico.interfaces.Servico#executarConsulta(br.com.cetip.infra.servico.interfaces.Requisicao)
    */
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoValidaConsultaPosicaoIFGarantia req = (RequisicaoServicoValidaConsultaPosicaoIFGarantia) requisicao;
      ResultadoServicoValidaConsultaPosicaoIFGarantia res = new ResultadoServicoValidaConsultaPosicaoIFGarantia();
 
      //dados garantido
      CodigoContaCetip  contaGarantido = req.obterCONTA_GARANTIDO_CodigoContaCetip();
      NomeSimplificado nomeGarantido = req.obterNOME_SIMPLIFICADO_GARANTIDO_NomeSimplificado();
      CodigoTipoIF   codTipoIFGarantido = req.obterTIPO_IF_GARANTIDO_CodigoTipoIF();
      CodigoIF codIFGarantido = req.obterCODIGO_IF_GARANTIDO_CodigoIF();
      Natureza natGarantido = req.obterNATUREZA_GARANTIDO_Natureza();
      CPFOuCNPJ cpfCnpjGarantido = req.obterCPF_CNPJ_GARANTIDO_CPFOuCNPJ();
      
      //dados garantidor
      CodigoContaCetip  contaGarantidor = req.obterCONTA_GARANTIDOR_CodigoContaCetip();
      CodigoTipoIF   codTipoIFGarantidor = req.obterTIPO_IF_GARANTIDOR_CodigoTipoIF();
      CodigoIF codIFGarantidor = req.obterCODIGO_IF_GARANTIDOR_CodigoIF();
      NomeSimplificado nomeGarantidor = req.obterNOME_SIMPLIFICADO_GARANTIDOR_NomeSimplificado();
      Natureza natGarantidor = req.obterNATUREZA_GARANTIDOR_Natureza();
      CPFOuCNPJ cpfCnpjGarantidor = req.obterCPF_CNPJ_GARANTIDOR_CPFOuCNPJ();
      CodigoContaCetip contaPrincipalGarantidor = new CodigoContaCetip();
      CodigoContaCetip contaPrincipalGarantido = new CodigoContaCetip();
      
      //obter a conta principal do participante pelo nome simplificado      
      if (Condicional.vazio(contaGarantido) &&
    	  Condicional.vazio(contaGarantidor)&&
    	  !Condicional.vazio(nomeGarantidor)) {
    	  ContaParticipanteDO cpDO =ContaParticipanteFactory.getInstance().obterContaPrincipalParticipanteDO(nomeGarantidor);
    	  if (!Condicional.vazio(cpDO)){
    	     contaPrincipalGarantidor = cpDO.getCodContaParticipante();
    	     //contaPrincipalGarantidor.atribuirContexto(Contexto.c)
    	  }
      }
      
      if (Condicional.vazio(contaGarantido) &&
          Condicional.vazio(contaGarantidor)&&
          !Condicional.vazio(nomeGarantido)) {
    	  
          ContaParticipanteDO cpDO =ContaParticipanteFactory.getInstance().obterContaPrincipalParticipanteDO(nomeGarantido);
          if (!Condicional.vazio(cpDO)){
             contaPrincipalGarantido = cpDO.getCodContaParticipante();
          }
      }

      //Acrescentar o parametro para fazer valiações
      NomeRegra predicado = ConstantesDeNomeDeRegras.validaConsultaPosicaoIFGarantia;

      AtributosColunados termos = new AtributosColunados();
      termos.atributo(contaGarantido);
      termos.atributo(nomeGarantido);
      termos.atributo(codTipoIFGarantido);
      termos.atributo(codIFGarantido);
      termos.atributo(natGarantido);
      termos.atributo(cpfCnpjGarantido);      
      termos.atributo(contaGarantidor);
      termos.atributo(nomeGarantidor);
      termos.atributo(codTipoIFGarantidor);
      termos.atributo(codIFGarantidor);
      termos.atributo(natGarantidor);
      termos.atributo(cpfCnpjGarantidor);
      termos.atributo(contaPrincipalGarantido);
      termos.atributo(contaPrincipalGarantidor);

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "### ServicoValidaConsultaPosicaoIFGarantia : AtributosColunados (termos) : ");
         Logger.debug(this, termos.toString());
      }

      FabricaDeMotorDeRegra.getMotorDeRegra().avalia(predicado, termos);

      return res;
   }

}
