package br.com.cetip.aplicacao.garantias.servico.colateral;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.administracao.apinegocio.ControleOperacionalFactory;
import br.com.cetip.dados.aplicacao.garantias.HabilitaIFGarantidorDO;
import br.com.cetip.dados.aplicacao.garantias.PosicaoIFGarantiaVDO;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.GerenciadorPersistenciaFactory;
import br.com.cetip.infra.servico.interfaces.Requisicao;
import br.com.cetip.infra.servico.interfaces.Resultado;
import br.com.cetip.infra.servico.interfaces.Servico;

/**
 * @requisicao.class
 * 
 * 
 *   @requisicao.method 
 * 	 atributo="Data"
 *   pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *   contexto="CONSULTA"
 *   
 *   @requisicao.method 
 *   atributo="CodigoContaCetip"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="MALOTE"
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
 * @resultado.class
 * 
 *  @resultado.method 
 *   atributo="CodigoContaCetip"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="CONTA_GARANTIDO"
 *          
 *  @resultado.method 
 *   atributo="RazaoSocial"
 *   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *   contexto="GARANTIDO"
 *     
 *  @resultado.method 
 *   atributo="NomeSimplificado"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="NOME_SIMPLIFICADO_GARANTIDO"
 *     
 *  @resultado.method 
 *   atributo="CodigoTipoConta"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="CONTA_GARANTIDO"
 * 
 *  @resultado.method 
 * 	 atributo="Natureza"
 *   pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *   contexto="NATUREZA_GARANTIDO"
 *   
 *   @resultado.method 
 *   atributo="CPFOuCNPJ"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="CPF_CNPJ_GARANTIDO"
 * 
 *  @resultado.method 
 * 	 atributo="CodigoSistema"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIDO"
 *   
 *   @resultado.method 
 * 	 atributo="CodigoTipoIF"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="TIPO_IF_GARANTIDO"
 *   
 *   @resultado.method 
 *   atributo="CodigoIF"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="CODIGO_IF_GARANTIDO"
 * 
 *  @resultado.method 
 * 	 atributo="Data"
 *   pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *   contexto="GARANTIDO_DATA_VENCIMENTO"
 *   
 *   @resultado.method 
 * 	 atributo="CodigoSistema"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIDOR"
 *   
 *   @resultado.method 
 *   atributo="CodigoTipoIF"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="TIPO_IF_GARANTIDOR"
 * 
 *  @resultado.method 
 * 	 atributo="CodigoIF"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="CODIGO_IF_GARANTIDOR"
 *        
 *   @resultado.method 
 * 	 atributo="QuantidadeInteiraPositiva"
 *   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *   contexto="GARANTIDOR_QUANTIDADE"
 *   
 *   @resultado.method 
 * 	 atributo="ValorMonetario"
 *   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *   contexto="GARANTIDOR_VAL_CURVA"
 *   
 *   @resultado.method 
 *   atributo="Data"
 *   pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *   contexto="GARANTIDOR_DATA_ATUALIZACAO"
 * 
 *  @resultado.method 
 * 	 atributo="ValorMonetario"
 *   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *   contexto="GARANTIDOR_VAL_MTM"    
 *   
 *   @resultado.method 
 * 	 atributo="Data"
 *   pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *   contexto="GARANTIDOR_DATA_ATU_MTM"
 *   
 *   @resultado.method 
 * 	 atributo="Id"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="GARANTIDOR_CESTA_GARANTIAS"
 *   
 *   @resultado.method 
 *   atributo="CodigoContaCetip"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="CONTA_GARANTIDOR"
 * 
 *  @resultado.method 
 * 	 atributo="NomeSimplificado"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="NOME_SIMPLIFICADO_GARANTIDOR"
 *   
 *   @resultado.method 
 * 	 atributo="RazaoSocial"
 *   pacote="br.com.cetip.infra.atributo.tipo.texto"
 *   contexto="GARANTIDOR"
 *   
 *   @resultado.method 
 *   atributo="CodigoTipoConta"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="CONTA_GARANTIDOR"
 * 
 *  @resultado.method 
 * 	 atributo="Natureza"
 *   pacote="br.com.cetip.infra.atributo.tipo.expressao"
 *   contexto="NATUREZA_GARANTIDOR"
 *   
 *   @resultado.method 
 * 	 atributo="CPFOuCNPJ"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="CPF_CNPJ_GARANTIDOR"
 *     
 *   @resultado.method 
 * 	 atributo="NumeroInteiro"
 *   pacote="br.com.cetip.infra.atributo.tipo.numero"
 *   contexto="QUANTIDADE"
 *  
 *  @resultado.method 
 * 	 atributo="Data"
 *   pacote="br.com.cetip.infra.atributo.tipo.tempo"
 *   contexto="CONSULTA"
 *   
 *   @resultado.method 
 *   atributo="CodigoContaCetip"
 *   pacote="br.com.cetip.infra.atributo.tipo.identificador"
 *   contexto="MALOTE"
 * 
 */
public class ServicoConsultaPosicaoIFGarantia implements Servico {

   public Resultado executar(Requisicao requisicao) throws Exception {
      throw new UnsupportedOperationException();
   }   


   private Id idHabilitaIFGarantidor = new Id();
   private StringBuffer hql = new StringBuffer();
   private List hqlParams = new ArrayList();
   private Booleano ehPrimeiroParametro= new Booleano();
   private CodigoContaCetip contaMalote=null;
   private NumeroInteiro qtdLinhas=null;
   private Data dataSistema = null;
   
   public Resultado executarConsulta(Requisicao requisicao) throws Exception {
      RequisicaoServicoConsultaPosicaoIFGarantia req = (RequisicaoServicoConsultaPosicaoIFGarantia) requisicao;
      ResultadoServicoConsultaPosicaoIFGarantia resultado = new ResultadoServicoConsultaPosicaoIFGarantia();


      PosicaoIFGarantiaVDO IFGarantiaVDO =  null;
      HabilitaIFGarantidorDO habIFGarantDO = null;
      List listParamIF = new ArrayList();

      //Dados de Filtro Garantido
      CodigoContaCetip contaGarantido = req.obterCONTA_GARANTIDO_CodigoContaCetip();
      NomeSimplificado nomSimplGarantido = req.obterNOME_SIMPLIFICADO_GARANTIDO_NomeSimplificado();
      Natureza natGarantido = req.obterNATUREZA_GARANTIDO_Natureza();
      CPFOuCNPJ cpfCnpjGarantido = req.obterCPF_CNPJ_GARANTIDO_CPFOuCNPJ();
      CodigoTipoIF codTipoIFGarantido = req.obterTIPO_IF_GARANTIDO_CodigoTipoIF();
      CodigoIF codigoIFGarantido = req.obterCODIGO_IF_GARANTIDO_CodigoIF();
      
     //Dados filtro Garantidor
      CodigoContaCetip contaGarantidor = req.obterCONTA_GARANTIDOR_CodigoContaCetip();
      NomeSimplificado nomSimplGarantidor = req.obterNOME_SIMPLIFICADO_GARANTIDOR_NomeSimplificado();
      Natureza natGarantidor = req.obterNATUREZA_GARANTIDOR_Natureza();
      CPFOuCNPJ cpfCnpjGarantidor = req.obterCPF_CNPJ_GARANTIDOR_CPFOuCNPJ();
      CodigoTipoIF codTipoIFGarantidor = req.obterTIPO_IF_GARANTIDOR_CodigoTipoIF();
      CodigoIF codigoIFGarantidor = req.obterCODIGO_IF_GARANTIDOR_CodigoIF();     
      
      if(!Condicional.vazio(contaGarantidor)){
    	  contaMalote = contaGarantidor;
      }else{
    	  contaMalote = contaGarantido;
      }
      
      ehPrimeiroParametro = Booleano.FALSO;
      
      hql.append(" from PosicaoIFGarantiaVDO pif ");
      param(contaGarantido, "pif.codContaGarantido = ?");
      param(nomSimplGarantido, "pif.nomeSimpGarantido = ?");
      param(natGarantido,"pif.naturezaGarantido = ?");
      param(cpfCnpjGarantido,"pif.cpfCnpjGarantido = ?");
      param(codTipoIFGarantido,"pif.codTipoIFGarantido = ?");
      param(codigoIFGarantido,"pif.codigoIFGarantido = ?");      
      param(contaGarantidor, "pif.codContaGarantidor = ?");
      param(nomSimplGarantidor, "pif.nomeSimpGarantidor = ?");
      param(natGarantidor,"pif.naturezaGarantidor = ?");
      param(cpfCnpjGarantidor,"pif.cpfCnpjGarantidor = ?");
      param(codTipoIFGarantidor,"pif.codTipoIFGarantidor = ?");
      param(codigoIFGarantidor,"pif.codigoIFGarantidor = ?");
      

      listParamIF = GerenciadorPersistenciaFactory.getGerenciadorPersistencia().find(hql.toString(),
            hqlParams.toArray());

      qtdLinhas = new NumeroInteiro(listParamIF.size());
      dataSistema = ControleOperacionalFactory.getInstance().obterD0();
      
      if (listParamIF.isEmpty()) {
         throw new Erro(CodigoErro.GARANTIAS_LISTA_VAZIA);
      }

      Iterator itParamIF = listParamIF.iterator();

      while (itParamIF.hasNext()) {
    	  IFGarantiaVDO = (PosicaoIFGarantiaVDO) itParamIF.next();
          preencheResultado(resultado,IFGarantiaVDO);
      }

      return resultado;
   }

   private void param(Atributo param, String s) {

	  Texto montaQuery = null;
	   
      if (!Condicional.vazio(param) ) {
    	  
    	 if (ehPrimeiroParametro.ehFalso()){
    	     ehPrimeiroParametro = Booleano.VERDADEIRO;
    	     montaQuery = new Texto("Where "+s);
    	 }else{
            montaQuery = new Texto("and "+s);
    	 }
    	 hql.append(' ').append(montaQuery);    	 
         hqlParams.add(param);
         
      }
   }

   private void preencheResultado(ResultadoServicoConsultaPosicaoIFGarantia resultado,PosicaoIFGarantiaVDO IFGarantDO) {
      resultado.atribuirCONTA_GARANTIDO_CodigoContaCetip(IFGarantDO.getCodContaGarantido());
      resultado.atribuirGARANTIDO_RazaoSocial(IFGarantDO.getRazaoSocialGarantido());
      resultado.atribuirNOME_SIMPLIFICADO_GARANTIDO_NomeSimplificado(IFGarantDO.getNomeSimpGarantido());
      resultado.atribuirCONTA_GARANTIDO_CodigoTipoConta(IFGarantDO.getTpContaGarantido());
      resultado.atribuirCODIGO_IF_GARANTIDO_CodigoIF(IFGarantDO.getCodigoIFGarantido());
      resultado.atribuirGARANTIDO_DATA_VENCIMENTO_Data(IFGarantDO.getDataVencimento());
      resultado.atribuirGARANTIDO_CodigoSistema(IFGarantDO.getModuloGarantido());
      resultado.atribuirTIPO_IF_GARANTIDO_CodigoTipoIF(IFGarantDO.getCodTipoIFGarantido());
      resultado.atribuirNATUREZA_GARANTIDO_Natureza(IFGarantDO.getNaturezaGarantido());
      resultado.atribuirCPF_CNPJ_GARANTIDO_CPFOuCNPJ(IFGarantDO.getCpfCnpjGarantido());
      
      resultado.atribuirGARANTIDOR_CodigoSistema(IFGarantDO.getModuloGarantidor());
      resultado.atribuirCODIGO_IF_GARANTIDOR_CodigoIF(IFGarantDO.getCodigoIFGarantidor());
      resultado.atribuirTIPO_IF_GARANTIDOR_CodigoTipoIF(IFGarantDO.getCodTipoIFGarantidor());
      resultado.atribuirGARANTIDOR_QUANTIDADE_QuantidadeInteiraPositiva(IFGarantDO.getQtdGarantidor());
      resultado.atribuirGARANTIDOR_VAL_CURVA_ValorMonetario(IFGarantDO.getValorIFCurva());
      resultado.atribuirGARANTIDOR_DATA_ATUALIZACAO_Data(IFGarantDO.getDataAtualizacaoCurva());
      resultado.atribuirGARANTIDOR_CESTA_GARANTIAS_Id(IFGarantDO.getNumIdCestaGarantias());
      resultado.atribuirCONTA_GARANTIDOR_CodigoContaCetip(IFGarantDO.getCodContaGarantidor());
      resultado.atribuirGARANTIDOR_RazaoSocial(IFGarantDO.getRazaoSocialGarantidor());
      resultado.atribuirNOME_SIMPLIFICADO_GARANTIDOR_NomeSimplificado(IFGarantDO.getNomeSimpGarantidor());
      resultado.atribuirCONTA_GARANTIDOR_CodigoTipoConta(IFGarantDO.getTpContaGarantidor());
      resultado.atribuirNATUREZA_GARANTIDOR_Natureza(IFGarantDO.getNaturezaGarantidor());
      resultado.atribuirCPF_CNPJ_GARANTIDOR_CPFOuCNPJ(IFGarantDO.getCpfCnpjGarantidor());
      resultado.atribuirGARANTIDOR_VAL_MTM_ValorMonetario(IFGarantDO.getValMarcaoMercado());
      resultado.atribuirGARANTIDOR_DATA_ATU_MTM_Data(IFGarantDO.getDataMarcacaoMercado()); 
      resultado.atribuirQUANTIDADE_NumeroInteiro(qtdLinhas);
      resultado.atribuirMALOTE_CodigoContaCetip(contaMalote);
      resultado.atribuirCONSULTA_Data(dataSistema);

      resultado.novaLinha();

   }

}